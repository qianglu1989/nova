package com.nove.version.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.nove.version.RedefineVersionAppContext;
import com.nove.version.RedefineVersionRequest;
import com.nove.version.ConnectPointContext;
import com.nove.version.autoconfig.properties.RedefineVersionProperties;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import com.netflix.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author luqiang
 * 预处理request信息,为后面路由提供基础数据
 */
public class RedefineVersionPreZuulFilter extends ZuulFilter {

    private static final Logger log = LoggerFactory.getLogger(RedefineVersionPreZuulFilter.class);

    private RedefineVersionProperties redefineVersionProperties;

    public RedefineVersionPreZuulFilter(RedefineVersionProperties redefineVersionProperties) {
        this.redefineVersionProperties = redefineVersionProperties;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 10000;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {

        RequestContext context = RequestContext.getCurrentContext();
        RedefineVersionRequest.Builder builder = RedefineVersionRequest.builder()
                .serviceId((String)context.get(FilterConstants.SERVICE_ID_KEY))
                .uri((String)context.get(FilterConstants.REQUEST_URI_KEY))
                .ip(context.getZuulRequestHeaders().get(FilterConstants.X_FORWARDED_FOR_HEADER.toLowerCase()))
                .addMultiParams(context.getRequestQueryParams())
                .addHeaders(context.getZuulRequestHeaders())
                .addHeaders(context.getOriginResponseHeaders().stream().collect(Collectors.toMap(Pair::first, Pair::second)));
        context.getOriginResponseHeaders().forEach(pair-> builder.addHeader(pair.first(), pair.second()));

        if(redefineVersionProperties.getRedefineVerRequest().isLoadBody()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(context.getRequest().getInputStream()));
                byte[] reqBody = IOUtils.toByteArray(reader);
                builder.requestBody(reqBody);
            } catch (IOException e) {
                String errorMsg = "获取request body出现异常";
                log.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        }

        ConnectPointContext connectPointContext = ConnectPointContext.builder().versionRequest(builder.build()).build();

        RedefineVersionAppContext.getVersionRibbonConnectionPoint().executeConnectPoint(connectPointContext);
        return null;
    }

}
