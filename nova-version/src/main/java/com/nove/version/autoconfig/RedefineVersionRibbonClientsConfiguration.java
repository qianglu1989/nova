package com.nove.version.autoconfig;


import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import com.nove.version.ribbon.loadbalancer.RedefineVerZoneAvoidanceRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用于加载ribbon client 基础规则
 * @author luqiang
 */
@Configuration
public class RedefineVersionRibbonClientsConfiguration {

    @Autowired(required = false)
    private IClientConfig config;

    @Bean
    public IRule ribbonRule() {
        RedefineVerZoneAvoidanceRule rule = new RedefineVerZoneAvoidanceRule();
        rule.initWithNiwsConfig(config);
        return rule;
    }
}
