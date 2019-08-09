package com.redefine.monitor.trace;

import org.springframework.core.NamedThreadLocal;

import java.util.Map;
/**
 * @author QIANGLU
 */
public class RedefineSpanContextHolder {

    private static final ThreadLocal<RedefineSpanContextHolder.RedefineSpanContext> CURRENT_SPAN = new NamedThreadLocal<>(
            "RedefineTrace Context");


    public static RedefineSpan getCurrentSpan() {
        return isTracing() ? CURRENT_SPAN.get().RedefineSpan : null;
    }

    static void setCurrentSpan(RedefineSpan redefineSpan) {

        push(redefineSpan);
    }


    static void removeCurrentSpan() {
        CURRENT_SPAN.remove();
    }


    static boolean isTracing() {
        return CURRENT_SPAN.get() != null;
    }

    public static void addTag(String key, Object val) {
        if (isTracing()) {
            CURRENT_SPAN.get().RedefineSpan.getTags().put(key, val);
        }
    }


    public static void addReqParams(Map<String, String[]> reqParams) {
        if (isTracing()) {
            CURRENT_SPAN.get().RedefineSpan.getRequestParams().putAll(reqParams);
        }
    }

    static void close() {
        CURRENT_SPAN.remove();
    }

    static void push(RedefineSpan redefineSpan) {
        if (isCurrent(redefineSpan)) {
            return;
        }
        CURRENT_SPAN.set(new RedefineSpanContextHolder.RedefineSpanContext(redefineSpan));
    }

    private static boolean isCurrent(RedefineSpan redefineSpan) {
        if (redefineSpan == null || CURRENT_SPAN.get() == null) {
            return false;
        }
        return redefineSpan.equals(CURRENT_SPAN.get().RedefineSpan);
    }

    private static class RedefineSpanContext {
        final RedefineSpan RedefineSpan;

        public RedefineSpanContext(RedefineSpan redefineSpan) {
            this.RedefineSpan = redefineSpan;
        }
    }


}
