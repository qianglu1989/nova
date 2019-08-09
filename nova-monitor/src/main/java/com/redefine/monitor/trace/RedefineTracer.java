package com.redefine.monitor.trace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Map;
/**
 * @author QIANGLU
 */
public class RedefineTracer implements NoveTracer {
    private static Logger log = LoggerFactory.getLogger(RedefineTracer.class);

    private KafkaReport kafkaReport;

    private String name;

    public RedefineTracer(KafkaReport kafkaReport, String name) {
        this.kafkaReport = kafkaReport;
        this.name = name;
    }

    @Override
    public RedefineSpan createSpan() {
        return createSpan(System.currentTimeMillis());
    }

    public RedefineSpan createSpan(long time) {
        RedefineSpan span = RedefineSpan.builder().name(this.name).time(time).build();
        RedefineSpanContextHolder.setCurrentSpan(span);
        return span;
    }

    @Override
    public RedefineSpan close() {

        RedefineSpan cur = RedefineSpanContextHolder.getCurrentSpan();

        try {
            this.kafkaReport.report();
        } catch (Exception e) {
            log.warn("kafkaReport send message error:{}", e.getMessage());
        }
        RedefineSpanContextHolder.close();
        return cur;
    }

    @Override
    public RedefineSpan getCurrentSpan() {
        return RedefineSpanContextHolder.getCurrentSpan();
    }

    @Override
    public void report(String key, Object val) {

        report(key, val, null);
    }
    @Override
    public void report(String key, Object val, Map<String, String[]> reqParams) {

        boolean isTraceing = RedefineSpanContextHolder.isTracing();
        if (!isTraceing) {
            createSpan();
        }
        if (reqParams != null) {
            RedefineSpanContextHolder.addReqParams(reqParams);
        }

        RedefineSpanContextHolder.addTag(key, val);
        if (!isTraceing) {
            close();
        }
    }

    public void addTag(String key, String value) {
        RedefineSpan s = getCurrentSpan();
        if (!StringUtils.isEmpty(key)) {
            s.tag(key, value);
        }
    }
}
