package com.nove.version;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
/**
 * @author QIANGLU
 */
public class RedefineVersionRequestContext {

    private static final Logger log = LoggerFactory.getLogger(RedefineVersionRequestContext.class);

    private static final HystrixRequestVariableDefault<RedefineVersionRequestContext> CURRENT_CONTEXT = new HystrixRequestVariableDefault<RedefineVersionRequestContext>();


    private final String apiVersion;
    private final RedefineVersionRequest versionRequest;
    private Map<String, Object> params;


    private RedefineVersionRequestContext(RedefineVersionRequest versionRequest, String apiVersion) {
        params = new HashMap<>();
        this.apiVersion = apiVersion;
        this.versionRequest = versionRequest;
    }


    public static RedefineVersionRequestContext currentRequestCentxt() {
        return CURRENT_CONTEXT.get();
    }

    public static void initRequestContext(RedefineVersionRequest versionRequest, String apiVersion) {
        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.initializeContext();
        }
        CURRENT_CONTEXT.set(new RedefineVersionRequestContext(versionRequest, apiVersion));
    }

    public static void shutdownRequestContext() {
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.getContextForCurrentThread().shutdown();
        }
    }


    public String getApiVersion() {
        return apiVersion;
    }

    public String getServiceId() {
        return versionRequest.getServiceId();
    }


    public void addParameter(String key, Object value){
        params.put(key, value);
    }

    public Object getParameter(String key){
        return params.get(key);
    }


    public String getStrParameter(String key){
        return (String) params.get(key);
    }

    public Integer getIntegerParameter(String key){
        return (Integer) params.get(key);
    }


    public Long getLongParameter(String key){
        return (Long) params.get(key);
    }

    public Boolean getBooleanParameter(String key){
        return (Boolean) params.get(key);
    }

    public RedefineVersionRequest getBambooRequest() {
        return versionRequest;
    }
}
