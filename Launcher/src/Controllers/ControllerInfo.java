package Controllers;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ControllerInfo {

    public Stage stage;

    @FXML
    public Label infoTitle;

    @FXML
    public Label fixBugsTitle;

    @FXML
    public Label lblFixBugs;

    @FXML
    public Button closeInfoButton;

    @FXML
    public Label commandTitle;

    @FXML
    public Label lblCommands;

    @FXML
    void closeInfo(ActionEvent event) {
        stage.close();
    }

    public void setStageAndSetupListeners(Stage stage) {
        this.stage = stage;
    }


}
