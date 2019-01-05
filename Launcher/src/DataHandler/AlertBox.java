package DataHandler;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class AlertBox {

    public static void display(String title, String message,boolean close) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.initStyle(StageStyle.UNDECORATED);


        Label label = new Label();
        label.setText(message);

        Button button = new Button("Fechar");
        if(close) {
            button.setOnAction(e -> System.exit(-1));
        }else {
            button.setOnAction(e -> window.close());
        }


        VBox layout = new VBox(10);
        layout.getChildren().addAll(label,button);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #c11f1f;");


        Scene scene = new Scene(layout,350,200);
        window.setScene(scene);
        window.showAndWait();
    }
}
