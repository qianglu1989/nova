package com.redefine.rabbitmq.listener;

import com.redefine.rabbitmq.utils.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;

/**
 * 用于消息发送失败回调
 *
 * @author QIANG
 */
public class RedefineReturnCallback implements ReturnCallback {

    private   Logger log = LoggerFactory.getLogger(RedefineReturnCallback.class);



    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        MessageProperties messageProperties = message.getMessageProperties();
        String body = MessageUtils.getBodyContentAsString(message.getBody(), messageProperties);


        log.error("消息发送失败状态提示,MSGID:{},BODY:{},replyCode:{},replyText:{}", messageProperties.getMessageId(), body, replyCode, replyText);



    }

}
