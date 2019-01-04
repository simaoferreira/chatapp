package DataHandler;
import org.json.simple.JSONObject;

public class TesteJson {

	public static void main(String[] args) {
		Data d =new Data("0","simon","olaa,tudo bem?");
		Json json = new Json(d);
		json.createJSON();
		
		String jsonString = json.toString();
		
		JSONObject newJObject = json.stringToObjJSON(jsonString);
		
		System.out.println(newJObject);
		System.out.println(newJObject.get("username"));
		
	}

}
