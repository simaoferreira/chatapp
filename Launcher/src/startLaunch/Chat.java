package startLaunch;

import controllers.ControllerChat;
import dataHandler.LoggerHandle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Chat extends Application{

    private static final String version = "v0.3.0";
    private static LoggerHandle             lh = null;

    public static void main(String[] args) {
    	lh = new LoggerHandle();
    	try {
    	    Application.launch(args);
    	    lh.log("INFO", "Aplication started");
    	    
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

        ControllerChat c = new ControllerChat(primaryStage,version,lh);
        c.startController();

    }
}
