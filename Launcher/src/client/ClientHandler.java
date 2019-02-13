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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

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

	public ClientHandler(Socket socket,Client client,ControllerChat mainClient, LoggerHandle lh) {
		this.socket = socket;
		this.mainClient = mainClient;
		this.lh = lh;
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
					AlertBox.display("Error", "Can't connect to server",false);
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
								//receber info do username
								JSONObject infoUser = (JSONObject) in.readObject();
								User user = mainClient.user = createUserFromJSON(infoUser);
								lh.log("INFO", "User: "+user.getUsername());
								lh.log("INFO", "Started to loading the aplication with the detail of user.");
								//inicio
								Platform.runLater(new Runnable() {
									@Override public void run() {
										mainClient.updateSceneToMenu();
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
										mainClient.usersOnline.add(u);
									}
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
							/**
							//receber o user que se conectou
							String userConnected = (String) in.readObject();
							//receber a lista com as conexoes atualizadas
							mainClient.connections = (String) in.readObject();
							textOutput = "The user '"+ userConnected+"' just connected";
							side = Side.LEFT;
							type= Type.LOGIN;
							userToSend = userConnected;
							atualizarClient = true;
							*/
							break;
						case 2:
							/**
							//receber o user que se desconectou
							String userDisconnect = (String) in.readObject();

							//receber a lista com as conexoes atualizadas
							mainClient.connections = (String) in.readObject();

							textOutput = "The user '"+userDisconnect+"' disconnected";
							side = Side.LEFT;
							type=Type.LOGOUT;
							userToSend = userDisconnect;
							atualizarClient = true;
							*/
							break;
						case 3:
							/**
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
							*/
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
		int parcialExpUser = Integer.parseInt(info.get("parcialExpUser").toString());
		int numMensagens = Integer.parseInt(info.get("numMessages").toString());
		int numWordsWritten = Integer.parseInt(info.get("numWords").toString());
		mainClient.user.setUserLvl(lvlUser);
		mainClient.user.setUserExp(expUser);
		mainClient.user.setUserParcialExp(parcialExpUser);
		mainClient.user.setMessagesSent(numMensagens);
		mainClient.user.setWordsWritten(numWordsWritten);
	}

	private void updatedInfoFriendFromJSON(String userFriend, JSONObject info) {
		int lvlUser = Integer.parseInt(info.get("lvlUser").toString());
		int expUser = Integer.parseInt(info.get("expUser").toString());
		int parcialExpUser = Integer.parseInt(info.get("parcialExpUser").toString());
		int numMensagens = Integer.parseInt(info.get("numMessages").toString());
		int numWordsWritten = Integer.parseInt(info.get("numWords").toString());
		for(User u : friends) {
			if(u.getUsername().equals(userFriend)) {
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
		User u = new User(username,firstName,lastName,age,email,lvlUser,expUser,parcialExpUser,numMensagens,numWordsWritten);
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
