package com.nove.version.autoconfig;

import com.nove.version.*;
import com.nove.version.autoconfig.properties.RedefineVersionProperties;
import com.nove.version.feign.config.RedefineVersionFeignConfiguration;
import com.nove.version.ribbon.EurekaServerExtractor;
import com.nove.version.ribbon.RedefineVerClientHttpRequestIntercptor;
import com.nove.version.zuul.config.RedefineVersionZuulConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author luqiang
 */
@Configuration
@EnableConfigurationProperties({RedefineVersionProperties.class})
@AutoConfigureBefore({RedefineVersionFeignConfiguration.class, RedefineVersionZuulConfiguration.class})
@Import(RedefineVersionWebConfiguration.class)
@RibbonClients(defaultConfiguration = RedefineVersionRibbonClientsConfiguration.class)
public class RedefineVersionAutoConfiguration {




    @Resource
    private SpringClientFactory springClientFactory;

    @Resource
    private RedefineVersionProperties versionProperties;

    @Autowired(required = false)
    private List<LoadBalanceRequestTrigger> requestTriggerList;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new RedefineVerClientHttpRequestIntercptor(versionProperties));
        return restTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public EurekaServerExtractor eurekaServerExtractor() {
        return new EurekaServerExtractor(springClientFactory);
    }




    @Bean
    @ConditionalOnMissingBean
    public RequestVersionExtractor requestVersionExtractor() {
        return new RequestVersionExtractor.Default();
    }


    @Bean
    @ConditionalOnMissingBean
    public RedefineVersionRibbonConnectionPoint redefineVersionRibbonConnectionPoint(
            RequestVersionExtractor requestVersionExtractor
    ) {
        if (requestTriggerList != null) {
            requestTriggerList = Collections.EMPTY_LIST;
        }
        return new DefaultRibbonConnectionPoint(requestVersionExtractor, requestTriggerList);
    }

    @Bean
    @Order(value = RedefineVersionConstants.INITIALIZING_ORDER)
    public RedefineVersionInitializingBean redefineVersionInitializingBean() {
        return new RedefineVersionInitializingBean();
    }

}
