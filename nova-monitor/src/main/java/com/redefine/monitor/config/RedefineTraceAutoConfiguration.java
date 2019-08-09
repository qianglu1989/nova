package com.redefine.monitor.config;

import com.redefine.monitor.interceptor.RedefineSpanInterceptor;
import com.redefine.monitor.model.RedefineKafkaProperties;
import com.redefine.monitor.trace.KafkaReport;
import com.redefine.monitor.trace.KafkaSend;
import com.redefine.monitor.trace.NoveTracer;
import com.redefine.monitor.trace.RedefineTracer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * 内置拦截器
 *
 * @author QIANG
 */
@Configuration
@EnableConfigurationProperties({RedefineKafkaProperties.class })
@ConditionalOnProperty(name = "redefine.kafka.bootstrap-servers")
public class RedefineTraceAutoConfiguration implements WebMvcConfigurer {

    private   Logger log = LoggerFactory.getLogger(RedefineTraceAutoConfiguration.class);

    @Value("${spring.application.name:default}")
    private String name;

    @Value("${redefine.trace.data.topic:novetrace}")
    private String topic;

    @Value("${redefine.trace.data.corePoolSize:50}")
    private int corePoolSize;

    @Value("${redefine.trace.data.queueSize:5000}")
    private int queueSize;

    private RedefineKafkaProperties redefineKafkaProperties;

    @Resource
    private Environment environment;

    public RedefineTraceAutoConfiguration(RedefineKafkaProperties redefineKafkaProperties) {
        this.redefineKafkaProperties = redefineKafkaProperties;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("add redefineSpanInterceptor to task...");
        registry.addInterceptor(redefineSpanInterceptor()).addPathPatterns("/**");
    }


    @Bean
    public RedefineSpanInterceptor redefineSpanInterceptor() {
        return new RedefineSpanInterceptor(noveTracer());
    }


    @Bean
    public NoveTracer noveTracer() {
        return new RedefineTracer(kafkaReport(), name);
    }

    @Bean
    public KafkaReport kafkaReport() {
        return KafkaReport.builder().environment(this.environment).corePoolSize(corePoolSize).queueSize(queueSize).topic(topic).kafkaSend(kafkaSend()).initExecutor().build();
    }

    /**
     *   *  bootstrap.servers：Kafka集群连接串，可以由多个host:port组成
     *      *  acks：broker消息确认的模式，有三种：
     *      *  0：不进行消息接收确认，即Client端发送完成后不会等待Broker的确认
     *      *  1：由Leader确认，Leader接收到消息后会立即返回确认信息
     *      *  all：集群完整确认，Leader会等待所有in-sync的follower节点都确认收到消息后，再返回确认信息
     *      *  我们可以根据消息的重要程度，设置不同的确认模式。默认为1
     *      *  retries：发送失败时Producer端的重试次数，默认为0
     *      *  batch.size：当同时有大量消息要向同一个分区发送时，Producer端会将消息打包后进行批量发送。如果设置为0，则每条消息都独立发送。默认为16384字节
     *      *  linger.ms：发送消息前等待的毫秒数，与batch.size配合使用。在消息负载不高的情况下，配置linger.ms能够让Producer在发送消息前等待一定时间，以积累更多的消息打包发送，达到节省网络资源的目的。默认为0
     *      *  key.serializer/value.serializer：消息key/value的序列器Class，根据key和value的类型决定
     *      *  buffer.memory：消息缓冲池大小。尚未被发送的消息会保存在Producer的内存中，如果消息产生的速度大于消息发送的速度，那么缓冲池满后发送消息的请求会被阻塞。默认33554432字节（32MB）
     * @return
     */
    @Bean
    public KafkaSend kafkaSend() {
        Map<String, Object> properties = this.redefineKafkaProperties.buildProducerProperties();
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());
        properties.put("acks", "0");
        properties.put("retries", 0);
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 2000);
        properties.put("buffer.memory", 33554432);
        Object bootstrapServers = properties.get("bootstrap.servers");
        if (bootstrapServers instanceof List) {
            properties.put("bootstrap.servers", join((List) bootstrapServers));
        }
        return KafkaSend.builder().topic(topic).properties(properties).build();
    }

    static String join(List<?> parts) {
        StringBuilder to = new StringBuilder();
        for (int i = 0, length = parts.size(); i < length; i++) {
            to.append(parts.get(i));
            if (i + 1 < length) {
                to.append(',');
            }
        }
        return to.toString();
    }
}
