package com.nove.version.feign.config;

import com.nove.version.autoconfig.properties.RedefineVersionProperties;
import com.nove.version.feign.RedefineVersionFeignClient;
import feign.Client;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author QIANGLU
 */
@Configuration
@EnableConfigurationProperties({RedefineVersionProperties.class})
public class RedefineVersionFeignClientsConfiguration {


    @Resource
    private Client feignClient;

    @Resource
    private RedefineVersionProperties versionProperties;

    @Bean
    public Client versionFeignClient() {
        return new RedefineVersionFeignClient(versionProperties, feignClient);
    }

}
