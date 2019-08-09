package com.redefine.rabbitmq.factory;

import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

/**
 * 扩展原生Factory，增加消息前处理
 * @author QIANG
 *
 */
public class RedefineSimpleRabbitListenerContainerFactory extends SimpleRabbitListenerContainerFactory {

	private MessagePostProcessor afterReceivePostProcessors;

	protected void initializeContainer(SimpleMessageListenerContainer instance) {
		if (afterReceivePostProcessors != null) {
			instance.setAfterReceivePostProcessors(afterReceivePostProcessors);
		}
	}

	public MessagePostProcessor getAfterReceivePostProcessors() {
		return afterReceivePostProcessors;
	}

	/**
	 * Set {@link MessagePostProcessor}s that will be applied after message
	 * reception, before invoking the {@link MessageListener}. Often used to
	 * decompress data. Processors are invoked in order, depending on
	 * {@code PriorityOrder}, {@code Order} and finally unordered.
	 * 
	 * @param afterReceivePostProcessors
	 *            the post processor.
	 * @since 1.4.2
	 */
	public void setAfterReceivePostProcessors(MessagePostProcessor afterReceivePostProcessors) {
		this.afterReceivePostProcessors = afterReceivePostProcessors;
	}

}
