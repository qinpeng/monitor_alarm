package com.zcloud.monitor.alarm;

import com.zcloud.monitor.alarm.manager.MonitorAlarmService;
import com.zcloud.monitor.alarm.util.BeanFactory;
import com.zcloud.monitor.alarm.util.Jsons;

/**
 * User: yuyangning
 * Date: 1/23/15
 * Time: 12:20 PM
 */
public class MonitorAlarmBootstrap {
    public static void main(String[] args) {

        MonitorAlarmService service = BeanFactory.getAlarmServiceIstance();

//        for (Map<String, Object> kibanaVisual : service.getVisualization()) {
//            Visualization visualization = VisualizationParser.parse(kibanaVisual);
//            if (visualization == null) continue;
//            //if (!visualization.getSegment().getType().equals("date_histogram")) continue;
//            //if (!visualization.getSegment().getType().equals("terms")) continue;
//            System.out.println(Jsons.objectToPrettyJSONStr(visualization));
//
//            service.getBuketAggregations(visualization);
//            System.out.println("---------------");
//        }

        System.out.println(Jsons.objectToPrettyJSONStr(service.getAlarmMetrics("")));

    }
}
