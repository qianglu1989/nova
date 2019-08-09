package com.redefine.nove;

import com.alibaba.fastjson.JSONObject;
import com.redefine.nove.abtest.RandomBucketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luqiang on 17/01/2019.
 */
public class NoveTestContextHolder {

    private static Logger log = LoggerFactory.getLogger(NoveTestContextHolder.class);

    private final static ThreadLocal<Map<String, NoveTestHandler>> HANDLER = new ThreadLocal<>();

    /**
     * 用于存储基础数据
     */
    private final static ThreadLocal<Map<String, String>> COMMON_PARAM = new ThreadLocal<>();
    /**
     * 用于存储当前分桶策略数据
     */
    private final static ThreadLocal<Map<String, Map<String, String>>> FUNC_PARAM = new ThreadLocal<>();


    public static void setCommonParam(Map<String, String> commonParam) {
        if (commonParam != null && commonParam.size() != 0) {
            COMMON_PARAM.set(commonParam);
        }
    }

    public static Map<String, String> getCommonParam() {
        return COMMON_PARAM.get();
    }

    public static void setFuncParam(Map<String, Map<String, String>> funcParam) {
        if (funcParam != null && funcParam.size() != 0) {
            FUNC_PARAM.set(funcParam);
        }
    }

    public static Map<String, Map<String, String>> getFuncParam() {
        return FUNC_PARAM.get();
    }

    public static Map<String, String> getParamDetail(String key) {
        return FUNC_PARAM.get().get(key);
    }


    public static NoveTestHandler getHandler(String type) {
        Map<String, NoveTestHandler> handlerMap = HANDLER.get();
        if (handlerMap != null && handlerMap.size() != 0) {
            return handlerMap.get(type);
        }
        return null;
    }

    public static Object invoke(Object param) {
        Map<String, NoveTestHandler> handlerMap = HANDLER.get();
        if (handlerMap != null && handlerMap.size() != 0) {
            NoveTestHandler noveTestHandler = handlerMap.get(0);
            return noveTestHandler.invoke(param);
        }
        return null;
    }

    public static void remove() {
        COMMON_PARAM.remove();
        FUNC_PARAM.remove();
        HANDLER.remove();
    }

    public static void checkReqParams(HttpServletRequest request) {
        COMMON_PARAM.set(RandomBucketUtils.setParam(request));
    }

    /**
     * 初始化数据
     *
     * @param funcs           自定义的功能名称
     * @param noveTestContext
     */
    public static void initBucketData(String[] funcs, NoveTestContext noveTestContext) {
        //init bucket data
        Map<String, String> commonParams = COMMON_PARAM.get();
        String bucketId = commonParams.get("bucketId");
        Map<String, NoveTestHandler> handlerMap = new HashMap<>(16);
        Map<String, Map<String, String>> funcMap = new HashMap<>(16);
        for (String func : funcs) {

            try {


                String testName = RandomBucketUtils.matchFunc(func, commonParams);
                Map<String, Object> funcParams = RandomBucketUtils.initDatas(func, testName, bucketId);


                //get bucket
                String bucket = RandomBucketUtils.hashBucket(func, funcParams);

                JSONObject jsonObject = (JSONObject) funcParams.get("data." + bucket);
                funcMap.putIfAbsent(func, JSONObject.toJavaObject(jsonObject, Map.class));
                //init handler
                NoveTestHandler noveTestHandler = noveTestContext.getNoveTestHandler(func + "_" + testName + "_" + bucket);
                if (noveTestHandler == null) {
                    noveTestHandler = noveTestContext.getNoveTestHandler(func + "_" + testName + "_default");
                    log.info("Nove ABTEST  func:{},testName:{},bucket:{} NoveTestHandler is null,set default handler:{}", func, testName, bucket, noveTestContext);
                }

                handlerMap.put(func, noveTestHandler);
            } catch (Exception e) {//防御性容错
            }
        }
        HANDLER.set(handlerMap);
        FUNC_PARAM.set(funcMap);
    }


}
