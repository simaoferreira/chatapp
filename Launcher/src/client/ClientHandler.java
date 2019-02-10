package client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.Timer;

import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import controllers.ControllerLauncher;
import controllers.Side;
import controllers.Type;
import dataHandler.AlertBox;
import dataHandler.Notifications;
import dataHandler.User;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

public class ClientHandler extends Thread{

	private ControllerLauncher mainClient;
	private Socket socket;
	//private DataInputStream dis;
	//private DataOutputStream dos;
	private ObjectOutputStream out = null;
	private ObjectInputStream  in = null;
	private boolean run = true;
	private StringBuilder stChat = new StringBuilder();
	private String connections;
	private JSONObject objData;
	private int codeNumber;
	private String username;
	private ArrayList<User> friends = new ArrayList<User>();
	private String text;
	private String id;
	private String liveNews;
	private String adminUser;
	private String textOutput;
	private Side side;
	private Type type;
	private String userToSend;
	private String actualTime;
	private Boolean atualizarClient;
	private JSONObject info;
	private Notifications notify = new Notifications();

	public ClientHandler(Socket socket,Client client,ControllerLauncher launcher) {
		this.socket = socket;
		this.mainClient = launcher;
	}

	/**
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
	 */

	/**
	@SuppressWarnings("unchecked")
	private JSONObject createObjWithData(String code, String user, String text) {
		JSONObject obj = new JSONObject();
		obj.put("code", code);
		obj.put("username", user);
		obj.put("text",text);
		return obj;
	}
	 * @throws IOException 
	 */


