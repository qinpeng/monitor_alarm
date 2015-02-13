package com.zcloud.monitor.alarm.entity;

import java.math.BigDecimal;

/**
 * User: yuyangning
 * Date: 1/30/15
 * Time: 10:46 AM
 */
public class AlarmMetric {

    private String name;
    private String message;
    private BigDecimal metric;
    private BigDecimal min;
    private BigDecimal max;

    private String okRange;
    private String warnRange;
    private String critRange;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getMetric() {
        return metric;
    }

    public void setMetric(BigDecimal metric) {
        this.metric = metric;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public String getOkRange() {
        return okRange;
    }

    public void setOkRange(String okRange) {
        this.okRange = okRange;
    }

    public String getWarnRange() {
        return warnRange;
    }

    public void setWarnRange(String warnRange) {
        this.warnRange = warnRange;
    }

    public String getCritRange() {
        return critRange;
    }

    public void setCritRange(String critRange) {
        this.critRange = critRange;
    }
}
