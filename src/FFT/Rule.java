package FFT;

import FFTLib.FFT.Clause;
import game.State;
import misc.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

import static misc.Config.RED;

public class Rule extends FFTLib.FFT.Rule {
    //ArrayList<ClauseList> symmetryClauses;
    //ArrayList<Clause> clauses;
    //Action action;

    // parsing constructor
    Rule(String clauseStr, String actionStr) {
        super(clauseStr, actionStr);
        /*
        symmetryClauses = new ArrayList<>();
        this.action = getAction(actionStr);
        this.clauses = getClauses(clauseStr);

        symmetryClauses.add(new ClauseList(Config.SYM_NONE, clauses));
        symmetryClauses.add(new ClauseList(Config.SYM_HREF, reflectH(clauses)));
        */
    }
    // FIXME
    private HashSet<Clause> getClauses(State state) {
        HashSet<Clause> clauses = new HashSet<>();

        int[][] board = state.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int pieceOcc = board[i][j];
                if (pieceOcc > 0) {
                    if (state.getTurn() == RED)
                        clauses.add(new Clause(i, j, pieceOcc, false));
                    else {
                        pieceOcc = (pieceOcc == 1) ? 2 : 1;
                        clauses.add(new Clause(i, j, pieceOcc, false));
                    }
                }
            }
        }
        clauses.add(new Clause("SL=" + state.getScoreLimit()));
        return clauses;
    }
}
