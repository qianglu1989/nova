package com.redefine.nove.autoconfigure;

import com.redefine.nove.NoveTestContext;
import com.redefine.nove.NoveTestHandler;
import com.redefine.nove.abtest.NoveParamInterceptor;
import com.redefine.nove.abtest.NoveTestInterceptor;
import com.redefine.nove.bus.refresh.NoveContextRefresher;
import com.redefine.nove.event.NoveRefreshListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author QIANGLU
 */
@Configuration
@ConditionalOnProperty(name = "redefine.abtest.enable", matchIfMissing = true)
public class NoveTestAutoConfiguration implements WebMvcConfigurer {

    private static Logger LOG = LoggerFactory.getLogger(NoveTestAutoConfiguration.class);

    @Resource
    private Map<String, NoveTestHandler> noveTestHandlerMap;

    public NoveTestAutoConfiguration() {
//        RandomBucketUtils.INIT_HANDLER.putAll(noveTestHandlerMap);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LOG.info("add noveTestInterceptor to registry...");
        registry.addInterceptor(noveParamInterceptor()).addPathPatterns("/**");
    }


    @Bean
    public NoveTestContext noveTestContext() {
        return new NoveTestContext(noveTestHandlerMap);
    }

    @Bean
    public NoveTestInterceptor noveTestInterceptor() {
        return new NoveTestInterceptor(noveTestContext());
    }

    @Bean
    public NoveParamInterceptor noveParamInterceptor() {

        return new NoveParamInterceptor();
    }

    @Bean
    public NoveRefreshListener noveRefreshListener(NoveContextRefresher noveContextRefresher) {
        return new NoveRefreshListener(noveContextRefresher);
    }



}
