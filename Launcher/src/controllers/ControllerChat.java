package controllers;


import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

//import com.gluonhq.charm.glisten.control.ProgressBar; 
//import com.gluonhq.charm.glisten.control.ProgressIndicator;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Timer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.sun.jna.Pointer;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import client.Client;

import static com.sun.jna.platform.win32.WinUser.GWL_STYLE;
import com.sun.jna.*;

import dataHandler.AlertBox;
import dataHandler.Informations;
import dataHandler.Notifications;
import dataHandler.Pontuation;
import dataHandler.PropertiesFile;
import dataHandler.ProtectionBadWords;
import dataHandler.User;
import dataHandler.LoggerHandle;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import startLaunch.Chat;
import sun.applet.Main;
import javafx.scene.media.MediaPlayer;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.TextArea;

public class ControllerChat {

	private static final Border darkblue = new Border(new BorderStroke(Color.rgb(32,33,35),
			BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(3)));
	private static final Pattern VALIDEMAIL = 
			Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	private static final int MAX_CHARS = 16;

	private static double xOffset = 0;
	private static double yOffset = 0;
	
	private final String urlThemeOne = this.getClass().getResource("themeOne.css").toExternalForm();
	private final String urlThemeTwo = this.getClass().getResource("themeTwo.css").toExternalForm();

	private Stage stage;
	public String version;
	private LoggerHandle lh = null;
	private PropertiesFile pf = null;
	
	String color = "";
	String language = "";

	private AnchorPane launcherPane;
	private AnchorPane appPane;
	private VBox updaterPane;

	private Client c;
	public User user;
	public ArrayList<User> usersOnline = null;
	public ArrayList<User> friends = null;

	public IntegerProperty numberNotifyChat = new SimpleIntegerProperty();
	public IntegerProperty numberNotifyFriends = new SimpleIntegerProperty();
	public IntegerProperty numberNotifyPopUp = new SimpleIntegerProperty();

	public ControllerChat(Stage primaryStage,String version, LoggerHandle lh, PropertiesFile pf) throws IOException {
		usersOnline = new ArrayList<User>();
		friends = new ArrayList<User>();
		this.stage = primaryStage;
		this.version = version;
		this.lh = lh;
		this.pf = pf;
		language = pf.getProperty("language");
    	color = pf.getProperty("color");
		numberNotifyChat.set(0);
		numberNotifyFriends.set(0);
		numberNotifyPopUp.set(0);
		user = new User("","","",0,"",0,0,0,0,0,false,false);
		stage.initStyle(StageStyle.UNDECORATED);

		loadUpdater();
		loadLauncher();
		loadChat();
	}

