package gui.board;

import gui.PlayArea;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static misc.Globals.*;

public class BoardPiece extends Circle {
    private int RADIUS = 20;
    private PlayArea playArea;

    private Color color;
    private boolean selected;
    private int team;
    private int row;
    private int col;


    BoardPiece(int team, PlayArea playArea) {
        this.playArea = playArea;
        this.team = team;
        this.row = -1;
        this.col = -1;

        color = (team == RED) ? Color.RED : Color.BLACK;
        setRadius(RADIUS);
        setStrokeWidth(2.5);
        setColor(color);

        setOnMouseEntered(me -> {
            if (!isControllable()) return;

            if (team == RED && !selected) {
                setColor(new Color(1.0, 0.5, 0.5, 1.0));
            } else if (team == BLACK && !selected) {
                setColor(new Color(0.3, 0.3, 0.3, 1.0));
            }
        });

        setOnMouseExited(me -> {
            if (!selected && isControllable()) {
                setColor(color);
            }
        });

        setOnMouseClicked(event -> {
            if (!selected && isControllable()) select();
        });
    }

    BoardPiece(int team, PlayArea playArea, int row, int col) {
        this(team, playArea);
        this.row = row;
        this.col = col;

    }

    private void select() {
        selected = true;
        setFill(color);
        setStroke(Color.BLUE);
        playArea.setSelected(this);
    }

    private boolean isControllable() {
        return playArea.getPlayer(team).getType() == HUMAN &&
                playArea.getState().getTurn() == this.team;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int getTeam() {
        return team;
    }

    private void setColor(Color color) {
        setFill(color);
        setStroke(color);
    }

    public void deselect() {
        selected = false;
        setColor(color);
    }
}