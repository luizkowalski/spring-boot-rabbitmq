package com.inkdrop.routers;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.inkdrop.domain.models.PrivateMessage;
import com.inkdrop.domain.presenters.PrivateMessagePresenter;
import com.rabbitmq.client.Channel;

@Component
public class PrivateMessageRouter {

	private static final String USER_DIRECT_EXCHANGE = "user-direct-exchange";

	@Autowired
	RabbitTemplate template;

	@Autowired
	private SimpMessagingTemplate webSocket;

	private Logger log = LogManager.getLogger(PrivateMessageRouter.class);

	private RabbitAdmin admin;
	private ConnectionFactory cf;

	private void addMessageListener(Queue q) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(cf);

		container.setConnectionFactory(cf);
		container.setQueueNames(q.getName());
		container.setMessageListener(new MessageListenerAdapter() {
			@Override
			public void onMessage(org.springframework.amqp.core.Message message, Channel channel) throws Exception {
				log.info(message);
				MessageProperties props = message.getMessageProperties();
				String user = "/".concat(props.getReceivedRoutingKey());
				webSocket.convertAndSend(user, new String(message.getBody()));
			}
		});

		if (!container.isRunning())
			container.start();
	}

	private void createQueue(String name) {
		Queue q = new Queue(name, false, false, true);
		DirectExchange roomExchange = new DirectExchange(USER_DIRECT_EXCHANGE, false, true);

		admin.declareExchange(roomExchange);

		admin.declareQueue(q);

		admin.declareBinding(BindingBuilder.bind(q).to(roomExchange).with(name));

		addMessageListener(q);
	}

	private String getQueueName(PrivateMessage message) {
		return "pm." + message.getTo().getUid();
	}

	private void initializeConfigurations() {
		cf = template.getConnectionFactory();
		admin = new RabbitAdmin(cf);
	}

	private boolean queueExists(String queue) {
		return admin.getQueueProperties(queue) != null;
	}

	public void sendMessageToUser(PrivateMessage message) {
		initializeConfigurations();

		String queueName = getQueueName(message);
		if (!queueExists(queueName)) {
			log.info("Queue is null, creating!");
			createQueue(queueName);
		}

		String messageJson = new PrivateMessagePresenter(message).toJson();
		template.convertAndSend(USER_DIRECT_EXCHANGE, queueName, messageJson);
	}
}
