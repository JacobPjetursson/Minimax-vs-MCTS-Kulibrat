package gui.board;

import game.Controller;
import game.State;
import gui.PlayArea;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

public class Board extends GridPane {
    private static final int boardRows = 4;
    private static final int boardColumns = 3;
    private static final int TILESIZE = 60;
    private BoardTile[][] tiles;

    public Board() {

        setAlignment(Pos.CENTER);
        tiles = new BoardTile[boardRows][boardColumns];
        for (int i = 0; i < boardRows; i++) {
            for (int j = 0; j < boardColumns; j++) {
                BoardTile bt = new BoardTile(i, j, TILESIZE);
                add(bt, j, i);
                tiles[i][j] = bt;
            }
        }
    }

    public BoardTile[][] getTiles() {
        return tiles;
    }

    public int getTileSize() {
        return TILESIZE;
    }

    public void update(Controller cont) {
        State state = cont.getState();
        int[][] stateBoard = state.getBoard();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                BoardTile tile = tiles[i][j];
                int stateTile = stateBoard[i][j];
                // moved to tile
                if (tile.getChildren().isEmpty() && stateTile != 0) {
                    tile.getChildren().add(new BoardPiece(stateTile, cont, i, j));
                }
                // moved from tile
                else if (!tile.getChildren().isEmpty() && stateTile == 0) {
                    tile.getChildren().remove(0);
                }
                // moved to tile already occupied
                else if (!tile.getChildren().isEmpty()) {
                    BoardPiece piece = (BoardPiece) tile.getChildren().get(0);
                    if (piece.getTeam() != stateTile) {
                        tile.getChildren().remove(piece);
                        tile.getChildren().add(new BoardPiece(stateTile, cont, i, j));
                    }
                }
            }
        }
    }
}
