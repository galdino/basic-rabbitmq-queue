package br.com.basicqueue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class CreateBindings {

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		Connection connection = connectionFactory.newConnection(CommonConfigs.AMQP_URL);
		try (Channel channel = connection.createChannel()) {
			channel.queueBind("MobileQ", "my-direct-exchange", "personalDevice");
			channel.queueBind("ACQ", "my-direct-exchange", "homeAppliance");
			channel.queueBind("LightQ", "my-direct-exchange", "homeAppliance");
		}
		
		connection.close();
		
	}

}
