package gui.menu;

import game.Controller;
import gui.PlayPane;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import misc.Globals;

public class NewGamePane extends AnchorPane {
    private int choiceWidth = Globals.WIDTH / 4;
    private int textFieldWidth = choiceWidth - 125;
    private String human = "Human";
    private String mcts = "Monte Carlo Tree Search";
    private String minimax = "Minimax";
    private String lookup = "Lookup Table";

    private TextField blackDelayField;
    private TextField redDelayField;
    private HBox redDelayBox;
    private HBox blackDelayBox;
    private ChoiceBox<String> playerRedChoices;
    private ChoiceBox<String> playerBlackChoices;
    private CheckBox overwriteDB;
    private TextField lookupDelayField;
    private HBox lookupDelayBox;

    private VBox finalBox;

    public NewGamePane() {
        setPrefSize(Globals.WIDTH, Globals.HEIGHT);
        setPadding(new Insets(30, 0, 0, 0));
        setStyle("-fx-background-color: black;");

        Label title = new Label("Game Options");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        title.setTextFill(Color.WHITE);
        title.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(title, 40.0);
        AnchorPane.setRightAnchor(title, 0.0);
        AnchorPane.setLeftAnchor(title, 0.0);

        playerRedChoices = new ChoiceBox<String>();
        playerRedChoices.setValue(human);
        playerRedChoices.setItems(FXCollections.observableArrayList(human, mcts, minimax, lookup));
        playerRedChoices.setMinWidth(choiceWidth);
        playerRedChoices.setMaxWidth(choiceWidth);
        playerRedChoices.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            if (playerRedChoices.getItems().get((Integer) newValue).equals(minimax) ||
                    playerRedChoices.getItems().get((Integer) newValue).equals(mcts)) {
                finalBox.getChildren().remove(lookupDelayBox);
                if (!finalBox.getChildren().contains(redDelayBox)) {
                    finalBox.getChildren().add(1, redDelayBox);
                }
                if (!playerBlackChoices.getValue().equals(lookup)) {
                    finalBox.getChildren().remove(overwriteDB);

                }
            } else if(playerRedChoices.getItems().get((Integer) newValue).equals(human)) {
                finalBox.getChildren().remove(redDelayBox);
                finalBox.getChildren().remove(lookupDelayBox);
                if (!playerBlackChoices.getValue().equals(lookup)) {
                    finalBox.getChildren().remove(overwriteDB);
                }
            } else if(playerRedChoices.getItems().get((Integer) newValue).equals(lookup)) {
                finalBox.getChildren().remove(redDelayBox);
                if (!finalBox.getChildren().contains(overwriteDB)) {
                    int index = finalBox.getChildren().contains(blackDelayBox) ? 4 : 3;
                    finalBox.getChildren().add(index, overwriteDB);
                }
                if(playerBlackChoices.getValue().equals(lookup)) {
                    finalBox.getChildren().add(4, lookupDelayBox);
                }
            }
        });

        Label playerRedLabel = new Label("Player Red: ");
        playerRedLabel.setFont(Font.font("Verdana", 15));
        playerRedLabel.setPadding(new Insets(0, 10, 0, 0));
        playerRedLabel.setTextFill(Color.WHITE);
        HBox playerRed = new HBox(playerRedLabel, playerRedChoices);
        playerRed.setAlignment(Pos.CENTER);

        playerBlackChoices = new ChoiceBox<String>();
        playerBlackChoices.setValue(human);
        playerBlackChoices.setItems(FXCollections.observableArrayList(human, mcts, minimax, lookup));
        playerBlackChoices.setMinWidth(choiceWidth);
        playerBlackChoices.setMaxWidth(choiceWidth);
        playerBlackChoices.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            if (playerBlackChoices.getItems().get((Integer) newValue).equals(minimax) ||
                    playerBlackChoices.getItems().get((Integer) newValue).equals(mcts)) {
                finalBox.getChildren().remove(lookupDelayBox);
                if (!finalBox.getChildren().contains(blackDelayBox)) {
                    int index = finalBox.getChildren().contains(redDelayBox) ? 3 : 2;
                    finalBox.getChildren().add(index, blackDelayBox);
                }
                if (!playerRedChoices.getValue().equals(lookup)) {
                    finalBox.getChildren().remove(overwriteDB);
                }
            } else if(playerBlackChoices.getItems().get((Integer) newValue).equals(human)) {
                finalBox.getChildren().remove(blackDelayBox);
                finalBox.getChildren().remove(lookupDelayBox);
                if (!playerRedChoices.getValue().equals(lookup)) {
                    finalBox.getChildren().remove(overwriteDB);
                }
            } else if(playerBlackChoices.getItems().get((Integer) newValue).equals(lookup)) {
                finalBox.getChildren().remove(blackDelayBox);
                if (!finalBox.getChildren().contains(overwriteDB)) {
                    int index = finalBox.getChildren().contains(redDelayBox) ? 4 : 3;
                    finalBox.getChildren().add(index, overwriteDB);
                }
                if(playerRedChoices.getValue().equals(lookup)) {
                    finalBox.getChildren().add(4, lookupDelayBox);
                }
            }

        });

        Label playerBlackLabel = new Label("Player Black: ");
        playerBlackLabel.setFont(Font.font("Verdana", 15));
        playerBlackLabel.setPadding(new Insets(0, 10, 0, 0));
        playerBlackLabel.setTextFill(Color.WHITE);
        HBox playerBlack = new HBox(playerBlackLabel, playerBlackChoices);
        playerBlack.setAlignment(Pos.CENTER);

        ChoiceBox<Integer> pointsToWinChoices = new ChoiceBox<>();
        pointsToWinChoices.setValue(5);
        pointsToWinChoices.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        pointsToWinChoices.setMinWidth(choiceWidth);
        pointsToWinChoices.setMaxWidth(choiceWidth);

        Label pointsToWinLabel = new Label("Points to win: ");
        pointsToWinLabel.setFont(Font.font("Verdana", 15));
        pointsToWinLabel.setPadding(new Insets(0, 10, 0, 0));
        pointsToWinLabel.setTextFill(Color.WHITE);
        HBox pointsToWinBox = new HBox(pointsToWinLabel, pointsToWinChoices);
        pointsToWinBox.setAlignment(Pos.CENTER);


        redDelayField = new TextField("1000");
        redDelayField.setMinWidth(textFieldWidth);
        redDelayField.setMaxWidth(textFieldWidth);
        redDelayField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                redDelayField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if(newValue.isEmpty()) {
                redDelayField.setText(newValue.replaceAll("", "0"));
            }
        });
        redDelayField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER) {
                this.requestFocus();
            }
        });
        Label AIDelayLabelRed = new Label("AI Calculation time in ms");
        AIDelayLabelRed.setFont(Font.font("Verdana", 15));
        AIDelayLabelRed.setPadding(new Insets(0, 10, 0, 0));
        AIDelayLabelRed.setTextFill(Color.WHITE);
        AIDelayLabelRed.setAlignment(Pos.CENTER);
        redDelayBox = new HBox(AIDelayLabelRed, redDelayField);
        redDelayBox.setAlignment(Pos.CENTER);

        blackDelayField = new TextField("1000");
        blackDelayField.setMinWidth(textFieldWidth);
        blackDelayField.setMaxWidth(textFieldWidth);
        blackDelayField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                blackDelayField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if(newValue.isEmpty()) {
                blackDelayField.setText(newValue.replaceAll("", "0"));
            }
        });
        blackDelayField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER) {
                this.requestFocus();
            }
        });
        Label AIDelayLabelBlack = new Label("AI Calculation time in ms");
        AIDelayLabelBlack.setFont(Font.font("Verdana", 15));
        AIDelayLabelBlack.setPadding(new Insets(0, 10, 0, 0));
        AIDelayLabelBlack.setTextFill(Color.WHITE);
        AIDelayLabelBlack.setAlignment(Pos.CENTER);
        blackDelayBox = new HBox(AIDelayLabelBlack, blackDelayField);
        blackDelayBox.setAlignment(Pos.CENTER);

        overwriteDB = new CheckBox("Overwrite Database\n (Takes a lot of time)");
        overwriteDB.setFont(Font.font("Verdana", 15));
        overwriteDB.setTextFill(Color.WHITE);

        Label lookupDelayLabel = new Label("Forced delay for lookup in ms");
        lookupDelayLabel.setFont(Font.font("Verdana", 15));
        lookupDelayLabel.setPadding(new Insets(0, 10, 0, 0));
        lookupDelayLabel.setTextFill(Color.WHITE);
        lookupDelayLabel.setAlignment(Pos.CENTER);
        lookupDelayField = new TextField("0");
        lookupDelayField.setMinWidth(textFieldWidth);
        lookupDelayField.setMaxWidth(textFieldWidth);
        lookupDelayField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                lookupDelayField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if(newValue.isEmpty()) {
                lookupDelayField.setText(newValue.replaceAll("", "0"));
            }
        });
        lookupDelayField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER) {
                this.requestFocus();
            }
        });
        lookupDelayBox = new HBox(lookupDelayLabel, lookupDelayField);
        lookupDelayBox.setAlignment(Pos.CENTER);


        Button startGame = new Button("Start Game");
        startGame.setMinWidth(Globals.WIDTH / 4);
        startGame.setOnMouseClicked(event -> {
            String redValue = playerRedChoices.getValue();
            String blackValue = playerBlackChoices.getValue();
            Stage stage = (Stage) getScene().getWindow();

            int playerRedMode = (redValue.equals(human)) ? Globals.HUMAN :
                    (redValue.equals(minimax)) ? Globals.MINIMAX : (redValue.equals(mcts)) ? Globals.MONTE_CARLO : Globals.LOOKUP_TABLE;

            int playerBlackMode = (blackValue.equals(human)) ? Globals.HUMAN :
                    (blackValue.equals(minimax)) ? Globals.MINIMAX : (blackValue.equals(mcts)) ? Globals.MONTE_CARLO : Globals.LOOKUP_TABLE;

            if(playerRedMode == Globals.LOOKUP_TABLE && playerBlackMode == Globals.LOOKUP_TABLE) {
                redDelayField.setText(lookupDelayField.getText());
                blackDelayField.setText(lookupDelayField.getText());
            }

            new Controller(stage, playerRedMode,
                    playerBlackMode, pointsToWinChoices.getValue(),
                    Integer.parseInt(redDelayField.getText()),
                    Integer.parseInt(blackDelayField.getText()), overwriteDB.isSelected());
        });

        Button back = new Button("Back");
        back.setMinWidth(Globals.WIDTH / 6);
        back.setOnMouseClicked(event -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.setScene(new Scene(new MenuPane(),
                    Globals.WIDTH, Globals.HEIGHT));
        });

        HBox btnBox = new HBox(startGame, back);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setSpacing(20);

        finalBox = new VBox(playerRed, playerBlack, pointsToWinBox, btnBox);
        finalBox.setAlignment(Pos.CENTER);
        finalBox.setSpacing(30);


        AnchorPane.setTopAnchor(finalBox, 0.0);
        AnchorPane.setRightAnchor(finalBox, 0.0);
        AnchorPane.setLeftAnchor(finalBox, 0.0);
        AnchorPane.setBottomAnchor(finalBox, 0.0);

        getChildren().addAll(title, finalBox);
    }
}
