package FFT;

import game.Move;
import game.State;
import misc.Globals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
        this.clauses = getClauses(clauseStr);
        this.action = getAction(actionStr);
        this.move = getMove(action); // TODO - SHIT
        symmetryRules.add(clauses);
        symmetryRules.add(reflectH(clauses));
    }

    String printRule() {
        return "IF (" + getClauseStr() + ") THEN (" + getActionStr() + ")";
    }

    private ArrayList<Clause> getClauses(String clauseStr) {
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
            if (part.contains("B_")) {
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
        for (Clause clause : action.clauses) {
            if (!clauseMsg.isEmpty())
                clauseMsg += " ∧ ";
            clauseMsg += clause.name;
        }
        return clauseMsg;
    }

    private ArrayList<Clause> getClauses(State state) {
        ArrayList<Clause> clauses = new ArrayList<>();

        int[][] board = state.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] > 0)
                    clauses.add(new Clause(i, j, board[i][j], false));
            }
        }
        clauses.add(new Clause("SL=" + state.getScoreLimit(), false));
        return clauses;
    }


    // Kulibrat specific
    private Move getMove(Action action) {
        // TODO - fix this piece of shit code
        int newRow = -1; int newCol = -1; int oldRow = -1; int oldCol = -1; int team = -1;

        if (action.clauses.isEmpty()) {
            System.err.println("Action clause list was empty");
            return null;
        }
        for (Clause c : action.clauses) {
            if (isAddClause(c)) {
                newRow = c.row;
                newCol = c.col;
                team = c.team;
            } else if (isRemoveClause(c)) {
                oldRow = c.row;
                oldCol = c.col;
                team = c.team;
            } else {
                System.err.println("Action contained non-allowed clauses");
                return null;
            }
        }
        return new Move(oldRow, oldCol, newRow, newCol, team);
    }

    private Action getAction(String actionStr) {

        ArrayList<Clause> clauses = new ArrayList<>();
        String[] parts = actionStr.split(" ");
        for (String part : parts) {
            if (separators.contains(part))
                continue;
            for (String sep : separators) {
                if (part.contains(sep))
                    part = part.replace(sep, "");
            }
            if (part.startsWith("+") || part.startsWith("-"))
                clauses.add(new Clause(part, true));
        }
        if (clauses.isEmpty()) {
            System.err.println("Failed to provide a valid move");
            return null;
        }

        return new Action(clauses);
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
                    clauseBoard[c.row][c.col] = -c.team;
                else
                    clauseBoard[c.row][c.col] = c.team;
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

    private boolean isAddClause(Clause c) {
        return c.name.startsWith("+");
    }

    private boolean isRemoveClause(Clause c) {
        return c.name.startsWith("-");
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
