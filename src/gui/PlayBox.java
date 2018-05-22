package gui;

import game.Controller;
import game.State;
import gui.board.Board;
import gui.board.Goal;
import gui.board.Player;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class PlayBox extends VBox {
    Board b;
    Goal goalRed;
    Goal goalBlack;
    Player playerRed;
    Player playerBlack;

    PlayBox(Player playerBlack, Goal goalRed, Board board, Goal goalBlack, Player playerRed) {
        this.b = board;
        this.goalBlack = goalBlack;
        this.goalRed = goalRed;
        this.playerBlack = playerBlack;
        this.playerRed = playerRed;
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: rgb(255, 255, 255);");
        getChildren().addAll(playerBlack, goalRed, board, goalBlack, playerRed);
    }

    public void update(Controller cont, State s) {
        b.update(cont, s);
        playerRed.update(cont, s);
        playerBlack.update(cont, s);
    }
}
