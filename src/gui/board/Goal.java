package gui.board;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

public class Goal extends StackPane {
    private boolean highlight;

    public Goal(int prefWidth) {
        setAlignment(Pos.CENTER);
        setPrefSize(prefWidth, 50);
        setMaxWidth(prefWidth);
        setStyle("-fx-background-color: rgb(200, 200, 200);");

        setOnMouseEntered(me -> {
            if (highlight) {
                setStyle("-fx-background-color: rgb(0, 0, 255);");
            }
        });

        setOnMouseExited(me -> {
            if (highlight) {
                setStyle("-fx-background-color: rgb(0, 0, 150);");
            }
        });

    }

    public boolean getHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
        if (highlight) {
            setStyle("-fx-background-color: rgb(0, 0, 150);");
        } else {
            setStyle("-fx-background-color: rgb(200, 200, 200);");
        }
    }
}