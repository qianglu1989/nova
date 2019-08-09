package com.nove.version.zuul.config;

import com.netflix.zuul.http.ZuulServlet;
import com.nove.version.autoconfig.properties.RedefineVersionProperties;
import com.nove.version.zuul.filter.RedefineVersionPostZuulFilter;
import com.nove.version.zuul.filter.RedefineVersionPreZuulFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author luqiang
 *         针对于网关的多版本数据处理，通过filter进行数据组装
 */
@Configuration
@ConditionalOnClass(value = ZuulServlet.class)
@EnableConfigurationProperties({RedefineVersionProperties.class})
public class RedefineVersionZuulConfiguration {

    @Resource
    private RedefineVersionProperties redefineVersionProperties;

    @Bean
    public RedefineVersionPreZuulFilter bambooPreZuulFilter() {
        return new RedefineVersionPreZuulFilter(redefineVersionProperties);
    }

    @Bean
    public RedefineVersionPostZuulFilter bambooPostZuulFilter() {
        return new RedefineVersionPostZuulFilter();
    }
}
