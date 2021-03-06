package controllers;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import java.io.IOException;
import java.net.UnknownHostException;

import client.Client;
import dataHandler.AlertBox;
import dataHandler.Informations;
import dataHandler.Notifications;
import dataHandler.ProtectionBadWords;
import dataHandler.User;
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

public class ControllerLauncher {


	private static double xOffset = 0;
    private static double yOffset = 0;
    
	public ControllerLauncher(Stage primaryStage,String version) throws IOException {

		this.stage = primaryStage;
		this.version = version;
		stage.initStyle(StageStyle.UNDECORATED);	

		loadLogin();
		loadMenu();

	}

	public void startController() {

		//String musicFile = "C:/Users/simao/eclipse-workspace/Launcher/src/Controllers/mastir.mp3";
		//Media sound = new Media(new File(musicFile).toURI().toString());
		//mediaPlayer = new MediaPlayer(sound);

		//c = new Client(this);
		usernameTextField.setTextFormatter(new TextFormatter<String>(change -> 
		change.getControlNewText().length() <= MAX_CHARS ? change : null));
		passwordTextField.setTextFormatter(new TextFormatter<String>(change -> 
		change.getControlNewText().length() <= MAX_CHARS+8 ? change : null));
		lblversionFirst.setText(version);
		lblversionSecond.setText(version);
		lbl.setVisible(false);
		Scene scene = new Scene(loginPane);
		
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
	}

