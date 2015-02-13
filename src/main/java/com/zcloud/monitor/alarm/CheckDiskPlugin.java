package com.zcloud.monitor.alarm;

import it.jnrpe.ICommandLine;
import it.jnrpe.plugins.Metric;
import it.jnrpe.plugins.MetricGatheringException;
import it.jnrpe.plugins.PluginBase;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CheckDiskPlugin extends PluginBase {
    @Override
    protected String getPluginName() {
        return "CHECK_DISK";
    }

    @Override
    public final Collection gatherMetrics(final ICommandLine cl)
            throws MetricGatheringException {
        String sPath = cl.getOptionValue("path");    // (1)

        BigDecimal oneMB = new BigDecimal(1024 * 1024);

        File f = new File(sPath);

        BigDecimal freeSpace = new BigDecimal(f.getFreeSpace()).divide(oneMB);    // (2)
        BigDecimal totalSpace = new BigDecimal(f.getTotalSpace()).divide(oneMB);  // (3)

        List res = new ArrayList();

        String msg = String.format("Free space : %d MB", freeSpace.longValue());

        res.add(new Metric("freespace", msg, freeSpace, new BigDecimal(0), totalSpace));  // (4)

        return res;
    }

    @Override
    public void configureThresholdEvaluatorBuilder(ThresholdsEvaluatorBuilder thrb, ICommandLine cl)
            throws BadThresholdException {

        thrb.withLegacyThreshold("freespace", null, cl.getOptionValue("warning"), cl.getOptionValue("critical"));
    }
}