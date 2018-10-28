package FFT;

import game.State;
import java.util.HashSet;

import misc.Config;

public class Rule extends FFTLib.FFT.Rule {
    //ArrayList<ClauseList> symmetryClauses;
    //ArrayList<Clause> clauses;
    Action action;
    public int boardWidth = Config.bWidth;
    public int boardHeight = Config.bHeight;

    // parsing constructor
    Rule(String clauseStr, String actionStr) {
        super(clauseStr, actionStr);
        symmetryClauses.add(new ClauseList(Config.SYM_HREF, reflectH(clauses)));

    }
    // FIXME
    private HashSet<Clause> getClauses(State state) {
        HashSet<Clause> clauses = new HashSet<>();

        int[][] board = state.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int pieceOcc = board[i][j];
                if (pieceOcc > 0) {
                    if (state.getTurn() == Config.RED)
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
