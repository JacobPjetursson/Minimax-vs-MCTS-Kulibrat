package FFT;

import game.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class EditFFTPane extends VBox {
    private int textFieldWidth = 150;
    private ListView<HBox> lw;
    private FFT fft;
    private Button addRuleBtn;

    public EditFFTPane(Controller cont, FFT fft) {
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
        Label newRuleGroupLabel = new Label("New rule group name: ");
        newRuleGroupLabel.setFont(Font.font("Verdana", 15));
        TextField newRuleGroupField = new TextField();
        newRuleGroupField.setMinWidth(textFieldWidth);
        newRuleGroupField.setMaxWidth(textFieldWidth);

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
        addRuleBtn.setStyle("-fx-background-color: green; ");
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

        Button accept = new Button("Save");
        Button cancel = new Button("Cancel");
        cancel.setOnMouseClicked(event -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();
        });
        HBox bottomBox = new HBox(10);
        VBox.setMargin(bottomBox, new Insets(10));
        bottomBox.setAlignment(Pos.BOTTOM_RIGHT);
        bottomBox.getChildren().addAll(accept, cancel);

        setVgrow(lw, Priority.ALWAYS);
        getChildren().addAll(title, lw, ruleGroupBox, ruleBox, bottomBox);
    }

    private void showRuleGroups() {
        ObservableList<HBox> ruleGroups = FXCollections.observableArrayList();
        for (int i = 0; i < fft.ruleGroups.size(); i++) {
            // Rule group
            RuleGroup rg = fft.ruleGroups.get(i);
            VBox rgVBox = new VBox(10);
            rgVBox.setAlignment(Pos.CENTER);
            Label rgLabel = new Label((i + 1) + ": " + rg.name);
            rgLabel.setFont(Font.font("Verdana", 14));
            rgLabel.setAlignment(Pos.TOP_CENTER);
            rgVBox.getChildren().add(rgLabel);
            for (int j = 0; j < rg.rules.size(); j++) {
                Rule r = rg.rules.get(j);
                Label rLabel = new Label((j + 1) + ": " + r.printRule());
                rLabel.setFont(Font.font("Verdana", 10));
                rgVBox.getChildren().add(rLabel);
            }
            // Edit / Remove buttons
            VBox rgButtons = new VBox(10);
            rgButtons.setAlignment(Pos.CENTER);

            Button editButton = new Button("Edit");
            editButton.setStyle("-fx-background-color: blue; ");
            editButton.setOnMouseClicked(event -> {
                // Open dialog with edit stuff
            });

            Button removeButton = new Button("Remove");
            removeButton.setStyle("-fx-background-color: #ff0000; ");
            removeButton.setOnMouseClicked(event -> {
                // Confirmation (Pretty big delete after all)

            });
            rgButtons.getChildren().addAll(editButton, removeButton);
            //AnchorPane.setR
            HBox finalBox = new HBox(rgVBox, rgButtons);
            finalBox.setAlignment(Pos.CENTER);
            ruleGroups.add(finalBox);
        }
        lw.setItems(ruleGroups);
        lw.getSelectionModel().selectLast();
        lw.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                addRuleBtn.setDisable(false));
    }
}
