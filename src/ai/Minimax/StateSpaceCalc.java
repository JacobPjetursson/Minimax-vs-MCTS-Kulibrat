package ai.Minimax;

import game.Logic;
import game.Move;
import game.State;

import java.util.HashMap;

import static misc.Globals.BLACK;
import static misc.Globals.RED;

public class StateSpaceCalc {
    private static HashMap<Long, MinimaxPlay> transTable = new HashMap<>();
    private static long legalMoves = 0;
    private static int team = RED;
    private static boolean findBranchFactor = true;


    private static Move calcStateSpace(State state) {
        MinimaxPlay bestPlay = iterativeDeepeningMinimax(state);
        System.out.println("FINAL TABLE SIZE: " + transTable.size());
        double avgBranchFactor = 0;
        if (findBranchFactor) avgBranchFactor = ((double) legalMoves / (double) transTable.size());
        System.out.println("AVG BRANCHING FACTOR: " + avgBranchFactor);
        System.out.println("Score: " + bestPlay.score + ", Play:  oldRow: " + bestPlay.move.oldRow + ", oldCol: " +
                bestPlay.move.oldCol + ", newRow: " + bestPlay.move.newRow + ", newCol: " + bestPlay.move.newCol + ", team: " + bestPlay.move.team);

        return bestPlay.move;

    }

    private static MinimaxPlay iterativeDeepeningMinimax(State state) {
        MinimaxPlay bestPlay = null;
        int CURR_MAX_DEPTH = 0;
        boolean done = false;
        while (!done) {
            Node simNode = new Node(state); // Start from fresh (Don't reuse previous game tree in new iterations)
            CURR_MAX_DEPTH++;
            int prevSize = transTable.size();
            bestPlay = minimax(simNode, CURR_MAX_DEPTH);
            System.out.println("CURRENT MAX DEPTH: " + CURR_MAX_DEPTH);
            System.out.println("TABLE SIZE: " + transTable.size());
            if (transTable.size() == prevSize) done = true;
        }
        return bestPlay;
    }

    public static MinimaxPlay minimax(Node node, int depth) {
        Move bestMove = null;
        int bestScore = (node.getState().getTurn() == team) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int score;
        if (Logic.gameOver(node.getState()) || depth == 0) {
            return new MinimaxPlay(bestMove, heuristic(node.getState()), depth);
        }
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
            //if(beta <= alpha) break;
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

    public static void main(String[] args) {
        Zobrist.initialize();

        State state = new State(1);
        Move move = calcStateSpace(state);
    }
}

