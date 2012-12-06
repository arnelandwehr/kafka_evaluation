package consumer;

import java.net.UnknownHostException;

import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			Mongo mongo = new Mongo();
			com.mongodb.DB db = mongo.getDB("kafka_messages");
			db.setWriteConcern(WriteConcern.SAFE);
			DB.setDatabase(db);
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		}


		KafkaStatisticsConsumer.instance().init();
		KafkaStatisticsConsumer.instance().start();
		
	}

}
