package com.zcloud.monitor.alarm.jnrpe;

import com.zcloud.monitor.alarm.entity.AlarmMetric;
import it.jnrpe.ICommandLine;
import it.jnrpe.plugins.Metric;
import it.jnrpe.plugins.MetricGatheringException;
import it.jnrpe.plugins.PluginBase;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder;

import java.util.Collection;
import java.util.List;

/**
 * User: yuyangning
 * Date: 1/29/15
 * Time: 6:34 PM
 */
public abstract class AlarmPluginBase extends PluginBase {


    public abstract void configureThresholdEvaluatorBuilder(final ThresholdsEvaluatorBuilder thrb,
                                                            final ICommandLine cl,
                                                            final List<AlarmMetric> alarmMetrics) throws BadThresholdException;

    public abstract Collection<Metric> gatherMetrics(final ICommandLine cl,
                                                     final List<AlarmMetric> alarmMetrics) throws MetricGatheringException;

}
