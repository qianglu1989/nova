package com.redefine.monitor.trace;

import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author QIANGLU
 */
public class KafkaSend {

    private Map<String, Object> properties;

    private String topic;

    private volatile KafkaProducer<String, Object>  producer;

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getTopic() {
        return topic;
    }

    public void close(long timeout, TimeUnit timeUnit) {
        this.producer.close(timeout,timeUnit);
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public KafkaProducer<String, Object> get() {

        if (this.producer == null) {
            synchronized (this) {
                if (this.producer == null) {
                    this.producer = new KafkaProducer<>(properties);
                }
            }
        }
        return this.producer;
    }

    public static KafkaBuilder builder() {
        return new KafkaBuilder();
    }

    public static class KafkaBuilder {

        private KafkaSend send = new KafkaSend();

        public KafkaBuilder properties(Map<String, Object> properties) {
            this.send.setProperties(properties);
            return this;
        }

        public KafkaBuilder topic(String topic) {
            this.send.setTopic(topic);
            return this;
        }

        public KafkaSend build() {
            return send;
        }

    }
}
