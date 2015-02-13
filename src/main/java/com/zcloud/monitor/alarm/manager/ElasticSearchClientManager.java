package com.zcloud.monitor.alarm.manager;

import com.zcloud.monitor.alarm.configuration.ElasticSearchConfiguration;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * User: yuyangning
 * Date: 10/31/14
 * Time: 11:41 AM
 */
public class ElasticSearchClientManager {

    private Client client;


    public ElasticSearchClientManager(ElasticSearchConfiguration configuration) {

        ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder().put("cluster.name", configuration.getClusterName());
        //settings.put("path.conf", this.getClass().getResource("/"));
        this.client = new TransportClient(settings.build());
        for (String ipAddress : configuration.getAddress()) {
            String[] ipAndPort = ipAddress.split(":");
            ((TransportClient) this.client).addTransportAddress(new InetSocketTransportAddress(ipAndPort[0], Integer.valueOf(ipAndPort[1])));
        }

    }

    public Client getClient() {
        return this.client;
    }

    public void closeClient() {
        if (this.client != null) {
            this.client.close();
        }
    }
}
