package gui.Dialogs;

import game.Controller;
import game.State;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import static misc.Globals.BLACK;
import static misc.Globals.RED;

public class RestartGameDialog extends ConfirmDialog {

    Controller cont;
    Stage primaryStage;
    public RestartGameDialog(String labelText, Stage primaryStage, Controller cont) {
        super(labelText);
        this.primaryStage = primaryStage;
        this.cont = cont;
    }

    @Override
    public void setYesBtnMouseClicked() {
        Stage stage = (Stage) getScene().getWindow();
        stage.close();
        new Controller(primaryStage, cont.getPlayerInstance(RED),
                cont.getPlayerInstance(BLACK), new State(cont.getScoreLimit()), cont.getTime(RED), cont.getTime(BLACK), cont.getOverwriteDB());
    }
}
