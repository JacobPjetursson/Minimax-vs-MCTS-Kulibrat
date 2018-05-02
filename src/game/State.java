package game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static misc.Globals.BLACK;
import static misc.Globals.RED;

public class State {
    private int[][] board;
    private int turn;
    private int redScore;
    private int blackScore;
    private int unplacedRed;
    private int unplacedBlack;
    private int scoreLimit;

    private ArrayList<Move> legalMoves;
    private Move move;

    // Starting state
    public State(int scoreLimit) {
        int rows = 4;
        int columns = 3;
        board = new int[rows][columns]; // Initializes to 0 (Java standard)
        redScore = 0;
        blackScore = 0;
        unplacedRed = 4;
        unplacedBlack = 4;
        turn = RED;
        this.scoreLimit = scoreLimit;
    }

    // Duplicate constructor, for "root" state
    public State(State state) {
        board = new int[state.board.length][];
        for (int i = 0; i < state.board.length; i++) {
            board[i] = Arrays.copyOf(state.board[i], state.board[i].length);
        }
        redScore = state.redScore;
        blackScore = state.blackScore;
        unplacedRed = state.unplacedRed;
        unplacedBlack = state.unplacedBlack;
        turn = state.turn;
        scoreLimit = state.scoreLimit;

        move = state.move;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoardEntry(int row, int col, int team) {
        board[row][col] = team;
    }

    public void addPoint(int team) {
        if (team == RED) {
            redScore++;
            unplacedRed++;
        } else {
            blackScore++;
            unplacedBlack++;
        }
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getScoreLimit() {
        return scoreLimit;
    }

    public void addUnPlaced(int team) {
        if (team == RED) unplacedRed++;
        else unplacedBlack++;
    }

    public void removeUnPlaced(int team) {
        if (team == RED) unplacedRed--;
        else unplacedBlack--;
    }

    public int getUnplaced(int team) {
        if (team == RED) return unplacedRed;
        else return unplacedBlack;
    }

    public ArrayList<Point> getPieces(int team) {
        ArrayList<Point> entries = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == team) {
                    entries.add(new Point(j, i));
                }
            }
        }
        if (getUnplaced(team) > 0) {
            entries.add(new Point(-1, -1));
        }
        return entries;
    }

    public int getScore(int team) {
        return (team == RED) ? redScore : blackScore;
    }

    public void setScore(int team, int score) {
        if (team == RED) redScore = score;
        else blackScore = score;
    }

    public State getNextState(Move m) {
        State state = new State(this);
        Logic.doTurn(m, state);
        state.move = m;
        return state;
    }


    // Creates and/or returns a list of new state objects which correspond to the children of the given state.
    public ArrayList<Move> getLegalMoves() {
        if (legalMoves != null) return legalMoves;
        legalMoves = Logic.legalMoves(turn, this);
        return legalMoves;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof State)) return false;
        State state = (State) obj;
        return this == state || turn == state.getTurn() &&
                scoreLimit == state.getScoreLimit() &&
                Arrays.deepEquals(board, state.board) &&
                redScore == state.getScore(RED) &&
                blackScore == state.getScore(BLACK);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(turn, scoreLimit, redScore, blackScore);
        result = 31 * result + Arrays.deepHashCode(board);
        return result;

    }
}
