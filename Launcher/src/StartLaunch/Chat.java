package StartLaunch;

import Controllers.ControllerLauncher;
import javafx.application.Application;
import javafx.stage.Stage;

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
