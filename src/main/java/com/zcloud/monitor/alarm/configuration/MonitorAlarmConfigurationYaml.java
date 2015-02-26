package com.zcloud.monitor.alarm.configuration;

import com.zcloud.monitor.alarm.util.CommonUtils;
import com.zcloud.monitor.alarm.yaml.SunYaml;

import java.util.Properties;
import java.util.Set;

/**
 * User: yuyangning
 * Date: 1/23/15
 * Time: 12:25 PM
 */
public class MonitorAlarmConfigurationYaml extends MonitorAlarmConfiguration {

    private MonitorAlarmConfiguration monitorAlarmConfiguration;

    public MonitorAlarmConfigurationYaml(String fileName) {
        SunYaml sunYaml = new SunYaml();
        monitorAlarmConfiguration = sunYaml.load(MonitorAlarmConfiguration.class, CommonUtils.getResourceStream(fileName));
    }

    @Override
    public void setIndex(String index) {
        this.monitorAlarmConfiguration.setIndex(index);
    }

    @Override
    public String getVisuTypeName() {
        return this.monitorAlarmConfiguration.getVisuTypeName();
    }

    @Override
    public void setVisuTypeName(String visuTypeName) {
        this.monitorAlarmConfiguration.setVisuTypeName(visuTypeName);
    }

    @Override
    public String getAlarmTypeName() {
        return this.monitorAlarmConfiguration.getAlarmTypeName();
    }

    @Override
    public void setAlarmTypeName(String alarmTypeName) {
        this.monitorAlarmConfiguration.setAlarmTypeName(alarmTypeName);
    }

    @Override
    public String getIndex() {
        return this.monitorAlarmConfiguration.getIndex();
    }

    @Override
    public void setDefServiceName(String defServiceName) {
        this.monitorAlarmConfiguration.setDefServiceName(defServiceName);
    }

    @Override
    public String getDefServiceName() {
        return this.monitorAlarmConfiguration.getDefServiceName();
    }
}
