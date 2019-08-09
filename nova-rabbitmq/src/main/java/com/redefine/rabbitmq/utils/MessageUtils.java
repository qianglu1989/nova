package com.redefine.rabbitmq.utils;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.utils.SerializationUtils;

/**
 * @author QIANGLU
 */
public class MessageUtils {

    public static String getBodyContentAsString(byte[] body, MessageProperties messageProperties) {
        if (body == null) {
            return null;
        }
        try {
            String contentType = (messageProperties != null) ? messageProperties.getContentType() : null;
            if (MessageProperties.CONTENT_TYPE_SERIALIZED_OBJECT.equals(contentType)) {
                return SerializationUtils.deserialize(body).toString();
            }
            if (MessageProperties.CONTENT_TYPE_TEXT_PLAIN.equals(contentType)
                    || MessageProperties.CONTENT_TYPE_JSON.equals(contentType)
                    || MessageProperties.CONTENT_TYPE_JSON_ALT.equals(contentType)
                    || MessageProperties.CONTENT_TYPE_XML.equals(contentType)) {
                return new String(body, "UTF-8");
            }
        } catch (Exception e) {
        }
        return body.toString() + "(byte[" + body.length + "])";
    }

    public static String getStackTraceAsString(Throwable t) {
        StringBuilder builder = new StringBuilder();
        builder.append("MESSAGE:").append(t.getMessage()).append(" CAUSE:").append(t.getCause());
        return builder.toString();

    }
}
