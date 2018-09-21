package gui.Dialogs;

import game.Controller;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class OverwriteDBDialog extends ConfirmDialog {

    Controller cont;
    public OverwriteDBDialog(String labelText, Controller cont) {
        super(labelText);
        this.cont = cont;
    }

    @Override
    public void setYesBtnMouseClicked() {
        Stage stage = (Stage) getScene().getWindow();
        stage.close();
        cont.buildDB();
    }
}
