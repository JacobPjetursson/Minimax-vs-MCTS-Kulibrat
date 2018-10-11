package FFT;

import game.Controller;
import gui.Dialogs.ConfirmRuleGroupDeleteDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import misc.Database;
import misc.Globals;

public class EditFFTScene extends VBox {
    private int textFieldWidth = 150;
    private ListView<BorderPane> lw;
    private FFTManager fftManager;

    public EditFFTScene(Stage primaryStage, Scene prevScene, FFTManager fftManager, Controller cont) {
        setSpacing(15);
        setAlignment(Pos.CENTER);
        this.fftManager = fftManager;
        Label title = new Label("Edit FFT with name:\n" + fftManager.currFFT.name);
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        title.setAlignment(Pos.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setMinHeight(65);
        // Make box here with rename, change, delete (not now)

        // Existing rule groups
        lw = new ListView<>();
        lw.setPickOnBounds(false);
        showRuleGroups();

        // New rule group
        Label newRuleGroupLabel = new Label("New rule group: ");
        newRuleGroupLabel.setFont(Font.font("Verdana", 15));
        TextField newRuleGroupField = new TextField();
        newRuleGroupField.setMinWidth(textFieldWidth);
        newRuleGroupField.setMaxWidth(textFieldWidth);

        Text text = new Text("Add");
        text.setFill(Color.WHITE);
        Button addNewRuleGroupBtn = new Button("Add");
        addNewRuleGroupBtn.setOnMouseClicked(event -> {
            RuleGroup rg = new RuleGroup(newRuleGroupField.getText());
            fftManager.currFFT.addRuleGroup(rg);
            newRuleGroupField.clear();
            showRuleGroups();
        });

        HBox ruleGroupBox = new HBox(newRuleGroupLabel, newRuleGroupField, addNewRuleGroupBtn);
        ruleGroupBox.setAlignment(Pos.CENTER);

        Label teamLabel = new Label(" as team: ");
        teamLabel.setFont(Font.font("Verdana", 15));
        ChoiceBox<String> teamChoice = new ChoiceBox<>();
        teamChoice.setMinWidth(textFieldWidth);
        teamChoice.setMaxWidth(textFieldWidth);
        teamChoice.setValue("Red");
        teamChoice.setItems(FXCollections.observableArrayList("Red", "Black"));

        Label forLabel = new Label(" for: ");
        forLabel.setFont(Font.font("Verdana", 15));
        ChoiceBox<String> verificationChoice = new ChoiceBox<>();
        verificationChoice.setMinWidth(textFieldWidth);
        verificationChoice.setMaxWidth(textFieldWidth);
        verificationChoice.setValue("Whole FFT");
        verificationChoice.setItems(FXCollections.observableArrayList("Whole FFT", "Existing Rules"));

        Label verifiedLabel = new Label("The FFT was successfully verified");
        verifiedLabel.setFont(Font.font("Verdana", 15));

        Button verifyButton = new Button("Verify FFT");
        verifyButton.setTooltip(new Tooltip("Checks if the current FFT is a winning strategy,\n" +
                "or if given rules are part of winning strategy"));
        verifyButton.setOnMouseClicked(event -> {
            if (!Database.connectAndVerify())
                return;
            int team = teamChoice.getSelectionModel().getSelectedIndex() + 1;
            boolean wholeFFT = verificationChoice.getSelectionModel().getSelectedIndex() == 0;
            boolean verified = fftManager.currFFT.verify(team, wholeFFT);
            System.out.println("VERIFIED: " + verified);
            if (!verified && fftManager.currFFT.failingPoint != null) {
                Scene scene = primaryStage.getScene();
                primaryStage.setScene(new Scene(new FFTFailurePane(scene, fftManager, cont), Globals.WIDTH, Globals.HEIGHT));
            } else if (verified && !getChildren().contains(verifiedLabel)) {
                getChildren().add(4, verifiedLabel);

            }
        });
        HBox verifyBox = new HBox();
        verifyBox.setAlignment(Pos.CENTER);
        verifyBox.setSpacing(10);
        verifyBox.getChildren().addAll(verifyButton, teamChoice, forLabel, verificationChoice);



        Button back = new Button("Back");
        back.setOnMouseClicked(event -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.setScene(prevScene);
        });
        HBox bottomBox = new HBox(10);
        VBox.setMargin(bottomBox, new Insets(10));
        bottomBox.setAlignment(Pos.BOTTOM_RIGHT);
        bottomBox.getChildren().add(back);

        setVgrow(lw, Priority.ALWAYS);
        getChildren().addAll(title, lw, ruleGroupBox, verifyBox, bottomBox);
    }

