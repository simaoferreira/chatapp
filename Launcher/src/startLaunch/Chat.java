package startLaunch;

import controllers.ControllerLauncher;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Chat extends Application{

    private static final String version = "v0.2.00";

    public static void main(String[] args) {
    	try {
    	    Application.launch(args);
    	    
    	} catch (IllegalStateException e) {}
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    	Platform.setImplicitExit(false);
    	primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent event) {
		        event.consume();
		    }
		});

        ControllerLauncher c = new ControllerLauncher(primaryStage,version);
        c.iniciarController();

    }
}
