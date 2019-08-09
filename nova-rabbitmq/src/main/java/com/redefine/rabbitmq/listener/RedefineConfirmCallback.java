package com.redefine.rabbitmq.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;

/**
 * 用于发送消息确认
 *
 * @author QIANG
 */
public class RedefineConfirmCallback implements ConfirmCallback {

    private Logger log = LoggerFactory.getLogger(RedefineConfirmCallback.class);



    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {


        log.debug("MQ监控消息发送确认, MSGID:{},ACK:{},cause:{}",  correlationData.getId(), ack,cause);




    }

}
