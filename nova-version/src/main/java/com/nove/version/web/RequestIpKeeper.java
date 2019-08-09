package com.nove.version.web;

import com.nove.version.RedefineVersionAppContext;
import org.apache.commons.lang3.StringUtils;

/**
 * @author QIANGLU
 */
public final class RequestIpKeeper {

    private static final ThreadLocal<String> IP_LOCAL = new ThreadLocal<>();


    private static RequestIpKeeper INSTANCE = new RequestIpKeeper();

    private RequestIpKeeper() {

    }

    public static RequestIpKeeper instance() {
        return INSTANCE;
    }


    void setIp(String ip) {
        IP_LOCAL.set(ip);
    }


    public String getIp() {
        return IP_LOCAL.get();
    }


    public void clear() {
        IP_LOCAL.remove();
    }


    public static String getRequestIp() {
        String ip = instance().getIp();
        if (StringUtils.isEmpty(ip)) {
            ip = RedefineVersionAppContext.getLocalIp();
        }
        return ip;
    }

}
