package com.nove.version.web;

import com.nove.version.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 获取请求的真实ip，并保存到当前线程中。
 * @author QIANGLU
 */
public class IpKeepInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(IpKeepInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取ip
        String ip = WebUtils.getIpAddr(request);
        //保存
        RequestIpKeeper.instance().setIp(ip);
        return super.preHandle(request, response, handler);
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清除ThreadLocal
        RequestIpKeeper.instance().clear();
        super.afterCompletion(request, response, handler, ex);
    }

}
