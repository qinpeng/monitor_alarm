package com.zcloud.monitor.alarm.configuration;


import com.zcloud.monitor.alarm.util.CommonUtils;
import com.zcloud.monitor.alarm.yaml.SunYaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * User: yuyangning
 * Date: 10/30/14
 * Time: 6:16 PM
 */
public class ElasticSearchConfigurationYaml extends ElasticSearchConfiguration {

    private ElasticSearchConfiguration searchConfiguration;

    public ElasticSearchConfigurationYaml(String fileName) {
        SunYaml sunYaml = new SunYaml();
        //searchConfiguration = sunYaml.load(ElasticSearchConfiguration.class, this.getClass().getClassLoader().getResourceAsStream(fileName));
        searchConfiguration = sunYaml.load(ElasticSearchConfiguration.class, CommonUtils.getResourceStream(fileName));
    }

    @Override
    public void setTimeout(int timeout) {
        searchConfiguration.setTimeout(timeout);
    }

    @Override
    public int getTimeout() {
        return searchConfiguration.getTimeout();
    }

    @Override
    public void setServerAddress(List<String> serverAddress) {
        searchConfiguration.setServerAddress(serverAddress);
    }

    @Override
    public List<String> getServerAddress() {
        return searchConfiguration.getServerAddress();
    }

    @Override
    public void setAddress(List<String> address) {
        super.setAddress(address);
    }

    @Override
    public List<String> getAddress() {
        return searchConfiguration.getAddress();
    }

    @Override
    public void setClusterName(String clusterName) {
        searchConfiguration.setClusterName(clusterName);
    }

    @Override
    public String getClusterName() {
        return searchConfiguration.getClusterName();
    }
}
