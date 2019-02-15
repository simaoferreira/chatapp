package dataHandler;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
	
	StringProperty username = new SimpleStringProperty();
	StringProperty firstName = new SimpleStringProperty();
	StringProperty lastName = new SimpleStringProperty();
	StringProperty email = new SimpleStringProperty();
	IntegerProperty age = new SimpleIntegerProperty();
	IntegerProperty userLvl = new SimpleIntegerProperty();
	IntegerProperty userExp = new SimpleIntegerProperty();
	IntegerProperty userParcialExp = new SimpleIntegerProperty();
	IntegerProperty messagesSent = new SimpleIntegerProperty();
	IntegerProperty wordsWritten = new SimpleIntegerProperty();
	
	public User(String username,String firstName, String lastName, int age, String email, int userLvl, int userExp, int userParcialExp, int messagesSent,
			int wordsWritten) {
		this.username.set(username);
		this.firstName.set(firstName);
		this.lastName.set(lastName);
		this.age.set(age);
		this.email.set(email);
		this.userLvl.set(userLvl);
		this.userExp.set(userExp);
		this.userParcialExp.set(userParcialExp);
		this.messagesSent.set(messagesSent);
		this.wordsWritten.set(wordsWritten);
	}
	
	public String getFullName() {
		return firstName.get()+" "+lastName.get();
	}
	
	public StringProperty getUsernameProperty() {
		return username;
	}
	
	public String getUsername() {
		return username.get();
	}

	public void setUsername(String username) {
		this.username.set(username);
	}

	public StringProperty getFirstNameProperty() {
		return firstName;
	}
	
	public String getFirstName() {
		return firstName.get();
	}

	public void setFirstName(String firstName) {
		this.firstName.set(firstName);
	}

	public StringProperty getLastNameProperty() {
		return lastName;
	}
	
	public String getLastName() {
		return lastName.get();
	}

	public void setLastName(String lastName) {
		this.lastName .set(lastName);
	}

	public StringProperty getEmailProperty() {
		return email;
	}
	
	public String getEmail() {
		return email.get();
	}

	public void setEmail(String email) {
		this.email.set(email);
	}

	public IntegerProperty getAgeProperty() {
		return age;
	}
	
	public int getAge() {
		return age.get();
	}

	public void setAge(int age) {
		this.age.set(age);
	}

	public IntegerProperty getUserLvlProperty() {
		return userLvl;
	}
	
	public int getUserLvl() {
		return userLvl.get();
	}

	public void setUserLvl(int userLvl) {
		this.userLvl.set(userLvl);
	}

	public IntegerProperty getUserExpProperty() {
		return userExp;
	}
	
	public int getUserExp() {
		return userExp.get();
	}

	public void setUserExp(int userExp) {
		this.userExp.set(userExp);
	}

	public IntegerProperty getUserParcialExpProperty() {
		return userParcialExp;
	}
	
	public int getUserParcialExp() {
		return userParcialExp.get();
	}

	public void setUserParcialExp(int userParcialExp) {
		this.userParcialExp.set(userParcialExp);
	}

	public IntegerProperty getMessagesSentProperty() {
		return messagesSent;
	}
	
	public int getMessagesSent() {
		return messagesSent.get();
	}

	public void setMessagesSent(int messagesSent) {
		this.messagesSent .set(messagesSent);
	}

	public IntegerProperty getWordsWrittenProperty() {
		return wordsWritten;
	}
	
	public int getWordsWritten() {
		return wordsWritten.get();
	}

	public void setWordsWritten(int wordsWritten) {
		this.wordsWritten.set(wordsWritten);
	}
	
	
	
	
}
