package com.nove.version.feign;

import com.nove.version.RedefineVersionAppContext;
import com.nove.version.RedefineVersionRequest;
import com.nove.version.ConnectPointContext;
import com.nove.version.autoconfig.properties.RedefineVersionProperties;
import com.nove.version.utils.WebUtils;
import com.nove.version.web.RequestIpKeeper;
import feign.Client;
import feign.Request;
import feign.Response;

import java.io.IOException;
import java.net.URI;

/**
 * 如果为内部请求，则需要使用feign ，所以此类主要作用是用来获取request的相关信息，为后面的路由提供数据基础。
 * @author QIANGLU
 */
public class RedefineVersionFeignClient implements Client {

    private Client delegate;
    private RedefineVersionProperties versionProperties;

    public RedefineVersionFeignClient(RedefineVersionProperties versionProperties, Client delegate) {
        this.delegate = delegate;
        this.versionProperties = versionProperties;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        URI uri = URI.create(request.url());
        RedefineVersionRequest.Builder builder = RedefineVersionRequest.builder()
                .serviceId(uri.getHost())
                .uri(uri.getPath())
                .ip(RequestIpKeeper.getRequestIp())
                .addMultiParams(WebUtils.getQueryParams(uri.getQuery()));
        if(versionProperties.getRedefineVerRequest().isLoadBody()){
            builder.requestBody(request.body());
        }


        request.headers().entrySet().forEach(entry ->{
            for (String v : entry.getValue()) {
                builder.addHeader(entry.getKey(), v);
            }
        });

        ConnectPointContext connectPointContext = ConnectPointContext.builder().versionRequest(builder.build()).build();

        try {
            RedefineVersionAppContext.getVersionRibbonConnectionPoint().executeConnectPoint(connectPointContext);
            return delegate.execute(request, options);
        }finally {
            RedefineVersionAppContext.getVersionRibbonConnectionPoint().shutdownconnectPoint();
        }
    }
}