	private void loadUpdater() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("updater.fxml"));
			loader.setController(this);
			updaterPane = (VBox) loader.load();
			updaterPane.setBorder(darkblue);

			updaterPane.setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					stage.setOpacity(0.9);
					xOffset = stage.getX() - event.getScreenX();
					yOffset = stage.getY() - event.getScreenY();
				}
			});

			updaterPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					stage.setOpacity(0.9);
					stage.setX(event.getScreenX() + xOffset);
					stage.setY(event.getScreenY() + yOffset);
				}
			});

			updaterPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					stage.setOpacity(1);
				}
			});
			lh.log("INFO", "Updater loaded");

		}catch(Exception e) {
			lh.log("SEVERE", "Updater could not be loaded loaded!");
		}

	}

	private void loadLauncher() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("NewLauncher.fxml"));
			loader.setController(this);
			launcherPane = (AnchorPane) loader.load();
			launcherPane.setBorder(darkblue);
			

			launcher_HBox_Top.setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					stage.setOpacity(0.9);
					xOffset = stage.getX() - event.getScreenX();
					yOffset = stage.getY() - event.getScreenY();
				}
			});

			launcher_HBox_Top.setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					stage.setOpacity(0.9);
					stage.setX(event.getScreenX() + xOffset);
					stage.setY(event.getScreenY() + yOffset);
				}
			});

			launcher_HBox_Top.setOnMouseReleased(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					stage.setOpacity(1);
				}
			});
			lh.log("INFO", "Launcher loaded");
		}catch(Exception e) {
			lh.log("SEVERE", "Launcher could not be loaded loaded!");
		}

	}

	private boolean inHierarchy(Node node, Node potentialHierarchyElement) {
		if (potentialHierarchyElement == null) {
			return true;
		}
		while (node != null) {
			if (node == potentialHierarchyElement) {
				return true;
			}
			node = node.getParent();
		}
		return false;
	}

	private void loadChat() throws IOException {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("NewGui.fxml"));
			loader.setController(this);
			appPane = (AnchorPane) loader.load();
			appPane.setBorder(darkblue);
			if(color.equals("red")) {
				appPane.getStylesheets().add(urlThemeOne);
			}else {
				appPane.getStylesheets().add(urlThemeTwo);
			}
			

			appPane.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
				if (!inHierarchy(evt.getPickResult().getIntersectedNode(), main_Notifications_PopUP)) {
					main_Notifications_PopUP.setVisible(false);
				}
			});


			main_HBox_Top.setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					stage.setOpacity(0.9);
					xOffset = stage.getX() - event.getScreenX();
					yOffset = stage.getY() - event.getScreenY();
				}
			});

			main_HBox_Top.setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					stage.setOpacity(0.9);
					stage.setX(event.getScreenX() + xOffset);
					stage.setY(event.getScreenY() + yOffset);
				}
			});

			main_HBox_Top.setOnMouseReleased(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					stage.setOpacity(1);
				}
			});

			lh.log("INFO", "Chat loaded");
		}catch(Exception e) {
			e.printStackTrace();
			lh.log("SEVERE", "Chat could not be loaded!");
		}

	}

	public void startController() {

		try {
			c = new Client(this,lh);
			try {
				launcher_VBox_Pane_LoginVBox_TextField_Username.setTextFormatter(new TextFormatter<String>(change -> 
				change.getControlNewText().length() <= MAX_CHARS ? change : null));
				launcher_VBox_Pane_LoginVBox_TextField_Password.setTextFormatter(new TextFormatter<String>(change -> 
				change.getControlNewText().length() <= MAX_CHARS ? change : null));

				launcher_VBox_Pane_RegisterVBox_TextField_Firstname.setTextFormatter(new TextFormatter<String>(change -> 
				change.getControlNewText().length() <= MAX_CHARS ? change : null));
				launcher_VBox_Pane_RegisterVBox_TextField_Lastname.setTextFormatter(new TextFormatter<String>(change -> 
				change.getControlNewText().length() <= MAX_CHARS ? change : null));
				launcher_VBox_Pane_RegisterVBox_TextField_Username.setTextFormatter(new TextFormatter<String>(change -> 
				change.getControlNewText().length() <= MAX_CHARS ? change : null));
				launcher_VBox_Pane_RegisterVBox_TextField_Password.setTextFormatter(new TextFormatter<String>(change -> 
				change.getControlNewText().length() <= MAX_CHARS ? change : null));
				launcher_VBox_Pane_RegisterVBox_TextField_PasswordAgain.setTextFormatter(new TextFormatter<String>(change -> 
				change.getControlNewText().length() <= MAX_CHARS ? change : null));

				//profilePane_Statistics_Label_WordsWritten.textProperty().bind();
				//profilePane_Statistics_Label_MessagesSend.textProperty().bind(new SimpleStringProperty(String.valueOf(user.getMessagesSent())));
				//profilePane_Statistics_Label_TotalExperience.textProperty().bind(new SimpleStringProperty(String.valueOf(user.getUserExp())));

				user.getFirstNameProperty().addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						settingsPane_TextField_Firstname.setPromptText(user.getFirstName());
						profilePane_Details_Label_Firstname.setText(user.getFirstName());
					}
				});

				user.getLastNameProperty().addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						settingsPane_TextField_Lastname.setPromptText(user.getLastName());
						profilePane_Details_Label_Lastname.setText(user.getLastName());
					}
				});

				user.getEmailProperty().addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						settingsPane_TextField_Email.setPromptText(user.getEmail());
						profilePane_Details_Label_Email.setText(user.getEmail());
					}
				});

				user.getAgeProperty().addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						settingsPane_TextField_Age.setPromptText(String.valueOf(user.getAge()));
						profilePane_Details_Label_Age.setText(String.valueOf(user.getAge()));

					}
				});

				user.getUserLvlProperty().addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						profilePane_Label_Level.setText(String.valueOf(user.getUserLvl()));
						main_Vbox_Left_QuickInfo_Label_Lvl.setText(String.valueOf(user.getUserLvl()));

					}
				});

				user.getUserExpProperty().addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						profilePane_Statistics_Label_TotalExperience.setText(String.valueOf(user.getUserExp()));

					}
				});

				user.getUserParcialExpProperty().addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						double parcialExp = Pontuation.getProgressBarValue(user.getUserParcialExp(),user.getUserLvl());
						//main_Vbox_Left_QuickInfo_ProgressBar.setProgress(parcialExp);
						animationProgressBar(main_Vbox_Left_QuickInfo_ProgressBar,parcialExp);
						profilePane_ProgressIndicator.setProgress(parcialExp);
						int percentage = (int) ((Math.round(parcialExp * 100.0) / 100.0)*100);
						profilePane_Label_Percentage_Experience.setText(percentage+"%");

					}
				});


				user.getMessagesSentProperty().addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						profilePane_Statistics_Label_MessagesSend.setText(String.valueOf(user.getMessagesSent()));

					}
				});

				user.getWordsWrittenProperty().addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						profilePane_Statistics_Label_WordsWritten.setText(String.valueOf(user.getWordsWritten()));

					}
				});

				numberNotifyChat.addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						if(numberNotifyChat.get() == 0) {
							main_Vbox_Left_HBox_Chat_Notify.setVisible(false);
						}else {
							if(!chatPane.isVisible()) {
								main_Vbox_Left_HBox_Chat_Notify.setText(String.valueOf(numberNotifyChat.get()));
								main_Vbox_Left_HBox_Chat_Notify.setVisible(true);
							}
						}
					}
				});

				numberNotifyPopUp.addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						if(numberNotifyPopUp.get() == 0) {
							main_bell_notifications_Label.setVisible(false);
						}else {
							if(!main_Notifications_PopUP.isVisible()) {
								main_bell_notifications_Label.setText(String.valueOf(numberNotifyPopUp.get()));
								main_bell_notifications_Label.setVisible(true);
							}
						}
					}
				});

				launcher_Label_Version.setText("v"+version);
				main_Version_Label.setText("v"+version);

				Scene scene = new Scene(updaterPane);

				scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
					final KeyCombination keyComb = new KeyCodeCombination(KeyCode.F4,
							KeyCombination.ALT_DOWN);
					public void handle(KeyEvent ke) {
						if (keyComb.match(ke)) {
							System.exit(0);
						}
					}
				});

				stage.setScene(scene);
				stage.show(); 

				Timer t = new Timer(1500, new ActionListener() {

					@Override
					public void actionPerformed(java.awt.event.ActionEvent e) {
						Platform.runLater(new Runnable() {
							@Override public void run() {
								c.sendVersion(version);
							}
						});
					}
				});
				t.setRepeats(false);
				t.start();

				long lhwnd = com.sun.glass.ui.Window.getWindows().get(0).getNativeWindow();
				Pointer lpVoid = new Pointer(lhwnd);
				WinDef.HWND hwnd = new WinDef.HWND(lpVoid);
				final User32 user32 = User32.INSTANCE;
				int oldStyle = user32.GetWindowLong(hwnd, GWL_STYLE);
				int newStyle = oldStyle | 0x00020000;//WS_MINIMIZEBOX
				user32.SetWindowLong(hwnd, GWL_STYLE, newStyle);

				lh.log("INFO", "Launcher scene was set succefully in stage.");
			}catch(Exception e) {
				lh.log("SEVERE", "Could not set scene Launcher to stage!",e);

			}

		} catch (IOException e) {
			lh.log("WARNING", "Could not connect to the server!");
			Platform.runLater(new Runnable() {
				@Override public void run() {
					AlertBox.display("Error", "Can't connect to server",Color.RED,true);
				}
			});
		}

	}


	public void updateSceneToMenu() {

		try {
			Scene scene = new Scene(appPane);


			FadeTransition fadeTransition = new FadeTransition();
			fadeTransition.setDuration(Duration.millis(500));
			fadeTransition.setNode(launcherPane);
			fadeTransition.setFromValue(1);
			fadeTransition.setToValue(0);
			fadeTransition.play();


			fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					stage.setScene(scene);
				}
			});

			scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
				final KeyCombination keyComb = new KeyCodeCombination(KeyCode.F4,
						KeyCombination.ALT_DOWN);
				public void handle(KeyEvent ke) {
					if (keyComb.match(ke)) {
						System.exit(0);
					}
				}
			});

			chatPane_HBox_Left_Handle_Input_TextArea.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
				final KeyCombination keyComb = new KeyCodeCombination(KeyCode.ENTER,
						KeyCombination.SHIFT_DOWN);
				public void handle(KeyEvent ke) {
					if (keyComb.match(ke)) {
						chatPane_HBox_Left_Handle_Input_TextArea.appendText("\n");
						ke.consume();
					}else if (ke.getCode().equals(KeyCode.ENTER)) {
						String texto = chatPane_HBox_Left_Handle_Input_TextArea.getText();
						if(!texto.equals("")) {
							c.sendMessagesToChat(texto);
							chatPane_HBox_Left_Handle_Input_TextArea.setText("");
						}
						ke.consume();
					}
				}
			});



			lh.log("INFO", "Updated scene succesfully to chat");
		}catch(Exception e) {
			lh.log("SEVERE", "Could not update scene to chat");
		}


	}

	public void updateSceneToLauncher() {
		Scene scene = new Scene(launcherPane);

		scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			final KeyCombination keyComb = new KeyCodeCombination(KeyCode.F4,
					KeyCombination.ALT_DOWN);
			public void handle(KeyEvent ke) {
				if (keyComb.match(ke)) {
					System.exit(0);
				}
			}
		});

		stage.setScene(scene);
		Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
		stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
		stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
		stage.show(); 
	}

	@FXML
	public Label updater_Label;

	@FXML
	public AnchorPane launcherAnchorPane;

	@FXML
	public VBox launcher_VBox;

	@FXML
	public Pane launcher_VBox_Pane;

	@FXML
	public VBox launcher_VBox_Pane_LoginVBox;

	@FXML
	public JFXTextField launcher_VBox_Pane_LoginVBox_TextField_Username;

	@FXML
	public JFXPasswordField launcher_VBox_Pane_LoginVBox_TextField_Password;

	@FXML
	public JFXButton launcher_VBox_Pane_LoginVBox_Button;

	@FXML
	public Label launcher_VBox_Pane_LoginVBox_Label_ChangeToRegister;

	@FXML
	public Label launcher_VBox_Pane_LoginVBox_Label_ErrorLogin;

	@FXML
	public VBox launcher_VBox_Pane_RegisterVBox;

	@FXML
	public JFXTextField launcher_VBox_Pane_RegisterVBox_TextField_Firstname;

	@FXML
	public JFXTextField launcher_VBox_Pane_RegisterVBox_TextField_Lastname;

	@FXML
	public JFXTextField launcher_VBox_Pane_RegisterVBox_TextField_Age;

	@FXML
	public JFXTextField launcher_VBox_Pane_RegisterVBox_TextField_Username;

	@FXML
	public JFXTextField launcher_VBox_Pane_RegisterVBox_TextField_Email;

	@FXML
	public JFXPasswordField launcher_VBox_Pane_RegisterVBox_TextField_Password;

	@FXML
	public JFXPasswordField launcher_VBox_Pane_RegisterVBox_TextField_PasswordAgain;

	@FXML
	public JFXButton launcher_VBox_Pane_RegisterVBox_Button;

	@FXML
	public Label launcher_VBox_Pane_RegisterVBox_Label_ChangeToLogin;

	@FXML
	public Label launcher_VBox_Pane_RegisterVBox_Label_ErrorRegister;

	@FXML
	public HBox launcher_HBox_Top;

	@FXML
	public FontAwesomeIconView launcher_Button_Minimize;

	@FXML
	public FontAwesomeIconView launcher_Button_Quit;
	
    @FXML
    private FontAwesomeIconView button_Configurations;

	@FXML
	public HBox launcher_HBox_Right_Version_HBox;

	@FXML
	public Label launcher_Label_Version;


	@FXML
	public VBox notifications_VBox;

	/////////////////////////////////////////////////////////////////
	/////////////////////////CHAT FXML VARIABLES/////////////////////
	/////////////////////////////////////////////////////////////////

	@FXML
	public VBox loadingScreen;

	@FXML
	public Label loadingScreen_Label;

	@FXML
	public AnchorPane mainAnchorPane;

	@FXML
	public VBox main_Vbox_Left;

	@FXML
	public Pane main_Vbox_Left_QuickInfo;

	@FXML
	public ProgressBar main_Vbox_Left_QuickInfo_ProgressBar;

	@FXML
	public Label main_Vbox_Left_QuickInfo_Label_Lvl;

	@FXML
	public Label main_Vbox_Left_QuickInfo_label_FullName;

	@FXML
	public HBox main_Vbox_Left_HBox_Search;

	@FXML
	public TextField main_Vbox_Left_HBox_Search_TextField;

	@FXML
	public HBox main_Vbox_Left_HBox_Dashboard;

	@FXML
	public HBox main_Vbox_Left_HBox_Chat;

	@FXML
	public Label main_Vbox_Left_HBox_Chat_Notify;

	@FXML
	public HBox main_Vbox_Left_HBox_Profile;

	@FXML
	public HBox main_Vbox_Left_HBox_Friends;

	@FXML
	public Label main_Vbox_Left_HBox_Friends_Notify;

	@FXML
	public HBox main_Vbox_Left_HBox_Settings;

	@FXML
	public Label linkedIn_Url;

	@FXML
	public Label github_Url;

	@FXML
	public VBox dashboardPane;

	@FXML
	public Pane dashboardPane_titlePane;

	@FXML
	public Label dashboardPane_TitlePane_Label;

	@FXML
	public HBox dashboardPane_HBox;

	@FXML
	public VBox dashboardPane_HBox_Left;

	@FXML
	public ScrollPane dashboardPane_HBox_Left_ScrollPane;

	@FXML
	public VBox dashboardPane_HBox_Left_ScrollPane_VBox;

	@FXML
	public VBox dashboardPane_HBox_Right;

	@FXML
	public VBox dashboardPane_HBox_Right_VBox_Servers_Status;

	@FXML
	public VBox dashboardPane_HBox_Right_VBox_Servers_Status_Main_Server;

	@FXML
	public Label dashboardPane_HBox_Right_VBox_Servers_Status_MainServer_Label_Status;

	@FXML
	public Label dashboardPane_HBox_Right_VBox_Servers_Status_MainServer_Label_NumberUsers;

	@FXML
	public VBox chatPane;

	@FXML
	public Pane chatPane_titlePane;

	@FXML
	public Label chatPane_TitlePane_Label;

	@FXML
	public HBox chatPane_HBox;

	@FXML
	public VBox chatPane_HBox_Left;

	@FXML
	public ScrollPane chatPane_HBox_Left_ScrollPane;

	@FXML
	public VBox chatPane_HBox_Left_ScrollPane_VBox;

	@FXML
	public HBox chatPane_HBox_Left_Handle_Input;

	@FXML
	public TextArea chatPane_HBox_Left_Handle_Input_TextArea;

	@FXML
	public FontAwesomeIconView chatPane_HBox_Left_Handle_Input_Button;

	@FXML
	public VBox chatPane_HBox_Right;

	@FXML
	public ScrollPane chatPane_HBox_Right_ScrollPane;

	@FXML
	public VBox chatPane_HBox_Right_ScrollPane_VBox;

	@FXML
	public VBox profilePane;

	@FXML
	public Pane profilePane_titlePane;

	@FXML
	public Label profilePane_TitlePane_Label;

	@FXML
	public ProgressIndicator profilePane_ProgressIndicator;

	@FXML
	public Label profilePane_Label_Level;

	@FXML
	public Label profilePane_Label_Percentage_Experience;

	@FXML
	public Label profilePane_Details_Label_Firstname;

	@FXML
	public Label profilePane_Details_Label_Lastname;

	@FXML
	public Label profilePane_Details_Label_Age;

	@FXML
	public Label profilePane_Details_Label_Email;

	@FXML
	public JFXButton profilePane_Details_Button_changeDetails;

	@FXML
	public Label profilePane_Statistics_Label_MessagesSend;

	@FXML
	public Label profilePane_Statistics_Label_WordsWritten;

	@FXML
	public Label profilePane_Statistics_Label_ArchivementsCompleted;

	@FXML
	public Label profilePane_Statistics_Label_TotalExperience;

	@FXML
	public VBox friendsPane;

	@FXML
	public ScrollPane friendsPane_ScrollPane_FriendsFeed;

	@FXML
	public VBox friendsPane_ScrollPane_FriendsFeed_VBox;

	@FXML
	public ScrollPane friendsPane_ScrollPane_Requests;

	@FXML
	public VBox friendsPane_ScrollPane_Requests_VBox;

	@FXML
	public ScrollPane friendsPane_ScrollPane_Friends;

	@FXML
	public VBox friendsPane_ScrollPane_Friends_VBox;

	@FXML
	public VBox settingsPane;

	@FXML
	public JFXTextField settingsPane_TextField_Firstname;

	@FXML
	public JFXTextField settingsPane_TextField_Lastname;

	@FXML
	public JFXTextField settingsPane_TextField_Age;

	@FXML
	public JFXTextField settingsPane_TextField_Email;

	@FXML
	public JFXButton settingsPane_Button_UpdateChanges;

	@FXML
	public FontAwesomeIconView settingsPane_Button_Clear;

	@FXML
	public HBox main_HBox_Top;

	@FXML
	public FontAwesomeIconView button_Minimize;

	@FXML
	public FontAwesomeIconView button_Quit;

	@FXML
	public FontAwesomeIconView main_bell_notifications;

	@FXML
	public Label main_bell_notifications_Label;

	@FXML
	public VBox main_Notifications_PopUP;

	@FXML
	public FontAwesomeIconView main_Notifications_PopUP_Close_Button;

	@FXML
	public VBox main_Notifications_PopUP_VBox;

	@FXML
	public Label main_Version_Label;

	/////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////

	@FXML
	public void changeToLogin(MouseEvent event) {
		launcher_VBox_Pane_RegisterVBox.setVisible(false);
		launcher_VBox_Pane_LoginVBox.setVisible(true);
		launcher_VBox_Pane_RegisterVBox_TextField_Firstname.clear();
		launcher_VBox_Pane_RegisterVBox_TextField_Lastname.clear();
		launcher_VBox_Pane_RegisterVBox_TextField_Age.clear();
		launcher_VBox_Pane_RegisterVBox_TextField_Username.clear();
		launcher_VBox_Pane_RegisterVBox_TextField_Email.clear();
		launcher_VBox_Pane_RegisterVBox_TextField_Password.clear();
		launcher_VBox_Pane_RegisterVBox_TextField_PasswordAgain.clear();
	}

	@FXML
	public void changeToRegister(MouseEvent event) {
		launcher_VBox_Pane_LoginVBox.setVisible(false);
		launcher_VBox_Pane_RegisterVBox.setVisible(true);
		launcher_VBox_Pane_LoginVBox_TextField_Username.clear();
		launcher_VBox_Pane_LoginVBox_TextField_Password.clear();
	}

	@FXML
	void closeWindow(MouseEvent event) {
		stage.close();
		System.exit(0);
	}

	@FXML
	void loginWithButton(MouseEvent event) {
		if(validateInputLogin()) {
			String username = launcher_VBox_Pane_LoginVBox_TextField_Username.getText();
			String password = launcher_VBox_Pane_LoginVBox_TextField_Password.getText();
			launcher_VBox_Pane_LoginVBox.setVisible(false);
			c.requestLoginAuthentication(username, password);
		}
	}

	@FXML
	void loginWithEnterKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			if(validateInputLogin()) {
				String username = launcher_VBox_Pane_LoginVBox_TextField_Username.getText();
				String password = launcher_VBox_Pane_LoginVBox_TextField_Password.getText();
				launcher_VBox_Pane_LoginVBox.setVisible(false);
				c.requestLoginAuthentication(username, password);
			}
		}
	}

	private boolean validateInputLogin() {
		Boolean res = true;

		HBox notify = new HBox();
		notify.setStyle("-fx-background-color: #b91515;");
		notify.setPadding(new Insets(5, 5, 5, 5));
		VBox.setMargin(notify, new Insets(5, 0, 0, 0));
		notify.setAlignment(Pos.CENTER);
		Label lbl = new Label();

		notify.setEffect(new DropShadow(20, Color.BLACK));

		if(launcher_VBox_Pane_LoginVBox_TextField_Username.getText().equals("") || 
				launcher_VBox_Pane_LoginVBox_TextField_Password.getText().equals("")) {
			lbl.setText("Please fill in all fields!");
			lbl.setStyle("-fx-font-family: Helvetica;");
			lbl.setStyle("-fx-font-size: 17px;");

			notify.getChildren().add(lbl);

			notifications_VBox.getChildren().add(notify);

			Timer t = new Timer(1500, new ActionListener() {

				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Platform.runLater(new Runnable() {
						@Override public void run() {
							notifications_VBox.getChildren().remove(notify);
						}
					});
				}
			});
			t.setRepeats(false);
			t.start();

			res=false;
		}

		return res;
	}


	@FXML
	void minimizeWindow(MouseEvent event) {
		stage.setIconified(true);
	}

	@FXML
	void registerWithButton(MouseEvent event) {
		if(validateInputRegister()) {
			String firstname = launcher_VBox_Pane_RegisterVBox_TextField_Firstname.getText();
			String lastname = launcher_VBox_Pane_RegisterVBox_TextField_Lastname.getText();
			int age = Integer.parseInt(launcher_VBox_Pane_RegisterVBox_TextField_Age.getText());
			String username = launcher_VBox_Pane_RegisterVBox_TextField_Username.getText();
			String email = launcher_VBox_Pane_RegisterVBox_TextField_Email.getText();
			String password = launcher_VBox_Pane_RegisterVBox_TextField_Password.getText();
			launcher_VBox_Pane_RegisterVBox.setVisible(false);
			c.registerAccount(username, password, firstname, lastname, age, email);
		}
	}

	@FXML
	void registerWithEnterKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			if(validateInputRegister()) {
				String firstname = launcher_VBox_Pane_RegisterVBox_TextField_Firstname.getText();
				String lastname = launcher_VBox_Pane_RegisterVBox_TextField_Lastname.getText();
				int age = Integer.parseInt(launcher_VBox_Pane_RegisterVBox_TextField_Age.getText());
				String username = launcher_VBox_Pane_RegisterVBox_TextField_Username.getText();
				String email = launcher_VBox_Pane_RegisterVBox_TextField_Email.getText();
				String password = launcher_VBox_Pane_RegisterVBox_TextField_Password.getText();
				launcher_VBox_Pane_RegisterVBox.setVisible(false);
				c.registerAccount(username, password, firstname, lastname, age, email);
			}
		}
	}

	private Boolean validateInputRegister() {
		Boolean res=true;
		HBox notify = new HBox();
		notify.setStyle("-fx-background-color: #b91515;");
		notify.setPadding(new Insets(5, 5, 5, 5));
		VBox.setMargin(notify, new Insets(5, 0, 0, 0));
		notify.setAlignment(Pos.CENTER);
		Label lbl = new Label();

		notify.setEffect(new DropShadow(20, Color.BLACK));
		if(launcher_VBox_Pane_RegisterVBox_TextField_Firstname.getText().equals("") ||
				launcher_VBox_Pane_RegisterVBox_TextField_Firstname.getText().equals("") ||
				launcher_VBox_Pane_RegisterVBox_TextField_Lastname.getText().equals("") ||
				launcher_VBox_Pane_RegisterVBox_TextField_Age.getText().equals("") ||
				launcher_VBox_Pane_RegisterVBox_TextField_Username.getText().equals("") ||
				launcher_VBox_Pane_RegisterVBox_TextField_Email.getText().equals("") ||
				launcher_VBox_Pane_RegisterVBox_TextField_Password.getText().equals("") ||
				launcher_VBox_Pane_RegisterVBox_TextField_PasswordAgain.getText().equals("")){
			lbl.setText("Please fill in all fields!");
			lbl.setStyle("-fx-font-family: Helvetica;");
			lbl.setStyle("-fx-font-size: 17px;");

			notify.getChildren().add(lbl);

			notifications_VBox.getChildren().add(notify);

			Timer t = new Timer(1500, new ActionListener() {

				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Platform.runLater(new Runnable() {
						@Override public void run() {
							notifications_VBox.getChildren().remove(notify);
						}
					});
				}
			});
			t.setRepeats(false);
			t.start();

			res=false;
		}

		try {
			@SuppressWarnings("unused")
			int age = Integer.parseInt(launcher_VBox_Pane_RegisterVBox_TextField_Age.getText());
		}catch(Exception e) {
			launcher_VBox_Pane_RegisterVBox_TextField_Age.clear();
			res=false;
		}

		if (validate(launcher_VBox_Pane_RegisterVBox_TextField_Email.getText()) == false) {
			launcher_VBox_Pane_RegisterVBox_TextField_Email.clear();
			res=false;
		}

		if(!launcher_VBox_Pane_RegisterVBox_TextField_Password.getText().equals(launcher_VBox_Pane_RegisterVBox_TextField_PasswordAgain.getText())) {
			launcher_VBox_Pane_RegisterVBox_TextField_PasswordAgain.clear();
			res=false;
		}

		return res;


	}

	public static boolean validate(String emailStr) {
		Matcher matcher = VALIDEMAIL.matcher(emailStr);
		return matcher.find();
	}

	@FXML
	void cancelChanges(MouseEvent event) {

	}

	@FXML
	void changeToChat(MouseEvent event) {
		dashboardPane.setVisible(false);
		chatPane.setVisible(true);
		profilePane.setVisible(false);
		friendsPane.setVisible(false);
		settingsPane.setVisible(false);
		numberNotifyChat.set(0);
	}

	@FXML
	void changeToDashboard(MouseEvent event) {
		dashboardPane.setVisible(true);
		chatPane.setVisible(false);
		profilePane.setVisible(false);
		friendsPane.setVisible(false);
		settingsPane.setVisible(false);
	}

	@FXML
	void changeToProfile(MouseEvent event) {
		dashboardPane.setVisible(false);
		chatPane.setVisible(false);
		profilePane.setVisible(true);
		friendsPane.setVisible(false);
		settingsPane.setVisible(false);
	}

	@FXML
	void changeToFriends(MouseEvent event) {
		dashboardPane.setVisible(false);
		chatPane.setVisible(false);
		profilePane.setVisible(false);
		friendsPane.setVisible(true);
		settingsPane.setVisible(false);
	}

	@FXML
	void changeToSettings(MouseEvent event) {
		dashboardPane.setVisible(false);
		chatPane.setVisible(false);
		profilePane.setVisible(false);
		friendsPane.setVisible(false);
		settingsPane.setVisible(true);
	}

	@FXML
	void closeNotificationPopUp(MouseEvent event) {
		main_Notifications_PopUP.setVisible(false);
	}

	@FXML
	void openGitHub(MouseEvent event) {
		try {
			Desktop.getDesktop().browse(new URI("https://github.com/simaoferreira"));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void openLinkedIn(MouseEvent event) {
		try {
			Desktop.getDesktop().browse(new URI("https://www.linkedin.com/in/sim%C3%A3o-ferreira-96173315a/"));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void openPopupNotifications(MouseEvent event) {
		main_Notifications_PopUP.setVisible(true);
		numberNotifyPopUp.set(0);
	}

	@FXML
	void search(KeyEvent event) {

	}

	@FXML
	void sendMessageAllChatWithKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			String texto = chatPane_HBox_Left_Handle_Input_TextArea.getText();
			c.sendMessagesToChat(texto);
			chatPane_HBox_Left_Handle_Input_TextArea.setText("");
		}

	}

	@FXML
	void sendMessageAllChatWithMouseClick(MouseEvent event) {
		String texto = chatPane_HBox_Left_Handle_Input_TextArea.getText();
		c.sendMessagesToChat(texto);
		chatPane_HBox_Left_Handle_Input_TextArea.setText("");
	}

	@FXML
	void updateChanges(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {

		}
	}

	@FXML
	void updateChangesWithMouse(MouseEvent event) {

	}
	
    @FXML
    void openConfigurations(MouseEvent event) {
    	if(appPane.getStylesheets().contains(urlThemeOne)) {
    		appPane.getStylesheets().remove(urlThemeOne);
    		appPane.getStylesheets().add(urlThemeTwo);
    	}else {
    		appPane.getStylesheets().remove(urlThemeTwo);
    		appPane.getStylesheets().add(urlThemeOne);
    	}
    }


	public void animationProgressBar(ProgressBar pg,double parcialExp) {
		Animation animation = new Timeline(
				new KeyFrame(Duration.seconds(1),
						new KeyValue(pg.progressProperty(), parcialExp)));
		animation.play();
	}

	public void fastScrollToBottom(ScrollPane scrollPane) {
		Animation animation = new Timeline(
				new KeyFrame(Duration.seconds(0.1),
						new KeyValue(scrollPane.vvalueProperty(), 1)));
		animation.play();
	}

	public void connectToServer() throws UnknownHostException, IOException {

	}

	public void addMessageToScrollPane(String text,String user, String hour,Side side,Type type) {
		VBox mensagem = new VBox();
		mensagem.setPadding(new Insets(10, 10, 10, 10));
		mensagem.getStyleClass().addAll("chat-messages");

		Label usernameLabel = new Label(user);
		usernameLabel.setStyle("-fx-font-family: Calibri;");
		usernameLabel.setStyle("-fx-font-size: 17;");
		usernameLabel.setStyle("-fx-text-fill: #fff;");
		usernameLabel.setUnderline(true);

		Label textLabel = new Label(text);
		textLabel.setStyle("-fx-font-family: Calibri;");
		textLabel.setStyle("-fx-font-size: 15;");
		textLabel.setStyle("-fx-text-fill: #fff;");
		textLabel.setWrapText(true);

		Label hourLabel = new Label(hour);
		hourLabel.setStyle("-fx-font-family: Calibri;");
		hourLabel.setStyle("-fx-font-size: 12;");
		hourLabel.setStyle("-fx-text-fill: #fff;");

		if(type == Type.LOGIN) {
			VBox.setMargin(mensagem, new Insets(0, 20,10, 0));
			mensagem.setStyle("-fx-background-color: #4d9b20");
			mensagem.setAlignment(Pos.CENTER);
			mensagem.getChildren().addAll(textLabel,hourLabel);

			Timer t = new Timer(10000, new ActionListener() {

				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Platform.runLater(new Runnable() {
						@Override public void run() {
							chatPane_HBox_Left_ScrollPane_VBox.getChildren().remove(mensagem);
						}
					});
				}
			});
			t.setRepeats(false);
			t.start();
		}else if(type == Type.LOGOUT) {
			VBox.setMargin(mensagem, new Insets(0, 20,10, 0));
			mensagem.setStyle("-fx-background-color:  #cc3d3d");
			mensagem.setAlignment(Pos.CENTER);
			mensagem.getChildren().addAll(textLabel,hourLabel);

			Timer t = new Timer(10000, new ActionListener() {

				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Platform.runLater(new Runnable() {
						@Override public void run() {
							chatPane_HBox_Left_ScrollPane_VBox.getChildren().remove(mensagem);
						}
					});
				}
			});
			t.setRepeats(false);
			t.start();
		}else if(type == Type.MESSAGE) {
			mensagem.setMaxWidth(380f);

			if(side == Side.RIGHT) {
				VBox.setMargin(mensagem, new Insets(0, 0,10, 380));
				mensagem.setStyle("-fx-background-color:  #5188bc");

			}else if(side == Side.LEFT) {
				VBox.setMargin(mensagem, new Insets(0, 0,10, 0));
				mensagem.setStyle("-fx-background-color:  #67696d");
			}

			mensagem.getChildren().addAll(usernameLabel,textLabel,hourLabel);
		}

		chatPane_HBox_Left_ScrollPane_VBox.getChildren().add(mensagem);
	}

	public void addNotifyPopUp(String title, String text) {
		VBox notifyBox = new VBox();
		notifyBox.setPadding(new Insets(10,10,10,10));
		notifyBox.getStyleClass().addAll("buttons","top-border-blue");
		notifyBox.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				changeToFriends(mouseEvent);
				friendsPane_ScrollPane_Requests_VBox.requestFocus();
			}
		});

		Label titleLabel = new Label(title);
		titleLabel.setStyle("-fx-font-family: Arial;-fx-font-size: 15px;-fx-text-fill: #0fafe0;");

		Label textLabel = new Label(text);
		textLabel.setStyle("-fx-font-size: 12px;-fx-text-fill: #000000;");

		notifyBox.getChildren().addAll(titleLabel,textLabel);

		main_Notifications_PopUP_VBox.getChildren().add(notifyBox);

	}

	public void addUserOnlineToScrollPane(User u) {
		String fullname = u.getFullName();
		String level = String.valueOf(u.getUserLvl());
		String exp = String.valueOf(u.getUserExp());

		Pane userField = new Pane();
		userField.setId(u.getUsername());
		userField.setPrefSize(180, 54);
		userField.getStyleClass().addAll("userBox","buttons");
		userField.setCursor(Cursor.HAND);

		Label fullnameLabel = new Label(fullname);
		fullnameLabel.setId("fullname");
		fullnameLabel.setStyle("-fx-font-size: 14;");
		fullnameLabel.setStyle("-fx-text-fill: #7c8184;");
		fullnameLabel.setLayoutX(6f);
		fullnameLabel.setLayoutY(7f);

		Label levelLabel = new Label("Lvl: "+level);
		levelLabel.setId("level");
		levelLabel.setStyle("-fx-font-size: 12;");
		levelLabel.setStyle("-fx-text-fill: #7c8184;");
		levelLabel.setLayoutX(6f);
		levelLabel.setLayoutY(27f);

		userField.getChildren().addAll(fullnameLabel,levelLabel);

		userField.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				showInfoUser(u);
			}
		});

		chatPane_HBox_Right_ScrollPane_VBox.getChildren().add(userField);
		dashboardPane_HBox_Right_VBox_Servers_Status_MainServer_Label_NumberUsers.setText(String.valueOf(usersOnline.size()+1));

	}

	public void addFriendToScrollPane(User u) {
		String fullname = u.getFullName();
		String level = String.valueOf(u.getUserLvl());
		String exp = String.valueOf(u.getUserExp());

		Pane userField = new Pane();
		userField.setId(u.getUsername());
		userField.setPrefSize(180, 54);
		userField.getStyleClass().addAll("userBox","buttons");
		userField.setCursor(Cursor.HAND);

		Label fullnameLabel = new Label(fullname);
		fullnameLabel.setId("fullname");
		fullnameLabel.setStyle("-fx-font-size: 14;");
		fullnameLabel.setStyle("-fx-text-fill: #7c8184;");
		fullnameLabel.setLayoutX(6f);
		fullnameLabel.setLayoutY(7f);

		Label levelLabel = new Label("Lvl: "+level);
		levelLabel.setId("level");
		levelLabel.setStyle("-fx-font-size: 12;");
		levelLabel.setStyle("-fx-text-fill: #7c8184;");
		levelLabel.setLayoutX(6f);
		levelLabel.setLayoutY(27f);

		userField.getChildren().addAll(fullnameLabel,levelLabel);

		userField.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				showInfoUser(u);
			}
		});

		friendsPane_ScrollPane_Friends_VBox.getChildren().add(userField);

	}

	private void showInfoUser(User u) {
		Stage window = new Stage();

		window.initModality(Modality.NONE);
		window.initStyle(StageStyle.UNDECORATED);

		Pane pane = new Pane();
		pane.setPrefWidth(600);

		HBox boxButtons = new HBox();
		boxButtons.setLayoutX(0);
		boxButtons.setLayoutY(0);
		boxButtons.setPrefWidth(600);
		boxButtons.setAlignment(Pos.CENTER_RIGHT);

		FontAwesomeIconView close = new FontAwesomeIconView();
		close.setGlyphName("CLOSE");
		close.setGlyphSize(18);
		close.setStyle("-fx-fill: e8e8e8;");
		close.setCursor(Cursor.HAND);
		HBox.setMargin(close, new Insets(0,5,0,0));
		close.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				window.close();
			}
		});

		FontAwesomeIconView minimize = new FontAwesomeIconView();
		minimize.setGlyphName("MINUS");
		minimize.setGlyphSize(18);
		minimize.setStyle("-fx-fill: e8e8e8;");
		minimize.setCursor(Cursor.HAND);
		HBox.setMargin(minimize, new Insets(6,10,0,0));
		minimize.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				window.setIconified(true);
			}
		});

		boxButtons.getChildren().addAll(minimize,close);

		VBox detailsBox = new VBox();
		detailsBox.setLayoutX(0);
		detailsBox.setLayoutY(0);
		detailsBox.setPrefWidth(600);
		detailsBox.setStyle("-fx-background-color:  #202123;-fx-border-color : #0fafe0;" + 
				"	-fx-border-width: 2px 2px 2px 2px;");
		detailsBox.setPadding(new Insets(10,10,10,10));

		Label fullnameLabel = new Label(u.getFullName());
		fullnameLabel.setStyle("-fx-font-family: Ebrima;-fx-font-size: 20px;-fx-text-fill: #fff;");
		fullnameLabel.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {

			}
		});
		detailsBox.getChildren().add(fullnameLabel);

		HBox boxAddButton = new HBox();
		boxAddButton.setAlignment(Pos.CENTER_RIGHT);
		boxAddButton.setPrefWidth(600);
		VBox.setMargin(boxAddButton, new Insets(5,0,0,0));

		FontAwesomeIconView iconAddFriend = new FontAwesomeIconView();
		iconAddFriend.setGlyphName("USER_PLUS");
		iconAddFriend.setGlyphSize(18);
		iconAddFriend.setStyle("-fx-fill: e8e8e8;");

		JFXButton buttonAddFriend =  new JFXButton(); 
		buttonAddFriend.setMinWidth(100);
		buttonAddFriend.setStyle("-fx-background-color: #4a8c44;-fx-background-radius: 0;");
		buttonAddFriend.setText("Add Friend");
		buttonAddFriend.setTextFill(Color.WHITE);
		buttonAddFriend.setCursor(Cursor.HAND);
		buttonAddFriend.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				c.sendRequestFriend(user.getUsername(),u.getUsername());
				buttonAddFriend.setDisable(true);
				buttonAddFriend.setText("Pendent");
				iconAddFriend.setGlyphSize(14);
				iconAddFriend.setGlyphName("HOURGLASS");
			}
		});

		buttonAddFriend.setGraphic(iconAddFriend);

		if(u.getExistsFriendRequest()) {
			buttonAddFriend.setDisable(true);
			buttonAddFriend.setText("Pendent");
			iconAddFriend.setGlyphSize(14);
			iconAddFriend.setGlyphName("HOURGLASS");
		}else {
			buttonAddFriend.setDisable(false);
			buttonAddFriend.setText("Add Friend");
			iconAddFriend.setGlyphSize(18);
			iconAddFriend.setGlyphName("USER_PLUS");
		}

		if(!u.getExistsFriendShip()){
			boxAddButton.getChildren().add(buttonAddFriend);
			detailsBox.getChildren().add(boxAddButton);
		}


		pane.getChildren().addAll(detailsBox,boxButtons);

		u.getExistsFriendRequestProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if(u.getExistsFriendRequest()) {
					buttonAddFriend.setDisable(true);
					buttonAddFriend.setText("Pendent");
					iconAddFriend.setGlyphSize(14);
					iconAddFriend.setGlyphName("HOURGLASS");
				}else {
					buttonAddFriend.setDisable(false);
					buttonAddFriend.setText("Add Friend");
					iconAddFriend.setGlyphSize(18);
					iconAddFriend.setGlyphName("USER_PLUS");
				}

			}
		});

		u.getExistsFriendShipProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if(u.getExistsFriendShip()) {
					buttonAddFriend.removeEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent mouseEvent) {
							c.sendRequestFriend(user.getUsername(),u.getUsername());
							buttonAddFriend.setDisable(true);
							buttonAddFriend.setText("Pendent");
							iconAddFriend.setGlyphSize(14);
							iconAddFriend.setGlyphName("HOURGLASS");
						}
					});
					boxAddButton.getChildren().remove(buttonAddFriend);
					detailsBox.getChildren().remove(boxAddButton);
				}else {
					buttonAddFriend.setDisable(false);
					buttonAddFriend.setText("Add Friend");
					iconAddFriend.setGlyphSize(18);
					iconAddFriend.setGlyphName("USER_PLUS");
				}

			}
		});

		Scene scene = new Scene(pane);

		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				window.setOpacity(0.9);
				xOffset = window.getX() - event.getScreenX();
				yOffset = window.getY() - event.getScreenY();
			}
		});

		scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				window.setOpacity(0.9);
				window.setX(event.getScreenX() + xOffset);
				window.setY(event.getScreenY() + yOffset);
			}
		});

		scene.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				window.setOpacity(1);
			}
		});

		window.setScene(scene);
		window.show();
	}

	public void addFriendRequestToScrollPane(User u,String time) {
		VBox friendRequestBox = new VBox();
		friendRequestBox.getStyleClass().addAll("full-border-grey");
		friendRequestBox.setStyle("-fx-background-color: #fff");
		friendRequestBox.setAlignment(Pos.CENTER_LEFT);
		friendRequestBox.setId(u.getUsername());
		VBox.setMargin(friendRequestBox, new Insets(0,0,10,0));

		Label titleLabel = new Label("Friend Request");
		titleLabel.setStyle("-fx-font-family: Calibri;-fx-font-size: 20px;-fx-text-fill: #0fafe0;");
		VBox.setMargin(titleLabel, new Insets(10,0,0,10));

		HBox infoBox = new HBox();
		infoBox.setAlignment(Pos.CENTER_LEFT);
		VBox.setMargin(infoBox, new Insets(5,0,0,10));

		Label infoUser = new Label(u.getFullName());
		infoUser.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				showInfoUser(u);
			}
		});
		infoUser.setUnderline(true);
		infoUser.setStyle("-fx-font-family: Calibri ;-fx-font-size: 15px;-fx-text-fill: #000000;");
		infoUser.setCursor(Cursor.HAND);
		HBox.setMargin(infoUser, new Insets(0,5,0,0));

		Label infoText = new Label("sent you a friend request.");
		infoUser.setStyle("-fx-font-family: Calibri ;-fx-font-size: 15px;-fx-text-fill: #000000;");

		infoBox.getChildren().addAll(infoUser,infoText);

		Label hourLabel = new Label(time);
		hourLabel.setStyle("-fx-font-family: Calibri ;-fx-font-size: 12px;-fx-text-fill: #000000;");
		VBox.setMargin(hourLabel, new Insets(5,0,10,10));

		HBox buttonsBox = new HBox();
		buttonsBox.setAlignment(Pos.CENTER_RIGHT);
		buttonsBox.getStyleClass().addAll("top-border-grey");

		JFXButton acceptFriendRequest =  new JFXButton(); 
		acceptFriendRequest.setMinWidth(100);
		acceptFriendRequest.setStyle("-fx-background-color: #4a8c44;-fx-background-radius: 0;");
		acceptFriendRequest.setText("Accept");
		acceptFriendRequest.setTextFill(Color.WHITE);
		acceptFriendRequest.setCursor(Cursor.HAND);
		acceptFriendRequest.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				c.acceptFriendRequest(user.getUsername(),u.getUsername());
			}
		});

		FontAwesomeIconView iconAccept = new FontAwesomeIconView();
		iconAccept.setGlyphName("CHECK");
		iconAccept.setGlyphSize(14);
		iconAccept.setStyle("-fx-fill: #fff;");

		acceptFriendRequest.setGraphic(iconAccept);
		HBox.setMargin(acceptFriendRequest, new Insets(5,10,5,0));

		JFXButton declineFriendRequest =  new JFXButton(); 
		declineFriendRequest.setMinWidth(100);
		declineFriendRequest.setStyle("-fx-background-color: #ad2d2d;-fx-background-radius: 0;");
		declineFriendRequest.setText("Decline");
		declineFriendRequest.setTextFill(Color.WHITE);
		declineFriendRequest.setCursor(Cursor.HAND);
		declineFriendRequest.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				c.declineFriendRequest(user.getUsername(),u.getUsername());
			}
		});

		FontAwesomeIconView iconDecline = new FontAwesomeIconView();
		iconDecline.setGlyphName("TIMES");
		iconDecline.setGlyphSize(14);
		iconDecline.setStyle("-fx-fill: #fff;");

		declineFriendRequest.setGraphic(iconDecline);
		HBox.setMargin(declineFriendRequest, new Insets(5,10,5,0));

		buttonsBox.getChildren().addAll(acceptFriendRequest,declineFriendRequest);

		friendRequestBox.getChildren().addAll(titleLabel,infoBox,hourLabel,buttonsBox);

		friendsPane_ScrollPane_Requests_VBox.getChildren().add(friendRequestBox);

	}

	public void removeUserOnlineToScrollPane(int i) {
		Pane product = (Pane) chatPane_HBox_Right_ScrollPane_VBox.getChildren().get(i);

		FadeTransition fadeTransitionRemove = new FadeTransition();
		fadeTransitionRemove.setDuration(Duration.millis(500));
		fadeTransitionRemove.setNode(product);
		fadeTransitionRemove.setFromValue(1);
		fadeTransitionRemove.setToValue(0);

		TranslateTransition translateRemove = new TranslateTransition();
		translateRemove.setDuration(Duration.millis(500));
		translateRemove.setNode(product);
		translateRemove.setFromX(product.getLayoutX());
		translateRemove.setToX(product.getLayoutX()+300);

		ParallelTransition pt = new ParallelTransition();
		pt.getChildren().addAll(fadeTransitionRemove,translateRemove);
		pt.play();

		pt.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				chatPane_HBox_Right_ScrollPane_VBox.getChildren().remove(product);
			}
		});
	}

	public void removeFriendRequestToScrollPane(String username) {
		ObservableList<Node> nodes = friendsPane_ScrollPane_Requests_VBox.getChildren();

		for(Node p :  nodes) {
			if(p.getId().equals(username)){
				FadeTransition fadeTransitionRemove = new FadeTransition();
				fadeTransitionRemove.setDuration(Duration.millis(500));
				fadeTransitionRemove.setNode(p);
				fadeTransitionRemove.setFromValue(1);
				fadeTransitionRemove.setToValue(0);

				TranslateTransition translateRemove = new TranslateTransition();
				translateRemove.setDuration(Duration.millis(500));
				translateRemove.setNode(p);
				translateRemove.setFromX(p.getLayoutX());
				translateRemove.setToX(p.getLayoutX()+300);

				ParallelTransition pt = new ParallelTransition();
				pt.getChildren().addAll(fadeTransitionRemove,translateRemove);
				pt.play();

				pt.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						friendsPane_ScrollPane_Requests_VBox.getChildren().remove(p);
					}
				});
			}
			break;
		}


	}


	public void restartApplication(Runnable runBeforeRestart, Integer TimeToWaitToExecuteTask) {
		try {

			// execute some custom code before restarting
			if (runBeforeRestart != null) {
				// Wait for 2 seconds before restart if null
				if (TimeToWaitToExecuteTask != null) {
					TimeUnit.SECONDS.sleep(TimeToWaitToExecuteTask);
				} else {
					TimeUnit.SECONDS.sleep(2);
				}
				runBeforeRestart.run();
			}

			final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
			final File currentJar = new File(Chat.class.getProtectionDomain().getCodeSource().getLocation().toURI());

			/* is it a jar file? */
			if (!currentJar.getName().endsWith(".jar"))
				return;

			/* Build command: java -jar application.jar */
			final ArrayList<String> command = new ArrayList<String>();
			command.add(javaBin);
			command.add("-jar");
			command.add(currentJar.getPath());

			final ProcessBuilder builder = new ProcessBuilder(command);
			builder.start();
			System.exit(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
