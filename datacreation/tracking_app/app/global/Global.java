package global;
import java.net.UnknownHostException;

import play.Application;
import play.GlobalSettings;

import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

import controllers.KafkaProducer;
import database.DB;


public class Global extends GlobalSettings {

	@Override
	public void beforeStart(Application arg0) {
		
		try {
			Mongo mongo = new Mongo();
			com.mongodb.DB db = mongo.getDB("kafka_messages");
			db.setWriteConcern(WriteConcern.SAFE);
			DB.setDatabase(db);
		} catch (UnknownHostException | MongoException e) {
			e.printStackTrace();
		}
		
 		KafkaProducer.instance().init();
		
		super.beforeStart(arg0);
	}

	
	
}
