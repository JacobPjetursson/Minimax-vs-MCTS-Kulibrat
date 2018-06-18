package game;

import java.util.Objects;

public class Move {
    public int oldRow;
    public int oldCol;
    public int newRow;
    public int newCol;
    public int team;

    public Move(int oldRow, int oldCol, int newRow, int newCol, int team) {
        this.oldRow = oldRow;
        this.oldCol = oldCol;
        this.newRow = newRow;
        this.newCol = newCol;
        this.team = team;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Move)) return false;
        Move move = (Move) o;
        return oldRow == move.oldRow &&
                oldCol == move.oldCol &&
                newRow == move.newRow &&
                newCol == move.newCol &&
                team == move.team;
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldRow, oldCol, newRow, newCol, team);
    }
}
