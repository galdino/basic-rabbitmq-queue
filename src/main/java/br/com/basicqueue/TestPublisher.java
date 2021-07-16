package br.com.basicqueue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class TestPublisher {

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		
		try (Connection connection = connectionFactory.newConnection(CommonConfigs.AMQP_URL);
			 Channel channel = connection.createChannel()) {
			
			String message = "Turn on home appliances";
			channel.basicPublish("my-direct-exchange", "homeAppliance", null, message.getBytes());
			
		}
	}

}
