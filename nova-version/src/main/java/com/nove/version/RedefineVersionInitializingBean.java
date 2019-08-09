package com.nove.version;

import com.nove.version.ribbon.EurekaServerExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Bamboo相关依赖的初始化工作
 * @author luqiang
 */
public class RedefineVersionInitializingBean implements InitializingBean, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(RedefineVersionInitializingBean.class);


    private ApplicationContext ctx;

    @Override
    public void afterPropertiesSet() {
        RedefineVersionAppContext.setDefaultConnectionPoint(ctx.getBean(RedefineVersionRibbonConnectionPoint.class));
        RedefineVersionAppContext.setEurekaServerExtractor(ctx.getBean(EurekaServerExtractor.class));
        setLocalIp();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }


    /**
     * 设置本机ip
     */
    private void setLocalIp(){
        try {
            RedefineVersionAppContext.setLocalIp(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            log.error("[IpHelper-getIpAddr] IpHelper error.", e);
        }
    }
}
