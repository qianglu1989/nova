package com.redefine.nove;

/**
 * @author luqiang on 14/01/2019.
 */
public interface NoveTestHandler<T, R> {

    /**
     * 处理逻辑
     *
     * @param data 入参
     * @return
     */
    R invoke(T data);
}
