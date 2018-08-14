package com.sxd.app_stm_001.app.entity;

import java.io.Serializable;
import java.util.List;

public class Source implements Serializable {

    private static final long serialVersionUID = -8230460698597538787L;

    private String dsId;

    private String kafkaHosts;

    private String zokkeeperHost;

    private String topic;

    private String groups;

    private Short status;

    private List<SourceField> fields;

    public String getDsId() {
        return dsId;
    }

    public void setDsId(String dsId) {
        this.dsId = dsId;
    }

    public String getKafkaHosts() {
        return kafkaHosts;
    }

    public void setKafkaHosts(String kafkaHosts) {
        this.kafkaHosts = kafkaHosts;
    }

    public String getZokkeeperHost() {
        return zokkeeperHost;
    }

    public void setZokkeeperHost(String zokkeeperHost) {
        this.zokkeeperHost = zokkeeperHost;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public List<SourceField> getFields() {
        return fields;
    }

    public void setFields(List<SourceField> fields) {
        this.fields = fields;
    }
}