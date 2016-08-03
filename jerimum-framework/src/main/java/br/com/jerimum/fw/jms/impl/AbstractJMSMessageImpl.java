package br.com.jerimum.fw.jms.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.core.JmsTemplate;

import br.com.jerimum.fw.exception.MessageException;
import br.com.jerimum.fw.jms.JMSMessage;
import br.com.jerimum.fw.jms.JMSMessageCreator;
import br.com.jerimum.fw.logging.LoggerUtils;

/**
 * 
 * @author Dali Freire - dalifreire@gmail.com
 */
public abstract class AbstractJMSMessageImpl implements JMSMessage, InitializingBean {

    protected JmsTemplate jmsTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {

        LoggerUtils.logDebug(AbstractJMSMessageImpl.class, "Starting JMS message with JMSConnectionFactory: {}",
            getConnectionFactory());

        this.jmsTemplate = new JmsTemplate(getConnectionFactory());
        this.jmsTemplate.setDeliveryPersistent(false);
        this.jmsTemplate.setDeliveryMode(1);
        setTimeOut(getTimeout());

        LoggerUtils.logDebug(AbstractJMSMessageImpl.class, "JMSTemplate created: {}", this.jmsTemplate);
    }

    /**
     * Sets the timeout for receive messages.
     * 
     * @param timeOut
     */
    public void setTimeOut(long timeOut) {
        this.jmsTemplate.setReceiveTimeout(timeOut);
    }

    @Override
    public String sendAndReceive(String message) throws MessageException {

        if (StringUtils.isEmpty(message)) {
            throw new MessageException("Message cannot be null!");
        }

        try {

            /*
             * sends the message
             */
            Message messageSent = sendTextMessage(message);

            /*
             * receives the response
             */
            String messageSelector = String.format("JMSCorrelationID='%s'", messageSent.getJMSMessageID());
            return receiveTextMessage(messageSelector);

        } catch (MessageException e) {
            LoggerUtils.logError(AbstractJMSMessageImpl.class, "Unable to send/receive message!", e);
            throw e;
        } catch (Exception e) {
            LoggerUtils.logError(AbstractJMSMessageImpl.class, "Unable to send/receive message!", e);
            throw new MessageException(e);
        }
    }

    @Override
    public String sendAndReceive(JMSMessageCreator<TextMessage> messageCreator) throws MessageException {

        if (messageCreator == null) {
            throw new MessageException("Message cannot be null!");
        }

        try {

            /*
             * sends the message
             */
            Message messageSent = sendMessage(messageCreator, getWriteQueue());

            /*
             * receives the response
             */
            String messageSelector = String.format("JMSCorrelationID='%s'", messageSent.getJMSMessageID());
            return receiveTextMessage(messageSelector);

        } catch (MessageException e) {
            LoggerUtils.logError(AbstractJMSMessageImpl.class, "Unable to send/receive message!", e);
            throw e;
        } catch (Exception e) {
            LoggerUtils.logError(AbstractJMSMessageImpl.class, "Unable to send/receive message!", e);
            throw new MessageException(e);
        }
    }

    @Override
    public TextMessage sendTextMessage(final String msg) throws MessageException {

        return sendTextMessage(msg, getWriteQueue());
    }

    @Override
    public TextMessage sendTextMessage(final String msg, final String correlationID) throws MessageException {

        try {

            JMSMessageCreator<TextMessage> messageCreator = new JMSMessageCreator<TextMessage>() {
                @Override
                public void setParams(TextMessage message) throws JMSException {
                    message.setText(msg);
                    message.setJMSCorrelationID(correlationID);
                }
            };

            return (TextMessage) sendMessage(messageCreator, getWriteQueue());

        } catch (Exception e) {
            throw new MessageException(e);
        }

    }

    @Override
    public TextMessage sendTextMessage(final String msg, Queue queue) throws MessageException {

        try {

            JMSMessageCreator<TextMessage> messageCreator = new JMSMessageCreator<TextMessage>() {
                @Override
                public void setParams(TextMessage message) throws JMSException {
                    message.setText(msg);
                }
            };

            return (TextMessage) sendMessage(messageCreator, queue);

        } catch (Exception e) {
            throw new MessageException(e);
        }
    }

    @Override
    public Message sendMessage(JMSMessageCreator<?> messageCreator) throws MessageException {

        return sendMessage(messageCreator, getWriteQueue());
    }

