package controllers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static play.test.Helpers.PUT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.routeAndCall;
import static play.test.Helpers.running;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import play.libs.Json;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import database.DB;

public class ApplicationTest {

	@Test
	public void applicationsAddsProblemToMongoDb() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				
				com.mongodb.DB dbMock = mock(com.mongodb.DB.class);
				DBCollection dbCollection = mock(DBCollection.class);
				when(dbMock.getCollection("events")).thenReturn(dbCollection);
				
				DB.setDatabase(dbMock);
				
				String jsonString = "{\"problems\" :  [ { \"question\":\"Wer hat gesagt &bdquo;Die Sicherheit der Bundesrepublik Deutschland wird auch am Hindukusch verteidigt&ldquo;?\"," +
						"\"options\" : [\"Hamid Karzai\",  \"Peter Struck\",\"Franz Josef Jung\",\"Karl-Theodor zu Guttenberg\",\"Che Guevara\",\"Angela Merkel\"]," +
						"\"answer\" : \"Peter Struck\" }] }";
				JsonNode jsonNode = Json
						.parse(jsonString);
				
				routeAndCall(fakeRequest(PUT,"/problems").withJsonBody(jsonNode));
				
				verify(dbCollection).save((DBObject) JSON.parse(jsonString));
			}
		});
	}

}
