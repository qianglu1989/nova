package com.redefine.nove.bus.config;

import com.redefine.nove.bus.refresh.NoveContextRefresher;
import com.redefine.nove.bus.refresh.NoveRefreshBusEndpoint;
import com.redefine.nove.bus.util.AbTestConfigUtils;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author QIANGLU
 */
@Configuration
@RemoteApplicationEventScan(basePackages = "com.redefine.nove.bus.event")
public class NoveBusAutoConfiguration {


    @Bean
    public static AbTestConfigUtils aBTestConfigUtils(ConfigurableApplicationContext context) {

        return new AbTestConfigUtils(context);
    }

    @Bean
    public NoveContextRefresher noveContextRefresher(ConfigurableApplicationContext context) {
        return new NoveContextRefresher(context);
    }


    @Bean
    public NoveRefreshBusEndpoint noveRefreshBusEndpoint(ApplicationContext context,
                                                         BusProperties bus) {
        return new NoveRefreshBusEndpoint(context, bus.getId());
    }

}
