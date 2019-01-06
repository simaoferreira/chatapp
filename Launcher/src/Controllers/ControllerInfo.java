package Controllers;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ControllerInfo {

    private Stage stage;

    @FXML
    public Label infoTitle;

    @FXML
    public Label fixBugsTitle;

    @FXML
    public Label lblFixBugs;

    @FXML
    private Button closeInfoButton;

    @FXML
    public Label commandTitle;

    @FXML
    public Label lblCommands;

    @FXML
    private void closeInfo(ActionEvent event) {
        stage.close();
    }

    protected void setStageAndSetupListeners(Stage stage) {
        this.stage = stage;
    }


}
