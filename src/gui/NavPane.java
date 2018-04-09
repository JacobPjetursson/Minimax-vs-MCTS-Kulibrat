package gui;

import gui.menu.MenuPane;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import misc.Globals;

import java.util.ArrayList;

public class NavPane extends VBox {
    private int buttonWidth = Globals.WIDTH / 6 + 20;
    private ArrayList<Button> buttons;

    private Button startAIButton;
    private Button stopAIButton;
    private Button restartButton;
    private Button menuButton;
    private VBox AIWidgets;

    public NavPane(int playerRedInstance, int playerBlackInstance, int pointsToWin, int redTime, int blackTime, boolean overwriteDB) {
        setMinWidth(Globals.WIDTH / 3);
        setAlignment(Pos.CENTER);
        setSpacing(40);
        buttons = new ArrayList<Button>();

        restartButton = new Button("Restart Game");
        buttons.add(restartButton);
        restartButton.setOnMouseClicked(event -> restartGame(playerRedInstance, playerBlackInstance, pointsToWin, redTime, blackTime, overwriteDB));

        startAIButton = new Button("Start AI vs. AI");
        buttons.add(startAIButton);

        stopAIButton = new Button("Stop AI vs. AI");
        buttons.add(stopAIButton);

        AIWidgets = new VBox(startAIButton, stopAIButton);
        AIWidgets.setSpacing(10);
        AIWidgets.setAlignment(Pos.CENTER);


        menuButton = new Button("Menu");
        buttons.add(menuButton);
        menuButton.setOnMouseClicked(event -> goToMenu());


        for (Button button : buttons) {
            button.setMinWidth(buttonWidth);
            button.setBorder(new Border(new BorderStroke(Color.BLACK,
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        }
        getChildren().addAll(restartButton, menuButton);
    }

    private void restartGame(int playerRedInstance, int playerBlackInstance, int pointsToWin, int redTime, int blackTime, boolean overwriteDB) {
        Stage prevStage = (Stage) getScene().getWindow();

        Stage newStage = new Stage();
        newStage.setScene(new Scene(new RestartGamePane(prevStage, playerRedInstance,
                playerBlackInstance, pointsToWin, redTime, blackTime, overwriteDB), 400, 150));
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.initOwner(getScene().getWindow());
        newStage.setOnCloseRequest(Event::consume);
        newStage.show();
    }

    private void goToMenu() {
        Stage stage = (Stage) getScene().getWindow();
        stage.setScene(new Scene(new MenuPane(getScene()), Globals.WIDTH, Globals.HEIGHT));
    }

    public void addAIWidgets(int mode) {
        getChildren().add(AIWidgets);
    }

    public Button getStartAIButton() {
        return startAIButton;
    }

    public Button getStopAIButton() {
        return stopAIButton;
    }

    public Button getRestartButton() {
        return restartButton;
    }

    public Button getMenuButton() {
        return menuButton;
    }
}
