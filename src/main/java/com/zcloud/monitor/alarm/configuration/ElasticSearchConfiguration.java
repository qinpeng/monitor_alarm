package com.zcloud.monitor.alarm.configuration;

import java.util.List;

/**
 * User: yuyangning
 * Date: 10/30/14
 * Time: 6:15 PM
 */
public class ElasticSearchConfiguration {

  private String clusterName;
  private List<String> address;

  private List<String> serverAddress;
  private int timeout = 3000;

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public List<String> getAddress() {
    return address;
  }

  public void setAddress(List<String> address) {
    this.address = address;
  }

  public List<String> getServerAddress() {
    return serverAddress;
  }

  public void setServerAddress(List<String> serverAddress) {
    this.serverAddress = serverAddress;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }
}
