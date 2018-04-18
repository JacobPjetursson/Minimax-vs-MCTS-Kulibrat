package gui;

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

    private BoardPiece selected;
    private ArrayList<Move> curHighLights;

    private State state;


    PlayArea(int playerRedType, int playerBlackType, int pointsToWin, int mode) {
        setPadding(new Insets(10, 10, 10, 10));
        setAlignment(Pos.CENTER);

        board = new Board();
        playerBlack = new Player(BLACK, (playerBlackType), this);
        playerRed = new Player(RED, (playerRedType), this);
        goalRed = new Goal(3 * board.getTileSize());
        goalBlack = new Goal(3 * board.getTileSize());
        info = new InfoPane(pointsToWin, mode);

        curHighLights = new ArrayList<>();

        setFocusTraversable(true);
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) { //Escape to select
                if (selected != null) {
                    highlightMoves(selected.getRow(), selected.getCol(), selected.getTeam(), false);
                    deselect();
                }
            }
        });

        ColumnConstraints column = new ColumnConstraints(WIDTH / 3);
        for (int i = 0; i < 2; i++)
            getColumnConstraints().add(column);

        VBox playBox = new VBox(playerBlack, goalRed, board, goalBlack, playerRed);
        playBox.setAlignment(Pos.CENTER);

        add(playBox, 0, 0);
        add(info, 1, 0);

    }

    public void update(State state, int turnNo) {
        this.state = state;
        board.update(state, this);
        playerRed.update(state);
        playerBlack.update(state);
        info.update(state, turnNo);
    }

    public void deselect() {
        if (selected != null) selected.deselect();
        selected = null;
    }

    public BoardPiece getSelected() {
        return selected;
    }

    public void setSelected(BoardPiece piece) {
        if (this.selected != null) {
            highlightMoves(selected.getRow(), selected.getCol(), selected.getTeam(), false);
            selected.deselect();
        }
        selected = piece;
        highlightMoves(piece.getRow(), piece.getCol(), piece.getTeam(), true);
    }

    public void highlightMoves(int row, int col, int team, boolean highlight) {
        if (highlight) curHighLights = Logic.legalMovesFromPiece(row,
                col, team, state);

        BoardTile[][] tiles = board.getTiles();

        for (Move m : curHighLights) {
            if (m.newCol == -1 && m.newRow == -1) {
                if (team == RED) {
                    goalRed.setHighlight(highlight);
                } else {
                    goalBlack.setHighlight(highlight);
                }
            } else tiles[m.newRow][m.newCol].setHighlight(highlight);
        }
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayer(int team) {
        if (team == RED) return playerRed;
        else return playerBlack;
    }

    public Goal getGoal(int team) {
        if (team == RED) return goalRed;
        else return goalBlack;
    }

    public State getState() {
        return state;
    }
}
