package dataHandler;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.control.*;
import javafx.geometry.*;

public class AlertBox {
	
	private static final Border red = new Border(new BorderStroke(Color.rgb(193, 31, 31),
			BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(5)));
	
	private static final Border white = new Border(new BorderStroke(Color.WHITE,
			BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(3)));

    public static void display(String title, String message,boolean close) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.initStyle(StageStyle.UNDECORATED);


        Label label = new Label();
        label.setTextFill(Color.WHITE);
        label.setText(message);

        Button button = new Button("Fechar");
        if(close) {
            button.setOnAction(e -> System.exit(-1));
        }else {
            button.setOnAction(e -> window.close());
        }


        VBox layout = new VBox(10);
        layout.setPrefSize(350, 200);
        layout.getChildren().addAll(label,button);
        layout.setAlignment(Pos.CENTER);
        layout.setBorder(red);
        layout.setStyle("-fx-border-color: #c11f1f;");
        //layout.setStyle("-fx-border-width: -1px -1px -1px -1px;");
        layout.setStyle("-fx-background-color: #202123;");
    	

        Scene scene = new Scene(layout,350,200);
        window.setScene(scene);
        window.showAndWait();
    }
}