    void showRuleGroups() {
        ObservableList<BorderPane> ruleGroups = FXCollections.observableArrayList();
        for (int i = 0; i < fftManager.currFFT.ruleGroups.size(); i++) {
            // Rule group
            final int index = i; // FUCKING JAVA CANCER
            RuleGroup rg = fftManager.currFFT.ruleGroups.get(i);
            VBox rgVBox = new VBox(10);
            rgVBox.setAlignment(Pos.CENTER);
            Label rgLabel = new Label((i + 1) + ": " + rg.name);
            rgLabel.setFont(Font.font("Verdana", 16));
            rgVBox.getChildren().add(rgLabel);
            for (int j = 0; j < rg.rules.size(); j++) {
                Rule r = rg.rules.get(j);
                Label rLabel = new Label((j + 1) + ": " + r.printRule());
                rLabel.setFont(Font.font("Verdana", 10));
                rgVBox.getChildren().add(rLabel);
            }
            // up/down list buttons
            int buttonSize = 150;
            VBox upDownButtons = new VBox();
            upDownButtons.setAlignment(Pos.CENTER);
            Button upButton = new Button("▲");
            Button downButton = new Button("▼");
            upButton.setMinWidth(50);
            downButton.setMinWidth(50);
            upButton.setOnMouseClicked(event -> {
                if (index == 0)
                    return;
                fftManager.currFFT.ruleGroups.remove(index);
                fftManager.currFFT.ruleGroups.add(index - 1, rg);
                FFTManager.save();
                showRuleGroups();
                lw.getSelectionModel().select(index - 1);
            });
            downButton.setOnMouseClicked(event -> {
                if (index == fftManager.currFFT.ruleGroups.size() - 1)
                    return;
                fftManager.currFFT.ruleGroups.remove(index);
                fftManager.currFFT.ruleGroups.add(index + 1, rg);
                FFTManager.save();
                showRuleGroups();
                lw.getSelectionModel().select(index + 1);
                showRuleGroups();
            });

            // Edit / Remove buttons
            VBox editRemoveButtons = new VBox(10);
            editRemoveButtons.setAlignment(Pos.CENTER);

            Button editButton = new Button("Edit");
            editButton.setStyle("-fx-border-color: #000000; -fx-background-color: blue;");
            editButton.setMinWidth(buttonSize);
            editButton.setOnMouseClicked(event -> {
                Stage newStage = new Stage();
                newStage.setScene(new Scene(
                        new EditRuleGroupPane(rg, this), 700, 500));
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.initOwner(getScene().getWindow());
                newStage.setOnCloseRequest(Event::consume);
                newStage.show();
            });

            Button removeButton = new Button("Remove");
            removeButton.setStyle("-fx-border-color: #000000; -fx-background-color: #ff0000;");
            removeButton.setMinWidth(buttonSize);
            removeButton.setOnMouseClicked(event -> {
                // Confirmation (Pretty big delete after all)
                String labelText = "Are you sure you want to delete the rule group:\n" + rg.name;
                Stage newStage = new Stage();
                newStage.setScene(new Scene(
                        new ConfirmRuleGroupDeleteDialog(labelText, this, index), 500, 150));
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.initOwner(getScene().getWindow());
                newStage.setOnCloseRequest(Event::consume);
                newStage.show();

            });
            editRemoveButtons.getChildren().addAll(editButton, removeButton);
            upDownButtons.getChildren().addAll(upButton, downButton);
            HBox allButtons = new HBox(editRemoveButtons, upDownButtons);
            allButtons.setAlignment(Pos.CENTER);
            allButtons.setSpacing(5);

            BorderPane finalPane = new BorderPane();
            finalPane.setCenter(rgVBox);
            finalPane.setRight(allButtons);
            ruleGroups.add(finalPane);
        }
        lw.setItems(ruleGroups);
        lw.getSelectionModel().selectLast();
    }

    public void removeRuleGroup(int index) {
        lw.getItems().remove(index);
        fftManager.currFFT.ruleGroups.remove(index);
        showRuleGroups();
        FFTManager.save();
    }
}
