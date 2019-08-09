package com.redefine.nove.abtest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.monitor.trace.RedefineSpanContextHolder;
import com.redefine.nove.bus.util.AbTestConfigUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author QIANGLU
 */
public class RandomBucketUtils {
    private static Logger LOG = LoggerFactory.getLogger(RandomBucketUtils.class);

    /**
     * 百分比标识
     */
    private static String PERCENT = "percent";

    /**
     * 因子标识
     */
    private static String FACTOR = "factor";
    /**
     * 固定前缀
     */
    private static String PRE = "abtest.";

    /**
     * 公共参数标示
     *
     * @param request
     */
    private static String REQUEST_COMMON_PARAMS = "requestCommonParam";

    private static String[] PARAM_NAMES = new String[]{"gaid", "sr", "ve", "channel", "tag", "uid", "la", "idtoken"};

    private static Set<String> PARAM_KEY = new HashSet<>(Arrays.asList(PARAM_NAMES));

    private RandomBucketUtils() {

    }

    static {
        Object obj = AbTestConfigUtils.getProperty("abtest.params");
        if (obj != null) {
            String ext = (String) obj;
            String[] extDetails = ext.split(",");
            Set<String> extKey = new HashSet<>(Arrays.asList(extDetails));
            PARAM_KEY.addAll(extKey);
        }
    }

    /**
     * 用于加载request数据
     *
     * @param request
     */
    public static Map<String, String> setParam(HttpServletRequest request) {

        Map<String, String> configs = new LinkedHashMap<>();

        String param = request.getParameter(REQUEST_COMMON_PARAMS);
        if (StringUtils.isNotEmpty(param)) {
            Map<String, String> params = JSON.parseObject(param, Map.class);
            String userId = params.get("userId");
            String gaid = params.get("gaid");

            if (StringUtils.isNotEmpty(userId)) {
                configs.put("bucketId", userId);
            } else if (StringUtils.isNotEmpty(gaid)) {
                configs.put("bucketId", gaid);
            } else {
                configs.put("bucketId", "0000");

            }
            PARAM_KEY.stream().forEach(key -> {
                configs.putIfAbsent(key, params.get(key));
            });

        }


        return configs;
    }


    /**
     * 根据func类型加载数据
     *
     * @param type 分桶策略的标识，此标志在 @NoveTest("funcName")
     */
    public static Map<String, Object> initDatas(String type, String testName, String bucketId) {

        Map<String, Object> funcParams = new HashMap<>(16);
        //计算捅数据
        try {
            String data = (String) AbTestConfigUtils.getProperty(PRE + type + "." + testName);
            funcParams = JSON.parseObject(data, Map.class);
            funcParams.put("bucketId", bucketId);
        } catch (Exception e) {
        }

        return funcParams;
    }


    /**
     * 根据标识获取分桶策略
     * sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
     *
     * @param type 代表的类型
     */
    public static String hashBucket(String type, Map<String, Object> funcParams) {


        List<String> bucket = new LinkedList<>();
        try {

            String sole = (String) funcParams.get("bucketId");

            String factor = (String) funcParams.getOrDefault(FACTOR, "L1");

            int hash = (((sole + factor).hashCode() % 100) + 100) % 100;


            //获取参数百分比值
            funcParams.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
                if (entry.getKey().contains(PERCENT)) {
                    IntStream.range(0, Integer.valueOf((String) entry.getValue())).forEach(i -> bucket.add(entry.getKey()));
                }

            });
            String result = bucket.get(hash).split("[.]")[1];


            logs(type, result, sole, factor, hash);
            return result;
        } catch (Exception e) {
            LOG.debug("RandomBucketUtils hashBucket,type:{}, warn:{}", type, e.getMessage());
            RedefineSpanContextHolder.addTag("warn_" + type, e.getMessage());
        }

        return "";
    }

    private static void logs(String type, String bucket, String id, String factor, int hash) {

        Map<String, Object> log = new HashMap<>(16);
        log.put("type", type);
        log.put("bucket", bucket);
        log.put("id", id);
        log.put("factor", factor);
        log.put("hash", hash);
        RedefineSpanContextHolder.addTag(type, log);
    }

    /**
     * 根据func 类型匹配test条目，使用匹配到的第一个条目
     *
     * @param func
     * @return
     */
    public static String matchFunc(String func, Map<String, String> commonParam) {


        Map<String, Object> funcDatas = AbTestConfigUtils.getAll().entrySet().stream().filter(map -> map.getKey().startsWith("abtest." + func)).sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        for (Map.Entry<String, Object> entry : funcDatas.entrySet()) {

            String value = (String) entry.getValue();
            Map<String, Object> details = JSON.parseObject(value, Map.class);
            JSONObject condition = (JSONObject) details.get("condition");
            //TODO 全量
            if (condition.size() == 0) {
                return (String) details.get("testName");
            }
            Map<String, Object> datas = condition.toJavaObject(Map.class);

            //进行规则校验
            AtomicBoolean result = new AtomicBoolean(true);
            datas.forEach((k, v) -> {

                String val = commonParam.get(k);
                if (v instanceof JSONObject || v instanceof String && result.get()) {

                    if (!v.equals(val)) {
                        result.set(false);
                    }
                } else if (v instanceof JSONArray && result.get()) {
                    List<String> list = ((JSONArray) v).toJavaList(String.class);
                    boolean lr = list.stream().anyMatch(d -> d.equals(val));

                    if (!lr) {
                        result.set(false);
                    }
                }
            });

            if (result.get()) {
                return (String) details.get("testName");
            }
        }

        LOG.info("Nove ABTEST can not match rule,func:{}", func);
        return "";
    }


}
