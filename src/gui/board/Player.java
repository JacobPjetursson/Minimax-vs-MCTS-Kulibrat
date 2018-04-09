package gui.board;

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

import static misc.Globals.*;

public class Player extends VBox {
    private int team;
    private int type;
    private PlayArea playArea;
    private GridPane gridPaneBoard;
    private GridPane gridPaneDisplay;


    public Player(int team, int type, PlayArea playArea) {
        this.team = team;
        this.playArea = playArea;
        this.type = type;
        setAlignment(Pos.CENTER);
        setSpacing(10);
        int size = playArea.getBoard().getTileSize();

        gridPaneBoard = new GridPane();
        gridPaneBoard.setAlignment(Pos.CENTER);
        gridPaneBoard.setPrefSize(size * 4, size);
        gridPaneBoard.setMaxWidth(size * 4);
        gridPaneBoard.setBorder(new Border(new BorderStroke(getColor(),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        ColumnConstraints column = new ColumnConstraints(size);
        for (int i = 0; i < 4; i++) {
            gridPaneBoard.getColumnConstraints().add(column);
        }

        for (int i = 0; i < 4; i++) {
            gridPaneBoard.add(pieceBox(new BoardPiece(team, playArea)), i, 0);
        }
        URL urlRed = this.getClass().getClassLoader().getResource("playerIconRed.png");
        URL urlBlack = this.getClass().getClassLoader().getResource("playerIconBlack.png");
        Image img = (team == RED) ? new Image(urlRed.toExternalForm()) :
                new Image(urlBlack.toExternalForm());
        ImageView imgView = new ImageView(img);

        imgView.setPreserveRatio(true);
        imgView.setFitHeight(size);
        imgView.setFitWidth(size);
        BorderPane imgPane = new BorderPane();
        imgPane.setCenter(imgView);

        gridPaneDisplay = new GridPane();
        gridPaneDisplay.setAlignment(Pos.CENTER);
        gridPaneDisplay.setPrefSize((size * 4) / 3, size);
        gridPaneDisplay.setMaxWidth((size * 4) / 3);

        Label typeLabel = new Label((type == HUMAN) ? "Human" : (type == MINIMAX) ? "Minimax" : (type == LOOKUP_TABLE) ? "Lookup\n Table" : "MCTS");
        typeLabel.setFont(Font.font("Verdana", 15));
        ColumnConstraints column1 = new ColumnConstraints((size * 4) / 3);
        for (int i = 0; i < 3; i++) {
            gridPaneDisplay.getColumnConstraints().add(column1);
        }
        gridPaneDisplay.add(imgPane, 1, 0);
        gridPaneDisplay.add(typeLabel, 2, 0);

        if (team == RED) getChildren().addAll(gridPaneBoard, gridPaneDisplay);
        else getChildren().addAll(gridPaneDisplay, gridPaneBoard);

    }

    public void update(State state) {
        if (gridPaneBoard.getChildren().size() > state.getUnplaced(team)) {
            if (playArea.getSelected() != null) {
                gridPaneBoard.getChildren().remove(playArea.getSelected().getParent());
            } else {
                gridPaneBoard.getChildren().remove(0);
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
                    gridPaneBoard.add(pieceBox(new BoardPiece(team, playArea)), i, 0);
                    break;
                }

            }
        }
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

    public int getType() {
        return type;
    }
}
