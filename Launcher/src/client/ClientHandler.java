package client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.Timer;

import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import controllers.ControllerChat;
import controllers.ControllerLauncher;
import controllers.Side;
import controllers.Type;
import dataHandler.AlertBox;
import dataHandler.Notifications;
import dataHandler.User;
import dataHandler.LoggerHandle;
import dataHandler.Pontuation;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import startLaunch.Chat;

public class ClientHandler extends Thread{

	private ControllerChat mainClient;
	private Socket socket;
	private LoggerHandle lh = null;
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

	public ClientHandler(Socket socket,Client client,ControllerChat mainClient, LoggerHandle lh) {
		this.socket = socket;
		this.mainClient = mainClient;
		this.lh = lh;
	}

	public void sendClientVersion(String version) {
		int code = 11;
		try {
			// enviar o codigo
			out.writeObject(code);

			//enviar a vers�o
			out.writeObject(version);

			lh.log("INFO", "Login authentication requested sucessfully");
		}catch(Exception e) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					lh.log("WARNING", "Could not connect to server");
					AlertBox.display("Error", "Can't connect to server",Color.RED,false);
				}
			});
		}

	}

	protected void sendMessageCase0(String user,String password) {
		int code = 0;
		try {
			// enviar o codigo
			out.writeObject(code);

			// enviar o nome do user
			out.writeObject(user);

			// enviar a pass do user
			out.writeObject(password);
			lh.log("INFO", "Login authentication requested sucessfully");
		}catch(Exception e) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					lh.log("WARNING", "Could not connect to server");
					AlertBox.display("Error", "Can't connect to server",Color.RED,false);
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
					AlertBox.display("Error", "Can't connect to server",Color.RED,true);
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
					AlertBox.display("Error", "Can't connect to server",Color.RED,false);
				}
			});
		}




	}



	protected void sendMessageCase7(String userSentInvite,String userTarget) {
		int code = 7;

		try {
			// enviar o codigo
			out.writeObject(code);

			//enviar o nome do user que estah a enviar o pedido de amizade
			out.writeObject(userSentInvite);

			//enviar o nome do user que estah a receber o pedido de amizade
			out.writeObject(userTarget);


		}catch(Exception e) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					AlertBox.display("Error", "Can't connect to server",Color.RED,false);
				}
			});
		}


		//enviar o codigo

		//enviar o nome do user que pediu convite
		//enviar o nome do user que vai receber convite

	}

	protected void sendMessageCase8(Boolean result,String userReceivedInvite,String userSentInvite) {
		int code = 8;

		try {
			// enviar o codigo
			out.writeObject(code);

			//enviar o nome do user que estah a enviar o pedido de amizade
			out.writeObject(userSentInvite);

			//enviar o nome do user que estah a receber o pedido de amizade
			out.writeObject(userReceivedInvite);

			out.writeObject(new Boolean(true));


		}catch(Exception e) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					AlertBox.display("Error", "Can't connect to server",Color.RED,false);
				}
			});
		}

		//enviar o codigo
		//enviar o resultado final do convite (accept ou decline)
		//enviar o nome do user que respondeu ao convite
		//enviar o nome do user que enviou o pedido

	}

	protected void sendMessageCase10(String username,String password,String firstname,String lastname,int age, String email) {
		int code = 10;
		JSONObject registerInfo = createObjWithInfo(username, password,firstname,lastname,age,email);

		try {
			// enviar o codigo
			out.writeObject(code);

			// enviar a informa��o do user que pretende registar
			out.writeObject(registerInfo);
			lh.log("INFO", "Registation request was send sucessfully");

		}catch(Exception e) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					lh.log("WARNING", "Could not connect to server");
					AlertBox.display("Error", "Can't connect to server",Color.RED,false);
				}
			});
		}

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
									new KeyValue(mainClient.chatPane_HBox_Left_ScrollPane.vvalueProperty(), 1)));
					animation.play();

					try {
						try {
							codeNumber = (int) in.readObject();
						} catch (Exception e) {
							run = false;
							lh.log("WARNING", "Connecting with server has been lost!", e);
							mainClient.chatPane_HBox_Left_Handle_Input_TextArea.setDisable(true);
							mainClient.chatPane_HBox_Left_Handle_Input_Button.setDisable(true);
							Platform.runLater(new Runnable() {
								@Override public void run() {
									AlertBox.display("Error", "Connecting with server has been lost!",Color.RED,false);
								}
							});
							//System.exit(-1);
						}

						switch(codeNumber) {
						case 0:
							//receber o boolean
							Boolean authBool = (Boolean) in.readObject();

							if(authBool) {
								//receber info do username
								JSONObject infoUser = (JSONObject) in.readObject();
								User user =  createUserFromJSON(infoUser);

								mainClient.user.setUsername(user.getUsername());
								mainClient.user.setAge(user.getAge());
								mainClient.user.setFirstName(user.getFirstName());
								mainClient.user.setLastName(user.getLastName());
								mainClient.user.setEmail(user.getEmail());
								mainClient.user.setUserLvl(user.getUserLvl());
								mainClient.user.setUserExp(user.getUserExp());
								mainClient.user.setUserParcialExp(user.getUserParcialExp());
								mainClient.user.setMessagesSent(user.getMessagesSent());
								mainClient.user.setWordsWritten(user.getWordsWritten());


								lh.log("INFO", "User: "+user.getUsername());
								lh.log("INFO", "Started to loading the aplication with the detail of user.");
								//inicio
								Platform.runLater(new Runnable() {
									@Override public void run() {
										mainClient.updateSceneToMenu();
										mainClient.addMessageToScrollPane("You just connected to the server",null,actualTime,null,Type.LOGIN);
										mainClient.dashboardPane.setVisible(true);
										mainClient.loadingScreen_Label.setText("Welcome, "+user.getFullName()+", to Our Chat");
										mainClient.main_Vbox_Left_QuickInfo_label_FullName.setText(user.getFullName());
										mainClient.main_Vbox_Left_QuickInfo_Label_Lvl.setText(String.valueOf(user.getUserLvl()));
										mainClient.profilePane_Label_Level.setText(String.valueOf(user.getUserLvl()));
										mainClient.profilePane_Details_Label_Firstname.setText(user.getFirstName());
										mainClient.profilePane_Details_Label_Lastname.setText(user.getLastName());
										mainClient.profilePane_Details_Label_Age.setText(String.valueOf(user.getAge()));
										mainClient.profilePane_Details_Label_Email.setText(user.getEmail());
										mainClient.profilePane_Statistics_Label_MessagesSend.setText(String.valueOf(user.getMessagesSent()));
										mainClient.profilePane_Statistics_Label_WordsWritten.setText(String.valueOf(user.getWordsWritten()));
										mainClient.profilePane_Statistics_Label_ArchivementsCompleted.setText("0");
										mainClient.profilePane_Statistics_Label_TotalExperience.setText(String.valueOf(user.getUserExp()));
										mainClient.settingsPane_TextField_Firstname.setPromptText(user.getFirstName());
										mainClient.settingsPane_TextField_Lastname.setPromptText(user.getLastName());
										mainClient.settingsPane_TextField_Age.setPromptText(String.valueOf(user.getAge()));
										mainClient.settingsPane_TextField_Email.setPromptText(user.getEmail());
										double parcialExp = Pontuation.getProgressBarValue(user.getUserParcialExp(),user.getUserLvl());
										mainClient.main_Vbox_Left_QuickInfo_ProgressBar.setProgress(parcialExp);
										mainClient.profilePane_ProgressIndicator.setProgress(parcialExp);
										int percentage = (int) ((Math.round(parcialExp * 100.0) / 100.0)*100);
										mainClient.profilePane_Label_Percentage_Experience.setText(percentage+"%");
									}
								});

								//receber numero de amigos
								int numberUsers = (int) in.readObject();

								Platform.runLater(new Runnable() {
									@Override public void run() {
										mainClient.dashboardPane_HBox_Right_VBox_Servers_Status_MainServer_Label_NumberUsers.setText(String.valueOf(numberUsers));
									}
								});

								if(numberUsers != 0) {
									for(int i=0;i<numberUsers;i++) {
										JSONObject userOnline = (JSONObject) in.readObject();
										User u = createUserFromJSON(userOnline);
										if(!u.getUsername().equals(mainClient.user.getUsername())){
											mainClient.usersOnline.add(u);
											mainClient.addUserOnlineToScrollPane(u);
										}

										Boolean ehFriend = (Boolean) in.readObject();
										if(ehFriend) {
											mainClient.friends.add(u);
										}
									}
								}

								int numberFriendsRequests = (int) in.readObject();

								for(int i=0;i<numberFriendsRequests;i++) {
									JSONObject userOnline = (JSONObject) in.readObject();
									User u = createUserFromJSON(userOnline);
									Platform.runLater(new Runnable() {
										@Override public void run() {
											mainClient.addFriendRequestToScrollPane(u,actualTime);
											mainClient.addNotifyPopUp("Friend Request", u.getFullName()+" sent you a friend request");
											mainClient.numberNotifyPopUp.set(mainClient.numberNotifyPopUp.get()+1);
										}
									});
								}

								//final
								Timer t = new Timer(4000, new ActionListener() {

									@Override
									public void actionPerformed(java.awt.event.ActionEvent e) {
										Platform.runLater(new Runnable() {
											@Override public void run() {
												FadeTransition fadeTransition = new FadeTransition();
												fadeTransition.setDuration(Duration.millis(1000));
												fadeTransition.setNode(mainClient.loadingScreen);
												fadeTransition.setFromValue(1);
												fadeTransition.setToValue(0);
												fadeTransition.play();

												fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {
													@Override
													public void handle(ActionEvent event) {
														mainClient.loadingScreen.setVisible(false);
													}
												});

											}
										});
									}
								});
								t.setRepeats(false);
								t.start();

								lh.log("INFO", "Loading the aplication with the detail of user terminated.");
							}else {
								String error = (String) in.readObject();
								Platform.runLater(new Runnable() {
									@Override public void run() {
										lh.log("INFO", "Client could not login because "+error);
										mainClient.launcher_VBox_Pane_LoginVBox.setVisible(true);
										mainClient.launcher_VBox_Pane_LoginVBox_TextField_Username.clear();
										mainClient.launcher_VBox_Pane_LoginVBox_TextField_Password.clear();
										mainClient.launcher_VBox_Pane_LoginVBox_TextField_Username.requestFocus();
										mainClient.launcher_VBox_Pane_LoginVBox_Label_ErrorLogin.setText(error);
										mainClient.launcher_VBox_Pane_LoginVBox_Label_ErrorLogin.setVisible(true);
										Timer t = new Timer(1500, new ActionListener() {

											@Override
											public void actionPerformed(java.awt.event.ActionEvent e) {
												Platform.runLater(new Runnable() {
													@Override public void run() {
														mainClient.launcher_VBox_Pane_LoginVBox_Label_ErrorLogin.setVisible(false);
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

							// receber a info do user que se conectou
							JSONObject userOnline = (JSONObject) in.readObject();
							User u = createUserFromJSON(userOnline);
							Platform.runLater(new Runnable() {
								@Override public void run() {
									mainClient.usersOnline.add(u);
									mainClient.addUserOnlineToScrollPane(u);
									mainClient.addMessageToScrollPane(u.getFullName()+" just connected to the server",null,actualTime,null,Type.LOGIN);
								}
							});
							break;
						case 2:

							//receber o username que se desconectou
							String usernameDisconnect = (String) in.readObject();

							Platform.runLater(new Runnable() {
								@Override public void run() {
									for(int i=0;i<mainClient.usersOnline.size();i++) {
										User targetUser = mainClient.usersOnline.get(i);

										if(targetUser.getUsername().equals(usernameDisconnect)) {
											mainClient.usersOnline.remove(targetUser);
											int numberUsers = Integer.parseInt(mainClient.dashboardPane_HBox_Right_VBox_Servers_Status_MainServer_Label_NumberUsers.getText());
											mainClient.dashboardPane_HBox_Right_VBox_Servers_Status_MainServer_Label_NumberUsers.setText(String.valueOf(numberUsers-1));
											mainClient.removeUserOnlineToScrollPane(i);
											mainClient.addMessageToScrollPane(targetUser.getFullName()+" has disconnected to the server",null,actualTime,null,Type.LOGOUT);
										}

									}
								}
							});
							break;
						case 3:
							//receber o owner da mensagem enviada para o servidor
							String ownerOfMessage = (String) in.readObject();
							JSONObject infoOwnerMessage = (JSONObject) in.readObject();
							String message = (String) in.readObject();
							Platform.runLater(new Runnable() {
								@Override public void run() {
									if (mainClient.user.getFullName().equals(ownerOfMessage)) {
										updatedUserInfoFromJSON(infoOwnerMessage);
										mainClient.addMessageToScrollPane(message,ownerOfMessage,actualTime,Side.RIGHT,Type.MESSAGE);
									}else {
										updatedUserOnlineInfoFromJSON(ownerOfMessage,infoOwnerMessage);
										mainClient.addMessageToScrollPane(message,ownerOfMessage,actualTime,Side.LEFT,Type.MESSAGE);
										if(!mainClient.chatPane.isVisible()) {
											mainClient.numberNotifyChat.set(mainClient.numberNotifyChat.get()+1);
										}
									}
								}
							});
							break;
						case 5:
							break;
						case 6:
							break;
						case 7:
							//receber o nome do user que pediu amizade a este cliente
							String ownerOfFriendRequest = (String) in.readObject();
							User userOfFriendRequest = getUserByUsername(ownerOfFriendRequest);
							Platform.runLater(new Runnable() {
								@Override public void run() {
									for(User u : mainClient.usersOnline) {
										if(u.getUsername().equals(userOfFriendRequest.getUsername())) {
											u.setExistsFriendRequest(true);
											break;
										}
									}
									mainClient.addFriendRequestToScrollPane(userOfFriendRequest,actualTime);
									mainClient.addNotifyPopUp("Friend Request", userOfFriendRequest.getFullName()+" sent you a friend request");
									mainClient.numberNotifyPopUp.set(mainClient.numberNotifyPopUp.get()+1);
								}
							});
							break;
						case 8:
							Boolean result = (Boolean) in.readObject();

							if(result) {
								Boolean ehReceptor = (Boolean) in.readObject();
								JSONObject infoUser = (JSONObject) in.readObject();
								User friend = createUserFromJSON(infoUser);
								
								Platform.runLater(new Runnable() {
									@Override public void run() {
										for(User u : mainClient.usersOnline) {
											if(u.getUsername().equals(friend.getUsername())) {
												u.setExistsFriendRequest(false);
												u.setExistsFriendShip(true);
												break;
											}
										}
										mainClient.addFriendToScrollPane(friend);
										mainClient.numberNotifyPopUp.set(mainClient.numberNotifyPopUp.get()+1);
										if(ehReceptor) {
											mainClient.addNotifyPopUp("Friend Request", "You accept the friend request of "+friend.getFullName());
											mainClient.removeFriendRequestToScrollPane(friend.getUsername());
										}else {
											mainClient.addNotifyPopUp("Friend Request", friend.getFullName()+" accepted your friend request.");
										}
										
									}
								});

							}else {
								
							}


							break;
						case 9:
							break;
						case 10:
							//receber o boolean 
							Boolean isRegistered = (Boolean) in.readObject();
							String textFeedback = (String) in.readObject();

							Platform.runLater(new Runnable() {
								@Override public void run() {
									if(!isRegistered) {
										AlertBox.display("Details",textFeedback,Color.RED,false);
										mainClient.launcher_VBox_Pane_RegisterVBox_TextField_Username.clear();
										mainClient.launcher_VBox_Pane_RegisterVBox.setVisible(true);
										mainClient.launcher_VBox_Pane_RegisterVBox_TextField_Username.requestFocus();
									}else {
										AlertBox.display("Details",textFeedback,Color.GREEN,false);
										mainClient.launcher_VBox_Pane_LoginVBox.setVisible(true);
										mainClient.launcher_VBox_Pane_RegisterVBox_TextField_Firstname.clear();
										mainClient.launcher_VBox_Pane_RegisterVBox_TextField_Lastname.clear();
										mainClient.launcher_VBox_Pane_RegisterVBox_TextField_Age.clear();
										mainClient.launcher_VBox_Pane_RegisterVBox_TextField_Username.clear();
										mainClient.launcher_VBox_Pane_RegisterVBox_TextField_Email.clear();
										mainClient.launcher_VBox_Pane_RegisterVBox_TextField_Password.clear();
										mainClient.launcher_VBox_Pane_RegisterVBox_TextField_PasswordAgain.clear();
									}
								}
							});

							/**
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
							 */
							break;
						case 11:
							long length;
							int bytesRead;
							int current = 0;
							FileOutputStream fos = null;
							BufferedOutputStream bos = null;

							Boolean isUpdated = (Boolean) in.readObject();

							if(isUpdated) {
								Platform.runLater(new Runnable() {
									@Override public void run() {
										mainClient.updateSceneToLauncher();
									}
								});

							}else {
								Platform.runLater(new Runnable() {
									@Override public void run() {
										mainClient.updater_Label.setText("Updating...");
									}
								});
								length = (long) in.readObject();
								byte [] buffer  = new byte [(int) length];
								String path = Chat.class.getProtectionDomain().getCodeSource().getLocation().getPath();
								String decodedPath = URLDecoder.decode(path, "UTF-8");
								fos = new FileOutputStream(decodedPath);
								bos = new BufferedOutputStream(fos);
								bytesRead = in.read(buffer,0,buffer.length);
								current = bytesRead;

								if(bytesRead<length) {
									while(bytesRead!=0) {
										bytesRead = in.read(buffer, current, (buffer.length-current));
										if(bytesRead >= 0)
											current += bytesRead;
									}
								}

								bos.write(buffer, 0 , current);
								bos.flush();
								bos.close();

								/**
				            	Platform.runLater(new Runnable() {
									@Override public void run() {
										mainClient.updater_Label.setText("Update completed");
									}
								});
								 */

								mainClient.restartApplication(null, null);
							}
							break;
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

	private User getUserByUsername(String ownerOfFriendRequest) {
		for(User u : mainClient.usersOnline) {
			if(u.getUsername().equals(ownerOfFriendRequest)) {
				return u;
			}
		}
		return null;
	}

	private void updatedUserInfoFromJSON(JSONObject info) {
		int lvlUser = Integer.parseInt(info.get("lvlUser").toString());
		int expUser = Integer.parseInt(info.get("expUser").toString());
		int parcialExpUser = Integer.parseInt(info.get("parcialExpUser").toString());
		int numMensagens = Integer.parseInt(info.get("numMessages").toString());
		int numWordsWritten = Integer.parseInt(info.get("numWords").toString());
		mainClient.user.setUserLvl(lvlUser);
		mainClient.user.setUserExp(expUser);
		mainClient.user.setUserParcialExp(parcialExpUser);
		mainClient.user.setMessagesSent(numMensagens);
		mainClient.user.setWordsWritten(numWordsWritten);
	}

	private void updatedUserOnlineInfoFromJSON(String user, JSONObject info) {
		int lvlUser = Integer.parseInt(info.get("lvlUser").toString());
		int expUser = Integer.parseInt(info.get("expUser").toString());
		int parcialExpUser = Integer.parseInt(info.get("parcialExpUser").toString());
		int numMensagens = Integer.parseInt(info.get("numMessages").toString());
		int numWordsWritten = Integer.parseInt(info.get("numWords").toString());
		for(User u : mainClient.usersOnline) {
			if(u.getFullName().equals(user)) {
				u.setUserLvl(lvlUser);
				u.setUserExp(expUser);
				u.setUserParcialExp(parcialExpUser);
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
		String email = info.get("email").toString();
		int age = Integer.parseInt(info.get("age").toString());
		int lvlUser = Integer.parseInt(info.get("lvlUser").toString());
		int expUser = Integer.parseInt(info.get("expUser").toString());
		int parcialExpUser = Integer.parseInt(info.get("parcialExpUser").toString());
		int numMensagens = Integer.parseInt(info.get("numMessages").toString());
		int numWordsWritten = Integer.parseInt(info.get("numWords").toString());
		boolean existsFriendRequest = Boolean.parseBoolean(info.get("existsFriendRequest").toString());
		boolean existsFriendShip = Boolean.parseBoolean(info.get("existsFriendShip").toString());
		User u = new User(username,firstName,lastName,age,email,lvlUser,expUser,parcialExpUser,numMensagens,numWordsWritten,existsFriendRequest,existsFriendShip);
		return u;
	}

	@SuppressWarnings("unchecked")
	private JSONObject createObjWithInfo(String username,String password,String firstname,String lastname,int age, String email) {
		JSONObject obj = new JSONObject();
		obj.put("username",username);
		obj.put("password", password);
		obj.put("lastname", lastname);
		obj.put("age", age);
		obj.put("email",email);
		obj.put("firstname", firstname);
		return obj;
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
