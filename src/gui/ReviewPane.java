package gui;

import game.Controller;
import game.Logic;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReviewPane extends AnchorPane {

    public ReviewPane(Stage primaryStage, Controller cont) {
        Button cancel = new Button("Cancel");
        cancel.setOnMouseClicked(event -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();
            if(Logic.gameOver(cont.getState())) {
                Stage newStage = new Stage();
                newStage.setScene(new Scene(new EndGamePane(primaryStage, Logic.getWinner(cont.getState()),
                        cont), 400, 150));
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.initOwner(cont.getWindow());
                newStage.setOnCloseRequest(Event::consume);
                newStage.show();
            }
        });
        getChildren().add(cancel);

    }
}
