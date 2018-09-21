package FFT;

import game.Move;

import java.util.ArrayList;

public class Action {
    ArrayList<Clause> addClauses;
    ArrayList<Clause> remClauses;
    boolean actionErr;

    Action(String[] clauses) {
        this.addClauses = new ArrayList<Clause>();
        this.remClauses = new ArrayList<Clause>();

        for (String c : clauses) {
            if (c.startsWith("+") && Character.isDigit(c.charAt(1))) {
                c = c.substring(1);
                addClauses.add(new Clause(c, true));
            }
            else if (c.startsWith("-") && Character.isDigit(c.charAt(1))) {
                c = c.substring(1);
                remClauses.add(new Clause(c, true));
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


    Action(Move move) {
        addClauses = new ArrayList<Clause>();
        remClauses = new ArrayList<Clause>();
        Clause addC = new Clause(move.newRow, move.newCol, Clause.PIECEOCC_NONE, false);
        StringBuilder sb = new StringBuilder(addC.name);
        sb.insert(0, "+");
        addC.name = sb.toString();
        addClauses.add(addC);
        Clause remC = new Clause(move.oldRow, move.oldCol, Clause.PIECEOCC_NONE, false);
        sb = new StringBuilder(remC.name);
        sb.insert(0, "-");
        remC.name = sb.toString();
        remClauses.add(remC);
    }

}
