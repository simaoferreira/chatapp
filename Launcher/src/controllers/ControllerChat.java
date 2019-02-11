package controllers;


import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import com.gluonhq.charm.glisten.control.ProgressBar; 
import com.gluonhq.charm.glisten.control.ProgressIndicator;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
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
import static com.sun.jna.platform.win32.WinUser.GWL_STYLE;
import com.sun.jna.*;

import dataHandler.AlertBox;
import dataHandler.Informations;
import dataHandler.Notifications;
import dataHandler.ProtectionBadWords;
import dataHandler.User;
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

	private AnchorPane launcherPane;
	private AnchorPane appPane;

	public ControllerChat(Stage primaryStage,String version) throws IOException {

		this.stage = primaryStage;
		this.version = version;
		stage.initStyle(StageStyle.UNDECORATED);

		loadLauncher();
		loadChat();
	}

	private void loadLauncher() throws IOException {
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

	}

	private void loadChat() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("NewGui.fxml"));
		loader.setController(this);
		appPane = (AnchorPane) loader.load();
		appPane.setBorder(darkblue);

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

	}

	public void iniciarController() {
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
	}

	public void updateSceneToMenu() {

		FadeTransition fadeTransition = new FadeTransition();
		fadeTransition.setDuration(Duration.millis(1000));
		fadeTransition.setNode(launcherPane);
		fadeTransition.setFromValue(1);
		fadeTransition.setToValue(0);
		fadeTransition.play();

		Scene scene = new Scene(appPane);

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
		
		Timer t = new Timer(1500, new ActionListener() {

			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				Platform.runLater(new Runnable() {
					@Override public void run() {
						FadeTransition fadeTransition = new FadeTransition();
						fadeTransition.setDuration(Duration.millis(1000));
						fadeTransition.setNode(loadingScreen);
						fadeTransition.setFromValue(1);
						fadeTransition.setToValue(0);
						fadeTransition.play();
						
						fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								loadingScreen.setVisible(false);
							}
						});
						
					}
				});
			}
		});
		t.setRepeats(false);
		t.start();
		
		

	}

	@FXML
	private AnchorPane launcherAnchorPane;

	@FXML
	private VBox launcher_VBox;

	@FXML
	private Pane launcher_VBox_Pane;

	@FXML
	private VBox launcher_VBox_Pane_LoginVBox;

	@FXML
	private JFXTextField launcher_VBox_Pane_LoginVBox_TextField_Username;

	@FXML
	private JFXPasswordField launcher_VBox_Pane_LoginVBox_TextField_Password;

	@FXML
	private JFXButton launcher_VBox_Pane_LoginVBox_Button;

	@FXML
	private Label launcher_VBox_Pane_LoginVBox_Label_ChangeToRegister;

	@FXML
	private Label launcher_VBox_Pane_LoginVBox_Label_ErrorLogin;

	@FXML
	private VBox launcher_VBox_Pane_RegisterVBox;

	@FXML
	private JFXTextField launcher_VBox_Pane_RegisterVBox_TextField_Firstname;

	@FXML
	private JFXTextField launcher_VBox_Pane_RegisterVBox_TextField_Lastname;

	@FXML
	private JFXTextField launcher_VBox_Pane_RegisterVBox_TextField_Age;

	@FXML
	private JFXTextField launcher_VBox_Pane_RegisterVBox_TextField_Username;

	@FXML
	private JFXTextField launcher_VBox_Pane_RegisterVBox_TextField_Email;

	@FXML
	private JFXPasswordField launcher_VBox_Pane_RegisterVBox_TextField_Password;

	@FXML
	private JFXPasswordField launcher_VBox_Pane_RegisterVBox_TextField_PasswordAgain;

	@FXML
	private JFXButton launcher_VBox_Pane_RegisterVBox_Button;

	@FXML
	private Label launcher_VBox_Pane_RegisterVBox_Label_ChangeToLogin;

	@FXML
	private Label launcher_VBox_Pane_RegisterVBox_Label_ErrorRegister;

	@FXML
	private HBox launcher_HBox_Top;

	@FXML
	private FontAwesomeIconView launcher_Button_Minimize;

	@FXML
	private FontAwesomeIconView launcher_Button_Quit;

	@FXML
	private HBox launcher_HBox_Right_Version_HBox;

	@FXML
	private Label launcher_Label_Version;


	@FXML
	private VBox notifications_VBox;

	/////////////////////////////////////////////////////////////////
	/////////////////////////CHAT FXML VARIABLES/////////////////////
	/////////////////////////////////////////////////////////////////
	
    @FXML
    private VBox loadingScreen;

	@FXML
	private AnchorPane mainAnchorPane;

	@FXML
	private VBox main_Vbox_Left;

	@FXML
	private Pane main_Vbox_Left_QuickInfo;

	@FXML
	private ProgressBar main_Vbox_Left_QuickInfo_ProgressBar;

	@FXML
	private Label main_Vbox_Left_QuickInfo_Label_Lvl;

	@FXML
	private Label main_Vbox_Left_QuickInfo_label_FullName;

	@FXML
	private HBox main_Vbox_Left_HBox_Search;

	@FXML
	private TextField main_Vbox_Left_HBox_Search_TextField;

	@FXML
	private HBox main_Vbox_Left_HBox_Dashboard;

	@FXML
	private HBox main_Vbox_Left_HBox_Chat;

	@FXML
	private Label main_Vbox_Left_HBox_Chat_Notify;

	@FXML
	private HBox main_Vbox_Left_HBox_Profile;

	@FXML
	private HBox main_Vbox_Left_HBox_Friends;

	@FXML
	private Label main_Vbox_Left_HBox_Friends_Notify;

	@FXML
	private HBox main_Vbox_Left_HBox_Settings;

	@FXML
	private Label linkedIn_Url;

	@FXML
	private Label github_Url;

	@FXML
	private VBox dashboardPane;

	@FXML
	private Pane dashboardPane_titlePane;

	@FXML
	private Label dashboardPane_TitlePane_Label;

	@FXML
	private HBox dashboardPane_HBox;

	@FXML
	private VBox dashboardPane_HBox_Left;

	@FXML
	private ScrollPane dashboardPane_HBox_Left_ScrollPane;

	@FXML
	private VBox dashboardPane_HBox_Left_ScrollPane_VBox;

	@FXML
	private VBox dashboardPane_HBox_Right;

	@FXML
	private VBox dashboardPane_HBox_Right_VBox_Servers_Status;

	@FXML
	private VBox dashboardPane_HBox_Right_VBox_Servers_Status_Main_Server;

	@FXML
	private Label dashboardPane_HBox_Right_VBox_Servers_Status_MainServer_Label_Status;

	@FXML
	private Label dashboardPane_HBox_Right_VBox_Servers_Status_MainServer_Label_NumberUsers;

	@FXML
	private VBox chatPane;

	@FXML
	private Pane chatPane_titlePane;

	@FXML
	private Label chatPane_TitlePane_Label;

	@FXML
	private HBox chatPane_HBox;

	@FXML
	private VBox chatPane_HBox_Left;

	@FXML
	private ScrollPane chatPane_HBox_Left_ScrollPane;

	@FXML
	private VBox chatPane_HBox_Left_ScrollPane_VBox;

	@FXML
	private HBox chatPane_HBox_Left_Handle_Input;

	@FXML
	private TextArea chatPane_HBox_Left_Handle_Input_TextArea;

	@FXML
	private FontAwesomeIconView chatPane_HBox_Left_Handle_Input_Button;

	@FXML
	private VBox chatPane_HBox_Right;

	@FXML
	private ScrollPane chatPane_HBox_Right_ScrollPane;

	@FXML
	private VBox chatPane_HBox_Right_ScrollPane_VBox;

	@FXML
	private VBox profilePane;

	@FXML
	private Pane profilePane_titlePane;

	@FXML
	private Label profilePane_TitlePane_Label;

	@FXML
	private ProgressIndicator profilePane_ProgressIndicator;

	@FXML
	private Label profilePane_Label_Level;

	@FXML
	private Label profilePane_Label_Percentage_Experience;

	@FXML
	private Label profilePane_Details_Label_Firstname;

	@FXML
	private Label profilePane_Details_Label_Lastname;

	@FXML
	private Label profilePane_Details_Label_Age;

	@FXML
	private Label profilePane_Details_Label_Email;

	@FXML
	private JFXButton profilePane_Details_Button_changeDetails;

	@FXML
	private Label profilePane_Statistics_Label_MessagesSend;

	@FXML
	private Label profilePane_Statistics_Label_WordsWritten;

	@FXML
	private Label profilePane_Statistics_Label_ArchivementsCompleted;

	@FXML
	private Label profilePane_Statistics_Label_TotalExperience;

	@FXML
	private VBox friendsPane;

	@FXML
	private ScrollPane friendsPane_ScrollPane_FriendsFeed;

	@FXML
	private VBox friendsPane_ScrollPane_FriendsFeed_VBox;

	@FXML
	private ScrollPane friendsPane_ScrollPane_Requests;

	@FXML
	private VBox friendsPane_ScrollPane_Requests_VBox;

	@FXML
	private ScrollPane friendsPane_ScrollPane_Friends;

	@FXML
	private VBox friendsPane_ScrollPane_Friends_VBox;

	@FXML
	private VBox settingsPane;

	@FXML
	private JFXTextField settingsPane_TextField_Firstname;

	@FXML
	private JFXTextField settingsPane_TextField_Lastname;

	@FXML
	private JFXTextField settingsPane_TextField_Age;

	@FXML
	private JFXTextField settingsPane_TextField_Email;

	@FXML
	private JFXButton settingsPane_Button_UpdateChanges;

	@FXML
	private FontAwesomeIconView settingsPane_Button_Clear;

	@FXML
	private HBox main_HBox_Top;

	@FXML
	private FontAwesomeIconView button_Minimize;

	@FXML
	private FontAwesomeIconView button_Quit;

	@FXML
	private FontAwesomeIconView main_bell_notifications;

	@FXML
	private Label main_bell_notifications_Label;

	@FXML
	private VBox main_Notifications_PopUP;

	@FXML
	private FontAwesomeIconView main_Notifications_PopUP_Close_Button;

	@FXML
	private VBox main_Notifications_PopUP_VBox;

	@FXML
	private Label main_Version_Label;

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
			updateSceneToMenu();
			//send info
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
	void loginWithEnterKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			if(validateInputLogin()) {
				String username = launcher_VBox_Pane_LoginVBox_TextField_Username.getText();
				String password = launcher_VBox_Pane_LoginVBox_TextField_Password.getText();
				//send info
			}
		}
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
	void updateChanges(MouseEvent event) {

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
