package gui;

import game.Controller;
import game.Logic;
import game.Move;
import game.State;
import gui.board.*;
import gui.info.InfoPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

import static misc.Globals.*;

public class PlayArea extends GridPane {

    private Player playerBlack;
    private Player playerRed;
    private Board board;
    private Goal goalRed;
    private Goal goalBlack;
    private InfoPane info;

    PlayArea(Controller cont) {
        setPadding(new Insets(10, 10, 10, 10));
        setAlignment(Pos.CENTER);


        board = new Board(60, 20, true);
        playerBlack = new Player(BLACK, cont, 60, 20, true);
        playerRed = new Player(RED, cont, 60, 20, true);
        goalRed = new Goal(3 * board.getTileSize(), 50);
        goalBlack = new Goal(3 * board.getTileSize(), 50);
        info = new InfoPane(cont.getScoreLimit(), cont.getMode());

        ColumnConstraints column = new ColumnConstraints(WIDTH / 3);
        for (int i = 0; i < 2; i++)
            getColumnConstraints().add(column);

        VBox playBox = new PlayBox(playerBlack, goalRed, board, goalBlack, playerRed);
        add(playBox, 0, 0);
        add(info, 1, 0);

    }

    public void update(Controller cont) {
        board.update(cont, cont.getState());
        playerRed.update(cont, cont.getState());
        playerBlack.update(cont, cont.getState());
        info.update(cont);
    }

    public Board getBoard() {
        return board;
    }

    public Goal getGoal(int team) {
        if (team == RED) return goalRed;
        else return goalBlack;
    }
    public Player getPlayer(int team) {
        if(team == RED) {
            return playerRed;
        } else {
            return playerBlack;
        }
    }

}
