package com.redefine.monitor.trace;

import java.util.concurrent.ThreadFactory;

/**
 * Created by QIANG on 2017/11/22
 * @author QIANG
 */
public class NoveThreadFactory implements ThreadFactory {

    private String name;
    public NoveThreadFactory(String name){
        this.name = name;
    }
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(this.name);
        return t;
    }
}
