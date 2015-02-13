package com.zcloud.monitor.alarm.entity;

import java.util.Map;

/**
 * User: yuyangning
 * Date: 1/23/15
 * Time: 6:30 PM
 */
public class AlarmAggs {
    private String id;
    private String type;
    private Map<String, Object> params;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