	protected void sendMessageCase0(String user,String password) {
		int code = 0;
		try {
			// enviar o codigo
			out.writeObject(code);

			// enviar o nome do user
			out.writeObject(user);

			// enviar a pass do user
			out.writeObject(password);
		}catch(Exception e) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					AlertBox.display("Error", "Can't connect to server",true);
				}
			});
		}

	}

	protected void sendMessageCase2(String user) {
		int code = 2;

		try {
			// enviar o codigo
			out.writeObject(code);

			//enviar nome do user que vai sair do chat
			out.writeObject(user);

		}catch(Exception e) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					AlertBox.display("Error", "Can't connect to server",true);
				}
			});
		}


		System.exit(-1);

	}

	protected void sendMessageCase3(String user,String text) {
		int code = 3;

		try {
			// enviar o codigo
			out.writeObject(code);

			//enviar o nome do user que estah a enviar a mensagem
			out.writeObject(user);

			//enviar o conteudo da mensagem
			out.writeObject(text);


		}catch(Exception e) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					AlertBox.display("Error", "Can't connect to server",true);
				}
			});
		}




	}



	protected void sendMessageCase7(String userSentInvite,String userTarget) {
		int code = 7;

		//enviar o codigo
		//enviar o nome do user que pediu convite
		//enviar o nome do user que vai receber convite

	}

	protected void sendMessageCase8(Boolean result,String userReceivedInvite,String userSentInvite) {
		int code = 8;

		//enviar o codigo
		//enviar o resultado final do convite (accept ou decline)
		//enviar o nome do user que respondeu ao convite
		//enviar o nome do user que enviou o pedido

	}

	protected void sendMessageCase10(String username, String password ) {
		int code = 7;

		//enviar o codigo
		//enviar o username 
		//enviar a password

	}




	public synchronized void run() {
		try {
			//dis = new DataInputStream(socket.getInputStream());
			//dos = new DataOutputStream(socket.getOutputStream());

			out = new ObjectOutputStream(socket.getOutputStream());
			in  = new ObjectInputStream (socket.getInputStream ());

			while(run) {
				try {

					atualizarClient = false;

					Calendar cal = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
					actualTime = sdf.format(cal.getTime());

					Animation animation = new Timeline(
							new KeyFrame(Duration.seconds(0.1),
									new KeyValue(mainClient.chatScrollPane.vvalueProperty(), 1)),
							new KeyFrame(Duration.seconds(0.1),
									new KeyValue(mainClient.connectionsScrollPane.vvalueProperty(), 1)));
					animation.play();

					try {
						try {
							codeNumber = (int) in.readObject();
						} catch (Exception e) {
							run = false;
							System.err.println("server offline!");
							mainClient.txtField.setDisable(true);
							mainClient.sendMessage.setDisable(true);
							Platform.runLater(new Runnable() {
								@Override public void run() {
									AlertBox.display("Error", "Connecting with server has been lost!",false);
								}
							});
							//System.exit(-1);
						}

						switch(codeNumber) {
						case 0:
							//receber o boolean
							Boolean authBool = (Boolean) in.readObject();

							if(authBool) {
								//receber id  
								mainClient.id = (int) in.readObject();
								//receber conexoes ativas
								mainClient.connections = (String) in.readObject();
								//receber info do username
								JSONObject infoUser = (JSONObject) in.readObject();
								mainClient.user = createUserFromJSON(infoUser);

								//receber numero de amigos
								int numberFriends = (int) in.readObject();

								if(numberFriends != 0) {
									ArrayList<JSONObject> friendsJSON= new ArrayList<JSONObject>(numberFriends);
									for(int i=0;i<numberFriends;i++) {
										JSONObject infoFriend = (JSONObject) in.readObject();
										friendsJSON.add(infoFriend);
									}

									for(JSONObject info : friendsJSON) {
										User friend = createUserFromJSON(info);
										friends.add(friend);
									}
								}

								Platform.runLater(new Runnable() {
									@Override public void run() {
										mainClient.updateSceneToMenu();
										mainClient.userLbl.setText(mainClient.user.getFullName());
									}
								});

								if(numberFriends != 0) {
									Platform.runLater(new Runnable() {
										@Override public void run() {
											for(User u :  friends) {
												mainClient.addFriendPane(u);
												mainClient.friendsPane.getChildren().add(mainClient.friendsLabel);
												mainClient.lblFriends.setText("");
											}
										}
									});
								}else {
									Platform.runLater(new Runnable() {
										@Override public void run() {
											mainClient.lblFriends.setText("You don't have friends yet!");
										}
									});
								}

								textOutput = "You connected to the server!";
								side = Side.LEFT;
								type= Type.LOGIN;
								userToSend = mainClient.user.getFullName();
								atualizarClient = true;

							}else {
								String error = (String) in.readObject();
								Platform.runLater(new Runnable() {
									@Override public void run() {
										mainClient.passwordTextField.clear();
										mainClient.lblerror.setText(error);
										mainClient.loginPaneFields.setVisible(true);
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
							}
							break;
						case 1:
							//receber o user que se conectou
							String userConnected = (String) in.readObject();
							//receber a lista com as conexoes atualizadas
							mainClient.connections = (String) in.readObject();
							textOutput = "The user '"+ userConnected+"' just connected";
							side = Side.LEFT;
							type= Type.LOGIN;
							userToSend = userConnected;
							atualizarClient = true;
							break;
						case 2:
							//receber o user que se desconectou
							String userDisconnect = (String) in.readObject();

							//receber a lista com as conexoes atualizadas
							mainClient.connections = (String) in.readObject();

							textOutput = "The user '"+userDisconnect+"' disconnected";
							side = Side.LEFT;
							type=Type.LOGOUT;
							userToSend = userDisconnect;
							atualizarClient = true;
							break;
						case 3:
							//receber o owner da mensagem enviada para o servidor
							String ownerOfMessage = (String) in.readObject();
							textOutput = (String) in.readObject();
							
							//receber a mensagem se o owner da mensagem nao corresponder ao dono desta thread
							if (!mainClient.user.getFullName().equals(ownerOfMessage)) {
								atualizarClient = true;
								side = Side.LEFT;
								type = Type.MESSAGE;
								userToSend = ownerOfMessage;
								//JSONObject infoOwnerMessageUpdated = (JSONObject) in.readObject();
								//updatedInfoFriendFromJSON(ownerOfMessage,infoOwnerMessageUpdated);
								
							}else {
								atualizarClient = true;
								side = Side.RIGHT;
								type = Type.MESSAGE;
								userToSend = "You";
								//JSONObject infoUserUpdated = (JSONObject) in.readObject();
								//updatedInfoUserFromJSON(infoUserUpdated);
							}

							break;
						case 5:
							break;
						case 6:
							break;
						case 7:
							break;
						case 9:
							break;
						case 10:
							break;
						}

						if(atualizarClient) {
							Platform.runLater(new Runnable() {
								@Override public void run() {
									mainClient.printMessage("> "+textOutput,userToSend,actualTime,side,type);
									mainClient.chatPane.getChildren().add(mainClient.centeredLabel);
									mainClient.lblConnections.setText(mainClient.connections);	
								}
							});
						}

					} catch (Exception e) {
						e.printStackTrace();
					}



					/**
					String reply = dis.readUTF();
					JSONParser parser = new JSONParser();

					if(!reply.equals("")) {
						try {
							objData = (JSONObject) parser.parse(reply);
						} catch (ParseException e) {
							e.printStackTrace();
						}

						codeNumber = Integer.parseInt(objData.get("code").toString());

						if(codeNumber == 6) {
							liveNews = objData.get("text").toString();
							adminUser = username = objData.get("username").toString();
						}

						username = objData.get("username").toString();
						text = objData.get("text").toString();
					}

					System.out.println("Reply:" +reply);
					 */

					/**
					switch (codeNumber) {
					case 0:
						if(username.equals("User not found")) {
							Platform.runLater(new Runnable() {
								@Override public void run() {
									mainClient.passwordTextField.clear();
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

							connections = text.split("/")[0];


							textOutput = "The user '"+lastUser+"' just connected";
							side = "left";


							Platform.runLater(new Runnable() {
								@Override public void run() {
									/**
                                    if(username.equals(lastUser)) {
                                        try {
                                            notify.displayTray(lastUser);
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        } catch (AWTException e) {
                                            e.printStackTrace();
                                        }
                                    }

									mainClient.updateSceneToMenu();
									mainClient.userLbl.setText(lastUser);
								}
							});

							if(mainClient.user == username.split(":")[0]) {
								info = (JSONObject) objData.get("info");
								mainClient.lvlUser = Integer.parseInt(info.get("lvlUser").toString());
								mainClient.expUser = Integer.parseInt(info.get("expUser").toString());
								mainClient.numMensagens = Integer.parseInt(info.get("numMessages").toString());
								mainClient.numWordsWritten = Integer.parseInt(info.get("numWords").toString());
								friends = text.split("/")[1];
								String[] listFriends = friends.split("\n");
								Platform.runLater(new Runnable() {
									@Override public void run() {
										for(String s :  listFriends) {
											if(!s.startsWith("You")) {
												mainClient.addFriendPane(s);
												mainClient.friendsPane.getChildren().add(mainClient.friendsLabel);
												mainClient.lblFriends.setText("");
											}else {
												mainClient.lblFriends.setText(s);	
											}
										}
									}
								});



							}

							type="1";
							userToSend = lastUser;
							atualizarClient = "1";
						}
						break;
					case 1:
						if (username.equals(mainClient.user)) {
							side = "right";
							info = (JSONObject) objData.get("info");
							mainClient.lvlUser = Integer.parseInt(info.get("lvlUser").toString());
							mainClient.expUser = Integer.parseInt(info.get("expUser").toString());
							mainClient.numMensagens = Integer.parseInt(info.get("numMessages").toString());
							mainClient.numWordsWritten = Integer.parseInt(info.get("numWords").toString());	
						}else {
							side = "left";
						}
						userToSend = username;
						textOutput = text+"\n";
						atualizarClient = "1";
						break;
					case 2:
						textOutput = "The user '"+username+"' just disconnected";
						connections = text;
						side = "left";
						type="2";
						userToSend = username;
						atualizarClient = "1";
						break;
					case 3:
						break;
					case 4:
						break;
					case 5:
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
						break;
					case 6:
						liveNews = text;
						adminUser = username;
						Platform.runLater(new Runnable() {
							@Override public void run() {
								mainClient.lblLiveNews.setText(liveNews+" - "+adminUser);
							}
						});
						break;
					case 7:
						textOutput = text;
						type="3";
						atualizarClient = "1";
						break;
					case 9:
						String codeString = text.split(":")[0];
						if(codeString.equals("accept") || codeString.equals("info")) {
							Platform.runLater(new Runnable() {
								@Override public void run() {
									mainClient.addFriendPane(username);
									mainClient.friendsPane.getChildren().add(mainClient.friendsLabel);
								}
							});
						}

						if(!codeString.equals("info")) {
							textOutput = text.split(":")[1];
							type = "3";
							atualizarClient = "1";
						}
						break;
					case 10:
						Platform.runLater(new Runnable() {
							@Override public void run() {
								AlertBox.display("Details",text,false);
							}
						});
						if(username.equals("null")) {
							mainClient.clearRegisterPane();
							Platform.runLater(new Runnable() {
								@Override public void run() {
									mainClient.usernameTextFieldRegister.requestFocus();
								}
							});
						}else {
							mainClient.changeToLoginAlternative();
						}
						break;

					}
					 */


				} catch (Exception e) {
					e.printStackTrace();
					close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updatedInfoUserFromJSON(JSONObject info) {
		int lvlUser = Integer.parseInt(info.get("lvlUser").toString());
		int expUser = Integer.parseInt(info.get("expUser").toString());
		int numMensagens = Integer.parseInt(info.get("numMessages").toString());
		int numWordsWritten = Integer.parseInt(info.get("numWords").toString());
		mainClient.user.setUserLvl(lvlUser);
		mainClient.user.setUserExp(expUser);
		mainClient.user.setMessagesSent(numMensagens);
		mainClient.user.setWordsWritten(numWordsWritten);
	}

	private void updatedInfoFriendFromJSON(String userFriend, JSONObject info) {
		int lvlUser = Integer.parseInt(info.get("lvlUser").toString());
		int expUser = Integer.parseInt(info.get("expUser").toString());
		int numMensagens = Integer.parseInt(info.get("numMessages").toString());
		int numWordsWritten = Integer.parseInt(info.get("numWords").toString());
		for(User u : friends) {
			if(u.getUsername().equals(userFriend)) {
				u.setUserLvl(lvlUser);
				u.setUserExp(expUser);
				u.setMessagesSent(numMensagens);
				u.setWordsWritten(numWordsWritten);
				break;
			}
		}
	}

	private User createUserFromJSON(JSONObject info) {
		String username = info.get("username").toString();
		String firstName = info.get("firstName").toString();
		String lastName = info.get("lastName").toString();
		int age = Integer.parseInt(info.get("age").toString());
		int lvlUser = Integer.parseInt(info.get("lvlUser").toString());
		int expUser = Integer.parseInt(info.get("expUser").toString());
		int numMensagens = Integer.parseInt(info.get("numMessages").toString());
		int numWordsWritten = Integer.parseInt(info.get("numWords").toString());
		User u = new User(username,firstName,lastName,age,lvlUser,expUser,numMensagens,numWordsWritten);
		return u;
	}

	public void close() {
		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
