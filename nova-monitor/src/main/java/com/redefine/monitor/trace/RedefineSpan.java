package com.redefine.monitor.trace;

import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/**
 * @author QIANGLU
 */
public class RedefineSpan implements Serializable {

    private String name;

    private long time;

    private long duration;

    private int concurrent;

    private String uri;

    private String instanceId;

    private Map<String, Object> tags = new HashMap<>();

    private Map<String, String[]> requestParams = new HashMap<>();

    public void tag(String key, String value) {
        if (StringUtils.hasText(value)) {
            this.tags.put(key, value);
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getTags() {
        return tags;
    }

    public void setTags(Map<String, Object> tags) {
        this.tags = tags;
    }

    public static RedefineBuilder builder() {
        return new RedefineBuilder();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Map<String, String[]> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Map<String, String[]> requestParams) {
        this.requestParams = requestParams;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(int concurrent) {
        this.concurrent = concurrent;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        return builder.append("name : ").append(name).append(" tags size: ").append(tags.size()).toString();
    }

    public static class RedefineBuilder {

        private RedefineSpan span = new RedefineSpan();

        public RedefineBuilder name(String name) {
            this.span.setName(name);
            return this;
        }

        public RedefineBuilder tags(String key, String val) {
            this.span.getTags().put(key, val);
            return this;
        }

        public RedefineBuilder reqParams(Map<String, String[]> params) {
            this.span.getRequestParams().putAll(params);
            return this;
        }

        public RedefineBuilder time(long time) {
            this.span.setTime(time);
            return this;
        }

        public RedefineBuilder duration(long duration) {
            this.span.setDuration(duration);
            return this;
        }

        public RedefineBuilder concurrent(int concurrent) {
            this.span.setConcurrent(concurrent);
            return this;
        }

        public RedefineBuilder uri(String uri) {
            this.span.setUri(uri);
            return this;
        }

        public RedefineBuilder instanceId(String instanceId) {
            this.span.setInstanceId(instanceId);
            return this;
        }

        public RedefineSpan build() {
            return this.span;
        }
    }


}
