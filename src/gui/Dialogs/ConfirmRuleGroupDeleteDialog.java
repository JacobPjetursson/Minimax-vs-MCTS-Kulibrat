package gui.Dialogs;

import FFT.EditFFTScene;
import javafx.stage.Stage;

public class ConfirmRuleGroupDeleteDialog extends ConfirmDialog {
    EditFFTScene editFFTScene;
    int index;

    public ConfirmRuleGroupDeleteDialog(String labelText, EditFFTScene editFFTScene, int index) {
        super(labelText);
        this.editFFTScene = editFFTScene;
        this.index = index;
    }

    @Override
    public void setYesBtnMouseClicked() {
        Stage stage = (Stage) getScene().getWindow();
        stage.close();
        editFFTScene.removeRuleGroup(index);
    }


}
