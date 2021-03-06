package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import controllers.ControllerChat;
import dataHandler.AlertBox;
import dataHandler.LoggerHandle;
import javafx.application.Platform;

public class Client extends Thread{

	private ClientHandler ch;
	private Socket socket;
	private ControllerChat client;
	private static final String ip = "81.84.174.114";
	private LoggerHandle lh = null;

	public Client(ControllerChat controllerChat, LoggerHandle lh) throws UnknownHostException, IOException {
		this.lh = lh;
		this.client = controllerChat;
		this.start();
		runConnection();
	}

	public void runConnection() throws UnknownHostException, IOException {
		socket = new Socket("localhost",32456);
		socket.setTcpNoDelay(true);
		ch = new ClientHandler(socket,this,client,lh);
		ch.start(); 
		lh.log("INFO", "Connected successfully to server!");

	}

	////////////////  CODE 0  ////////////////
	public void requestLoginAuthentication(String user, String pass) {
		ch.sendMessageCase0(user, pass);
	}

	////////////////CODE 10  ////////////////
	public void registerAccount(String username,String password,String firstname,String lastname,int age, String email) {
		ch.sendMessageCase10(username, password,firstname,lastname,age,email);
	}

	////////////////  CODE 3  ////////////////
	public void sendMessagesToChat(String text) {
		ch.sendMessageCase3(client.user.getUsername(), text);
	}

	///////////////  CODE 11 /////////////////
	public void sendVersion(String version) {
		ch.sendClientVersion(version);
	}


	/**
    ////////////////  CODE 5  ////////////////
    public void sendPrivateMessageToChat(String user) {
        String textField = client.text;
        int index = textField.indexOf("\"");
        String userTarget = textField.substring(6, index-1);
        if(userTarget.equals(client.user) || userTarget.equals(client.id)) {
            ch.printError("You can't send message to yourself!");
        }else {
            String text = textField.substring(index+1, textField.length()-1);
            String users = user+":"+userTarget;
            String code = "5";
            ch.sendMessage(code,users,text);
        }

    }
	 */

	////////////////CODE 7  ////////////////
	public void sendRequestFriend(String username,String usernameTarget) {
		ch.sendMessageCase7(username, usernameTarget);
	}


	////////////////CODE 8  ////////////////
	public void acceptFriendRequest(String username,String usernameTarget) {
		ch.sendMessageCase8(true, username, usernameTarget);

	}

	////////////////CODE 8  ////////////////
	public void declineFriendRequest(String username,String usernameTarget) {
		ch.sendMessageCase8(false, username, usernameTarget);
	}

	/**
    public void replyPrivateMessageToChat(String user, String lastUserPrivate) {
        if(lastUserPrivate.equals("")) {
            ch.printError("You can't reply! Wait for a new message");
        }else {
            String textField = client.text;
            String text = textField.substring(3, textField.length());
            String users = user+":"+lastUserPrivate;
            String code = "5";
            ch.sendMessage(code, users, text);
        }
    }
	 */

	/**
    public void requestInfoUser() {
        String text = client.user.getFullName() + "\r\n\r\n"+
                " Level: "+client.user.getUserLvl()+"\r\n" + 
                " Experience: "+client.user.getUserExp()+"\r\n" + 
                " Number of messages sent: "+client.user.getMessagesSent()+"\r\n" +
                " Number of words written: "+client.user.getWordsWritten()+"\r\n";
        AlertBox.display("Details", text,false);
    }
	 */
}
