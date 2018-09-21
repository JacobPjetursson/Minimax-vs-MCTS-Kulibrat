package FFT;

import java.util.ArrayList;

public class Action {
    ArrayList<Clause> addClauses;
    ArrayList<Clause> remClauses;

    Action(ArrayList<Clause> addClauses, ArrayList<Clause> remClauses) {
        this.addClauses = addClauses;
        this.remClauses = remClauses;
    }

    /*
    Action(Move move) {
        clauses = new ArrayList<Clause>();
        Clause addC = new Clause(move.newRow, move.newCol, move.team, false);
        StringBuilder sb = new StringBuilder(addC.name);
        sb.insert(0, "+");
        addC.name = sb.toString();
        clauses.add(addC);
        Clause remC = new Clause(move.oldRow, move.oldCol, move.team, false);
        sb = new StringBuilder(remC.name);
        sb.insert(0, "-");
        remC.name = sb.toString();
        clauses.add(remC);
    }
    */
}
