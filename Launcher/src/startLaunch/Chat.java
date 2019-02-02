package startLaunch;

import controllers.ControllerLauncher;
import javafx.application.Application;
import javafx.stage.Stage;

public class Chat extends Application{

    private static final String version = "v0.1.50";

    public static void main(String[] args) {
    	try {
    	    Application.launch(args);
    	    
    	} catch (IllegalStateException e) {}
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ControllerLauncher c = new ControllerLauncher(primaryStage,version);
        c.iniciarController();

    }
}
