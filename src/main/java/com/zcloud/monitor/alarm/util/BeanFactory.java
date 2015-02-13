package com.zcloud.monitor.alarm.util;

import com.zcloud.monitor.alarm.configuration.ElasticSearchConfiguration;
import com.zcloud.monitor.alarm.configuration.ElasticSearchConfigurationYaml;
import com.zcloud.monitor.alarm.configuration.MonitorAlarmConfiguration;
import com.zcloud.monitor.alarm.configuration.MonitorAlarmConfigurationYaml;
import com.zcloud.monitor.alarm.manager.MonitorAlarmService;

/**
 * User: yuyangning
 * Date: 2/2/15
 * Time: 4:05 PM
 */
public class BeanFactory {

    private final static String CONFIG_NAME = "monitor_alarm.yml";
    private static MonitorAlarmService alarmService;
    private static ElasticSearchConfiguration elasticSearchConfiguration;
    private static MonitorAlarmConfiguration kibanaConfig;

    static {
        elasticSearchConfiguration = new ElasticSearchConfigurationYaml(CONFIG_NAME);
        kibanaConfig = new MonitorAlarmConfigurationYaml(CONFIG_NAME);
    }

    public static MonitorAlarmService getAlarmServiceIstance() {
        if (alarmService == null) {
            synchronized (BeanFactory.class) {
                if (alarmService == null) {
                    alarmService = new MonitorAlarmService(elasticSearchConfiguration, kibanaConfig);
                }
            }
        }
        return alarmService;
    }
}
