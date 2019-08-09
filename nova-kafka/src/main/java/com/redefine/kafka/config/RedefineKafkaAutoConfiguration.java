package com.redefine.kafka.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.*;
import org.springframework.util.StringUtils;

/**
 * @author QIANGLU
 */
@Configuration
@EnableKafka
@EnableConfigurationProperties(KafkaProperties.class)
public class RedefineKafkaAutoConfiguration {

    private final KafkaProperties properties;

    public RedefineKafkaAutoConfiguration(KafkaProperties properties) {
        this.properties = properties;
    }


    @Bean
    @ConditionalOnMissingBean(ConsumerFactory.class)
    public ConsumerFactory<?, ?> kafkaConsumerFactory(Environment environment) {
        String groupId = this.properties.getConsumer().getGroupId();
        if (StringUtils.isEmpty(groupId)) {
            this.properties.getConsumer().setGroupId(environment.getProperty("spring.application.name"));
        }
        return new DefaultKafkaConsumerFactory<>(
                this.properties.buildConsumerProperties());
    }


}
