package com.nove.version.ribbon;

import com.nove.version.RedefineVersionAppContext;
import com.nove.version.ConnectPointContext;
import com.nove.version.RedefineVersionRequest;
import com.nove.version.autoconfig.properties.RedefineVersionProperties;
import com.nove.version.utils.WebUtils;
import com.nove.version.web.RequestIpKeeper;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;


/**
 * 用于@LoadBalance 标记的 RestTemplate，主要作用是用来获取request的相关信息，为后面的路由提供数据基础。
 * @author QIANGLU
 */
public class RedefineVerClientHttpRequestIntercptor implements ClientHttpRequestInterceptor {

    private RedefineVersionProperties versionProperties;

    public RedefineVerClientHttpRequestIntercptor(RedefineVersionProperties versionProperties) {
        this.versionProperties = versionProperties;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        URI uri = request.getURI();
        RedefineVersionRequest.Builder rvReqBuilder = RedefineVersionRequest.builder()
                .serviceId(uri.getHost())
                .uri(uri.getPath())
                .ip(RequestIpKeeper.getRequestIp())
                .addMultiHeaders(request.getHeaders())
                .addMultiParams(WebUtils.getQueryParams(uri.getQuery()));

        if (versionProperties.getRedefineVerRequest().isLoadBody()) {
            rvReqBuilder.requestBody(body);
        }

        //封装基础请求信息
        RedefineVersionRequest versionRequest = rvReqBuilder.build();

        //构造请求连接上下文信息
        ConnectPointContext connectPointContext = ConnectPointContext.builder().versionRequest(versionRequest).build();
        try {

            //获取链接上下文实例 进行信息封装
            RedefineVersionAppContext.getVersionRibbonConnectionPoint().executeConnectPoint(connectPointContext);
            return execution.execute(request, body);
        } finally {
            RedefineVersionAppContext.getVersionRibbonConnectionPoint().shutdownconnectPoint();
        }
    }
}
