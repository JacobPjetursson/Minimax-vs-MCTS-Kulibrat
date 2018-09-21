package FFT;

import java.util.Objects;

public class Clause {
    public static final int PIECEOCC_NONE = 0;
    public static final int PIECEOCC_PLAYER = 1;
    public static final int PIECEOCC_ENEMY = 2;


    String name;
    boolean boardPlacement;
    int row = -1; int col = -1;
    int pieceOcc;
    boolean negation;


    Clause(int row, int col, int pieceOcc, boolean negation) {
        this.row = row;
        this.col = col;
        this.pieceOcc = pieceOcc;
        this.boardPlacement = true;
        this.negation = negation;
        if (negation)
            this.name = "!";
        else
            this.name = "";

        String teamStr = (pieceOcc == PIECEOCC_PLAYER) ? "P_" :
                (pieceOcc == PIECEOCC_ENEMY) ? "E_" : "";
        this.name += String.format("%s%d_%d", teamStr, row, col);
    }

    Clause(String name, boolean boardPlacement) {
        this.name = name;
        if (name.startsWith("!")) {
            this.negation = true;
            name = name.substring(1);
        }
        this.boardPlacement = boardPlacement;

        if (name.startsWith("+") || name.startsWith("-"))
            name = name.substring(1);

        if (boardPlacement) {
            if (name.startsWith("P"))
                this.pieceOcc = PIECEOCC_PLAYER;
            else if (name.startsWith("E"))
                this.pieceOcc = PIECEOCC_ENEMY;
            else if (!Character.isDigit(name.charAt(0))) {
                System.err.println("Board position clause must be specified with either P, E, or ''");
            }
            // Parsing
            String info = (name.replaceAll("[\\D]", ""));
            this.row = Integer.parseInt(info.substring(0, 1));
            this.col = Integer.parseInt(info.substring(1, 2));

        }
    }

    Clause(Clause duplicate) {
        this.name = duplicate.name;
        this.boardPlacement = duplicate.boardPlacement;
        this.row = duplicate.row;
        this.col = duplicate.col;
        this.pieceOcc = duplicate.pieceOcc;
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