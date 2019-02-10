package dataHandler;

public class User {
	
	private String username;
	private String firstName;
	private String lastName;
	private int age;
	private int userLvl;
	private int userExp;
	private int messagesSent;
	private int wordsWritten;
	
	
	public User(String username,String firstName, String lastName, int age, int userLvl, int userExp, int messagesSent,
			int wordsWritten) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.userLvl = userLvl;
		this.userExp = userExp;
		this.messagesSent = messagesSent;
		this.wordsWritten = wordsWritten;
	}
	
	public String getFullName() {
		return firstName+" "+lastName;
	}
	
	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public int getAge() {
		return age;
	}


	public void setAge(int age) {
		this.age = age;
	}


	public int getUserLvl() {
		return userLvl;
	}


	public void setUserLvl(int userLvl) {
		this.userLvl = userLvl;
	}


	public int getUserExp() {
		return userExp;
	}


	public void setUserExp(int userExp) {
		this.userExp = userExp;
	}


	public int getMessagesSent() {
		return messagesSent;
	}


	public void setMessagesSent(int messagesSent) {
		this.messagesSent = messagesSent;
	}


	public int getWordsWritten() {
		return wordsWritten;
	}


	public void setWordsWritten(int wordsWritten) {
		this.wordsWritten = wordsWritten;
	}
	
	
	
	

}
