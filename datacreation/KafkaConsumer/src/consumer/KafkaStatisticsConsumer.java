package consumer;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.Message;
import kafka.message.MessageAndMetadata;

import com.google.common.collect.ImmutableMap;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.util.JSON;

public class KafkaStatisticsConsumer {

	private final static KafkaStatisticsConsumer INSTANCE = new KafkaStatisticsConsumer();
	private ConsumerConnector consumerConnector;

	public static KafkaStatisticsConsumer instance() {
		return INSTANCE;
	}

	public void init() {
		// specify some consumer properties
		Properties props = new Properties();
		props.put("zk.connect", "localhost:2181");
		props.put("zk.connectiontimeout.ms", "1000000");
		props.put("groupid", "statistics");

		// Create the connection to the cluster
		ConsumerConfig consumerConfig = new ConsumerConfig(props);
		consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
	}

	public void start() {

		Map<String, List<KafkaStream<Message>>> topicMessageStreams = consumerConnector.createMessageStreams(ImmutableMap.of("user_messages", 1));
		List<KafkaStream<Message>> streams = topicMessageStreams.get("user_messages");

		// create list of 4 threads to consume from each of the partitions
		ExecutorService executor = Executors.newFixedThreadPool(1);

		// consume the messages in the threads
		for (final KafkaStream<Message> stream : streams) {
			executor.submit(new Runnable() {
				public void run() {
					for (MessageAndMetadata<Message> msgAndMetadata : stream) {
						createStatisticForData(msgAndMetadata);
					}
				}
			});
		}
	}

	private final void createStatisticForData(MessageAndMetadata<Message> data) {

		int event_size = data.message().serializedSize();

		BasicDBObject messageAsJson = convertToJsonObject(data);

		String user = (String) messageAsJson.get("user");
		String requestId = (String) messageAsJson.get("requestId");
		String timeStamp = (String) messageAsJson.get("timeStamp");

		String type = (String) messageAsJson.get("type");

		DBCollection collection = DB.getDb().getCollection("user_statistics");

		String dbStatisticId = "request_" + user + "_" + requestId;

		BasicDBObject dbStatistics = loadStatisticsMapFromDB(user, requestId, timeStamp, collection, dbStatisticId);

		dbStatistics.put("overall_size", (Long.parseLong(dbStatistics.getString("overall_size")) + event_size) + "");
		dbStatistics.put("message_count", (Long.parseLong(dbStatistics.getString("message_count")) + 1) + "");
		dbStatistics.put("end_time", timeStamp);
		dbStatistics.put("duration", (Long.parseLong(dbStatistics.getString("end_time")) - Long.parseLong(dbStatistics.getString("start_time"))) / 1000 + "");
		dbStatistics.put("average_size", (Long.parseLong(dbStatistics.getString("overall_size")) / Long.parseLong(dbStatistics.getString("message_count")))
				+ "");
		dbStatistics.put("average_message_second",
				(Long.parseLong(dbStatistics.getString("message_count")) / Math.max(1, Long.parseLong(dbStatistics.getString("duration")))) + "");
		dbStatistics.put("average_bits_second",
				(Long.parseLong(dbStatistics.getString("overall_size")) / Math.max(1, Long.parseLong(dbStatistics.getString("duration")))) + "");

		Map<String, Integer> typeCount = (Map<String, Integer>) dbStatistics.get("type_count");
		int count = (typeCount.containsKey(type)) ? typeCount.get(type) + 1 : 1;
		typeCount.put(type, count);

		collection.save(dbStatistics);
	}

	private BasicDBObject loadStatisticsMapFromDB(String user, String requestId, String timeStamp, DBCollection collection, String dbStatisticId) {

		BasicDBObject findOne = (BasicDBObject) collection.findOne(dbStatisticId);

		Map<String, Object> initMap = new HashMap<>();
		initMap.put("_id", dbStatisticId);
		initMap.put("user", user);
		initMap.put("requestId", requestId);
		initMap.put("overall_size", "0");
		initMap.put("message_count", "0");
		initMap.put("average_size", "0");
		initMap.put("start_time", timeStamp);
		initMap.put("end_time", "0");
		initMap.put("average_message_second", "0");
		initMap.put("average_bits_second", "0");
		initMap.put("duration", "0");
		initMap.put("type_count", new HashMap<String, Integer>());

		return (BasicDBObject) ((findOne == null) ? new BasicDBObject(initMap) : findOne);
	}

	private BasicDBObject convertToJsonObject(MessageAndMetadata<Message> data) {
		Message message = data.message();
		ByteBuffer buffer = message.payload();
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		String body = new String(bytes);
		BasicDBObject json = (BasicDBObject) JSON.parse(body);
		return json;
	}

}
