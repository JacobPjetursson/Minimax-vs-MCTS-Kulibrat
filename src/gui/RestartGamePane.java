package gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import misc.Globals;

class RestartGamePane extends AnchorPane {

    RestartGamePane(Stage primaryStage, int playerRedMode, int playerBlackMode, int pointsToWin, int redTime, int blackTime) {
        Label label = new Label("Are you sure you want to restart?");
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        label.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(label, 20.0);
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);

        Button yesBtn = new Button("Yes");
        HBox yes = new HBox(yesBtn);
        yes.setAlignment(Pos.CENTER);
        AnchorPane.setLeftAnchor(yes, 40.0);
        AnchorPane.setTopAnchor(yes, 0.0);
        AnchorPane.setBottomAnchor(yes, 0.0);
        yesBtn.setOnMouseClicked(event -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();

            primaryStage.setScene(new Scene(new PlayPane(playerRedMode,
                    playerBlackMode, pointsToWin, redTime, blackTime),
                    Globals.WIDTH, Globals.HEIGHT));

        });

        Button noBtn = new Button("No");
        HBox no = new HBox(noBtn);
        no.setAlignment(Pos.CENTER);
        AnchorPane.setRightAnchor(no, 40.0);
        AnchorPane.setTopAnchor(no, 0.0);
        AnchorPane.setBottomAnchor(no, 0.0);
        noBtn.setOnMouseClicked(event -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();
        });

        getChildren().addAll(label, yes, no);
    }
}
