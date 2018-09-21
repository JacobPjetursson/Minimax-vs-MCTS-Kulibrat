package FFT;

import game.Controller;
import gui.Dialogs.ConfirmRuleGroupDeleteDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditFFTScene extends VBox {
    private int textFieldWidth = 150;
    private ListView<BorderPane> lw;
    private FFT fft;
    private Button addRuleBtn;
    private Scene prevScene;

    public EditFFTScene(Scene prevScene, Controller cont, FFT fft) {
        this.prevScene = prevScene;
        setSpacing(15);
        setAlignment(Pos.CENTER);
        this.fft = fft;
        Label title = new Label("Edit Fast and Frugal Tree");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        title.setAlignment(Pos.CENTER);

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
            fft.addRuleGroup(rg);
            newRuleGroupField.clear();
            showRuleGroups();
        });

        HBox ruleGroupBox = new HBox(newRuleGroupLabel, newRuleGroupField, addNewRuleGroupBtn);
        ruleGroupBox.setAlignment(Pos.CENTER);

        // New rule
        Label newRuleLabel = new Label("New Rule");
        newRuleLabel.setFont(Font.font("Verdana", 15));
        HBox newRuleBox = new HBox();
        newRuleBox.setAlignment(Pos.CENTER);
        Label label = new Label("IF ");
        newRuleBox.getChildren().add(label);
        TextField ruleField = new TextField();
        ruleField.setMinWidth(textFieldWidth);
        ruleField.setMaxWidth(textFieldWidth);
        newRuleBox.getChildren().add(ruleField);
        label = new Label("THEN ");
        newRuleBox.getChildren().add(label);
        TextField actionField = new TextField();
        actionField.setMinWidth(textFieldWidth);
        actionField.setMaxWidth(textFieldWidth);
        newRuleBox.getChildren().add(actionField);


        addRuleBtn = new Button("Add");
        addRuleBtn.setDisable(true);
        addRuleBtn.setOnMouseClicked(event -> {
            int selIndex = lw.getSelectionModel().getSelectedIndex();
            RuleGroup rg = fft.ruleGroups.get(selIndex);
            Rule r = new Rule(ruleField.getText(), actionField.getText());
            rg.rules.add(r);
            ruleField.clear();
            actionField.clear();
            addRuleBtn.setDisable(true);
            fft.save();
            showRuleGroups();
        });

        VBox ruleBox = new VBox(newRuleLabel, newRuleBox, addRuleBtn);
        ruleBox.setAlignment(Pos.CENTER);
        ruleBox.setSpacing(10);

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
        getChildren().addAll(title, lw, ruleGroupBox, ruleBox, bottomBox);
    }

    void showRuleGroups() {
        ObservableList<BorderPane> ruleGroups = FXCollections.observableArrayList();
        for (int i = 0; i < fft.ruleGroups.size(); i++) {
            // Rule group
            final int index = i; // FUCKING JAVA CANCER
            RuleGroup rg = fft.ruleGroups.get(i);
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
                fft.ruleGroups.remove(index);
                fft.ruleGroups.add(index - 1, rg);
                fft.save();
                showRuleGroups();
                lw.getSelectionModel().select(index - 1);
            });
            downButton.setOnMouseClicked(event -> {
                if (index == fft.ruleGroups.size() - 1)
                    return;
                fft.ruleGroups.remove(index);
                fft.ruleGroups.add(index + 1, rg);
                fft.save();
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
                        new EditRuleGroupPane(fft, rg, this), 700, 500));
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

            BorderPane finalPane = new BorderPane();
            finalPane.setCenter(rgVBox);
            finalPane.setRight(allButtons);
            ruleGroups.add(finalPane);
        }
        lw.setItems(ruleGroups);
        lw.getSelectionModel().selectLast();
        lw.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                addRuleBtn.setDisable(false));
    }

    public void removeRuleGroup(int index) {
        lw.getItems().remove(index);
        fft.ruleGroups.remove(index);
        showRuleGroups();
        fft.save();
    }
}
