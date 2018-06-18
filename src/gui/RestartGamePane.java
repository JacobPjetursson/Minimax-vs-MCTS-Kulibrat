package gui;

import game.Controller;
import game.State;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import static misc.Globals.BLACK;
import static misc.Globals.RED;

class RestartGamePane extends AnchorPane {

    RestartGamePane(Stage primaryStage, Controller cont) {
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
            new Controller(primaryStage, cont.getPlayerInstance(RED),
                    cont.getPlayerInstance(BLACK), new State(cont.getScoreLimit()), cont.getTime(RED), cont.getTime(BLACK), cont.getOverwriteDB());
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
