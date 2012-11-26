package controllers;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.javaapi.producer.ProducerData;
import kafka.producer.ProducerConfig;


public class KafkaProducer {

	public final static KafkaProducer INSTANCE = new KafkaProducer();
	private Producer<String, String> producer;
	
	
	public static KafkaProducer instance() {
		return INSTANCE;
	}
	
	
	public void init() {
		Properties props = new Properties();
		props.put("zk.connect", "127.0.0.1:2181");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
//		props.put("producer.type", "async");
//		props.put("compression.codec", "1");
		ProducerConfig config = new ProducerConfig(props);
		producer = new Producer<String, String>(config);
	}
	
	public void sendMessageToTopic(String topic, String message) {
		ProducerData<String, String> data = new ProducerData<String, String>(topic, message);
		producer.send(data);	
	}
	
	public void close() {
		producer.close();
	}
	
}
