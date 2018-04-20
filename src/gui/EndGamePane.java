package gui;

import game.Controller;
import gui.menu.MenuPane;
import gui.menu.NewGamePane;
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

import static misc.Globals.BLACK;
import static misc.Globals.RED;

public class EndGamePane extends AnchorPane {


    public EndGamePane(Stage primaryStage, int team, Controller cont) {
        Label label = new Label();
        if (team == RED) label.setText("Congratulations player Red!");
        else label.setText(("Congratulations player Black!"));


        label.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        label.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(label, 20.0);
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);

        Button menuBtn = new Button("Menu");
        HBox menu = new HBox(menuBtn);
        menu.setAlignment(Pos.CENTER);
        AnchorPane.setLeftAnchor(menu, 40.0);
        AnchorPane.setTopAnchor(menu, 0.0);
        AnchorPane.setBottomAnchor(menu, 0.0);
        menuBtn.setOnMouseClicked(event -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();
            primaryStage.setScene(new Scene(new MenuPane(),
                    Globals.WIDTH, Globals.HEIGHT));

        });

        Button restartGameBtm = new Button("Restart Game");
        HBox restartGame = new HBox(restartGameBtm);
        restartGame.setAlignment(Pos.CENTER);
        AnchorPane.setRightAnchor(restartGame, 40.0);
        AnchorPane.setTopAnchor(restartGame, 0.0);
        AnchorPane.setBottomAnchor(restartGame, 0.0);
        restartGameBtm.setOnMouseClicked(event -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();
            new Controller(primaryStage, cont.getPlayerInstance(RED),
                    cont.getPlayerInstance(BLACK), cont.getPointsToWin(), cont.getTime(RED), cont.getTime(BLACK), cont.getOverwriteDB());
        });

        getChildren().addAll(label, menu, restartGame);

    }
}
