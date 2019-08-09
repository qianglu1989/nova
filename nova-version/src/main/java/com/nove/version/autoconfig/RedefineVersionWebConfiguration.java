package com.nove.version.autoconfig;

import com.nove.version.web.IpKeepInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author QIANGLU
 * 用于加载基础拦截器
 */
@Configuration
public class RedefineVersionWebConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new IpKeepInterceptor());
    }
}
