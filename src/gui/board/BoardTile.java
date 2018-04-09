package gui.board;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class BoardTile extends StackPane {
    private int row;
    private int col;
    private boolean highlight;

    BoardTile(int row, int col, int tilesize) {
        this.row = row;
        this.col = col;
        setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        setAlignment(Pos.CENTER);

        setPrefSize(tilesize, tilesize);

        setOnMouseEntered(me -> {
            if (highlight) {
                setStyle("-fx-background-color: rgb(0, 225, 0);");
            }
        });

        setOnMouseExited(me -> {
            if (highlight) {
                setStyle("-fx-background-color: rgb(0, 150, 0);");
            }
        });
    }

    public boolean getHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;

        if (highlight) {
            setStyle("-fx-background-color: rgb(0, 150, 0);");
        } else {
            setStyle("-fx-background-color: rgb(255, 255, 255);");
        }
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}

