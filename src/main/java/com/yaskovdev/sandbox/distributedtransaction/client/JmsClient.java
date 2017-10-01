package com.yaskovdev.sandbox.distributedtransaction.client;

import com.yaskovdev.sandbox.distributedtransaction.exception.CannotSendNotificationException;
import com.yaskovdev.sandbox.distributedtransaction.model.Notification;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import static java.lang.Thread.currentThread;
import static javax.jms.DeliveryMode.NON_PERSISTENT;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class JmsClient {

    private static final Logger logger = getLogger(JmsClient.class);

    private final String brokerUrl;

    private final String queueName;

    public JmsClient(@Value("${jms.broker.url}") final String brokerUrl,
            @Value("${jms.queue.name}") final String queueName) {
        this.brokerUrl = brokerUrl;
        this.queueName = queueName;
    }

    public void sendNotification(final Notification notification) {
        try {
            final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

            final Connection connection = connectionFactory.createConnection();
            connection.start();

            final Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);

            final Destination destination = session.createQueue(queueName);

            final MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(NON_PERSISTENT);

            final String text = "Got " + notification + " from: " + currentThread().getName() + " : " + hashCode();
            final TextMessage message = session.createTextMessage(text);

            logger.info("Sent message: " + message.hashCode() + " : " + currentThread().getName());
            producer.send(message);

            session.close();
            connection.close();
        } catch (final JMSException e) {
            throw new CannotSendNotificationException(e);
        }
    }
}
