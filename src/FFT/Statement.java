package FFT;

import game.Move;
import game.State;

import java.util.ArrayList;
import java.util.Objects;

public class Statement {
    ArrayList<ArrayList<Clause>> symmetryStatements;
    ArrayList<Clause> clauses;
    State state;
    Move move;

    Statement(State state, Move move) {
        this.state = state;
        this.move = move;
        symmetryStatements = new ArrayList<>();
        clauses = getClauses(state, move);
        symmetryStatements.add(clauses);
        symmetryStatements.add(getClauses(state.reflect(), move.reflect()));
    }

    Statement(ArrayList<Clause> clauses) {
        this.clauses = clauses;
        this.state = getState(clauses);
        this.move = getMove(clauses);
        symmetryStatements = new ArrayList<>();
        symmetryStatements.add(clauses);
        symmetryStatements.add(getClauses(state.reflect(), move.reflect()));
    }

    void printStatement() {
        String msg = "";
        for (int i = 0; i < symmetryStatements.size(); i++) {
            ArrayList<Clause> statement = symmetryStatements.get(i);
            msg += "(";
            String clauseMsg = "";
            for (Clause clause : statement) {
                if (!clauseMsg.isEmpty())
                    clauseMsg += " ∧ ";
                clauseMsg += (clause.name);
                if (clause.value >= 0)
                    clauseMsg += "=" + clause.value;
            }
            msg += clauseMsg;
            msg += ")";
            if (i + 1 < symmetryStatements.size())
                msg += "\nV ";
        }

        System.out.println(msg + "\n");
    }

    void printBoard() {
        state.printBoard();
        move.print();
        System.out.println();
    }


    private ArrayList<Clause> getClauses(State state, Move move) {
        ArrayList<Clause> clauses = new ArrayList<>();

        int[][] board = state.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] > 0)
                    clauses.add(new Clause(i, j, board[i][j]));
            }
        }
        if (move != null) {
            clauses.add(new Clause(move.oldRow, move.oldCol, move.newRow, move.newCol, move.team));
        }
        return clauses;
    }

    private State getState(ArrayList<Clause> clauses) {
        State state = new State(1);
        for (Clause c : clauses) {
            if (!c.action)
                state.setBoardEntry(c.row, c.col, c.value);
        }
        return state;
    }

    private Move getMove(ArrayList<Clause> clauses) {
        Move m = null;
        for (Clause c : clauses) {
            if (c.action)
                m = new Move(c.oldRow, c.oldCol, c.newRow, c.newCol, c.team);
        }
        return m;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Statement)) return false;

        Statement stmt = (Statement) obj;
        return this == stmt ||
                (this.symmetryStatements.equals(stmt.symmetryStatements));
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.symmetryStatements);
    }

}
