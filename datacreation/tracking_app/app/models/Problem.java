package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mongodb.BasicDBObject;

public class Problem {
	
	public enum InputType {
		CHECKBOX,
		INLINE_ANSWER,
		INTEXT_ANSWER,
		RADIO,
		SIMPLE_ANSWER;
	}
	
	public static List<Problem> createFromDbObjects(List<BasicDBObject> dbObjects) {
		
		List<Problem> problems = new ArrayList<>();
		for (BasicDBObject object : dbObjects) {
			problems.add(createFromDbObject(object));
		}
		return problems;
	}
	
	public static Problem createFromDbObject(BasicDBObject dbObject) {
		
		String question = dbObject.getString("question");
		@SuppressWarnings("unchecked")
		List<String> answer = (List<String>) dbObject.get("answer");
		@SuppressWarnings("unchecked")
		List<String> options = (List<String>) ((dbObject.get("options")!=null) ? dbObject.get("options") : Collections.<String>emptyList());
		InputType inputType = InputType.valueOf(dbObject.getString("type")); 
		
		return new Problem(question,answer,inputType,options);
	}
	
	public Problem(String question, List<String> answer, InputType type,
			List<String> options) {
		super();
		this.question = question;
		this.answer = answer;
		this.type = type;
		this.options = new ArrayList<>(options);
	}



	private final String question;
	
	private final List<String> answer;
	
	private final InputType type;
	
	private final  List<String> options;

	public String getQuestion() {
		return question;
	}

	public List<String> getAnswers() {
		return answer;
	}

	public InputType getType() {
		return type;
	}

	public List<String> getOptions() {
		return options;
	}
	
}
