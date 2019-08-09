package com.nove.version.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.nove.version.RedefineVersionAppContext;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * @author luqiang
 * 做一些善后工作。比如删除BambooRequestContext在ThreadLocal中的信息。
 */
public class RedefineVersionPostZuulFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RedefineVersionAppContext.getVersionRibbonConnectionPoint().shutdownconnectPoint();
        return null;
    }
}
