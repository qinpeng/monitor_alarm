package com.zcloud.monitor.alarm.manager;

import com.google.common.collect.Lists;
import com.zcloud.monitor.alarm.entity.AlarmAggs;
import com.zcloud.monitor.alarm.entity.Visualization;
import com.zcloud.monitor.alarm.util.Jsons;

import java.util.List;
import java.util.Map;

/**
 * User: yuyangning
 * Date: 1/26/15
 * Time: 11:27 AM
 */
public class VisualizationParser {

    public static Visualization parse(Map<String, Object> kibanaVisual) {

        if (kibanaVisual == null) return null;
        Visualization visualization = new Visualization();

        String _id = (String) kibanaVisual.get("_id");
        visualization.setId(_id);
        String title = (String) kibanaVisual.get("title");
        visualization.setTitle(title);

        Map<String, Object> kibanaSavedObjectMeta = (Map<String, Object>) kibanaVisual.get("kibanaSavedObjectMeta");
        if (kibanaSavedObjectMeta != null) {
            String searchSource = (String) kibanaSavedObjectMeta.get("searchSourceJSON");
            if (searchSource != null) {

                Map<String, Object> searchSoureJson = Jsons.objectFromJSONStr(searchSource, Map.class);

                String index = (String) searchSoureJson.get("index");
                visualization.setIndex(index);

                Map<String, Object> query = (Map<String, Object>) searchSoureJson.get("query");
                visualization.setQuery(query);

                List<Map<String, Object>> filters = (List<Map<String, Object>>) searchSoureJson.get("filter");
                visualization.setFilters(filters);
            }
        }

        String visStateString = (String) kibanaVisual.get("visState");
        if (visStateString != null) {
            Map<String, Object> visState = Jsons.objectFromJSONStr(visStateString, Map.class);

            List<Map<String, Object>> aggs = (List<Map<String, Object>>) visState.get("aggs");
            if (aggs != null) {
                List<AlarmAggs> metrics = Lists.newArrayList();

                for (Map<String, Object> agg : aggs) {
                    AlarmAggs aggregation = new AlarmAggs();

                    String id = "";
                    if (agg.get("id") != null) {
                        id = agg.get("id").toString();
                    }
                    String type = (String) agg.get("type");
                    String schema = (String) agg.get("schema");
                    Map<String, Object> params = (Map<String, Object>) agg.get("params");

                    aggregation.setId(id);
                    aggregation.setType(type);
                    aggregation.setParams(params);

                    if ("metric".equals(schema)) {
                        metrics.add(aggregation);
                    } else if ("segment".equals(schema)) {
                        visualization.setSegment(aggregation);
                    }
                }

                visualization.setMetrics(metrics);
            }
        }

        return visualization;
    }
}
