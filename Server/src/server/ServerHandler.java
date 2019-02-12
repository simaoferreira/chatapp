package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import handlers.DataBaseCatalog;
import handlers.LoggerHandle;

public class ServerHandler extends Thread{

	private Socket             socket;
	private Server             server;
	//private DataInputStream  dis;
	//private DataOutputStream dos;
	private ObjectOutputStream out = null;
	private ObjectInputStream  in = null;
	private String             username;
	private int                codeNumber;
	private boolean            run = true;
	private DataBaseCatalog    dbh;
	private LoggerHandle       lh;
	private ArrayList<ServerHandler> friendsThreads = new ArrayList<ServerHandler>();

	public ServerHandler(DataBaseCatalog dbh, LoggerHandle lh, Socket socket,Server server) {
		this.socket = socket;
		this.server= server;
		this.dbh = dbh;
		this.lh = lh;
	}

	private boolean checkUserLogged(String username) {
		for(ServerHandler sh : server.connections) {
			if(sh.username.equals(username)) {
				return true;
			}
		}
		return false;
	}

	public synchronized void run() {

		try {
			
			//dis = new DataInputStream(socket.getInputStream());
			//dos = new DataOutputStream(socket.getOutputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
			in  = new ObjectInputStream (socket.getInputStream ());

			while(run) {

				try {
					StringBuilder connectionsUpdated = new StringBuilder();
					
					//receber o codigo
					try {
						codeNumber  = (int) in.readObject();
                    } catch (Exception e) {
                        run = false;
                        if (username==null) {
                        	lh.log("INFO", "Lost a connection;");
                        }else {
                        	lh.log("INFO", "User "+username+" disconnected;");
                        	server.connections.remove(this);
    						
    						for(int i=0;i<server.connections.size();i++) {
    	                        connectionsUpdated.append(i+": "+server.connections.get(i).username+"\n");
    	                    }
    						
    						//enviar o codigo 2
    						sendToClients(2,false);
    						
    						//enviar o username que disconectou
    						sendToClients(dbh.getFullName(dbh.getID(username)),false);
    						
    						//enviar as conexoes atuais
    						sendToClients(connectionsUpdated.toString(),false);
    						
                        }
                        Thread.currentThread().interrupt();
                        break;
                    }
					

					int idBD;
					JSONObject infoUserObj;
					

					switch(codeNumber) {
					case 0:
						//receber o nome do user
						String user   = (String) in.readObject();
						//receber a pass do user
	                    String password = (String) in.readObject();
	                    lh.log("INFO", "User " + user + " trying to login into the server;");
	                    
						if(dbh.checkLogin(user, password) && !checkUserLogged(user)) {
							username = user;
							server.connections.add(this);

							//enviar o codigo 0
							out.writeObject(codeNumber);
							//enviar True
							out.writeObject(new Boolean(true));
							
							idBD = dbh.getID(username);
							JSONObject userObjInfo = createObjWithInfo(idBD);
							//enviar info do username
							out.writeObject(userObjInfo);
							
							//enviar numero de users online
							int size = server.connections.size();
							out.writeObject(size);
							
							if(size!=0) {
								for(ServerHandler sh : server.connections) {
									int i = dbh.getID(sh.username);
									infoUserObj = createObjWithInfo(i);
									//enviar a info do amigo com id i
									out.writeObject(infoUserObj);
								}
							}
							
							//enviar informação para os restantes clientes com code 1
							sendToClients(1,false);
							
							//enviar o user que se conectou
							sendToClients(userObjInfo,false);
							
							lh.log("INFO", "User " + user + " logged in the server");
						}else{
							//enviar o codigo 0
							out.writeObject(codeNumber);
							//enviar False
							out.writeObject(new Boolean(false));
							
							if(!dbh.checkLogin(user, password)) {
								out.writeObject("Username or password incorrect!");
							}else {
								out.writeObject("This user is already logged in!");
							}
							lh.log("INFO", "User " + user + " couldn not login into the server");
						}
						break;
					case 3:
						//receber o nome do user que estah a enviar a mensagem
						String ownerOfMessage = (String) in.readObject();
						//receber o conteudo da mensagem
						String message = (String) in.readObject();
						String[] words = message.split(" ");
						dbh.updateWordsWritten(ownerOfMessage, words.length);
						
						//enviar o codigo 3
						sendToClients(codeNumber,true);
						//enviar o username que enviou mensagem
						idBD = dbh.getID(ownerOfMessage);
						sendToClients(dbh.getFullName(idBD),true);
						//enviar o texto se esta thread nao pertencer ao dono
						sendToClients(message, true);
						
						infoUserObj = createObjWithStats(idBD);
						//enviar info do username para os amigos do owner
						
						//sendToClients(infoUserObj,true);
						break;
					case 6:
						//receber o user do admin
						//receber a live new
						
						//enviar o codigo 6
						//enviar o username do admin
						//enviar a live news
						break;
					case 7:
						//receber o nome do user que pediu convite
						//receber o nome do user que vai receber convite
						
						//enviar o codigo 7
						//enviar o texto
						break;
					case 8:
						//receber o resultado final do convite (accept ou decline)
						//receber o nome do user que respondeu ao convite
						//receber o nome do user que enviou o pedido
						
						//enviar o codigo 9
						//enviar o resultado do convite 
						//enviar username se convite for aceite
						//enviar texto
						break;
					case 10:
						//receber o username 
						//receber a password
						
						//enviar o codigo 10
						//enviar texto
						//enviar username
						break;
					}
				} catch (ClassNotFoundException e) {
					lh.log("SEVERE","Could not read the codeNumber",e);
				}
			}

			in.close();
			out.close();
			socket.close();








			/**
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
			 */

			/**
                JSONObject obj;
                JSONObject infoUserObj;
                String[] words;
                int idBD;
			 */

			/**
                switch(codeNumber) {
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
                case 1:
                	words = text.split(" ");
                    dbh.updateWordsWritten(username, words.length);
                    idBD = dbh.getID(username);
                    infoUserObj = createObjWithInfo(idBD);
                    obj = createObjWithData(codeNumber,username,text,infoUserObj);
                    sendToClients(obj.toString());
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
                case 3:
                	obj = createObjWithData(codeNumber,"null","null",null);
                    sendText(obj.toString());
                	break;
                case 4:
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
                	if(dbh.estaRegistado(text)) {
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
                            obj = createObjWithData(9, userReceivedRequest, "accept:The user "+ userReceivedRequest + " accepted your invite!", null);
                            sendToOneClient(userSentRequest,obj.toString());
                            obj = createObjWithData(9, userSentRequest, "info:The user "+ userSentRequest + " see that you accepted your invite!", null);
                            sendToOneClient(userReceivedRequest,obj.toString());
                        }else {
                            dbh.removeRequestFriend(userSentRequest, userReceivedRequest);
                            obj = createObjWithData(9, username, "decline:The user "+ userReceivedRequest + " declined your invite!", null);
                            sendToOneClient(userSentRequest,obj.toString());
                        }


                    }else {
                        obj = createObjWithData(9, username, "decline:Invite declined because you didn't receive an invite from " +userSentRequest+"!", null);
                        sendToOneClient(userSentRequest,obj.toString());
                    }
                	break;
                case 9:
                	break;
                case 10:
                	if(dbh.estaRegistado(username)) {
                		obj = createObjWithData(codeNumber,"null","The username \""+username+"\" is already registered",null);
                	}else {
                		obj = createObjWithData(codeNumber,username,"User registered! Now you can login!",null);
                		dbh.addUser(username, text);
                	}
                	sendText(obj.toString());
                	break;
                }
			 */

		} catch (Exception e) {
			e.printStackTrace();
			lh.log("WARNING", "Error trying creating the streams!");
		}

	}

