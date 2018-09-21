package FFT;

import game.Move;
import game.State;
import misc.Globals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static misc.Globals.RED;

public class Rule {
    ArrayList<ArrayList<Clause>> symmetryRules;
    ArrayList<Clause> clauses;
    Action action;
    Move move;

    static final ArrayList<String> separators = new ArrayList<>(
            Arrays.asList("and", "And", "AND", "&", "&&", "∧", ","));


    Rule(ArrayList<Clause> clauses, Action action) {
        symmetryRules = new ArrayList<>();
        this.clauses = clauses;
        this.action = action;
        this.move = getMove(action); // TODO - SHIT
        symmetryRules = new ArrayList<>();
        symmetryRules.add(clauses);
        symmetryRules.add(reflectH(clauses));
    }

    // parsing constructor
    Rule(String clauseStr, String actionStr) {
        symmetryRules = new ArrayList<>();
        this.action = getAction(actionStr);
        this.clauses = getClauses(clauseStr);

        this.move = getMove(action); // TODO - SHIT
        symmetryRules.add(clauses);
        symmetryRules.add(reflectH(clauses));
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
            boolean boardPlacement = false;
            // TODO - Cancer way rn
            if (Character.isDigit(part.charAt(0)) || Character.isDigit(part.charAt(1))) {
                boardPlacement = true;
            }
            clauses.add(new Clause(part, boardPlacement));
        }

        return clauses;
    }

    String getClauseStr() {
        String clauseMsg = "";
        for (Clause clause : clauses) {
            if (!clauseMsg.isEmpty())
                clauseMsg += " AND ";
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
        clauses.add(new Clause("SL=" + state.getScoreLimit(), false));
        return clauses;
    }


    // Kulibrat specific
    private Move getMove(Action action) {
        // TODO - fix this piece of shit code
        int newRow = -1; int newCol = -1; int oldRow = -1; int oldCol = -1; int team = -1;

        if (action.addClauses.isEmpty() && action.remClauses.isEmpty()) {
            System.err.println("Action clause list was empty");
            return null;
        }
        if (action.addClauses.size() > 1 || action.remClauses.size() > 1) {
            System.err.println("Only moves with a single add clause and/or a single remove clause is allowed in this game");
            return null;
        }
        for (Clause c : action.addClauses) {
            newRow = c.row;
            newCol = c.col;
        }
        for (Clause c : action.remClauses) {
            oldRow = c.row;
            oldCol = c.col;
        }
        return new Move(oldRow, oldCol, newRow, newCol, team);
    }

    private static Action getAction(String actionStr) {
        ArrayList<Clause> addClauses = new ArrayList<>();
        ArrayList<Clause> remClauses = new ArrayList<>();
        String[] parts = actionStr.split(" ");
        for (String part : parts) {
            if (separators.contains(part))
                continue;
            for (String sep : separators) {
                if (part.contains(sep))
                    part = part.replace(sep, "");
            }
            if (part.startsWith("+")) {
                part = part.substring(1);
                addClauses.add(new Clause(part, true));
            }
            else if (part.startsWith("-")) {
                part = part.substring(1);
                remClauses.add(new Clause(part, true));
            }
        }
        if (addClauses.isEmpty() && remClauses.isEmpty()) {
            System.err.println("Failed to provide a valid move");
            return null;
        }

        return new Action(addClauses, remClauses);
    }

    boolean applies(State state) {
        ArrayList<Clause> stClauses = getClauses(state);
        for (ArrayList<Clause> clauses : symmetryRules) {
            boolean match = true;
            for (Clause c : clauses) {
                if (c.negation) {
                    Clause temp = new Clause(c);
                    temp.name = temp.name.replace("!", "");
                    if (stClauses.contains(c)) {
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
                if(val < 0)
                    clauses.add(new Clause(i, j, -val, true));
                else
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
        if (clauses.isEmpty() || action == null)
            return false;
        for (Clause c : clauses)
            if (c.clauseErr)
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
                (this.symmetryRules.equals(rule.symmetryRules));
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.symmetryRules);
    }

}
