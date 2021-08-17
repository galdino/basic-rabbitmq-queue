package br.com.basicqueue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

public class ExchangeToExchange {
	
	public static void declareExchange() throws IOException, TimeoutException {
		try(Channel channel = ConnectionManager.getConnection().createChannel()){
			channel.exchangeDeclare("linked-direct-exchange", BuiltinExchangeType.DIRECT, true);
			channel.exchangeDeclare("home-direct-exchange", BuiltinExchangeType.DIRECT, true);
		}
	}
	
	public static void declareQueues() throws IOException, TimeoutException {
		try(Channel channel = ConnectionManager.getConnection().createChannel()){
			channel.queueDeclare("MobileQ", true, false, false, null);
			channel.queueDeclare("ACQ", true, false, false, null);
			channel.queueDeclare("LightQ", true, false, false, null);
			
			channel.queueDeclare("FanQ", true, false, false, null);
			channel.queueDeclare("LaptopQ", true, false, false, null);
		}
	}
	
	public static void declareQueueBindings() throws IOException, TimeoutException {
		try(Channel channel = ConnectionManager.getConnection().createChannel()){
			channel.queueBind("MobileQ", "linked-direct-exchange", "personalDevice");
			channel.queueBind("ACQ", "linked-direct-exchange", "homeAppliance");
			channel.queueBind("LightQ", "linked-direct-exchange", "homeAppliance");
			
			channel.queueBind("FanQ", "home-direct-exchange", "homeAppliance");
			channel.queueBind("LaptopQ", "home-direct-exchange", "personalDevice");
		}
	}
	
	public static void declareExchangesBinding() throws IOException, TimeoutException {
		try(Channel channel = ConnectionManager.getConnection().createChannel()){
			channel.exchangeBind("linked-direct-exchange", "home-direct-exchange", "homeAppliance");
		}
	}
	
	public static void subscribeMessage() throws IOException {
		Channel channel = ConnectionManager.getConnection().createChannel();
		channel.basicConsume("MobileQ", true, (consumerTag, message) -> {
																			System.out.println("\n\n" + message.getEnvelope());
																			System.out.println("MobileQ: " + new String(message.getBody()));					
																		}, 
											  System.out::println);
		channel.basicConsume("ACQ", true, (consumerTag, message) -> {
																		System.out.println("\n\n" + message.getEnvelope());
																		System.out.println("ACQ: " + new String(message.getBody()));					
																	}, 
										  System.out::println);
		channel.basicConsume("LightQ", true, (consumerTag, message) -> {
																			System.out.println("\n\n" + message.getEnvelope());
																			System.out.println("LightQ: " + new String(message.getBody()));					
																		}, 
										     System.out::println);
		channel.basicConsume("FanQ", true, (consumerTag, message) -> {
																			System.out.println("\n\n" + message.getEnvelope());
																			System.out.println("FanQ: " + new String(message.getBody()));					
																		}, 
										   System.out::println);
		channel.basicConsume("LaptopQ", true, (consumerTag, message) -> {
																			System.out.println("\n\n" + message.getEnvelope());
																			System.out.println("LaptopQ: " + new String(message.getBody()));					
																		}, 
										      System.out::println);
	}
	
	public static void publishMessage() throws IOException, TimeoutException {
		try(Channel channel = ConnectionManager.getConnection().createChannel()){
			String message = "Direct message - Turn on the home appliances";
			channel.basicPublish("home-direct-exchange", "homeAppliance", null, message.getBytes());
		}
	}

	public static void main(String[] args) throws IOException, TimeoutException {
		ExchangeToExchange.declareExchange();
		ExchangeToExchange.declareQueues();
		ExchangeToExchange.declareQueueBindings();
		ExchangeToExchange.declareExchangesBinding();
		
		new Thread(() -> {
				try {
					ExchangeToExchange.subscribeMessage();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}).start();
		
		new Thread(() -> {
				try {
					ExchangeToExchange.publishMessage();
				} catch (IOException | TimeoutException e) {
					e.printStackTrace();
				}
		}).start();
	}

}
