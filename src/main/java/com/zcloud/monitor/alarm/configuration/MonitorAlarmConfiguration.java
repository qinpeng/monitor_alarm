package com.zcloud.monitor.alarm.configuration;

/**
 * User: yuyangning
 * Date: 1/23/15
 * Time: 12:25 PM
 */
public class MonitorAlarmConfiguration {

    private String index;
    private String visuTypeName;
    private String alarmTypeName;
    private String defServiceName;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getVisuTypeName() {
        return visuTypeName;
    }

    public void setVisuTypeName(String visuTypeName) {
        this.visuTypeName = visuTypeName;
    }

    public String getAlarmTypeName() {
        return alarmTypeName;
    }

    public void setAlarmTypeName(String alarmTypeName) {
        this.alarmTypeName = alarmTypeName;
    }

    public String getDefServiceName() {
        return defServiceName;
    }

    public void setDefServiceName(String defServiceName) {
        this.defServiceName = defServiceName;
    }
}
