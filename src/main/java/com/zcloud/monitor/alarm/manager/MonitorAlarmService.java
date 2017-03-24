package com.zcloud.monitor.alarm.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zcloud.monitor.alarm.configuration.ElasticSearchConfiguration;
import com.zcloud.monitor.alarm.configuration.MonitorAlarmConfiguration;
import com.zcloud.monitor.alarm.entity.AlarmAggs;
import com.zcloud.monitor.alarm.entity.AlarmMetric;
import com.zcloud.monitor.alarm.entity.Visualization;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MatchAllFilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: yuyangning
 * Date: 1/23/15
 * Time: 2:42 PM
 */
public class MonitorAlarmService {

    private static Logger logger = LoggerFactory.getLogger(MonitorAlarmService.class);
    private final Client client;
    private final MonitorAlarmConfiguration alarmConfiguration;
    private final static int LIMIT_SIZE = 500;
    private ElasticSearchConfiguration esConfig;

    public MonitorAlarmService(ElasticSearchConfiguration esearchConfig,
                               MonitorAlarmConfiguration alarmConfiguration) {

        this.alarmConfiguration = alarmConfiguration;
        this.client = new ElasticSearchClientManager(esearchConfig).getClient();
        this.esConfig = esearchConfig;
    }


    public List<Map<String, Object>> getVisualization() {

        List<Map<String, Object>> hits = Lists.newArrayList();
        try {
            SearchResponse response = client.prepareSearch(alarmConfiguration.getIndex())
                    .setTypes(alarmConfiguration.getVisuTypeName())
                    .setFrom(0)
                    .setSize(LIMIT_SIZE)
                    .setQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
                            FilterBuilders.matchAllFilter()))
                    .execute().actionGet();

            for (SearchHit hit : response.getHits().getHits()) {
                Map<String, Object> data = hit.getSource();
                data.put("_id", hit.getId());
                hits.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Get kibana visualization error.", e);
        }

