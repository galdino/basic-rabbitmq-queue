package br.com.basicqueue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class CreateQueues {

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		Connection connection = connectionFactory.newConnection(CommonConfigs.AMQP_URL);
		Channel channel = connection.createChannel();
		
		channel.queueDeclare("MobileQ", true, false, false, null);
		channel.queueDeclare("ACQ", true, false, false, null);
		channel.queueDeclare("LightQ", true, false, false, null);
		
		channel.close();
		connection.close();

	}

}
