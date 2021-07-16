package br.com.basicqueue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class TestConsumer {

	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		Connection connection = connectionFactory.newConnection(CommonConfigs.AMQP_URL);
		Channel channel = connection.createChannel();
		
		DeliverCallback deliverCallback = (ct, m) -> {
			System.out.println(ct);
			System.out.println(new String(m.getBody()));
		};
		
		CancelCallback cancelCallback = System.out::println;
		
		channel.basicConsume("LightQ", true, deliverCallback, cancelCallback);
		
	}

}
