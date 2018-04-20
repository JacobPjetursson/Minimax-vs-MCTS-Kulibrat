package gui;

import game.Controller;
import gui.board.BoardPiece;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import misc.Globals;

import static misc.Globals.*;

public class PlayPane extends GridPane {
    private PlayArea playArea;
    private NavPane navPane;

    public PlayPane(Controller cont) {
        setup();

        playArea = new PlayArea(cont);
        navPane = new NavPane(cont);
        add(playArea, 1, 0);
        add(navPane, 0, 0);

    }

    private void setup() {
        setAlignment(Pos.CENTER);
        setPrefSize(Globals.WIDTH, Globals.HEIGHT);
        ColumnConstraints column = new ColumnConstraints(Globals.WIDTH / 3);
        ColumnConstraints column1 = new ColumnConstraints(Globals.WIDTH * 2 / 3);
        getColumnConstraints().add(column);
        getColumnConstraints().add(column1);
    }

    public PlayArea getPlayArea() {
        return playArea;
    }

    public NavPane getNavPane() {
        return navPane;
    }
}
