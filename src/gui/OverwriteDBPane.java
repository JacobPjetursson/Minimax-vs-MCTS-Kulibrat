package gui;

import game.Controller;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class OverwriteDBPane extends AnchorPane {

    public OverwriteDBPane(Controller cont) {
        Label label = new Label("The DB Table for this score limit has not been built.\n" +
                "       Do you want to build it? It will take a while");
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
            cont.buildDB();
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