    @Override
    public Message sendMessage(JMSMessageCreator<?> messageCreator, Queue queue) throws MessageException {

        if (queue == null) {
            throw new MessageException("Queue cannot be null!");
        }

        try {

            LoggerUtils.logDebug(AbstractJMSMessageImpl.class, "Sending message to queue '{}'...", queue);
            jmsTemplate.send(queue, messageCreator);
            LoggerUtils.logDebug(AbstractJMSMessageImpl.class, "Message '{}' sent!", messageCreator.getMessage());

            return messageCreator.getMessage();

        } catch (Exception e) {
            throw new MessageException(e);
        }
    }

    @Override
    public Message receiveMessage(String messageSelector) throws MessageException {

        return receiveMessage(messageSelector, getReadQueue());
    }

    @Override
    public Message receiveMessage(String messageSelector, Queue queue) throws MessageException {

        if (messageSelector == null) {
            throw new MessageException("MessageSelector cannot be null!");
        }

        if (queue == null) {
            throw new MessageException("Queue cannot be null!");
        }

        try {

            LoggerUtils.logDebug(AbstractJMSMessageImpl.class, "Selecting message using: {}", messageSelector);

            Message resMessage = null;
            long finishTime = System.currentTimeMillis() + getTimeout();
            while (resMessage == null && System.currentTimeMillis() < finishTime) {

                resMessage = this.jmsTemplate.receiveSelected(queue.getQueueName(), messageSelector);
            }

            LoggerUtils.logTrace(AbstractJMSMessageImpl.class, "Received message: '{}'", resMessage);

            return resMessage;

        } catch (Exception e) {
            throw new MessageException(e);
        }
    }

    /**
     * Recupera a mensagem da fila de reponse de acordo com o messageSelector passado como
     * parametro.
     * 
     * @param messageSelector
     * @return String
     * @throws JMSException
     * @throws MessageException
     */
    public String receiveTextMessage(String messageSelector) throws MessageException {

        try {

            TextMessage receivedMessage = (TextMessage) receiveMessage(messageSelector);
            return receivedMessage != null ? receivedMessage.getText() : null;

        } catch (Exception e) {
            throw new MessageException(e);
        }
    }

    /**
     * Recupera multiplas mensagens da fila de reponse de acordo com o messageSelector passado como
     * parametro.
     * 
     * @param messageSelector
     * @param maxMsgCount
     * @param totalTimeOut
     * @return {@link List}<Message>
     * @throws MessageException
     */
    public List<Message> receiveMultipleMessages(String messageSelector, int maxMsgCount, long totalTimeOut)
        throws MessageException {

        if (messageSelector == null) {
            throw new MessageException("MessageSelector cannot be null!");
        }

        if (getReadQueue() == null) {
            throw new MessageException("Response queue cannot be null!");
        }

        LoggerUtils.logDebug(AbstractJMSMessageImpl.class, "Searching message by: {}", messageSelector);

        List<Message> mensagens = new ArrayList<Message>();

        long finishTime = System.currentTimeMillis() + totalTimeOut;
        while (mensagens.size() < maxMsgCount && System.currentTimeMillis() < finishTime) {

            Message message = receiveMessage(messageSelector);
            if (message != null) {
                mensagens.add(message);
            }
        }
        LoggerUtils.logDebug(AbstractJMSMessageImpl.class, "{} messages encountered: {}", mensagens.size(), mensagens);

        return mensagens;
    }

    /**
     * Recupera multiplas mensagens da fila de reponse de acordo com o messageSelector passado como
     * parametro.
     * 
     * @param selector
     * @param maxMsgCount
     * @param totalTimeOut
     * @return {@link List}<String>
     * @throws MessageException
     */
    public List<String> receiveMultipleTextMessages(String selector, int maxMsgCount, long totalTimeOut)
        throws MessageException {

        List<Message> messages = receiveMultipleMessages(selector, maxMsgCount, totalTimeOut);

        try {

            List<String> textMessages = new ArrayList<String>();
            for (Message response : messages) {
                TextMessage txtMsg = (TextMessage) response;
                textMessages.add(txtMsg.getText());
            }

            return textMessages;

        } catch (Exception e) {
            throw new MessageException(e);
        }
    }

}
