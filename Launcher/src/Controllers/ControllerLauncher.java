package Controllers;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import java.io.IOException;
import java.net.UnknownHostException;
import Client.Client;
import DataHandler.AlertBox;
import DataHandler.Informations;
import DataHandler.Notifications;
import DataHandler.ProtectionBadWords;
import javafx.animation.Animation;
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
import javafx.util.Duration;
import javafx.scene.media.MediaPlayer;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCodeCombination;

public class ControllerLauncher {


    public ControllerLauncher(Stage primaryStage,String version) throws IOException {

        this.stage = primaryStage;
        this.version = version;
        stage.initStyle(StageStyle.UNDECORATED);	

        loadLogin();
        loadMenu();

    }

    public void iniciarController() {

        //String musicFile = "C:/Users/simao/eclipse-workspace/Launcher/src/Controllers/mastir.mp3";
        //Media sound = new Media(new File(musicFile).toURI().toString());
        //mediaPlayer = new MediaPlayer(sound);

        try {
            c = new Client(this);
            usernameTextField.setTextFormatter(new TextFormatter<String>(change -> 
            change.getControlNewText().length() <= MAX_CHARS ? change : null));
            passwordTextField.setTextFormatter(new TextFormatter<String>(change -> 
            change.getControlNewText().length() <= MAX_CHARS+8 ? change : null));
            lblversionFirst.setText(version);
            lblversionSecond.setText(version);
            lbl.setVisible(false);
            stage.setScene(new Scene(loginPane));
            stage.show();   
        } catch (IOException e) {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    AlertBox.display("Error", "Can't connect to server",true);
                }
            });
        }
    }

    private void loadMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainV2.fxml"));
        loader.setController(this);
        menuPane = (GridPane) loader.load();
        menuPane.setBorder(darkblue);
        menuPane.setStyle("-fx-background: #4e5460;");

    }

    private void loadLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("launcher.fxml"));
        loader.setController(this);
        loginPane = (Pane) loader.load();
        loginPane.setBorder(darkblue);
        loginPane.setStyle("-fx-background: #4e5460;");	
    }

    private static final Border darkblue = new Border(new BorderStroke(Color.rgb(32,33,35),
            BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(3)));

    private Client c;
    private MediaPlayer mediaPlayer;
    private static final int MAX_CHARS = 12;
    private String version;
    private GridPane menuPane;
    private Pane loginPane;
    private Stage stage;
    public String user="";
    public String text;
    public String lastUserPrivate=""; 
    public String id="";
    public int lvlUser;
    public int expUser;
    public int numMensagens;
    public int numWordsWritten;
    private double xMensagem = 10;
    private double yMensagem = 30;
    private double xUserOfMensagem = 10;
    private double yUserOfMensagem = 10;
    private double height;
    private ProtectionBadWords pbw = new ProtectionBadWords();
    private Informations info = new Informations();
    public GridPane centeredLabel;


    @FXML
    public ScrollPane connectionsScrollPane;

    @FXML
    public Pane chatPane;

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
    public Label lblListConnections;

    @FXML
    public Button minimizeButton;

    @FXML
    public Button infoButton;


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
    public void initialize(String text,String user, String hour,String side,String isConnection) {

        Label userOfMensagem = new Label(user);
        userOfMensagem.setStyle("-fx-font: normal 11px Helvetica");
        userOfMensagem.setUnderline(true);
        Label mensagem = new Label(text);
        mensagem.setStyle("-fx-font: normal 11px Helvetica");
        Label actualHour = new Label(hour);
        actualHour.setStyle("-fx-font: normal 9px Helvetica");
        mensagem.setMaxWidth(200);
        mensagem.setWrapText(true);
        centeredLabel = new GridPane();
        //centeredLabel.setMinHeight(40);
        centeredLabel.setPrefWidth(250);


        if(isConnection.equals("1")) {
            centeredLabel.add(mensagem, 0, 0);
            centeredLabel.setStyle("-fx-background-radius: 10 10 10 10; -fx-background-color:#8a909b; -fx-padding: 5 5 5 5;");
            mensagem.setStyle("-fx-text-fill:#e5ff00;");
        }else if(isConnection.equals("2")){
            centeredLabel.add(mensagem, 0, 0);
            centeredLabel.setStyle("-fx-background-radius: 10 10 10 10; -fx-background-color:#8a909b; -fx-padding: 5 5 5 5;");
            mensagem.setStyle("-fx-text-fill:#bf0000;");
        }else {
            centeredLabel.add(userOfMensagem, 0, 0);
            centeredLabel.add(mensagem, 0, 1);
            centeredLabel.add(actualHour, 0, 2);
            centeredLabel.setStyle("-fx-background-radius: 10 10 10 10; -fx-background-color:#8a909b; -fx-padding: 5 5 5 5;");
        }

        if(side.equals("left")) {
            centeredLabel.setLayoutX(xUserOfMensagem);
            centeredLabel.setLayoutY(yUserOfMensagem);
        }else {
            centeredLabel.setStyle("-fx-background-radius: 10 10 10 10; -fx-background-color:#1e3563; -fx-padding: 5 5 5 5;");
            centeredLabel.setLayoutX(xUserOfMensagem+300);
            centeredLabel.setLayoutY(yUserOfMensagem);
        }

        centeredLabel.heightProperty().addListener((obs, oldVal, newVal) -> {
            height = centeredLabel.getHeight();
            yUserOfMensagem = yUserOfMensagem+height+10;
        });

    }



    @FXML
    void loginKeyPressed(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {

            c.requestUpdateConnections(usernameTextField.getText(),passwordTextField.getText());
            //mediaPlayer.play();
        }
    }

    public void updateSceneToMenu() {
        Scene scene = new Scene(menuPane);
        stage.setScene(scene);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.I,
                    KeyCombination.CONTROL_DOWN);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    c.requestInfoUser();
                }
            }
        });

    }

    @FXML
    void loginKeyButton(ActionEvent event) throws IOException {
        c.requestUpdateConnections(usernameTextField.getText(),passwordTextField.getText());
    }

    @FXML
    void onSendMessage(ActionEvent event) throws InterruptedException {

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
            c.requestCloseConnection();
        }else if(text.startsWith("/send")) {
            c.sendPrivateMessageToChat(user);
            txtField.setText("");
        }else if(text.startsWith("/r")){
            if(lastUserPrivate.equals("")) {
                System.err.print("Don't know the user!\n");
                txtField.setPromptText("You can't reply! Please send a new message or you wait for a new message");
                txtField.setText("");
            }else {
                c.replyPrivateMessageToChat(user,lastUserPrivate);
                txtField.setText("");
            }

        }else if(text.equals("/clear")) {
            c.requestClearChat();
            txtField.setText("");
        }else if(text.equals("/getinfo")) {
            c.requestInfoUser();
            txtField.setText("");
        }else {
            if(text.startsWith("/")) {
                txtField.setPromptText("Can´t recognize command! Go see info for details please.");
            }else {
                txtField.setText("");
                c.sendMessagesToChat(user);
            }
        }

        txtField.requestFocus();

    }


    @FXML
    void keyListener(KeyEvent event) {    	
        if (event.getCode() == KeyCode.UP) {
            System.out.println("deu!");
        }else if (event.getCode() == KeyCode.ENTER) {

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
                c.requestCloseConnection();
            }else if(text.startsWith("/send")) {
                c.sendPrivateMessageToChat(user);
                txtField.setText("");
            }else if(text.startsWith("/r")){
                if(lastUserPrivate.equals("")) {
                    System.err.print("Don't know the user!\n");
                    txtField.setPromptText("You can't reply! Please send a new message or you wait for a new message");
                    txtField.setText("");
                }else {
                    c.replyPrivateMessageToChat(user,lastUserPrivate);
                    txtField.setText("");
                }

            }else if(text.equals("/clear")) {
                c.requestClearChat();
                txtField.setText("");
            }else if(text.equals("/getinfo")) {
                c.requestInfoUser();
                txtField.setText("");
            }else {
                if(text.startsWith("/")) {
                    txtField.setPromptText("Can´t recognize command! Go see info for details please.");
                }else {
                    txtField.setText("");
                    c.sendMessagesToChat(user);
                }
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
        c.requestCloseConnection();
        System.exit(-1);
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Controllers/info.fxml"));
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
