package br.com.basicqueue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

public class TopicExchange {
	
	public static void declareExchange() throws IOException, TimeoutException {
		try(Channel channel = ConnectionManager.getConnection().createChannel()){
			channel.exchangeDeclare("my-topic-exchange", BuiltinExchangeType.TOPIC, true);
		}
	}
	
	public static void declareQueues() throws IOException, TimeoutException {
		try(Channel channel = ConnectionManager.getConnection().createChannel()){
			channel.queueDeclare("HealthQ", true, false, false, null);
			channel.queueDeclare("SportsQ", true, false, false, null);
			channel.queueDeclare("EducationQ", true, false, false, null);
		}
	}
	
	public static void declareBindings() throws IOException, TimeoutException {
		try(Channel channel = ConnectionManager.getConnection().createChannel()){
			channel.queueBind("HealthQ", "my-topic-exchange", "health.*");
			channel.queueBind("SportsQ", "my-topic-exchange", "#.sports.*");
			channel.queueBind("EducationQ", "my-topic-exchange", "#.education");
		}
	}
	
	public static void subscribeMessage() throws IOException {
		Channel channel = ConnectionManager.getConnection().createChannel();
		channel.basicConsume("HealthQ", true, (ct,m) -> {
											   	System.out.println("\n\n=========== Health Queue ==========");
											   	System.out.println(ct);
											   	System.out.println("HealthQ: " + new String(m.getBody()));
											   	System.out.println(m.getEnvelope());
											  }, 
											  System.out::println);
		channel.basicConsume("SportsQ", true, (ct,m) -> {
												System.out.println("\n\n=========== Sports Queue ==========");
												System.out.println(ct);
												System.out.println("SportsQ: " + new String(m.getBody()));
												System.out.println(m.getEnvelope());
											  }, 
												System.out::println);
		channel.basicConsume("EducationQ", true, (ct,m) -> {
												System.out.println("\n\n=========== Education Queue ==========");
												System.out.println(ct);
												System.out.println("EducationQ: " + new String(m.getBody()));
												System.out.println(m.getEnvelope());
											}, 
												System.out::println);
	}
	
	public static void publishMessage() throws IOException, TimeoutException {
		try(Channel channel = ConnectionManager.getConnection().createChannel()){
			String message = "Drink a lot of water and stay healthy!";
			channel.basicPublish("my-topic-exchange", "health.education", null, message.getBytes());
			
			message = "Learn something new everyday.";
			channel.basicPublish("my-topic-exchange", "education", null, message.getBytes());
			
			message = "Stay fit in mind and body.";
			channel.basicPublish("my-topic-exchange", "education.health", null, message.getBytes());
		}
	}

	public static void main(String[] args) throws IOException, TimeoutException {
		TopicExchange.declareExchange();
		TopicExchange.declareQueues();
		TopicExchange.declareBindings();
		
		Thread subscribe = new Thread(() -> {
										try {
											TopicExchange.subscribeMessage();
										} catch (IOException e) {
											e.printStackTrace();
										}
									 });
		
		Thread publish = new Thread(() -> {
										try {
											TopicExchange.publishMessage();
										} catch (IOException | TimeoutException e) {
											e.printStackTrace();
										}
								   });
		
		subscribe.start();
		publish.start();
	}

}
