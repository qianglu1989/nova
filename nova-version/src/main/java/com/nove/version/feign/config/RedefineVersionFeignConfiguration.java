package com.nove.version.feign.config;

import com.netflix.loadbalancer.ILoadBalancer;
import feign.Feign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * @author luqiang
 */
@ConditionalOnClass(value = {ILoadBalancer.class, Feign.class})
@EnableFeignClients(defaultConfiguration = {RedefineVersionFeignClientsConfiguration.class})
@Configuration
public class RedefineVersionFeignConfiguration {


}
