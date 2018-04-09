package gui;

import game.Controller;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import misc.Globals;

import static misc.Globals.*;

public class PlayPane extends GridPane {
    private PlayArea playArea;
    private NavPane navPane;

    private int mode;

    public PlayPane(int playerRedInstance, int playerBlackInstance, int pointsToWin, int redTime, int blackTime) {
        setup();
        mode = setMode(playerRedInstance, playerBlackInstance);

        playArea = new PlayArea(playerRedInstance, playerBlackInstance, pointsToWin, mode);
        navPane = new NavPane(playerRedInstance, playerBlackInstance, pointsToWin, redTime, blackTime);
        add(playArea, 1, 0);
        add(navPane, 0, 0);

        new Controller(this, playerRedInstance, playerBlackInstance, pointsToWin, mode, redTime, blackTime);
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

    private int setMode(int playerRedInstance, int playerBlackInstance) {
        if (playerRedInstance == HUMAN && playerBlackInstance == HUMAN) {
            return HUMAN_VS_HUMAN;
        } else if (playerRedInstance != HUMAN ^ playerBlackInstance != HUMAN) {
            return HUMAN_VS_AI;
        } else {
            return AI_VS_AI;
        }
    }
}
