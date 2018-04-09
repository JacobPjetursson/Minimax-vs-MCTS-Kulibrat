package gui;

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


    public EndGamePane(Stage primaryStage, int team) {
        Label label = new Label();
        if (team == RED) label.setText("Congratulations player Red!");
        else if (team == BLACK) label.setText(("Congratulations player Black!"));
        else label.setText(("No turns possible: It's a draw!"));

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

        Button newGameBtn = new Button("New Game");
        HBox newGame = new HBox(newGameBtn);
        newGame.setAlignment(Pos.CENTER);
        AnchorPane.setRightAnchor(newGame, 40.0);
        AnchorPane.setTopAnchor(newGame, 0.0);
        AnchorPane.setBottomAnchor(newGame, 0.0);
        newGameBtn.setOnMouseClicked(event -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();

            primaryStage.setScene(new Scene(new NewGamePane(),
                    Globals.WIDTH, Globals.HEIGHT));

        });

        getChildren().addAll(label, menu, newGame);

    }
}
