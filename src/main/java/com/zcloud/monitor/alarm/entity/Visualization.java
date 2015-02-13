package com.zcloud.monitor.alarm.entity;

import com.google.common.collect.Lists;
import com.zcloud.monitor.alarm.util.CommonUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentBuilderString;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * User: yuyangning
 * Date: 1/23/15
 * Time: 5:47 PM
 */
public class Visualization {

    private String id;
    private String title;
    private String index;
    private Map<String, Object> query;
    private List<AlarmAggs> metrics;
    private AlarmAggs segment;
    private List<Map<String, Object>> filters;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIndex() {
        return CommonUtils.indexString(this.index);
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public Map<String, Object> getQuery() {
        return query;
    }

    public void setQuery(Map<String, Object> query) {
        this.query = query;
    }

    public List<AlarmAggs> getMetrics() {
        return metrics;
    }

    public List<Map<String, Object>> getFilters() {
        return filters;
    }

    public void setFilters(List<Map<String, Object>> filters) {
        this.filters = filters;
    }

    public void setMetrics(List<AlarmAggs> metrics) {
        this.metrics = metrics;
    }

    public AlarmAggs getSegment() {
        return segment;
    }

    public void setSegment(AlarmAggs segment) {
        this.segment = segment;
    }

    public QueryBuilder builderQuery() {

        if (this.query == null && this.query.get("query_string") == null) return QueryBuilders.matchAllQuery();

        Map<String, Object> queryString = (Map<String, Object>) this.query.get("query_string");
        QueryBuilder builder = QueryBuilders.queryString((String) queryString.get("query"));

        return builder;
    }

    public FilterBuilder builderFilter() {

        if (this.segment == null) return FilterBuilders.matchAllFilter();

        BoolFilterBuilder builder = FilterBuilders.boolFilter();
        String field = (String) this.segment.getParams().get("field");

        if ("date_histogram".equals(this.segment.getType())) {
            String interval = (String) this.segment.getParams().get("interval");
            long lte = System.currentTimeMillis();
            long gte = lte;

            if ("second".equals(interval)) {
                gte = lte - 1000;
            } else if ("minute".equals(interval)) {
                gte = lte - 60000;
            } else if ("hour".equals(interval)) {
                gte = lte - 3600000;
            } else if ("day".equals(interval)) {
                gte = lte - 24 * 3600 * 1000;
            } else if ("week".equals(interval)) {
                gte = lte - 7 * 24 * 3600 * 1000;
            }
            //builder.must(FilterBuilders.rangeFilter(field).gte("now-1d").lte("now"));
            builder.must(FilterBuilders.rangeFilter(field).gte(gte).lte(lte));
        } else if ("terms".equals(this.segment.getType())) {
            if (this.getFilters() == null || this.getFilters().size() == 0) {
                builder.must(FilterBuilders.rangeFilter("@timestamp").gte("now-30m").lte("now"));
            } else {
                try {
                    XContentBuilder xbuilder = JsonXContent.contentBuilder();
                    xbuilder.map(this.getFilters().get(0));
                    return FilterBuilders.bytesFilter(xbuilder.bytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return builder;
    }

    public List<AbstractAggregationBuilder> builderAggregations() {

        List<AbstractAggregationBuilder> builders = Lists.newArrayList();
        if (this.metrics != null) {

            if ("date_histogram".equals(this.segment.getType())) {
                for (AlarmAggs metric : metrics) {
                    String field = (String) metric.getParams().get("field");
                    if ("min".equals(metric.getType())) {
                        builders.add(AggregationBuilders.min(metric.getId()).field(field));
                    } else if ("max".equals(metric.getType())) {
                        builders.add(AggregationBuilders.max(metric.getId()).field(field));
                    } else if ("avg".equals(metric.getType())) {
                        builders.add(AggregationBuilders.avg(metric.getId()).field(field));
                    } else if ("sum".equals(metric.getType())) {
                        builders.add(AggregationBuilders.sum(metric.getId()).field(field));
                    } else if ("count".equals(metric.getType())) {
                        if (this.segment != null) {
                            field = (String) this.segment.getParams().get("field");
                            builders.add(AggregationBuilders.count(metric.getId()).field(field));
                        }
                    }
                }
            } else if ("terms".equals(this.segment.getType())) {
                String field = (String) this.segment.getParams().get("field");
                Integer size = (Integer) this.getSegment().getParams().get("size");
                String order = (String) this.getSegment().getParams().get("order");
                builders.add(AggregationBuilders.terms(this.segment.getId())
                        .field(field)
                        .size(size)
                        .order(Terms.Order.count("asc".equals(order))));
            }

        }
        return builders;
    }

}
