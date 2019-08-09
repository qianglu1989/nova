package com.redefine.nove.autoconfigure;

import com.redefine.nove.utils.PropertyConfigUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author QIANGLU
 */
@Configuration
public class CommonsAutoConfiguration {


    @Bean
    public static PropertyConfigUtils propertyConfigUtils(ConfigurableApplicationContext context) {

        return new PropertyConfigUtils(context);
    }
}
