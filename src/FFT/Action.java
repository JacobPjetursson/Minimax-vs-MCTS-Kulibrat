package FFT;

import game.Move;
import misc.Globals;

import java.util.ArrayList;

public class Action {
    ArrayList<Clause> addClauses;
    ArrayList<Clause> remClauses;
    boolean actionErr;

    Action(ArrayList<String> clauses) {
        this.addClauses = new ArrayList<Clause>();
        this.remClauses = new ArrayList<Clause>();

        for (String c : clauses) {
            if (c.startsWith("+") && Character.isDigit(c.charAt(1))) {
                c = c.substring(1);
                addClauses.add(new Clause(c));
            }
            else if (c.startsWith("-") && Character.isDigit(c.charAt(1))) {
                c = c.substring(1);
                remClauses.add(new Clause(c));
            } else {
                System.err.println("Invalid action format! Should be plus or minus, followed by row and column specification");
                actionErr = true;
                return;
            }
        }

        if (addClauses.isEmpty() && remClauses.isEmpty()) {
            System.err.println("Action clause list was empty");
            actionErr = true;
            return;
        }
        if (addClauses.size() > 1 || remClauses.size() > 1) {
            System.err.println("Only moves with a single add clause and/or a single remove clause is allowed in this game");
            actionErr = true;
        }
    }

    Action(ArrayList<Clause> addClauses, ArrayList<Clause> remClauses) {
        this.addClauses = addClauses;
        this.remClauses = remClauses;
    }

    // Kulibrat specific
    Move getMove() {
        // TODO - fix this piece of shit code
        int newRow = -1; int newCol = -1; int oldRow = -1; int oldCol = -1;

        for (Clause c : addClauses) {
            newRow = c.row;
            newCol = c.col;
        }
        for (Clause c : remClauses) {
            oldRow = c.row;
            oldCol = c.col;
        }
        return new Move(oldRow, oldCol, newRow, newCol, -1);
    }

    Action applySymmetry(int symmetry) {
        switch(symmetry) {
            case Globals.SYM_HREF:
                return reflectH();
            default:
                return this;
        }
    }

    Action reflectH() {
        ArrayList<Clause> rAddClauses = new ArrayList<>(addClauses);
        ArrayList<Clause> rRemClauses = new ArrayList<>(remClauses);
        int[][] cBoard = makeClauseBoard(rAddClauses, rRemClauses);
        int[][] refH = new int[Globals.bHeight][Globals.bWidth];

        // Reflect
        for (int i = 0; i < cBoard.length; i++) {
            for (int j = 0; j < cBoard[i].length; j++) {
                refH[i][j] = cBoard[i][cBoard[i].length - 1 - j];
            }
        }
        addClauseBoardToList(refH, rAddClauses, rRemClauses);

        return new Action(rAddClauses, rRemClauses);
    }

    private int[][] makeClauseBoard(ArrayList<Clause> addClauses, ArrayList<Clause> remClauses) {
        int[][] clauseBoard = new int[Globals.bHeight][Globals.bWidth];
        // These clauses will be reflected/rotated
        ArrayList<Clause> addChangeClauses = new ArrayList<>();
        ArrayList<Clause> remChangeClauses = new ArrayList<>();

        for (Clause c : addClauses) {
            if (c.row != -1) {
                addChangeClauses.add(c);
                clauseBoard[c.row][c.col] = c.pieceOcc;
            }
        }
        addClauses.removeAll(addChangeClauses);
        for (Clause c : remClauses) {
            if (c.row != -1) {
                remChangeClauses.add(c);
                clauseBoard[c.row][c.col] = -c.pieceOcc;
            }
        }
        remClauses.removeAll(remChangeClauses);

        return clauseBoard;
    }

    private void addClauseBoardToList(int[][] cb, ArrayList<Clause> addClauses, ArrayList<Clause> remClauses) {
        // Add back to list
        for(int i = 0; i < cb.length; i++) {
            for (int j = 0; j < cb[i].length; j++) {
                int val = cb[i][j];
                if(val == -Clause.PIECEOCC_NONE)
                    remClauses.add(new Clause(i, j, Clause.PIECEOCC_NONE, false));
                else if (val == Clause.PIECEOCC_NONE)
                    addClauses.add(new Clause(i, j, Clause.PIECEOCC_NONE, false));
            }
        }
    }
}
