package com.redefine.rabbitmq.listener;

import com.redefine.rabbitmq.utils.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;

/**
 * 尝试接收N次失败回调,将放弃消息重试 进行数据转存
 * 
 * @author QIANG
 *
 */
public class RedefineMessageRecoverer implements MessageRecoverer {

	private static Logger LOG = LoggerFactory.getLogger(RedefineMessageRecoverer.class);



	@Override
	public void recover(Message message, Throwable cause) {
		MessageProperties messageProperties = message.getMessageProperties();
		String error = getStackTraceAsString(cause);
		String body = MessageUtils.getBodyContentAsString(message.getBody(), messageProperties);
		LOG.info("消息接收失败N次状态提示,MSGID:{},消息体:[{}],异常信息:[{}]", messageProperties.getMessageId(), body, error);


	}

	private String getStackTraceAsString(Throwable t) {
		StringBuilder builder = new StringBuilder();
		builder.append("MESSAGE:").append(t.getMessage()).append(" CAUSE:").append(t.getCause());
		return builder.toString();

	}


}
