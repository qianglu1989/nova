package com.redefine.rabbitmq.listener;

import com.redefine.rabbitmq.utils.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.exception.ListenerExecutionFailedException;
import org.springframework.util.ErrorHandler;

/**
 * 消息消费出现异常处理
 *
 * @author QIANG
 */
public class RedefineErrorHandler implements ErrorHandler {

    private static Logger LOG = LoggerFactory.getLogger(RedefineErrorHandler.class);



    @Override
    public void handleError(Throwable t) {
        ListenerExecutionFailedException exception = (ListenerExecutionFailedException) t;
        Message message = exception.getFailedMessage();
        MessageProperties messageProperties = message.getMessageProperties();
        String body = MessageUtils.getBodyContentAsString(message.getBody(), messageProperties);
        LOG.error("消息接收失败状态提示,MSGID:{},消息体:[{}],异常信息:{}", messageProperties.getMessageId(), body,
                MessageUtils.getStackTraceAsString(t));

    }





}
