package StartLaunch;
import Controllers.ControllerLauncher;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.event.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

@SuppressWarnings("restriction")
public class Chat extends Application{
	    
	private static final String version = "v0.0.80";
		    
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		ControllerLauncher c = new ControllerLauncher(primaryStage,version);
		c.iniciarController();

	}
}
