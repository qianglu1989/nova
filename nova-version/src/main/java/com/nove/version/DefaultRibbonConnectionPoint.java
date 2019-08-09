package com.nove.version;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 * @author QIANGLU
 */
public class DefaultRibbonConnectionPoint implements RedefineVersionRibbonConnectionPoint, ApplicationContextAware {

    private RequestVersionExtractor versionExtractor;
    private ApplicationContext ctx;
    private static ThreadLocal<List<LoadBalanceRequestTrigger>> curRequestTriggers = new ThreadLocal();
    private List<LoadBalanceRequestTrigger> requestTriggerList;

    public DefaultRibbonConnectionPoint(RequestVersionExtractor versionExtractor) {
        this(versionExtractor, null);
    }

    public DefaultRibbonConnectionPoint(RequestVersionExtractor versionExtractor, List<LoadBalanceRequestTrigger> requestTriggerList) {
        this.versionExtractor = versionExtractor;
        this.requestTriggerList = requestTriggerList;
    }

    /**
     * 封装上下文信息
     * @param connectPointContext
     */
    @Override
    public void executeConnectPoint(ConnectPointContext connectPointContext) {

        //存储当前线程上下文
        ConnectPointContext.CONTEXT_LOCAL.set(connectPointContext);

        RedefineVersionRequest versionRequest = connectPointContext.getVersionRequest();
        String requestVersion = versionExtractor.extractVersion(versionRequest);
        //因为hstrix 线程隔离的问题，所以再次进行数据传递
        RedefineVersionRequestContext.initRequestContext(versionRequest, requestVersion);
        executeBeforeReuqestTrigger();
    }

    @Override
    public void shutdownconnectPoint() {
        try {
            executeAfterReuqestTrigger();
        } catch (Exception e) {
            ConnectPointContext.getContextLocal().setExcption(e);
        } finally {
            curRequestTriggers.remove();
            ConnectPointContext.CONTEXT_LOCAL.remove();
            RedefineVersionRequestContext.shutdownRequestContext();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    private List<LoadBalanceRequestTrigger> chooseRequestTrigger() {
        if (curRequestTriggers.get() != null) {
            return curRequestTriggers.get();
        }

        Collection<LoadBalanceRequestTrigger> triggers;

        if (requestTriggerList != null) {
            triggers = requestTriggerList;
        } else {
            triggers = ctx.getBeansOfType(LoadBalanceRequestTrigger.class).values();
        }

        List<LoadBalanceRequestTrigger> requestTriggers = new ArrayList<>();
        triggers.forEach(trigger -> {
            if (trigger.shouldExecute()) {
                requestTriggers.add(trigger);
            }
        });
        curRequestTriggers.set(requestTriggers);
        return requestTriggers;
    }


    protected void executeBeforeReuqestTrigger() {
        ConnectPointContext connectPointContext = ConnectPointContext.getContextLocal();
        List<LoadBalanceRequestTrigger> requestTriggers = chooseRequestTrigger();
        if (requestTriggers != null && !requestTriggers.isEmpty()) {
            requestTriggers.forEach(trigger -> trigger.before(connectPointContext));
        }
    }


    protected void executeAfterReuqestTrigger() {
        ConnectPointContext connectPointContext = ConnectPointContext.getContextLocal();
        List<LoadBalanceRequestTrigger> requestTriggers = chooseRequestTrigger();
        if (requestTriggers != null && !requestTriggers.isEmpty()) {
            requestTriggers.forEach(trigger -> trigger.after(connectPointContext));
        }
    }
}