	private void sendToFriends(JSONObject infoUserObj, boolean sendToThisClient) throws IOException {
		ArrayList<Integer> friends = dbh.getFriends(dbh.getID(this.username));
		
		for(ServerHandler sh : server.connections) {
			if(ehFriend(sh,friends)) {
				sh.sendText(infoUserObj);
			}
		}
		
		if(sendToThisClient) {
			out.writeObject(infoUserObj);
		}
		
		
	}
	
	private Boolean ehFriend(ServerHandler sh, ArrayList<Integer> friends) {
		
		for(Integer f :  friends) {
			if(dbh.getID(sh.username) == f) {
				return true;
			}
		}
		return false;
		
	}

	private void createArrayWithSHOfFriends(ArrayList<Integer> friends) {
		for(Integer i :  friends) {
			String user = dbh.getUsername(i);
			System.out.println(user);
			for(ServerHandler sh : server.connections) {
				if(sh.username.equals(user) && sh != this) {
					System.out.println(this.username +":"+sh.username);
					friendsThreads.add(sh);
				}
			}
		}
		
	}

	private void sendToOneClient(String username, Object obj) throws IOException {
		for(ServerHandler sh : server.connections) {
			if(sh.username.equals(username)) {
				sh.sendText(obj);
				break;
			}
		}
	}

	private void sendToClients(Object obj,Boolean sendToThisClient) throws IOException {

		for(int i=0;i<server.connections.size();i++) {
			ServerHandler sh = server.connections.get(i);
			if(!sendToThisClient) {
				if(!sh.equals(this)) {
					sh.sendText(obj);
				}
			}else {
				sh.sendText(obj);
			}
			
		}

	}

	/**
    protected void updateInfoConnections() throws IOException {
        StringBuilder connections = new StringBuilder();

        for(int i=0;i<server.connections.size();i++) {
            connections.append(i+": "+server.connections.get(i).username+"\n");
        }

        JSONObject obj = createObjWithData(10,null,connections.toString(),null);
        sendToClients(obj.toString()); 
    }
	 */

	protected void sendText(Object obj) throws IOException {
		out.writeObject(obj);
	}

	/**
    @SuppressWarnings("unchecked")
    private JSONObject createObjWithData(int code, String user, String text,JSONObject info) {
        JSONObject obj = new JSONObject();
        obj.put("code", String.valueOf(code));
        obj.put("username", user);
        obj.put("text",text);
        obj.put("info", info);

        return obj;

    }
	 */

	@SuppressWarnings("unchecked")
    private JSONObject createObjWithStats(int id) {
        JSONObject obj = new JSONObject();
        obj.put("lvlUser", dbh.getLvlUser(id));
        obj.put("expUser", dbh.getExpUser(id));
        obj.put("parcialExpUser", dbh.getParcialExpUser(id));
        obj.put("numMessages", dbh.getMessagesSent(id));
        obj.put("numWords", dbh.getWordsWritten(id));
        return obj;
    }
	
    @SuppressWarnings("unchecked")
    private JSONObject createObjWithInfo(int id) {
        JSONObject obj = new JSONObject();
        obj.put("username",dbh.getUsername(id));
        obj.put("firstName", dbh.getFirstName(id));
        obj.put("lastName", dbh.getLastName(id));
        obj.put("age", dbh.getAge(id));
        obj.put("email",dbh.getEmail(id));
        obj.put("lvlUser", dbh.getLvlUser(id));
        obj.put("expUser", dbh.getExpUser(id));
        obj.put("parcialExpUser", dbh.getParcialExpUser(id));
        obj.put("numMessages", dbh.getMessagesSent(id));
        obj.put("numWords", dbh.getWordsWritten(id));
        return obj;
    }

}
