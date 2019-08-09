package com.redefine.nove;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 用于存储上下文数据
 * @author luqiang on 17/01/2019.
 */
public class NoveTestContext<T, R> {

    private final static Logger log = LoggerFactory.getLogger(NoveTestContext.class);

    private Map<String, NoveTestHandler<T, R>> noveTestHandlerMap;

    public NoveTestContext(Map<String, NoveTestHandler<T, R>> noveTestHandlerMap) {
        this.noveTestHandlerMap = noveTestHandlerMap;
    }


    public Map<String, NoveTestHandler<T, R>> getNoveTestHandlerMap() {
        return noveTestHandlerMap;
    }

    public NoveTestHandler getNoveTestHandler(String type) {
        return this.noveTestHandlerMap.get(type);
    }

    public void setNoveTestHandlerMap(Map<String, NoveTestHandler<T, R>> noveTestHandlerMap) {
        this.noveTestHandlerMap = noveTestHandlerMap;
    }
}