	private void loadMenu() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("mainV2.fxml"));
		loader.setController(this);
		
		menuPane = (GridPane) loader.load();
		menuPane.setBorder(darkblue);
		menuPane.setStyle("-fx-background: #4e5460;");
		menuPane.getStylesheets().add("themeOne.css");
		menuPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });
		
		menuPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	stage.setX(event.getScreenX() + xOffset);
            	stage.setY(event.getScreenY() + yOffset);
            }
        });

	}

	private void loadLogin() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("launcher.fxml"));
		loader.setController(this);
		loginPane.getStylesheets().add(getClass().getResource("themeOne.css").toExternalForm());
		loginPane = (Pane) loader.load();
		loginPane.setBorder(darkblue);
		loginPane.setStyle("-fx-background: #4e5460;");
		
		loginPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });
		
		loginPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	stage.setX(event.getScreenX() + xOffset);
            	stage.setY(event.getScreenY() + yOffset);
            }
        });
	}

	private static final Border darkblue = new Border(new BorderStroke(Color.rgb(32,33,35),
			BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(3)));
	
	Client c;
	public MediaPlayer mediaPlayer;
	private static final int MAX_CHARS = 12;
	public String version;
	public GridPane menuPane;
	private Pane loginPane;
	public Stage stage;
	public String text;
	public String connections;
	public String lastUserPrivate=""; 
	public int id= -1;
	public Label mensagem;
	public Label userOfMensagem;
	public User user = null;
	//private double xMensagem = 10;
	//public double yMensagem = 30;
	public double xUserOfMensagem = 10;
	public double yUserOfMensagem = 10;
	public double yFriendUser = 0;
	public double xFriendUser = 0;
	public double height;
	public double heightFriend;
	public ProtectionBadWords pbw = new ProtectionBadWords();
	public Informations info = new Informations();
	public Notifications notify = new Notifications();
	public Rectangle rec;
	public GridPane centeredLabel;
	public GridPane friendsLabel;
	public boolean canConnect;

	@FXML
	private TextField passwordTextFieldRegister;

	@FXML
	public Pane loginPaneFields;

	@FXML
	private Label lblCreateAccount;

	@FXML
	private Label lblLoginAccount;

	@FXML
	private TextField passwordAgainTextFieldRegister;

	@FXML
	private Button registerButton;

	@FXML
	private Pane registerPane;

	@FXML
	public TextField usernameTextFieldRegister;

	@FXML
	public Label userLbl;

	@FXML
	public ScrollPane connectionsScrollPane;

	@FXML
	public Pane chatPane;
	
	@FXML
	public Pane friendsPane;

	@FXML
	public ScrollPane chatScrollPane;

	@FXML
	public Label titleLiveNews;

	@FXML
	public Label lblLiveNews;

	@FXML
	public Label lblversionSecond;

	@FXML
	public Button closeButton;

	@FXML
	public Label lbl;

	@FXML
	public Button sendMessage;

	@FXML
	public TextField txtField;

	@FXML
	public Label lblConnections;

	@FXML
	public Button minimizeButton;

	@FXML
	public Button infoButton;

	@FXML
	public Label labelFriends;

	@FXML
	public ScrollPane scrollPaneFriends;

	@FXML
	public Label labelUsersOnline;

	@FXML
	public Label lblFriends;

	@FXML
	public Label lblversionFirst;

	@FXML
	public TextField passwordTextField;

	@FXML
	public Circle circleLogin;

	@FXML
	public Separator separatorPassword;

	@FXML
	public ImageView backgrounPic;

	@FXML
	public Button loginButton;

	@FXML
	public Separator separatorUsername;

	@FXML
	public Pane paneLauncher;

	@FXML
	public TextField usernameTextField;

	@FXML
	public Button closeButtonFirst;

	@FXML
	public Button minimizeButtonFirst;

	@FXML
	public Label lblerror;
	

	@FXML
	public void addFriendPane(User u) {
		Label mensagem = new Label(u.getFullName());
		Label infoUser = new Label("Level: "+ u.getUserLvl() + "  Exp: "+u.getUserExp());
		infoUser.setStyle("-fx-font: normal 11px \" Comic Sans MS\"");
		mensagem.setStyle("-fx-font: normal 15px \" Comic Sans MS\"");
		friendsLabel = new GridPane();
		friendsLabel.setPrefWidth(190);
		friendsLabel.add(mensagem, 0, 0);
		friendsLabel.add(infoUser, 0, 1);
		friendsLabel.setStyle("-fx-background-color:#0099ff; -fx-padding: 5 5 5 5;");
		friendsLabel.setLayoutX(xFriendUser);
		friendsLabel.setLayoutY(yFriendUser);
		yFriendUser = yFriendUser+52;
		
		friendsLabel.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		    	AlertBox.display("Details", u.getFullName(),Color.RED,false);
		    }
		});
	}
	
	@FXML
	public void printMessage(String text,String user, String hour,Side side,Type type) {

		Label userOfMensagem = new Label(user);
		userOfMensagem.setStyle("-fx-font: normal 11px \"Comic Sans MS\"");
		userOfMensagem.setUnderline(true);
		Label mensagem = new Label(text);
		mensagem.setStyle("-fx-font: normal 11px \" Comic Sans MS\"");
		Label actualHour = new Label(hour);
		actualHour.setStyle("-fx-font: normal 7px \" Comic Sans MS\"");
		mensagem.setMaxWidth(200);
		mensagem.setWrapText(true);
		centeredLabel = new GridPane();
		//centeredLabel.setMinHeight(40);
		centeredLabel.setPrefWidth(250);


		if(type == Type.LOGIN) {
			centeredLabel.add(mensagem, 0, 0);
			centeredLabel.setStyle("-fx-background-radius: 10 10 10 10; -fx-background-color:#8a909b; -fx-padding: 5 5 5 5;");
			mensagem.setStyle("-fx-text-fill:#e5ff00;");
			
			centeredLabel.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			    @Override
			    public void handle(MouseEvent mouseEvent) {
			    	AlertBox.display("Details", user + " connected at "+hour,Color.RED,false);
			    }
			});
			
		}else if(type == Type.LOGOUT){
			centeredLabel.add(mensagem, 0, 0);
			centeredLabel.setStyle("-fx-background-radius: 10 10 10 10; -fx-background-color:#8a909b; -fx-padding: 5 5 5 5;");
			mensagem.setStyle("-fx-text-fill:#bf0000;");
			
			centeredLabel.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			    @Override
			    public void handle(MouseEvent mouseEvent) {
			    	AlertBox.display("Details", user + " disconnected at "+hour,Color.RED,false);
			    }
			});
			
		}else if(type == Type.FRIENDS){
			centeredLabel.add(mensagem, 0, 0);
			centeredLabel.setStyle("-fx-background-radius: 10 10 10 10; -fx-background-color:#0099ff; -fx-padding: 5 5 5 5;");
			mensagem.setStyle("-fx-text-fill:#000000;");
			
			centeredLabel.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			    @Override
			    public void handle(MouseEvent mouseEvent) {
			    	AlertBox.display("Details", text+ " at "+hour,Color.RED,false);
			    }
			});
			
		}else {
			centeredLabel.add(userOfMensagem, 0, 0);
			centeredLabel.add(mensagem, 0, 1);
			centeredLabel.add(actualHour, 0, 2);
			centeredLabel.setStyle("-fx-background-radius: 10 10 10 10; -fx-background-color:#8a909b; -fx-padding: 5 5 5 5;");
			
		}

		if(side == Side.LEFT) {
			centeredLabel.setLayoutX(xUserOfMensagem);
			centeredLabel.setLayoutY(yUserOfMensagem);
			
			if(type != Type.LOGIN && type != Type.LOGOUT && type != Type.FRIENDS) {
				centeredLabel.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				    @Override
				    public void handle(MouseEvent mouseEvent) {
				    	AlertBox.display("Details", user + " sent this message at "+hour,Color.RED,false);
				    }
				});
			}
		}else {
			centeredLabel.setStyle("-fx-background-radius: 10 10 10 10; -fx-background-color:#1e3563; -fx-padding: 5 5 5 5;");
			centeredLabel.setLayoutX(xUserOfMensagem+300);
			centeredLabel.setLayoutY(yUserOfMensagem);
			
			centeredLabel.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			    @Override
			    public void handle(MouseEvent mouseEvent) {
			    	AlertBox.display("Details", "You sent message at "+hour,Color.RED,false);
			    }
			});
			
		}
		
		centeredLabel.heightProperty().addListener((obs, oldVal, newVal) -> {
			height = centeredLabel.getHeight();
			yUserOfMensagem = yUserOfMensagem+height+10;
		});
		

	}


	@FXML
	public void changeToLogin(MouseEvent event) {
		registerPane.setVisible(false);
		loginPaneFields.setVisible(true);
		usernameTextFieldRegister.clear();
		passwordTextFieldRegister.clear();
		passwordAgainTextFieldRegister.clear();
	}
	
	public void clearRegisterPane() {
		usernameTextFieldRegister.clear();
		passwordTextFieldRegister.clear();
		passwordAgainTextFieldRegister.clear();
	}
	
	public void changeToLoginAlternative() {
		registerPane.setVisible(false);
		loginPaneFields.setVisible(true);
		usernameTextFieldRegister.clear();
		passwordTextFieldRegister.clear();
		passwordAgainTextFieldRegister.clear();
	}

	@FXML
	void registerKeyButton(ActionEvent event) {
		String username = usernameTextFieldRegister.getText();
		String password = passwordTextFieldRegister.getText();
		String passwordAgain = passwordAgainTextFieldRegister.getText();
		if(passwordAgain.equals(password)) {
			//c.registerAccount(username, password);
		}else {
			AlertBox.display("Details", "Passwords don't match!",Color.RED,false);
			passwordTextFieldRegister.clear();
			passwordAgainTextFieldRegister.clear();
		}
	}

	@FXML
	void registerKeyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			String username = usernameTextFieldRegister.getText();
			String password = passwordTextFieldRegister.getText();
			String passwordAgain = passwordAgainTextFieldRegister.getText();
			if(passwordAgain.equals(password)) {
				//c.registerAccount(username, password);
			}else {
				AlertBox.display("Details", "Passwords don't match!",Color.RED,false);
				passwordTextFieldRegister.clear();
				passwordAgainTextFieldRegister.clear();
			}
		}

	}

	@FXML
	void changeToRegister(MouseEvent event) {
		loginPaneFields.setVisible(false);
		registerPane.setVisible(true);
		usernameTextField.clear();
		passwordTextField.clear();
	}

	@FXML
	void openInfoByLabel(MouseEvent  event) {
		//c.requestInfoUser();
	}

	@FXML
	void loginKeyPressed(KeyEvent event) throws IOException {
		if (event.getCode() == KeyCode.ENTER) {
			//c.requestUpdateConnections(usernameTextField.getText(),passwordTextField.getText());
			loginPaneFields.setVisible(false);
			//mediaPlayer.play();
		}
	}

	@FXML
	void loginKeyButton(ActionEvent event) throws IOException {
		//c.requestUpdateConnections(usernameTextField.getText(),passwordTextField.getText());
		loginPaneFields.setVisible(false);
		
	}

	private void makeFadeOut() {
		
	}

	public void updateSceneToMenu() {
		
		FadeTransition fadeTransition = new FadeTransition();
		fadeTransition.setDuration(Duration.millis(1000));
		fadeTransition.setNode(loginPane);
		fadeTransition.setFromValue(1);
		fadeTransition.setToValue(0);
		fadeTransition.play();
		
		Scene scene = new Scene(menuPane);
		scene.getStylesheets().add(getClass().getResource("/themeOne.css").toExternalForm());
		
		fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.setScene(scene);
			}
		});
		
		
		scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			final KeyCombination keyComb = new KeyCodeCombination(KeyCode.I,
					KeyCombination.CONTROL_DOWN);
			public void handle(KeyEvent ke) {
				if (keyComb.match(ke)) {
					//c.requestInfoUser();
				}
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

	}



	@FXML
	void onSendMessage(ActionEvent event) throws InterruptedException {
		handleInput();
		txtField.requestFocus();
	}


	@FXML
	void keyListener(KeyEvent event) {    	
		if (event.getCode() == KeyCode.UP) {
			System.out.println("deu!");
		}else if (event.getCode() == KeyCode.ENTER) {
			handleInput();
		}
	}
	
	private void handleInput() {
		text = pbw.findAnDeleteBadWords(txtField.getText());

		fastScrollToBottom(chatScrollPane);
		if(text.equals("")) {

		}else if(text.equals("/close")){
			Platform.runLater(new Runnable() {
				@Override 
				public void run() {
					stage.close(); 

				}
			});
			//c.requestCloseConnection();
		/**
		}else if(text.startsWith("/send")) {
			c.sendPrivateMessageToChat(user);
			txtField.clear();
		
		}else if(text.startsWith("/r")){
			if(lastUserPrivate.equals("")) {
				System.err.print("Don't know the user!\n");
				txtField.setPromptText("You can't reply! Please send a new message or you wait for a new message");
				txtField.clear();
			}else {
				c.replyPrivateMessageToChat(user,lastUserPrivate);
				txtField.clear();
			}
		*/
		}else if(text.equals("/clear")) {
			// limpar baloes de chat
			txtField.clear();
		}else if(text.equals("/getinfo")) {
			//c.requestInfoUser();
			txtField.clear();
		}else if(text.startsWith("/addfriend ")){
			//c.sendRequestFriend();
			txtField.clear();
		}else if(text.startsWith("/accept ")){
			//c.acceptFriendRequest();
			txtField.clear();
		}else if(text.startsWith("/decline ")){
			//c.declineFriendRequest();
			txtField.clear();
		}else {
			if(text.startsWith("/")) {
				txtField.setPromptText("Can�t recognize command! Go see info for details please.");
			}else {
				txtField.clear();
				//c.sendMessagesToChat(user.getUsername());
			}
		}
	}

	@FXML
	void closeWindow(ActionEvent event) {
		Platform.runLater(new Runnable() {
			@Override 
			public void run() {
				stage.close(); 

			}
		});
		//c.requestCloseConnection();
		System.exit(0);
	}

	@FXML
	void minimizeWindow(ActionEvent event) {
		Platform.runLater(new Runnable() {
			@Override 
			public void run() {
				stage.setIconified(true); 

			}
		});

	}

	@FXML
	void openInfo(ActionEvent event) throws IOException {
		Stage infoStage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../controllers/info.fxml"));
		Pane infoPane = (Pane) loader.load();
		infoPane.setBorder(darkblue);
		infoPane.setStyle("-fx-background: #cc4b2a;");
		infoStage.initStyle(StageStyle.UNDECORATED);

		ControllerInfo controller = (ControllerInfo) loader.getController();

		controller.setStageAndSetupListeners(infoStage);
		controller.commandTitle.setText("Commands: (disabled for now (thanks for you patience!)");
		controller.lblCommands.setText(info.getTextCommands());

		controller.lblFixBugs.setText(info.getTextFixBugs());

		infoStage.setScene(new Scene(infoPane));
		infoStage.show();
		/**
		infoStage.initModality(Modality.APPLICATION_MODAL);
		infoStage.initOwner(stage);
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(new Text("This is a Dialog"));
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        infoStage.setScene(dialogScene);
        infoStage.show();
		 */


	}

	static void fastScrollToBottom(ScrollPane scrollPane) {
		Animation animation = new Timeline(
				new KeyFrame(Duration.seconds(0.1),
						new KeyValue(scrollPane.vvalueProperty(), 1)));
		animation.play();
	}


	public void connectToServer() throws UnknownHostException, IOException {

	}

}
