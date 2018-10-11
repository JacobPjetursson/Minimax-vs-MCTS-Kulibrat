package FFT;

import game.State;
import misc.Globals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static misc.Globals.RED;

public class Rule {
    ArrayList<ClauseList> symmetryClauses;
    ArrayList<Clause> clauses;
    Action action;


    static final ArrayList<String> separators = new ArrayList<>(
            Arrays.asList("and", "And", "AND", "&", "&&", "∧", ","));


    Rule(ArrayList<Clause> clauses, Action action) {
        this.clauses = clauses;
        this.action = action;
        symmetryClauses = new ArrayList<>();
        symmetryClauses.add(new ClauseList(Globals.SYM_NONE, clauses));
        symmetryClauses.add(new ClauseList(Globals.SYM_HREF, reflectH(clauses)));
    }

    // parsing constructor
    Rule(String clauseStr, String actionStr) {
        symmetryClauses = new ArrayList<>();
        this.action = getAction(actionStr);
        this.clauses = getClauses(clauseStr);

        symmetryClauses.add(new ClauseList(Globals.SYM_NONE, clauses));
        symmetryClauses.add(new ClauseList(Globals.SYM_HREF, reflectH(clauses)));
    }

    String printRule() {
        return "IF (" + getClauseStr() + ") THEN (" + getActionStr() + ")";
    }

    private static ArrayList<Clause> getClauses(String clauseStr) {
        ArrayList<Clause> clauses = new ArrayList<>();
        String[] parts = clauseStr.split(" ");
        for (String part : parts) {
            if (separators.contains(part)) {
                continue;
            }
            for (String sep : separators) {
                if (part.contains(sep)) {
                    part = part.replace(sep, "");
                }
            }
            if ((part.charAt(0) == '!' && Character.isDigit(part.charAt(1)))) {
                String newpart = "!E" + part.substring(1);
                clauses.add(new Clause(newpart));
                newpart = "!P" + part.substring(1);
                clauses.add(new Clause(newpart));
            } else if (Character.isDigit(part.charAt(0))) {
                clauses.add(new Clause("E" + part));
                clauses.add(new Clause("P" + part));
            }
            else
                clauses.add(new Clause(part));
        }

        return clauses;
    }

    String getClauseStr() {
        String clauseMsg = "";
        for (Clause clause : clauses) {
            if (!clauseMsg.isEmpty())
                clauseMsg += " ∧ ";
            clauseMsg += clause.name;
        }
        return clauseMsg;
    }

    String getActionStr() {
        String clauseMsg = "";
        for (Clause clause : action.addClauses) {
            if (!clauseMsg.isEmpty())
                clauseMsg += " ∧ ";
            clauseMsg += "+" + clause.name;
        }
        for (Clause clause : action.remClauses) {
            if (!clauseMsg.isEmpty())
                clauseMsg += " ∧ ";
            clauseMsg += "-" + clause.name;
        }
        return clauseMsg;
    }

    private ArrayList<Clause> getClauses(State state) {
        ArrayList<Clause> clauses = new ArrayList<>();

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

    private static Action getAction(String actionStr) {
        String[] parts = actionStr.split(" ");
        ArrayList<String> corrected_parts = new ArrayList<>();
        for (String part : parts) {
            if (separators.contains(part))
                continue;
            for (String sep : separators) {
                if (part.contains(sep))
                    part = part.replace(sep, "");
            }
            corrected_parts.add(part);

        }

        return new Action(corrected_parts);
    }

    boolean applies(State state, int symmetry) {
        ArrayList<Clause> stClauses = getClauses(state);
        for (ClauseList clauseList : symmetryClauses) {
            if (clauseList.symmetry != symmetry)
                continue;
            boolean match = true;
            for (Clause c : clauseList.clauses) {
                if (c.negation) {
                    Clause temp = new Clause(c);
                    temp.name = temp.name.replace("!", "");
                    if (stClauses.contains(temp)) {
                        match = false;
                        break;
                    }
                }
                else if (!stClauses.contains(c)) {
                    match = false;
                    break;
                }
            }
            if (match)
                return true;
        }
        return false;
    }

    private int[][] makeClauseBoard(ArrayList<Clause> clauses) {
        int[][] clauseBoard = new int[Globals.bHeight][Globals.bWidth];
        // These clauses will be reflected/rotated
        ArrayList<Clause> changeClauses = new ArrayList<Clause>();

        for (Clause c : clauses) {
            if (c.boardPlacement && c.row != -1) {
                changeClauses.add(c);
                if (c.negation)
                    clauseBoard[c.row][c.col] = -c.pieceOcc;
                else
                    clauseBoard[c.row][c.col] = c.pieceOcc;
            }
        }
        clauses.removeAll(changeClauses);
        return clauseBoard;
    }

    private void addClauseBoardToList(int[][] cb, ArrayList<Clause> clauses) {
        // Add back to list
        for(int i = 0; i < cb.length; i++) {
            for (int j = 0; j < cb[i].length; j++) {
                int val = cb[i][j];
                //System.out.println("VALUE:" + val);
                if(val < 0)
                    clauses.add(new Clause(i, j, -val, true));
                else if (val > 0)
                    clauses.add(new Clause(i, j, val, false));
            }
        }
    }

    private ArrayList<Clause> reflectH(ArrayList<Clause> clauses) {
        ArrayList<Clause> rClauses = new ArrayList<>(clauses);
        int[][] cBoard = makeClauseBoard(rClauses);
        int[][] refH = new int[Globals.bHeight][Globals.bWidth];

        // Reflect
        for (int i = 0; i < cBoard.length; i++) {
            for (int j = 0; j < cBoard[i].length; j++) {
                refH[i][j] = cBoard[i][cBoard[i].length - 1 - j];
            }
        }

        addClauseBoardToList(refH, rClauses);
        return rClauses;
    }

    static boolean isValidRuleFormat(String clauseStr, String actionStr) {
        ArrayList<Clause> clauses = getClauses(clauseStr);
        Action action = getAction(actionStr);
        if (clauses.isEmpty())
            return false;
        for (Clause c : clauses)
            if (c.clauseErr)
                return false;
        if (action.actionErr)
            return false;
        for (Clause c : action.addClauses)
            if (c.clauseErr)
                return false;
        for (Clause c : action.remClauses)
            if (c.clauseErr)
                return false;

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Rule)) return false;

        Rule rule = (Rule) obj;
        return this == rule ||
                (this.symmetryClauses.equals(rule.symmetryClauses));
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.symmetryClauses);
    }

    private class ClauseList {
        ArrayList<Clause> clauses;
        int symmetry;

        public ClauseList(int symmetry, ArrayList<Clause> clauses) {
            this.clauses = clauses;
            this.symmetry = symmetry;
        }
    }

}
