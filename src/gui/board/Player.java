package gui.board;

import game.Controller;
import game.State;
import gui.PlayArea;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;

import static misc.Globals.*;

public class Player extends VBox {
    private int team;
    private GridPane gridPaneBoard;
    private ArrayList<BoardPiece> pieces;
    private boolean clickable;
    private int pieceRadius;


    public Player(int team, Controller cont, int width, int pieceRadius, boolean clickable) {
        this.team = team;
        this.clickable = clickable;
        this.pieceRadius = pieceRadius;
        pieces = new ArrayList<>();
        int type = cont.getPlayerInstance(team);
        setAlignment(Pos.CENTER);
        setSpacing(width/6);
        setStyle("-fx-background-color: rgb(255, 255, 255);");

        gridPaneBoard = new GridPane();
        gridPaneBoard.setAlignment(Pos.CENTER);
        gridPaneBoard.setPrefSize(width * 4, width);
        gridPaneBoard.setMaxWidth(width * 4);
        gridPaneBoard.setBorder(new Border(new BorderStroke(getColor(),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        ColumnConstraints column = new ColumnConstraints(width);
        for (int i = 0; i < 4; i++) {
            gridPaneBoard.getColumnConstraints().add(column);
        }

        for (int i = 0; i < 4; i++) {
            BoardPiece bp = new BoardPiece(team, cont, pieceRadius, clickable);
            pieces.add(bp);
            gridPaneBoard.add(pieceBox(bp), i, 0);
        }
        URL urlRed = this.getClass().getClassLoader().getResource("playerIconRed.png");
        URL urlBlack = this.getClass().getClassLoader().getResource("playerIconBlack.png");
        Image img = (team == RED) ? new Image(urlRed.toExternalForm()) :
                new Image(urlBlack.toExternalForm());
        ImageView imgView = new ImageView(img);

        imgView.setPreserveRatio(true);
        imgView.setFitHeight(width);
        imgView.setFitWidth(width);
        BorderPane imgPane = new BorderPane();
        imgPane.setCenter(imgView);

        GridPane gridPaneDisplay = new GridPane();
        gridPaneDisplay.setAlignment(Pos.CENTER);
        gridPaneDisplay.setPrefSize((width * 4) / 3, width);
        gridPaneDisplay.setMaxWidth((width * 4) / 3);

        Label typeLabel = new Label((type == HUMAN) ? "Human" : (type == MINIMAX) ? "Minimax" : (type == LOOKUP_TABLE) ? "Lookup\n Table" : "MCTS");
        typeLabel.setFont(Font.font("Verdana", width/4));
        ColumnConstraints column1 = new ColumnConstraints((width * 4) / 3);
        for (int i = 0; i < 3; i++) {
            gridPaneDisplay.getColumnConstraints().add(column1);
        }
        gridPaneDisplay.add(imgPane, 1, 0);
        gridPaneDisplay.add(typeLabel, 2, 0);

        getChildren().add(gridPaneBoard);
        if (clickable && team == RED) getChildren().add(1, gridPaneDisplay);
        else if (clickable) getChildren().add(0, gridPaneDisplay);

    }

    public void update(Controller cont, State state) {
        if (gridPaneBoard.getChildren().size() > state.getUnplaced(team)) {
            if (cont.getSelected() != null) { //Human turn
                BoardPiece bp = cont.getSelected();
                pieces.remove(bp);
                gridPaneBoard.getChildren().remove(bp.getParent());
            } else {
                HBox parent = (HBox) gridPaneBoard.getChildren().get(0);
                BoardPiece bp = (BoardPiece) parent.getChildren().get(0);
                pieces.remove(bp);
                gridPaneBoard.getChildren().remove(parent);
            }
        } else if (gridPaneBoard.getChildren().size() < state.getUnplaced(team)) {
            for (int i = 0; i < 4; i++) {
                boolean occupied = false;
                for (Node node : gridPaneBoard.getChildren()) {
                    if (GridPane.getColumnIndex(node) == i) {
                        occupied = true;
                        break;
                    }
                }
                if (!occupied) {
                    BoardPiece bp = new BoardPiece(team, cont, pieceRadius, clickable);
                    pieces.add(bp);
                    gridPaneBoard.add(pieceBox(bp), i, 0);
                    break;
                }

            }
        }
    }

    public ArrayList<BoardPiece> getPieces() {
        return pieces;
    }

    private Color getColor() {
        if (team == RED) return Color.RED;
        else return Color.BLACK;
    }

    private HBox pieceBox(BoardPiece piece) {
        HBox box = new HBox(piece);
        box.setAlignment(Pos.CENTER);
        return box;
    }
}
