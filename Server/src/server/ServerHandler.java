package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import handlers.DataBaseCatalog;

public class ServerHandler extends Thread{

    private Socket socket;
    private Server server;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String username;
    private String text;
    private String codeNumber;
    private String id;
    private boolean run = true;
    private String desconnectedUser;
    private JSONObject objData;
    private DataBaseCatalog dbh;

    public ServerHandler(DataBaseCatalog dbh, Socket socket,Server server) {
        this.socket = socket;
        this.server= server;
        this.dbh = dbh;

    }

    public void run() {

        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            while(run) {

                while(dis.available()==0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                String dataIn = dis.readUTF();
                JSONParser parser = new JSONParser();

                try {
                    objData = (JSONObject) parser.parse(dataIn);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                codeNumber = objData.get("code").toString();
                username = objData.get("username").toString();
                text = objData.get("text").toString();


                System.out.println("Mensagem recebida "+objData.toString());


                if(codeNumber.equals("3")) {
                    JSONObject obj = createObjWithData(codeNumber,"null","null",null);
                    sendText(obj.toString());
                }else if(codeNumber.equals("0")) { 
                    if(dbh.checkLogin(username, text)) {
                        server.connections.add(this);
                        if(!server.liveNews.equals("")) {
                            JSONObject objNews = createObjWithData("6",server.adminUser,server.liveNews,null);
                            this.sendText(objNews.toString());
                        }
                        System.out.println(this.getName());
                        StringBuilder connections = new StringBuilder();

                        if(server.connections.isEmpty()) {
                            try {
                                dos.writeUTF("Nenhuma conexão!");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else {
                            for(int i=0;i<server.connections.size();i++) {
                                connections.append(i+": "+server.connections.get(i).username+"\n");
                                if(server.connections.get(i).username.equals(username)) {
                                    id=Integer.toString(i);
                                }
                            }
                            String infoUser = username+":"+id;
                            int idBD = dbh.getID(username);
                            JSONObject infoUserObj = createObjWithInfo(idBD);
                            JSONObject obj2 = createObjWithData(codeNumber,infoUser,connections.toString(),infoUserObj);

                            sendToClients(obj2.toString());

                        }
                    }else {
                        JSONObject obj = createObjWithData(codeNumber,"User not found","",null);
                        sendText(obj.toString());
                    }

                }else if(codeNumber.equals("2")) {
                    StringBuilder connections = new StringBuilder();

                    for(ServerHandler sh : server.connections) {
                        if(sh.username.equals(username)) {
                            desconnectedUser = sh.username;
                            JSONObject obj = createObjWithData("4",desconnectedUser,"null",null);
                            sh.sendText(obj.toString());
                            server.connections.remove(sh);
                            break;
                        }
                    }

                    for(int i=0;i<server.connections.size();i++) {
                        connections.append(i+": "+server.connections.get(i).username+"\n");
                    }
                    JSONObject obj = createObjWithData(codeNumber,desconnectedUser,connections.toString(),null);
                    System.out.println(obj.toString());

                    sendToClients(obj.toString());

                }else if(codeNumber.equals("1")){
                    String[] words = text.split(" ");
                    dbh.updateWordsWritten(username, words.length);
                    int idBD = dbh.getID(username);
                    JSONObject infoUserObj = createObjWithInfo(idBD);
                    JSONObject obj = createObjWithData(codeNumber,username,text,infoUserObj);
                    sendToClients(obj.toString());

                }else if(codeNumber.equals("5")) {
                    String[] users = username.split(":");
                    username = users[0];
                    String targetUser = users[1];
                    String[] words = text.split(" ");
                    dbh.updateWordsWritten(username, words.length);
                    int idBD = dbh.getID(username);
                    JSONObject infoUserObj = createObjWithInfo(idBD);
                    int count=0;
                    for(ServerHandler sh : server.connections) {
                        if(sh.username.equals(username)) {
                            JSONObject obj = createObjWithData(codeNumber,username,text,infoUserObj);
                            sh.sendText(obj.toString());
                        }
                        if(sh.username.equals(targetUser) || Integer.toString(count).equals(targetUser)) {
                            JSONObject obj = createObjWithData(codeNumber,username,text,infoUserObj);
                            sh.sendText(obj.toString());
                        }
                        count++;
                    }

                }else if(codeNumber.equals("6")) {
                    if(!text.equals("")) {
                        JSONObject obj = createObjWithData(codeNumber,username,text,null);
                        sendToClients(obj.toString());
                        server.liveNews = text;
                        server.adminUser = username;
                    }

                }else if(codeNumber.equals("7")) {
                    dbh.addFriend(username, text);
                    JSONObject obj = createObjWithData(codeNumber, username, "Invite of user " + text +" accepted!", null);
                    for(ServerHandler sh : server.connections) {
                        if(sh.username.equals(username)) {
                            sh.sendText(obj.toString());
                        }
                    }
                }

            }
            dis.close();
            dos.close();
            socket.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    private void sendToClients(String textIn) {

        for(int i=0;i<server.connections.size();i++) {
            ServerHandler sh = server.connections.get(i);
            sh.sendText(textIn);
        }

    }

    private void sendText(String textIn) {

        try {
            dos.writeUTF(textIn);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @SuppressWarnings("unchecked")
    private JSONObject createObjWithData(String code, String user, String text,JSONObject info) {
        JSONObject obj = new JSONObject();
        obj.put("code", code);
        obj.put("username", user);
        obj.put("text",text);
        obj.put("info", info);

        return obj;

    }

    @SuppressWarnings("unchecked")
    private JSONObject createObjWithInfo(int id) {
        JSONObject obj = new JSONObject();
        obj.put("lvlUser", dbh.getLvlUser(id));
        obj.put("expUser", dbh.getExpUser(id));
        obj.put("numMessages", dbh.getMessagesSent(id));
        obj.put("numWords", dbh.getWordsWritten(id));
        return obj;

    }

}
