package com.redefine.rabbitmq.listener;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.CorrelationDataPostProcessor;
import org.springframework.amqp.rabbit.support.CorrelationData;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

/**
 * 数据发送前进行组装 Update or replace the correlation data provided in the send method.
 *
 * @author QIANG
 */
public class RedefineCorrelationDataPostProcessor implements CorrelationDataPostProcessor {

    private Logger log = LoggerFactory.getLogger(RedefineCorrelationDataPostProcessor.class);

    /**
     * 用于记录应用标识
     */
    private String appId;

    public RedefineCorrelationDataPostProcessor(String appId) {
        this.appId = appId;
    }

    @Override
    public CorrelationData postProcess(Message message, CorrelationData correlationData) {

        MessageProperties messageProperties = message.getMessageProperties();
        messageProperties.setTimestamp(new Date());
        try {
            InetAddress netAddress = InetAddress.getLocalHost();
            messageProperties.getHeaders().put("sendNetAddress", netAddress);
        } catch (UnknownHostException e) {
            log.warn("获取本地IP出现异常，MSG:{}", e);
        }
        if (StringUtils.isNotEmpty(appId)) {
            messageProperties.setAppId(appId);
        }
        String msgID = message.getMessageProperties().getMessageId();
        if (StringUtils.isEmpty(msgID)) {
            msgID = UUID.randomUUID().toString();
            message.getMessageProperties().setMessageId(msgID);
        }
        if (correlationData == null) {
            correlationData = new CorrelationData(msgID);
        }
        return correlationData;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

}
