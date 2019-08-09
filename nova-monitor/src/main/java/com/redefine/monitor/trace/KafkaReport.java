package com.redefine.monitor.trace;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author QIANGLU
 */
public class KafkaReport {

    private static Logger log = LoggerFactory.getLogger(KafkaReport.class);

    private KafkaSend kafkaSend;

    private String topic;

    private Environment environment;

    private int corePoolSize = 3;

    private int queueSize = 500;

    private ExecutorService executorService;


    public void report() {


        KafkaProducer<String, Object> producer = null;
        try {

            producer = kafkaSend.get();
            ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topic, dispose());
            producer.send(producerRecord);
        } catch (Exception e) {
            producer.close(1, TimeUnit.SECONDS);
            log.warn("send tracer message fail ,please check:{}", e.getMessage());

        }

    }

    private String dispose() {
        RedefineSpan span = RedefineSpanContextHolder.getCurrentSpan();

        return JSON.toJSONString(span);
    }


    public static ReportBuilder builder() {

        return new ReportBuilder();
    }

    public static class ReportBuilder {

        private KafkaReport report = new KafkaReport();

        public ReportBuilder properties(KafkaSend kafkaSend) {
            this.report.setKafkaSend(kafkaSend);
            return this;
        }

        public ReportBuilder kafkaSend(KafkaSend kafkaSend) {
            this.report.setKafkaSend(kafkaSend);
            return this;
        }

        public ReportBuilder corePoolSize(int corePoolSize) {
            this.report.setCorePoolSize(corePoolSize);
            return this;
        }

        public ReportBuilder queueSize(int queueSize) {
            this.report.setQueueSize(queueSize);
            return this;
        }

        public ReportBuilder environment(Environment environment) {
            this.report.setEnvironment(environment);
            return this;
        }

        public ReportBuilder topic(String topic) {
            this.report.setTopic(topic);
            return this;
        }

        public ReportBuilder initExecutor() {

            this.report.executorService = new ThreadPoolExecutor(this.report.corePoolSize, this.report.corePoolSize, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(this.report.queueSize), new ThreadPoolExecutor.DiscardOldestPolicy());
            log.info("内置监控推送器创建异步消费线程池,核心线程数为:[{}],最大线程数为:[{}],默认队列为LinkedBlockingQueue,其队列深度为:[{}],拒绝策略为:CallerRunsPolicy", this.report.corePoolSize,
                    this.report.corePoolSize, this.report.queueSize);


            return this;
        }

        public KafkaReport build() {
            return report;
        }

    }

    public KafkaSend getKafkaSend() {
        return kafkaSend;
    }

    public void setKafkaSend(KafkaSend kafkaSend) {
        this.kafkaSend = kafkaSend;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {

        this.executorService = executorService;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
