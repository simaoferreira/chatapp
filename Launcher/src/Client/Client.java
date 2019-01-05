package Client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import Controllers.ControllerLauncher;
import DataHandler.AlertBox;

public class Client extends Thread{
    
    private ClientHandler ch;
    private String user;
    private Socket socket;
    private ControllerLauncher client;
    private static final String ip = "81.84.174.114";

    public Client(ControllerLauncher controllerLauncher) throws UnknownHostException, IOException {
        this.client = controllerLauncher;
        this.start();
        runConnection();
    }

    public void runConnection() throws UnknownHostException, IOException {
        socket = new Socket(ip,32456);
        socket.setTcpNoDelay(true);
        ch = new ClientHandler(socket,this,client);
        ch.start();  	
    }

    ////////////////  CODE 0  ////////////////
    public void requestUpdateConnections(String user, String pass) {
        this.user=user;
        String code = "0";
        ch.sendMessage(code,user,pass);
    }

    ////////////////  CODE 1  ////////////////
    public void sendMessagesToChat(String user) {
        String inputText = client.text;
        String code = "1";
        ch.sendMessage(code,user,inputText);
    }

    ////////////////  CODE 2  ////////////////
    public void requestCloseConnection() {
        String code = "2";
        ch.sendMessage(code, user, "null");
    }

    ////////////////  CODE 3  ////////////////
    public void requestClearChat() {
        String code = "3";
        ch.sendMessage(code, "null", "null");
    }

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

    public void printError(String string) {
        ch.printError(string);

    }

    public void requestInfoUser() {
        System.out.println(client.lvlUser);
        String text = client.user + "\r\n\r\n"+
                " Level: "+client.lvlUser+"\r\n" + 
                " Experience: "+client.expUser+"\r\n" + 
                " Number of messages sent: "+client.numMensagens+"\r\n" +
                " Number of words written: "+client.numWordsWritten+"\r\n";
        AlertBox.display("Details", text,false);
    }
}
