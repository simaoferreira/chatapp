package startLaunch;

import controllers.ControllerChat;
import dataHandler.LoggerHandle;
import dataHandler.PropertiesFile;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Chat extends Application{

    private static final String version = "0.6.4";
    private static LoggerHandle             lh = null;
    private static PropertiesFile           pf = null;

    public static void main(String[] args) {
    	lh = new LoggerHandle();
    	pf = new PropertiesFile("config.properties",lh);
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
    	
        ControllerChat c = new ControllerChat(primaryStage,version,lh,pf);
        c.startController();

    }
}
