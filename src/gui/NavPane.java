package gui;

import game.Controller;
import gui.menu.MenuPane;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
    private CheckBox helpHuman;
    private HBox helpHumanBox;
    private VBox AIWidgets;

    public NavPane(Controller cont) {
        setMinWidth(Globals.WIDTH / 3);
        setAlignment(Pos.CENTER);
        setSpacing(40);
        buttons = new ArrayList<Button>();

        restartButton = new Button("Restart Game");
        buttons.add(restartButton);
        restartButton.setOnMouseClicked(event -> restartGame(cont));

        startAIButton = new Button("Start AI vs. AI");
        buttons.add(startAIButton);

        stopAIButton = new Button("Stop AI vs. AI");
        buttons.add(stopAIButton);

        AIWidgets = new VBox(startAIButton, stopAIButton);
        AIWidgets.setSpacing(10);
        AIWidgets.setAlignment(Pos.CENTER);

        helpHuman = new CheckBox();
        Label helpHumanLabel = new Label("Show perfect move");
        helpHumanLabel.setFont(Font.font("Verdana", 14));
        helpHumanLabel.setPadding(new Insets(0, 0, 0, 5));
        helpHumanBox = new HBox(helpHuman, helpHumanLabel);
        helpHumanBox.setAlignment(Pos.CENTER);


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

    private void restartGame(Controller cont) {
        Stage prevStage = (Stage) getScene().getWindow();

        Stage newStage = new Stage();
        newStage.setScene(new Scene(new RestartGamePane(prevStage, cont), 400, 150));
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.initOwner(getScene().getWindow());
        newStage.setOnCloseRequest(Event::consume);
        newStage.show();
    }

    private void goToMenu() {
        Stage stage = (Stage) getScene().getWindow();
        stage.setScene(new Scene(new MenuPane(getScene()), Globals.WIDTH, Globals.HEIGHT));
    }

    public void addAIWidgets() {
        getChildren().add(AIWidgets);
    }

    public void addHelpHumanBox() {
        getChildren().add(helpHumanBox);
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

    public CheckBox getHelpHumanBox() {
        return helpHuman;
    }
}
