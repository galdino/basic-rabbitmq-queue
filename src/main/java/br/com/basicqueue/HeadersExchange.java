package br.com.basicqueue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

public class HeadersExchange {
	
	public static void declareExchange() throws IOException, TimeoutException {
		try(Channel channel = ConnectionManager.getConnection().createChannel()){
			channel.exchangeDeclare("my-header-exchange", BuiltinExchangeType.HEADERS, true);
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
			Map<String, Object> healthArgs = new HashMap<String, Object>();
			healthArgs.put("x-match", "any");
			healthArgs.put("h1", "Header1");
			healthArgs.put("h2", "Header2");
			
			channel.queueBind("HealthQ", "my-header-exchange", "", healthArgs);
			
			Map<String, Object> sportArgs = new HashMap<String, Object>();
			sportArgs.put("x-match", "all");
			sportArgs.put("h1", "Header1");
			sportArgs.put("h2", "Header2");
			
			channel.queueBind("SportsQ", "my-header-exchange", "", sportArgs);
			
			Map<String, Object> educationArgs = new HashMap<String, Object>();
			educationArgs.put("x-match", "any");
			educationArgs.put("h1", "Header1");
			educationArgs.put("h2", "Header2");
			
			channel.queueBind("EducationQ", "my-header-exchange", "", educationArgs);
		}
	}
	
	public static void subscribeMessage() throws IOException {
		Channel channel = ConnectionManager.getConnection().createChannel();
		channel.basicConsume("HealthQ", true, (ct, m) -> {
												System.out.println("\n\n=========== Health Queue ==========");
												System.out.println(ct);
												System.out.println("HealthQ: " + new String(m.getBody()));
												System.out.println(m.getEnvelope());
											  }, 
											  System.out::println);
		channel.basicConsume("SportsQ", true, (ct, m) -> {
												System.out.println("\n\n=========== Sports Queue ==========");
												System.out.println(ct);
												System.out.println("SportsQ: " + new String(m.getBody()));
												System.out.println(m.getEnvelope());
											  }, 
											  System.out::println);
		channel.basicConsume("EducationQ", true, (ct, m) -> {
												System.out.println("\n\n=========== Education Queue ==========");
												System.out.println(ct);
												System.out.println("EducationQ: " + new String(m.getBody()));
												System.out.println(m.getEnvelope());
											  }, 
											  System.out::println);
	}
	
	public static void publishMessage() throws IOException, TimeoutException {
		try(Channel channel = ConnectionManager.getConnection().createChannel()){
			String message = "Header Exchange example 1";
			Map<String,Object> headerMap = new HashMap<>();
			headerMap.put("h1", "Header1");
			headerMap.put("h3", "Header3");
			BasicProperties properties = new BasicProperties().builder().headers(headerMap).build();
			channel.basicPublish("my-header-exchange", "", properties, message.getBytes());
			
			message = "Header Exchange example 2";
			headerMap.put("h2", "Header2");
			properties = new BasicProperties().builder().headers(headerMap).build();
			channel.basicPublish("my-header-exchange", "", properties, message.getBytes());
		}
		
	}

	public static void main(String[] args) throws IOException, TimeoutException {
		HeadersExchange.declareExchange();
		HeadersExchange.declareQueues();
		HeadersExchange.declareBindings();
		
		Thread subscribe = new Thread(() -> {
							try {
								HeadersExchange.subscribeMessage();
							} catch (IOException e) {
								e.printStackTrace();
							}
				   		 });
		
		Thread publish = new Thread(() -> {
							try {
								HeadersExchange.publishMessage();
							} catch (IOException | TimeoutException e) {
								e.printStackTrace();
							}
						 });
		
		subscribe.start();
		publish.start();
	}

}
