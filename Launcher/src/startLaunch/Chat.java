package startLaunch;

import controllers.ControllerLauncher;
import javafx.application.Application;
import javafx.stage.Stage;

public class Chat extends Application{

    private static final String version = "v0.1.00";

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ControllerLauncher c = new ControllerLauncher(primaryStage,version);
        c.iniciarController();

    }
}
