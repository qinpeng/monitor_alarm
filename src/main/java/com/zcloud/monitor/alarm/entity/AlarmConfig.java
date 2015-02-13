package com.zcloud.monitor.alarm.entity;

import java.util.Map;

/**
 * User: yuyangning
 * Date: 1/30/15
 * Time: 11:36 AM
 */
public class AlarmConfig {

    private String vid;
    private Map<String, Object> thresholds;
    private boolean enable;

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public Map<String, Object> getThresholds() {
        return thresholds;
    }

    public void setThresholds(Map<String, Object> thresholds) {
        this.thresholds = thresholds;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
