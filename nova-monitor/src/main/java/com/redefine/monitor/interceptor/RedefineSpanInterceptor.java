package com.redefine.monitor.interceptor;

import com.redefine.monitor.trace.NoveTracer;
import com.redefine.monitor.trace.RedefineSpanContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * @author QIANGLU
 */
public class RedefineSpanInterceptor extends HandlerInterceptorAdapter {


    private NoveTracer noveTracer;
    private ThreadLocal<Long> stime = new ThreadLocal<>();
    private final ConcurrentMap<String, AtomicInteger> concurrents = new ConcurrentHashMap<>();

    public RedefineSpanInterceptor() {
    }

    public RedefineSpanInterceptor(NoveTracer noveTracer) {
        this.noveTracer = noveTracer;
    }

    /**
     * This implementation always returns {@code true}.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        try {
            this.noveTracer.createSpan();
            addRequestTags(request);

            Long startTime = System.currentTimeMillis();
            stime.set(startTime);
            // 并发计数
            getConcurrent(request).incrementAndGet();

        } catch (Exception e) {
        }
        return true;
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

        try {
            Long endTime = System.currentTimeMillis();
            Long startTime = stime.get();
            RedefineSpanContextHolder.getCurrentSpan().setDuration(endTime - startTime);
            RedefineSpanContextHolder.getCurrentSpan().setConcurrent(getConcurrent(request).get());
            if (ex != null) {
                RedefineSpanContextHolder.addTag("error", ex.getMessage());
            }
            this.noveTracer.close();
            getConcurrent(request).decrementAndGet();

        } catch (Exception e) {
        }finally {
            stime.remove();
        }
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void afterConcurrentHandlingStarted(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
    }

    // 获取并发计数器

    private AtomicInteger getConcurrent(HttpServletRequest request) {

        String key = request.getRequestURI();
        AtomicInteger concurrent = concurrents.get(key);
        if (concurrent == null) {
            concurrents.putIfAbsent(key, new AtomicInteger());
            concurrent = concurrents.get(key);
        }
        return concurrent;
    }

    private String getFullUrl(HttpServletRequest request) {
        StringBuffer requestURI = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString == null) {
            return requestURI.toString();
        } else {
            return requestURI.append('?').append(queryString).toString();
        }
    }

    private void addRequestTags(HttpServletRequest request){
        RedefineSpanContextHolder.getCurrentSpan().setUri(request.getRequestURI());
        RedefineSpanContextHolder.addReqParams(request.getParameterMap());
        RedefineSpanContextHolder.addTag("http_url",getFullUrl(request));
    }
}