        return hits;
    }

    public List<Map<String, Object>> getAlarmConfiguration(String service) {
        List<Map<String, Object>> hits = Lists.newArrayList();

        FilterBuilder filter = null;
        try {


            if (StringUtils.isBlank(service)) {
                filter = FilterBuilders.boolFilter()
                        .should(FilterBuilders.notFilter(FilterBuilders.existsFilter("service")))
                        .should(FilterBuilders.termFilter("service", alarmConfiguration.getDefServiceName()));
            } else {
                filter = FilterBuilders.termsFilter("service", service);
            }

            SearchRequestBuilder request = client.prepareSearch(alarmConfiguration.getIndex())
                    .setTypes(alarmConfiguration.getAlarmTypeName())
                    .setFrom(0)
                    .setSize(LIMIT_SIZE)
                    .setQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), filter));

            //System.out.println(request.internalBuilder().toString());
            SearchResponse response = request.execute().actionGet();
            if (response.getHits() != null && response.getHits().getHits() != null) {
                for (SearchHit hit : response.getHits().getHits()) {
                    hits.add(hit.getSource());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Get alarm config error. Service is " + service + ". Filter is " + filter != null ? filter.toString() : null + ". ES address is " + esConfig.getServerAddress(),e);
        }

        return hits;
    }

    public List<Map<String, Object>> getVisualization(Collection<String> ids) {
        List<Map<String, Object>> hits = Lists.newArrayList();
        try {
            if (ids == null || ids.size() == 0) return hits;
            MultiGetResponse response = client.prepareMultiGet()
                    .add(alarmConfiguration.getIndex(), alarmConfiguration.getVisuTypeName(), ids)
                    .execute().actionGet();

            for (MultiGetItemResponse itemResponse : response.getResponses()) {
                Map<String, Object> data = itemResponse.getResponse().getSource();
                data.put("_id", itemResponse.getId());
                hits.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Get Visualization error.", e);
        }

        return hits;

    }

    public Aggregations getBuketAggregations(Visualization visualization) {
        if (visualization == null) return null;

        try {
            SearchRequestBuilder request = client.prepareSearch(visualization.getIndex())
                    .setFrom(0)
                    .setSize(0)
                    .setQuery(QueryBuilders.filteredQuery(visualization.builderQuery(),
                            visualization.builderFilter()));
            for (AbstractAggregationBuilder aggregationBuilder : visualization.builderAggregations()) {
                request.addAggregation(aggregationBuilder);
            }

            //System.out.println(request.internalBuilder().toString());
            SearchResponse response = request.execute().actionGet();
            return response.getAggregations();
        } catch (ElasticsearchException e) {
            e.printStackTrace();
            logger.error("Get vid[{}] aggregations error.", visualization.getId(), e);
        }

        return null;
    }

    public List<AlarmMetric> getAlarmMetrics(String service) {

        try {
            List<Map<String, Object>> alarmConfigs = getAlarmConfiguration(service);
            Map<String, Map<String, Object>> alarmConfigMaps = Maps.newHashMap();

            for (Map<String, Object> alarmConfig : alarmConfigs) {
                String id = (String) alarmConfig.get("vid");
                Object flag = alarmConfig.get("enable");

                if (StringUtils.isNotBlank(id)
                        && flag != null && Boolean.valueOf(flag.toString()) == true) {
                    alarmConfigMaps.put(id, alarmConfig);
                }
            }
            alarmConfigs = null;

            List<AlarmMetric> alarmMetrics = Lists.newArrayList();
            List<Map<String, Object>> kibanaVisuals = getVisualization(alarmConfigMaps.keySet());
            for (Map<String, Object> kibanaVisual : kibanaVisuals) {

                Visualization visualization = VisualizationParser.parse(kibanaVisual);
                if (visualization == null) continue;

                if ("terms".equals(visualization.getSegment().getType())) {

                    AlarmMetric alarmMetric = getMetricTermBuket(alarmConfigMaps, visualization);
                    if (alarmMetric != null) alarmMetrics.add(alarmMetric);

                } else if ("date_histogram".equals(visualization.getSegment().getType())) {

                    List<AlarmAggs> metrics = visualization.getMetrics();
                    Aggregations aggregations = getBuketAggregations(visualization);

                    for (AlarmAggs aggs : metrics) {

                        AlarmMetric metric = getMetirc(alarmConfigMaps, visualization, aggs, aggregations);
                        if (metric != null) {
                            alarmMetrics.add(metric);
                        }
                    }
                }
            }

            return alarmMetrics;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Get metric error.", e);
        }
        return new ArrayList<AlarmMetric>();
    }

    private AlarmMetric getMetirc(Map<String, Map<String, Object>> alarmConfigMaps,
                                  Visualization visualization,
                                  AlarmAggs aggs,
                                  Aggregations aggregations) {

        Map<String, Object> config = alarmConfigMaps.get(visualization.getId());
        if (config == null) return null;

        Map<String, Object> thresholds = (Map<String, Object>) config.get("thresholds");
        if (thresholds == null) return null;

        String warnRange = (String) thresholds.get(aggs.getId());
        if (StringUtils.isEmpty(warnRange)) return null;

        AlarmMetric metric = new AlarmMetric();
        metric.setWarnRange(warnRange);
        double value = 0;
        StringBuilder name = new StringBuilder(visualization.getId());

        if (aggregations != null && aggregations.get(aggs.getId()) != null) {

            if ("min".equals(aggs.getType())) {

                Min min = aggregations.get(aggs.getId());
                value = min.getValue();
                if (Double.isNaN(value) || Double.isInfinite(value)) value = 0;
                metric.setMetric(new BigDecimal(value));

            } else if ("max".equals(aggs.getType())) {

                Max max = aggregations.get(aggs.getId());
                value = max.getValue();
                if (Double.isNaN(value) || Double.isInfinite(value)) value = 0;
                metric.setMetric(new BigDecimal(value));

            } else if ("avg".equals(aggs.getType())) {

                Avg avg = aggregations.get(aggs.getId());
                value = avg.getValue();
                if (Double.isNaN(value) || Double.isInfinite(value)) value = 0;
                metric.setMetric(new BigDecimal(value));

            } else if ("sum".equals(aggs.getType())) {

                Sum sum = aggregations.get(aggs.getId());
                value = sum.getValue();
                if (Double.isNaN(value) || Double.isInfinite(value)) value = 0;
                metric.setMetric(new BigDecimal(value));

            } else if ("count".equals(aggs.getType())) {

                ValueCount count = aggregations.get(aggs.getId());
                metric.setMetric(new BigDecimal(count.getValue()));
            }
        } else {
            metric.setMetric(new BigDecimal(value));
        }

        //metric name
        name.append(":").append(warnRange);
        if (aggs.getParams() != null && aggs.getParams().containsKey("field")) {
            name.append("_").append(aggs.getParams().get("field"));
        }
        metric.setName(name.toString());
        metricMessage(metric, false);

        return metric;

    }

    private AlarmMetric getMetricTermBuket(Map<String, Map<String, Object>> alarmConfigMaps, Visualization visualization) {

        if (!"terms".equals(visualization.getSegment().getType())) return null;
        Aggregations aggregations = getBuketAggregations(visualization);

        Map<String, Object> config = alarmConfigMaps.get(visualization.getId());
        if (config == null) return null;

        Map<String, Object> thresholds = (Map<String, Object>) config.get("thresholds");
        if (thresholds == null) return null;

        String warnRange = (String) thresholds.get(visualization.getSegment().getId());
        if (StringUtils.isEmpty(warnRange)) return null;
        String key = (String) config.get("key");
        if (StringUtils.isEmpty(key)) key = "";

        Terms terms = aggregations.get(visualization.getSegment().getId());
        long count = 0;
        long sum = terms.getSumOfOtherDocCounts();

        List<Terms.Bucket> buckets = terms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            sum = sum + bucket.getDocCount();
            if (bucket.getKey().equals(key)) {
                count = bucket.getDocCount();
            }
        }

        if (sum != 0) {
            AlarmMetric metric = new AlarmMetric();
            metric.setWarnRange(warnRange);
            metric.setMetric(new BigDecimal(count * 100 / sum));
            metric.setName(visualization.getTitle());
            metricMessage(metric, true);

            return metric;
        }
        return null;
    }

    private void metricMessage(AlarmMetric metric, boolean isPercent) {
        if (metric == null) return;

        StringBuilder message = new StringBuilder();
        message.append(metric.getName());
        message.append(":");
        message.append(message(metric, isPercent));
        message.append(", current is ");
        message.append(String.valueOf(metric.getMetric().doubleValue()));
        if (isPercent) message.append("%");
        metric.setMessage(message.toString());
    }

    private static String message(AlarmMetric metric, boolean isPercent) {
        StringBuilder msg = new StringBuilder();
        if (metric.getWarnRange() != null) {
            String[] datas = metric.getWarnRange().trim().split(":");
            if (datas.length == 2) {
                for (int i = 0; i < datas.length; i++) {
                    if (isPercent && StringUtils.isNotBlank(datas[i])) {
                        datas[i] = datas[i] + "%";
                    }
                }

                msg.append(datas[0]);
                if (StringUtils.isBlank(datas[0])) {
                    msg.append("<");
                } else if (StringUtils.isBlank(datas[1])) {
                    msg.append(">");
                } else {
                    msg.append("-");
                }

                msg.append(datas[1]);
            }
        }
        return msg.toString();
    }

}
