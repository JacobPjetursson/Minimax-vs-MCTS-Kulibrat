package FFT;

import game.Move;
import misc.Config;

import java.util.ArrayList;

public class Action extends FFTLib.FFT.Action {
    ArrayList<Clause> addClauses;
    ArrayList<Clause> remClauses;
    //boolean actionErr;

    Action(ArrayList<String> clauses) {
        super(clauses);
        if (addClauses.size() > 1 || remClauses.size() > 1) {
            System.err.println("Only moves with a single add clause and/or a single remove clause is allowed in this game");
            actionErr = true;
        }
    }

    Action(ArrayList<FFTLib.FFT.Clause> addClauses, ArrayList<FFTLib.FFT.Clause> remClauses) {
        super(addClauses, remClauses);
   }

    // Kulibrat specific
    @Override
    public Move getMove() {
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


    public Action applySymmetry(int symmetry) {
        FFTLib.FFT.Action a;
        switch(symmetry) {
            case Config.SYM_HREF:
                a = reflectH();
                break;
            default:
                a = this;
        }
        return new Action(a.addClauses, (a.remClauses));
    }

/*
    private int[][] makeClauseBoard(ArrayList<Clause> addClauses, ArrayList<Clause> remClauses) {
        int[][] clauseBoard = new int[Config.bHeight][Config.bWidth];
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
    */
}
