package com.redefine.rabbitmq.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;

/**
 * 处理消息前处理
 * 
 * @author QIANG
 *
 */
public class RedefineMessagePostProcessor implements MessagePostProcessor {

	private static  Logger  LOG = LoggerFactory.getLogger(RedefineMessagePostProcessor.class);


	@Override
	public Message postProcessMessage(Message message) throws AmqpException {

		MessageProperties messageProperties = message.getMessageProperties();
		LOG.debug("消息接收状态提示,MSGID:{}", messageProperties.getMessageId());



		return message;
	}

}
