package ai.Minimax;

import game.Logic;
import game.Move;
import game.State;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import static misc.Globals.BLACK;
import static misc.Globals.RED;

public class StateSpaceCalc {
    private static HashMap<Long, MinimaxPlay> transTable = new HashMap<>();
    private static HashSet<CustomState> fullSpace = new HashSet<>();
    private static long legalMoves = 0;
    private static int team = RED;
    private static boolean findBranchFactor = true;


    private static void calcStateSpace(State state, int scoreLimit) {
        fullSpace = fillSpace(scoreLimit);
        int prevSize = fullSpace.size();
        iterativeDeepeningMinimax(state);
        System.out.println("FINAL STATE SPACE SIZE: " + transTable.size());
        double avgBranchFactor = 0;
        if (findBranchFactor) avgBranchFactor = ((double) legalMoves / (double) transTable.size());
        System.out.println("AVG BRANCHING FACTOR: " + avgBranchFactor);
    }

    private static void iterativeDeepeningMinimax(State state) {
        int CURR_MAX_DEPTH = 0;
        boolean done = false;
        while (!done) {
            Node simNode = new Node(state); // Start from fresh (Don't reuse previous game tree in new iterations)
            CURR_MAX_DEPTH++;
            int prevSize = transTable.size();
            minimax(simNode, CURR_MAX_DEPTH);
            System.out.println("CURRENT MAX DEPTH: " + CURR_MAX_DEPTH);
            System.out.println("TABLE SIZE: " + transTable.size());
            if (transTable.size() == prevSize) done = true;
        }
    }

    public static MinimaxPlay minimax(Node node, int depth) {
        Move bestMove = null;
        int bestScore = (node.getState().getTurn() == team) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int score;
        if (Logic.gameOver(node.getState()) || depth == 0) {
            return new MinimaxPlay(bestMove, heuristic(node.getState()), depth);
        }
        CustomState cs = new CustomState(node.getState());
        fullSpace.remove(cs);
        MinimaxPlay transpoPlay = transTable.get(node.getHashCode());

        if (transpoPlay != null && depth <= transpoPlay.depth) {
            return transpoPlay;
        }

        for (Node child : node.getChildren()) {
            score = minimax(child, depth - 1).score;
            if (node.getState().getTurn() == team) {
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = child.getState().getMove();
                }
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    bestMove = child.getState().getMove();
                }
            }
        }

        if (transpoPlay == null || depth > transpoPlay.depth) {
            if (transpoPlay == null) if (findBranchFactor) legalMoves += node.getState().getLegalMoves().size();
            transTable.put(node.getHashCode(), new MinimaxPlay(bestMove, bestScore, depth));
        }

        return new MinimaxPlay(bestMove, bestScore, depth);
    }

    private static int heuristic(State state) {
        int opponent = (team == RED) ? BLACK : RED;
        int winner = Logic.getWinner(state);

        if (winner == team) return 1000;
        else if (winner == opponent) return -1000;
        return 0;
    }

    private static HashSet<CustomState> fillSpace(int pointsToWin) {
        HashSet<CustomState> fullSpace = new HashSet<>();
        int stateSpace = 170019 * 2 * (int) Math.pow((double)pointsToWin, 2.0);
        System.out.println(stateSpace);

        while(fullSpace.size() < stateSpace) {
            CustomState cs;
            int[] board = new int[12];
            int upRed = 4;
            int upBlack = 4;
            int players = 2;
            for(int r1 = 0; r1 < board.length; r1++) {
                for(int r2 = r1; r2 < board.length; r2++) {
                    for(int r3 = r2; r3 < board.length; r3++) {
                        for(int r4 = r3; r4 < board.length; r4++) {
                            board[r4] = RED;

                            board = new int[12];
                        }
                        board[r3] = RED;
                    }
                    board[r2] = RED;
                }
                board[r1] = RED;
            }
            for(int t = 0; t < players; t++) {
                for(int r = 0; r < upRed; r++) {
                    for(int b = 0; b < upBlack; b++) {

                        for(int row = 0; row < 4; row++) {
                            for(int col = 0; col < 3; col++) {

                            }
                        }
                    }
                }
            }
        }
        return fullSpace;
    }

    public static void main(String[] args) {
        Zobrist.initialize();
        int scoreLimit = 1;

        State state = new State(scoreLimit);
        calcStateSpace(state, scoreLimit);
    }

    private static class CustomState {
        int turn;
        int pointsToWin;
        int redScore;
        int blackScore;
        int [][] board;

        CustomState(int pointsToWin, int[][] board, int turn, int redScore, int blackScore) {
            this.pointsToWin = pointsToWin;
            this.board = new int[board.length][];
            for (int i = 0; i < board.length; i++) {
                board[i] = Arrays.copyOf(board[i], board[i].length);
            }
            this.turn = turn;
            this.redScore = redScore;
            this.blackScore = blackScore;
        }

        CustomState(State state) {
            board = new int[state.getBoard().length][];
            for (int i = 0; i < state.getBoard().length; i++) {
                board[i] = Arrays.copyOf(state.getBoard()[i], state.getBoard()[i].length);
            }
            redScore = state.getScore(RED);
            blackScore = state.getScore(BLACK);
            turn = state.getTurn();
            pointsToWin = state.getPointsToWin();
        }
        int getTurn() {
            return turn;
        }
        int getPointsToWin() {
            return pointsToWin;
        }
        int getScore(int team) {
            if(team == RED)
                return redScore;
            else return blackScore;
        }
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CustomState)) return false;
            CustomState cstate = (CustomState) obj;
            return this == cstate || turn == cstate.getTurn() &&
                    pointsToWin == cstate.getPointsToWin() &&
                    Arrays.deepEquals(board, cstate.board) &&
                    redScore == cstate.getScore(RED) &&
                    blackScore == cstate.getScore(BLACK);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(turn, pointsToWin, redScore, blackScore);
            result = 31 * result + Arrays.deepHashCode(board);
            return result;

        }
    }

}

