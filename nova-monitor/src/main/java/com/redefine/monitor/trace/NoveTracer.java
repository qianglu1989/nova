package com.redefine.monitor.trace;

import java.util.Map;

/**
 * @author QIANGLU
 */
public interface NoveTracer {

    /**
     * 用于创建span
     * @return
     */
    public RedefineSpan createSpan();

    /**
     * 用于关闭span
     * @return
     */
    public RedefineSpan close();

    /**
     * 获取当前span
     * @return
     */
    public RedefineSpan getCurrentSpan();

    /**
     * 实时发送report
     * @param key key
     * @param val val
     */
    public void report(String key,Object val);

    /**
     * 实时发送report
     * @param key key
     * @param val val
     * @param reqParams params
     */
    public void report(String key, Object val, Map<String, String[]> reqParams);
}
