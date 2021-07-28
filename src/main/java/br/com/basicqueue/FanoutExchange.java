package br.com.basicqueue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

public class FanoutExchange {
	
	public static void declareExchange() throws IOException, TimeoutException {
		try (Channel channel = ConnectionManager.getConnection().createChannel()){
			channel.exchangeDeclare("my-fanout-exchange", BuiltinExchangeType.FANOUT, true);
		}
	}
	
	public static void declareQueues() throws IOException, TimeoutException {
		try(Channel channel = ConnectionManager.getConnection().createChannel()){
			channel.queueDeclare("MobileQF", true, false, false, null);
			channel.queueDeclare("ACQF", true, false, false, null);
			channel.queueDeclare("LightQF", true, false, false, null);
		}
	}
	
	public static void declareBindings() throws IOException, TimeoutException {
		try(Channel channel = ConnectionManager.getConnection().createChannel()){
			channel.queueBind("MobileQF", "my-fanout-exchange", "");
			channel.queueBind("ACQF", "my-fanout-exchange", "");
			channel.queueBind("LightQF", "my-fanout-exchange", "");
		}
	}
	
	public static void subscribeMessage() throws IOException, TimeoutException {
		Channel channel = ConnectionManager.getConnection().createChannel();
			channel.basicConsume("MobileQF", true, 
											((ct, m)->{
												System.out.println(ct);
												System.out.println("MobileQF: " + new String(m.getBody()));
											 }), 
											System.out::println);
			
			channel.basicConsume("ACQF", true, 
										((ct, m)->{
											System.out.println(ct);
											System.out.println("ACQF: " + new String(m.getBody()));
										}), 
										System.out::println);
			
			channel.basicConsume("LightQF", true, 
											((ct, m)->{
												System.out.println(ct);
												System.out.println("LightQF: " + new String(m.getBody()));
											}), 
											System.out::println);
	}
	
	public static void publishMessage() throws IOException, TimeoutException {
		try(Channel channel = ConnectionManager.getConnection().createChannel()){
			String message = "Main power is ON";
			channel.basicPublish("my-fanout-exchange", "", null, message.getBytes());
		}
	}

	public static void main(String[] args) throws IOException, TimeoutException {
		FanoutExchange.declareQueues();
		FanoutExchange.declareExchange();
		FanoutExchange.declareBindings();
		
		Thread subscribe = new Thread() {
			public void run() {
				try {
					FanoutExchange.subscribeMessage();
				} catch (IOException | TimeoutException e) {
					e.printStackTrace();
				}
			};
		};
		
		Thread publish = new Thread() {
			public void run() {
				try {
					FanoutExchange.publishMessage();;
				} catch (IOException | TimeoutException e) {
					e.printStackTrace();
				}
			};
		};
		
		subscribe.start();
		publish.start();
		
	}

}
