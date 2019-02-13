package controllers;


import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import com.gluonhq.charm.glisten.control.ProgressBar; 
import com.gluonhq.charm.glisten.control.ProgressIndicator;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
import dataHandler.ProtectionBadWords;
import dataHandler.User;
import dataHandler.LoggerHandle;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
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

	private Stage stage;
	private String version;
	private LoggerHandle lh = null;

	private AnchorPane launcherPane;
	private AnchorPane appPane;
	
	private Client c;
	public User user;
	public ArrayList<User> usersOnline = null;

	public ControllerChat(Stage primaryStage,String version, LoggerHandle lh) throws IOException {
		usersOnline = new ArrayList<User>();
		this.stage = primaryStage;
		this.version = version;
		this.lh = lh;
		stage.initStyle(StageStyle.UNDECORATED);

		loadLauncher();
		loadChat();
	}

	private void loadLauncher() throws IOException {
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

				launcher_Label_Version.setText(version);

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
				stage.show(); 
				
				long lhwnd = com.sun.glass.ui.Window.getWindows().get(0).getNativeWindow();
				Pointer lpVoid = new Pointer(lhwnd);
				WinDef.HWND hwnd = new WinDef.HWND(lpVoid);
				final User32 user32 = User32.INSTANCE;
				int oldStyle = user32.GetWindowLong(hwnd, GWL_STYLE);
				int newStyle = oldStyle | 0x00020000;//WS_MINIMIZEBOX
				user32.SetWindowLong(hwnd, GWL_STYLE, newStyle);
				
				lh.log("INFO", "Launcher scene was set succefully in stage.");
			}catch(Exception e) {
				lh.log("SEVERE", "Could not set scene Launcher to stage!");
			}
			
		} catch (IOException e) {
			lh.log("WARNING", "Could not connect to the server!");
			Platform.runLater(new Runnable() {
				@Override public void run() {
					AlertBox.display("Error", "Can't connect to server",true);
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
			
			lh.log("INFO", "Updated scene succesfully to chat");
		}catch(Exception e) {
			lh.log("SEVERE", "Could not update scene to chat");
		}
		

	}

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
	void changeToLogin(MouseEvent event) {
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
	void changeToRegister(MouseEvent event) {
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
			c.requestLoginAuthentication(username, password);
		}
	}
	
	@FXML
	void loginWithEnterKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			if(validateInputLogin()) {
				String username = launcher_VBox_Pane_LoginVBox_TextField_Username.getText();
				String password = launcher_VBox_Pane_LoginVBox_TextField_Password.getText();
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
			//enviar pedido de registo
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
	void registerWithEnterKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			if(validateInputRegister()) {
				String firstname = launcher_VBox_Pane_RegisterVBox_TextField_Firstname.getText();
				String lastname = launcher_VBox_Pane_RegisterVBox_TextField_Lastname.getText();
				int age = Integer.parseInt(launcher_VBox_Pane_RegisterVBox_TextField_Age.getText());
				String username = launcher_VBox_Pane_RegisterVBox_TextField_Username.getText();
				String email = launcher_VBox_Pane_RegisterVBox_TextField_Email.getText();
				String password = launcher_VBox_Pane_RegisterVBox_TextField_Password.getText();
				//enviar pedido de registo
			}
		}
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

	}

	@FXML
	void openLinkedIn(MouseEvent event) {

	}

	@FXML
	void openPopupNotifications(MouseEvent event) {
		main_Notifications_PopUP.setVisible(true);
	}

	@FXML
	void search(KeyEvent event) {

	}

	@FXML
	void sendMessageAllChatWithKey(KeyEvent event) {

	}

	@FXML
	void sendMessageAllChatWithMouseClick(MouseEvent event) {

	}

	@FXML
	void updateChanges(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			
		}
	}
	
	@FXML
	void updateChangesWithMouse(MouseEvent event) {

	}


	public void fastScrollToBottom(ScrollPane scrollPane) {
		Animation animation = new Timeline(
				new KeyFrame(Duration.seconds(0.1),
						new KeyValue(scrollPane.vvalueProperty(), 1)));
		animation.play();
	}

	public void connectToServer() throws UnknownHostException, IOException {

	}

}
