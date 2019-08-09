package com.redefine.rabbitmq.config;

import com.redefine.rabbitmq.converter.RedefineMessageConverter;
import com.redefine.rabbitmq.listener.RedefineConfirmCallback;
import com.redefine.rabbitmq.listener.RedefineCorrelationDataPostProcessor;
import com.redefine.rabbitmq.listener.RedefineReturnCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;



/**
 * @Configuration
 * @ConditionalOnClass({ RabbitTemplate.class, Channel.class })
 * @EnableConfigurationProperties(RabbitProperties.class)
 */
/**
 * @author QIANGLU
 */
@Deprecated
public class RedefineRabbitAutoConfiguration {

    @Value("${spring.application.name:Nove}")
    private String appId;


    private Logger log = LoggerFactory.getLogger(RedefineRabbitAutoConfiguration.class);

    @Bean
    public CachingConnectionFactory rabbitConnectionFactory(RabbitProperties config) throws Exception {
        RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
        if (config.determineHost() != null) {
            factory.setHost(config.determineHost());
        }
        factory.setPort(config.determinePort());
        if (config.determineUsername() != null) {
            factory.setUsername(config.determineUsername());
        }
        if (config.determinePassword() != null) {
            factory.setPassword(config.determinePassword());
        }
        if (config.determineVirtualHost() != null) {
            factory.setVirtualHost(config.determineVirtualHost());
        }
        if (config.getRequestedHeartbeat() != null) {
        }
        RabbitProperties.Ssl ssl = config.getSsl();
        if (ssl.isEnabled()) {
            factory.setUseSSL(true);
            if (ssl.getAlgorithm() != null) {
                factory.setSslAlgorithm(ssl.getAlgorithm());
            }
            factory.setKeyStore(ssl.getKeyStore());
            factory.setKeyStorePassphrase(ssl.getKeyStorePassword());
            factory.setTrustStore(ssl.getTrustStore());
            factory.setTrustStorePassphrase(ssl.getTrustStorePassword());
        }
        if (config.getConnectionTimeout() != null) {
        }
        factory.afterPropertiesSet();
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(factory.getObject());
        connectionFactory.setAddresses(config.determineAddresses());
        // 强制开启ReturnCallback ConfirmCallback
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        if (config.getCache().getChannel().getSize() != null) {
            connectionFactory.setChannelCacheSize(config.getCache().getChannel().getSize());
        } else {
            connectionFactory.setChannelCacheSize(50);

        }
        if (config.getCache().getConnection().getMode() != null) {
            connectionFactory.setCacheMode(config.getCache().getConnection().getMode());
        }
        if (config.getCache().getConnection().getSize() != null) {
            connectionFactory.setConnectionCacheSize(config.getCache().getConnection().getSize());
        }
        if (config.getCache().getChannel().getCheckoutTimeout() != null) {
        }
        log.info("create rabbitmq server ,address:{}", config.determineAddresses());
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, RabbitProperties properties,
                                         MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        RabbitProperties.Template templateProperties = properties.getTemplate();
        RabbitProperties.Retry retryProperties = templateProperties.getRetry();
        if (retryProperties.isEnabled()) {
            rabbitTemplate.setRetryTemplate(createRetryTemplate(retryProperties));
        }
        if (templateProperties.getReceiveTimeout() != null) {
        }
        if (templateProperties.getReplyTimeout() != null) {
        }

        rabbitTemplate.setConfirmCallback(new RedefineConfirmCallback());
        rabbitTemplate.setReturnCallback(new RedefineReturnCallback());
        rabbitTemplate.setCorrelationDataPostProcessor(new RedefineCorrelationDataPostProcessor(appId));
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(MessageConverter.class)
    public RedefineMessageConverter simpleMessageConverter() {
        RedefineMessageConverter converter = new RedefineMessageConverter();
        converter.setMessageConverter(new Jackson2JsonMessageConverter());
        converter.setCreateMessageIds(true);
        return converter;
    }

    private RetryTemplate createRetryTemplate(RabbitProperties.Retry properties) {
        RetryTemplate template = new RetryTemplate();
        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        policy.setMaxAttempts(properties.getMaxAttempts());
        template.setRetryPolicy(policy);
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setMultiplier(properties.getMultiplier());
        template.setBackOffPolicy(backOffPolicy);
        return template;
    }


}
