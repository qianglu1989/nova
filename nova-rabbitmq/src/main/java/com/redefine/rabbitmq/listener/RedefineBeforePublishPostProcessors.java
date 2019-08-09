package com.redefine.rabbitmq.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;

/**
 * 发送前处理数据
 * Created by QIANG on 2017/11/23
 *
 * @author QIANG
 */
public class RedefineBeforePublishPostProcessors implements MessagePostProcessor {

    private Logger log = LoggerFactory.getLogger(RedefineMessagePostProcessor.class);



    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        MessageProperties messageProperties = message.getMessageProperties();
        log.debug("消息发送前状态提示,MSGID:{}", messageProperties.getMessageId());


        return message;
    }
}
