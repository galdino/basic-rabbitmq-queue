package br.com.basicqueue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MessagePublisher {

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		Connection connection = connectionFactory.newConnection(CommonConfigs.AMQP_URL);
		Channel channel = connection.createChannel();
		
		for (int i = 1; i <= 4; i++) {
			String message = "Getting started with RabbitMQ" + i;
			channel.basicPublish("", CommonConfigs.DEFAULT_QUEUE, null, message.getBytes());			
		}
		
		channel.close();
		connection.close();
	}

}
