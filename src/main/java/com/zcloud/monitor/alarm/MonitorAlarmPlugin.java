package com.zcloud.monitor.alarm;

import com.zcloud.monitor.alarm.entity.AlarmMetric;
import com.zcloud.monitor.alarm.jnrpe.AlarmPluginBase;
import com.zcloud.monitor.alarm.manager.MonitorAlarmService;
import com.zcloud.monitor.alarm.util.BeanFactory;
import it.jnrpe.ICommandLine;
import it.jnrpe.ReturnValue;
import it.jnrpe.plugins.Metric;
import it.jnrpe.plugins.MetricGatheringException;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.thresholds.ReturnValueBuilder;
import it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: yuyangning
 * Date: 1/29/15
 * Time: 6:24 PM
 */
public class MonitorAlarmPlugin extends AlarmPluginBase {

    private final static String PLUGIN_NAME = "ELASTIC_ALARM";
    private final MonitorAlarmService alarmService;

    public MonitorAlarmPlugin() {
        alarmService = BeanFactory.getAlarmServiceIstance();
    }

    @Override
    protected String getPluginName() {
        return PLUGIN_NAME;
    }

    public ReturnValue execute(final ICommandLine cl)
            throws BadThresholdException {

        List<AlarmMetric> alarmMetrics = alarmService.getAlarmMetrics();

        ThresholdsEvaluatorBuilder thrb = new ThresholdsEvaluatorBuilder();
        configureThresholdEvaluatorBuilder(thrb, cl, alarmMetrics);
        ReturnValueBuilder builder = ReturnValueBuilder.forPlugin(getPluginName(), thrb.create());

        try {
            Collection<Metric> metrics = gatherMetrics(cl, alarmMetrics);

            for (Metric m : metrics) {
                builder.withValue(m);
            }

            return builder.create();
        } catch (MetricGatheringException mge) {
            return ReturnValueBuilder.forPlugin(getPluginName())
                    .withForcedMessage(mge.getMessage())
                    .withStatus(mge.getStatus())
                    .create();
        }

    }

    @Override
    public void configureThresholdEvaluatorBuilder(ThresholdsEvaluatorBuilder thrb, ICommandLine cl, List<AlarmMetric> alarmMetrics) throws BadThresholdException {

        for (AlarmMetric alarmMetric : alarmMetrics) {
            thrb.withLegacyThreshold(alarmMetric.getName(), alarmMetric.getOkRange(), alarmMetric.getWarnRange(), alarmMetric.getCritRange());
        }
    }

    @Override
    public Collection<Metric> gatherMetrics(ICommandLine cl, List<AlarmMetric> alarmMetrics) throws MetricGatheringException {
        List<Metric> res = new ArrayList();

        for (AlarmMetric alarmMetric : alarmMetrics) {
            res.add(new Metric(alarmMetric.getName(), alarmMetric.getMessage(), alarmMetric.getMetric(), alarmMetric.getMin(), alarmMetric.getMax()));
        }
        return res;

    }
}
