package DataHandler;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Json {
	
	Data d;
	JSONObject obj;
	

	public Json(Data d) {
		this.d = d;
		this.obj = new JSONObject();
	}



	@SuppressWarnings("unchecked")
	public void createJSON() {
			obj.put("code", d.getCode());
		    obj.put("username", d.getUser());
		    obj.put("text", d.getText());

	    
	}
	
	public String jsonToString() {
		
		StringBuilder sb = new StringBuilder();
	      sb.append("{\n");
		sb.append(" 'code':"+obj.get("code")+",\n");
		sb.append(" 'username':"+"'"+obj.get("username")+"',\n");
		sb.append(" 'text':"+"'"+obj.get("text")+"',\n");
		sb.append("}\n");
	      
	      return sb.toString();
	      


	}
	
	public JSONObject stringToObjJSON(String text) {
		JSONParser parser = new JSONParser();
		
		JSONObject newJObject = null;
		try {
			newJObject = (JSONObject) parser.parse(text);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return newJObject;
	}
	
	
	public String toString() {
		return obj.toString();
	}



	public JSONObject getObj() {
		return obj;
	}
	


}
