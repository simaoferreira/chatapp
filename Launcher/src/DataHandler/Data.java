package DataHandler;
import java.io.Serializable;

public class Data implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3043293281380733696L;
	/**
	 * 
	 */
	String code;
	String user;
	String text;
	
	public Data(String code, String user, String text) {
		this.code = code;
		this.user = user;
		this.text = text;
	}


	public String getCode() {
		return code;
	}

	public String getUser() {
		return user;
	}

	public String getText() {
		return text;
	}
	
	
	
	
	
	
	
	

}
