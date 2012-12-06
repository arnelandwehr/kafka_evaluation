package consumer;

public class DB {

	private static com.mongodb.DB db;

	public static void setDatabase(com.mongodb.DB db) {
		DB.db = db;
	}

	public static com.mongodb.DB getDb() {
		return db;
	}
	
}
