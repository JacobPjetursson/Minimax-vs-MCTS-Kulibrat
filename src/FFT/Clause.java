package FFT;

import java.util.Objects;

public class Clause {
    String name;
    int value;
    boolean action;
    int row;
    int col;
    int oldRow;
    int oldCol;
    int newRow;
    int newCol;
    int team;

    Clause(int row, int col, int team) {
        this.row = row;
        this.col = col;
        this.name = "P_" + row + "_" + col;
        this.value = team;
        this.action = false;
    }

    Clause(int oldRow, int oldCol, int newRow, int newCol, int team) {
        this.oldRow = oldRow;
        this.oldCol = oldCol;
        this.newRow = newRow;
        this.newCol = newCol;
        this.value = -1;
        this.team = team;
        this.name = "A_" + team + ": (" + oldRow + "," + oldCol + ") "
                + "-> " + "(" + newRow + "," + newCol + ")";
        this.action = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Clause)) return false;

        Clause clause = (Clause) obj;
        return this == clause ||
                (this.name.equals(clause.name) && this.value == clause.value && this.action == clause.action);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(name, value, action);
    }
}