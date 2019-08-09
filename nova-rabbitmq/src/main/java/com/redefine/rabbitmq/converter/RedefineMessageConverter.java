package com.redefine.rabbitmq.converter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
/**
 * @author QIANGLU
 */
public class RedefineMessageConverter extends SimpleMessageConverter {

	private MessageConverter messageConverter;

	private String json = "json";
	@Override
	public Object fromMessage(Message message) throws MessageConversionException {
		Object context = null;
		MessageProperties properties = message.getMessageProperties();
		if (properties != null) {
			String contentType = properties.getContentType();
			if (messageConverter != null && contentType != null && contentType.contains(json)) {
				context = messageConverter.fromMessage(message);
			} else {
				context = super.fromMessage(message);
			}
		}
		return context;
	}

	public MessageConverter getMessageConverter() {
		return messageConverter;
	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

}
