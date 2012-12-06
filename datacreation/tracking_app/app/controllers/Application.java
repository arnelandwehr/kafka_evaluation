package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import models.Problem;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.assessment;
import views.html.login;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import database.DB;

public class Application extends Controller {

	/**
	 * Login page.
	 */
	public static Result login() {
		return ok(login.render());
	}

	/**
	 * Handle login form submission.
	 */
	public static Result authenticate() {

		Map<String, String[]> body = request().body().asFormUrlEncoded();
		session("user", body.get("user")[0].replaceAll("\\s+", "_"));
		return redirect(routes.Application.getAssessmentPage());
	}

	/**
	 * Logout and clean the session.
	 */
	public static Result logout() {
		session().clear();
		return redirect(routes.Application.login());
	}

	@Security.Authenticated(Secured.class)
	public static Result putMessage() {
		
		String text = request().body().asText();
		BasicDBObject object = (BasicDBObject) JSON.parse(text);
		String user = session("user");
		String requestId = session("requestId");
		object.put("user", user);
		object.put("requestId", requestId);
		DBCollection dbCollection = DB.getDb().getCollection("messages_"+user);
		dbCollection.save(object);
		
		KafkaProducer.instance().sendMessageToTopic("user_messages", object.toString());
		
		return created();
	}

	public static Result putProblem() {
		String text = request().body().asText();
		BasicDBObject dbObject = (BasicDBObject) JSON.parse(text);
		BasicDBList dbList = (BasicDBList) dbObject.get("problems");
		
		DBCollection collection = DB.getDb().getCollection("problems");
		for (Object object : dbList) {
			collection.insert((BasicDBObject) object);
		}

		return created();
	}

	@Security.Authenticated(Secured.class)
	public static Result getAssessmentPage() {

		session("requestId", System.currentTimeMillis()+"");
		
		DBCollection collection = DB.getDb().getCollection("problems");
		DBCursor cursor = collection.find();

		List<BasicDBObject> objects = new ArrayList<>();
		for (DBObject dbObject : cursor) {
			objects.add((BasicDBObject) dbObject);
		}
		Collections.shuffle(objects);
		List<Problem> problems = Problem.createFromDbObjects(objects);
		
		return ok(assessment.render(problems));
	}

}