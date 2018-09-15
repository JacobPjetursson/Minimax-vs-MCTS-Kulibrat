package FFT;

import java.util.Objects;

public class Clause {
    String name;
    boolean boardPlacement;
    int row = -2; int col = -2; int team = -2;
    boolean negation;


    Clause(int row, int col, int team, boolean negation) {
        this.row = row;
        this.col = col;
        this.team = team;
        this.boardPlacement = true;
        this.negation = negation;
        if (negation)
            this.name = "!";
        else
            this.name = "";

        this.name += String.format("B_%d_%d=%d", row, col, team);
    }

    Clause(String name, boolean boardPlacement) {
        this.name = name;
        if (name.contains("!"))
            this.negation = true;
        this.boardPlacement = boardPlacement;

        if (boardPlacement) {
            // Parsing
            String info = (name.replaceAll("[\\D]", ""));

            this.row = Integer.parseInt(info.substring(0, 1));
            this.col = Integer.parseInt(info.substring(1, 2));
            if (info.length() >= 3)
                this.team = Integer.parseInt(info.substring(2, 3));

        }
    }

    Clause(Clause duplicate) {
        this.name = duplicate.name;
        this.boardPlacement = duplicate.boardPlacement;
        this.row = duplicate.row;
        this.col = duplicate.col;
        this.team = duplicate.team;
        this.negation = duplicate.negation;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Clause)) return false;

        Clause clause = (Clause) obj;
        return this == clause ||
                (this.name.equals(clause.name));
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(name);
    }
}