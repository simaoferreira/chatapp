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
    private int codeNumber;
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


                codeNumber = Integer.parseInt(objData.get("code").toString());
                username = objData.get("username").toString();
                text = objData.get("text").toString();


                System.out.println("Mensagem recebida "+objData.toString());

                JSONObject obj;
                JSONObject infoUserObj;
                String[] words;
                int idBD;

                switch(codeNumber) {
                case 3: 
                    obj = createObjWithData(codeNumber,"null","null",null);
                    sendText(obj.toString());
                    break;
                case 0:
                    if(dbh.checkLogin(username, text)) {
                        server.connections.add(this);
                        if(!server.liveNews.equals("")) {
                            JSONObject objNews = createObjWithData(6,server.adminUser,server.liveNews,null);
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
                            idBD = dbh.getID(username);
                            String friends = dbh.getFriends(idBD);
                            if(friends.equals("")) {
                                friends = "You don't have friends yet!";
                            }
                            infoUserObj = createObjWithInfo(idBD);
                            JSONObject obj2 = createObjWithData(codeNumber,infoUser,connections.toString()+"/"+friends,infoUserObj);

                            sendToClients(obj2.toString());

                        }
                    }else {
                        obj = createObjWithData(codeNumber,"User not found","",null);
                        sendText(obj.toString());
                    }
                    break;

                case 2:
                    StringBuilder connections = new StringBuilder();

                    for(ServerHandler sh : server.connections) {
                        if(sh.username.equals(username)) {
                            desconnectedUser = sh.username;
                            obj = createObjWithData(4,desconnectedUser,"null",null);
                            sh.sendText(obj.toString());
                            server.connections.remove(sh);
                            break;
                        }
                    }

                    for(int i=0;i<server.connections.size();i++) {
                        connections.append(i+": "+server.connections.get(i).username+"\n");
                    }
                    obj = createObjWithData(codeNumber,desconnectedUser,connections.toString(),null);
                    System.out.println(obj.toString());

                    sendToClients(obj.toString());
                    break;
                case 1:
                    words = text.split(" ");
                    dbh.updateWordsWritten(username, words.length);
                    idBD = dbh.getID(username);
                    infoUserObj = createObjWithInfo(idBD);
                    obj = createObjWithData(codeNumber,username,text,infoUserObj);
                    sendToClients(obj.toString());
                    break;
                case 5:
                    String[] users = username.split(":");
                    username = users[0];
                    String targetUser = users[1];
                    words = text.split(" ");
                    dbh.updateWordsWritten(username, words.length);
                    idBD = dbh.getID(username);
                    infoUserObj = createObjWithInfo(idBD);
                    int count=0;
                    for(ServerHandler sh : server.connections) {
                        if(sh.username.equals(username)) {
                            obj = createObjWithData(codeNumber,username,text,infoUserObj);
                            sh.sendText(obj.toString());
                        }
                        if(sh.username.equals(targetUser) || Integer.toString(count).equals(targetUser)) {
                            obj = createObjWithData(codeNumber,username,text,infoUserObj);
                            sh.sendText(obj.toString());
                        }
                        count++;
                    }
                    break;
                case 6:
                    if(!text.equals("")) {
                        obj = createObjWithData(codeNumber,username,text,null);
                        sendToClients(obj.toString());
                        server.liveNews = text;
                        server.adminUser = username;
                    }
                    break;
                case 7:
                    if(dbh.checkUser(text)) {
                        boolean existsFriendship = dbh.checksFriendship(username,text);
                        boolean existsFriendRequest = dbh.checkRequestInvite(username, text);

                        if(!existsFriendship && !existsFriendRequest) {
                            dbh.addRequestFriend(username, text);
                            obj = createObjWithData(codeNumber, username, "The user "+ username + " sent you a friend request!", null);
                            sendToOneClient(text,obj.toString());
                        }else {
                            if(existsFriendship) {
                                obj = createObjWithData(codeNumber, username, "The user "+ text + " is already your friend!", null);
                                sendToOneClient(username,obj.toString());
                            }else if(existsFriendRequest) {
                                obj = createObjWithData(codeNumber, username, "You already sent a friend request to "+ text + " !", null);
                                sendToOneClient(username,obj.toString());
                            }
                        }

                    }else {
                        obj = createObjWithData(codeNumber, username, "The user "+ text + " doesn't exist!", null);
                        sendToOneClient(username,obj.toString());
                    }

                    break;
                case 8:
                    String userReceivedRequest = username;
                    String userSentRequest = text.split(":")[1];
                    String statusRequest = text.split(":")[0];

                    if(dbh.checkRequestInvite(userSentRequest,userReceivedRequest)) {
                        obj = null;

                        if(statusRequest.equals("accept")) {
                            dbh.removeRequestFriend(userSentRequest, userReceivedRequest);
                            dbh.addFriend(userSentRequest, userReceivedRequest);
                            obj = createObjWithData(9, username, "The user "+ userReceivedRequest + " accepted your invite!", null);
                        }else {
                            dbh.removeRequestFriend(userSentRequest, userReceivedRequest);
                            obj = createObjWithData(9, username, "The user "+ userReceivedRequest + " declined your invite!", null);
                        }
                        sendToOneClient(userSentRequest,obj.toString());

                    }else {
                        obj = createObjWithData(9, username, "Invite declined because you didn't receive an invite from " +userSentRequest+"!", null);
                        sendToOneClient(userSentRequest,obj.toString());
                    }
                    break;
                case 10:
                    dbh.addUser(username, text);
                }

            }
            dis.close();
            dos.close();
            socket.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    private void sendToOneClient(String username, String text) throws IOException {
        for(ServerHandler sh : server.connections) {
            if(sh.username.equals(username)) {
                sh.sendText(text);
                break;
            }
        }
    }

    private void sendToClients(String textIn) throws IOException {

        for(int i=0;i<server.connections.size();i++) {
            ServerHandler sh = server.connections.get(i);
            sh.sendText(textIn);
        }

    }

    protected void updateInfoConnections() throws IOException {
        StringBuilder connections = new StringBuilder();

        for(int i=0;i<server.connections.size();i++) {
            connections.append(i+": "+server.connections.get(i).username+"\n");
        }

        JSONObject obj = createObjWithData(10,null,connections.toString(),null);
        sendToClients(obj.toString()); 
    }

    protected void sendText(String textIn) throws IOException {
        dos.writeUTF(textIn);
        dos.flush();
    }

    @SuppressWarnings("unchecked")
    private JSONObject createObjWithData(int code, String user, String text,JSONObject info) {
        JSONObject obj = new JSONObject();
        obj.put("code", String.valueOf(code));
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
