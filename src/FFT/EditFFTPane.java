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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class EditFFTPane extends VBox {
    private int textFieldWidth = 150;
    private ListView<VBox> lw;
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
        addRuleBtn.setOnMouseClicked(event -> {
            int selIndex = lw.getSelectionModel().getSelectedIndex();
            RuleGroup rg = fft.ruleGroups.get(selIndex);
            Rule r = new Rule(ruleField.getText(), actionField.getText());
            rg.rules.add(r);
            ruleField.clear();
            actionField.clear();
            addRuleBtn.setDisable(true);
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
        ObservableList<VBox> ruleGroups = FXCollections.observableArrayList();
        for (int i = 0; i < fft.ruleGroups.size(); i++) {
            RuleGroup rg = fft.ruleGroups.get(i);
            VBox v = new VBox(10);
            v.setAlignment(Pos.CENTER);
            Label rgLabel = new Label((i + 1) + ": " + rg.name);
            rgLabel.setFont(Font.font("Verdana", 14));
            rgLabel.setAlignment(Pos.TOP_CENTER);
            v.getChildren().add(rgLabel);
            for (int j = 0; j < rg.rules.size(); j++) {
                Rule r = rg.rules.get(j);
                Label rLabel = new Label((j + 1) + ": " + r.printRule());
                rLabel.setFont(Font.font("Verdana", 10));
                v.getChildren().add(rLabel);
            }
            ruleGroups.add(v);
        }
        lw.setItems(ruleGroups);
        lw.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                addRuleBtn.setDisable(false));
    }
}
