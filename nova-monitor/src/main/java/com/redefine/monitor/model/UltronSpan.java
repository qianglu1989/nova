package com.redefine.monitor.model;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
/**
 * @author QIANGLU
 */
public class UltronSpan implements Serializable {

    private String id;

    private String traceId;

    private String parentId;

    private String spanId;

    private String kind;

    private String name;

    private String timestamp;

    private String duration;

    private long date;

    private Map<String, String> localEndpoint;

    private Map<String, String> tags;

    private String status;

    private String formatDate;

    public String getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(String formatDate) {
        this.formatDate = formatDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {

        //因为接到的spanId 的key 为id
        this.id = UUID.randomUUID().toString();
        this.spanId = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;

        long date = new Long(timestamp.substring(0, 13));
        this.date = date;
    }

    public String getDuration() {

        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Map<String, String> getLocalEndpoint() {
        return localEndpoint;
    }

    public void setLocalEndpoint(Map<String, String> localEndpoint) {
        this.localEndpoint = localEndpoint;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}
