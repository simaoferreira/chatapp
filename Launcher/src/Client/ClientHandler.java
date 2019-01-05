package Client;



import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.Timer;

import java.awt.AWTException;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Controllers.ControllerLauncher;
import DataHandler.AlertBox;
import DataHandler.Notifications;
import javafx.application.Platform;

public class ClientHandler extends Thread{

    private ControllerLauncher mainClient;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean run = true;
    private StringBuilder stChat = new StringBuilder();
    private String connections;
    private JSONObject objData;
    private String codeNumber;
    private String username;
    private String text;
    private String id;
    private String liveNews;
    private String adminUser;
    private String textOutput;
    private String side;
    private String isConnection;
    private String userToSend;
    private String actualTime;
    private String atualizarClient;
    private JSONObject info;
    private Notifications notify = new Notifications();

    public ClientHandler(Socket socket,Client client,ControllerLauncher launcher) {
        this.socket = socket;
        this.mainClient = launcher;
    }

    public void sendMessage(String code,String user,String text) {
        try {
            JSONObject obj = createObjWithData(code,user,text);
            dos.writeUTF(obj.toString());
            dos.flush();
        } catch (IOException e) {
            run = false;
            System.err.println("Não conseguiu escrever!");
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    AlertBox.display("Error", "Can't connect to server",true);
                }
            });
            close();
        }

    }

    @SuppressWarnings("unchecked")
    private JSONObject createObjWithData(String code, String user, String text) {
        JSONObject obj = new JSONObject();
        obj.put("code", code);
        obj.put("username", user);
        obj.put("text",text);
        return obj;
    }

    public void run() {
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            while(run) {
                try {
                    while(dis.available()==0) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    isConnection = "0";
                    atualizarClient = "0";

                    String reply = dis.readUTF();
                    JSONParser parser = new JSONParser();


                    try {
                        objData = (JSONObject) parser.parse(reply);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    codeNumber = objData.get("code").toString();
                    if(codeNumber.equals("6")) {
                        liveNews = objData.get("text").toString();
                        adminUser = username = objData.get("username").toString();
                    }
                    username = objData.get("username").toString();
                    text = objData.get("text").toString();

                    if((codeNumber.equals("5") || codeNumber.equals("1"))) {
                        info = (JSONObject) objData.get("info");
                        mainClient.lvlUser = Integer.parseInt(info.get("lvlUser").toString());
                        mainClient.expUser = Integer.parseInt(info.get("expUser").toString());
                        mainClient.numMensagens = Integer.parseInt(info.get("numMessages").toString());
                        mainClient.numWordsWritten = Integer.parseInt(info.get("numWords").toString());	                    
                    }



                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    actualTime = sdf.format(cal.getTime());
                    System.out.println("Reply:" +reply);

                    /**
					Animation animation = new Timeline(
					        new KeyFrame(Duration.seconds(0.1),
					            new KeyValue(mainClient.chatScrollPane.vvalueProperty(), 1)),
							new KeyFrame(Duration.seconds(0.1),
									new KeyValue(mainClient.connectionsScrollPane.vvalueProperty(), 1)));
					animation.play();
                     */

                    if(codeNumber.equals("3")){
                        stChat.setLength(0);
                        stChat = new StringBuilder();
                    }else if(codeNumber.equals("0")) {
                        if(username.equals("User not found")) {
                            Platform.runLater(new Runnable() {
                                @Override public void run() {
                                    mainClient.passwordTextField.setText("");
                                    mainClient.lblerror.setVisible(true);
                                    Timer t = new Timer(1500, new ActionListener() {

                                        @Override
                                        public void actionPerformed(java.awt.event.ActionEvent e) {
                                            Platform.runLater(new Runnable() {
                                                @Override public void run() {
                                                    mainClient.lblerror.setVisible(false);
                                                }
                                            });
                                        }
                                    });
                                    t.setRepeats(false);
                                    t.start();
                                }
                            });
                        }else {
                            String[] infoUser = username.split(":");
                            username = infoUser[0];
                            id = infoUser[1];
                            if(mainClient.id.equals("")) {
                                mainClient.id = id;
                            }

                            String lastUser = username;

                            if(mainClient.user.equals("")) {
                                mainClient.user = lastUser;
                            }

                            connections = text;

                            textOutput = "The user '"+lastUser+"' just connected";
                            side = "left";


                            Platform.runLater(new Runnable() {
                                @Override public void run() {
                                    try {
                                        notify.displayTray(lastUser);
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (AWTException e) {
                                        e.printStackTrace();
                                    }
                                    mainClient.updateSceneToMenu();
                                }
                            });

                            if(mainClient.user == username.split(":")[0]) {
                                info = (JSONObject) objData.get("info");
                                mainClient.lvlUser = Integer.parseInt(info.get("lvlUser").toString());
                                mainClient.expUser = Integer.parseInt(info.get("expUser").toString());
                                mainClient.numMensagens = Integer.parseInt(info.get("numMessages").toString());
                                mainClient.numWordsWritten = Integer.parseInt(info.get("numWords").toString());

                            }

                            isConnection="1";
                            userToSend = lastUser;
                            atualizarClient = "1";
                        }
                    }else if(codeNumber.equals("2")) {
                        textOutput = "The user '"+username+"' just disconnected";
                        connections = text;
                        side = "left";
                        isConnection="2";
                        userToSend = username;
                        atualizarClient = "1";
                    }else if(codeNumber.equals("4")){
                        run = false;
                        break;
                    }else if(codeNumber.equals("1")){
                        if (username.equals(mainClient.user)) {
                            side = "right";
                        }else {
                            side = "left";
                        }
                        userToSend = username;
                        textOutput = text+"\n";
                        atualizarClient = "1";
                    }else if(codeNumber.equals("5")){
                        System.out.println("lastUser= "+username);
                        if (username.equals(mainClient.user)) {
                            side = "right";
                        }else {
                            side = "left";
                        }
                        textOutput = text+"\n";
                        userToSend = username + " (Private Message)";
                        System.out.println("lastUser= "+username);
                        if(!username.equals(mainClient.user)) {
                            mainClient.lastUserPrivate = username;
                        }
                        atualizarClient = "1";
                    }else if(codeNumber.equals("6")) {
                        liveNews = text;
                        adminUser = username;
                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                mainClient.lblLiveNews.setText(liveNews+" - "+adminUser);
                            }
                        });
                    }

                    if(atualizarClient.equals("1")) {
                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                mainClient.initialize("> "+textOutput,userToSend,actualTime,side,isConnection);
                                mainClient.chatPane.getChildren().add(mainClient.centeredLabel);
                                mainClient.lblListConnections.setText(connections);	
                            }
                        });
                    }
                } catch (IOException e) {
                    System.err.println("Não conseguiu ler!");
                    close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            dos.close();
            dis.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printError(String string) {
        stChat.append("====| "+string+" |====\n");
        mainClient.lbl.setText(stChat.toString());
    }
}
