package ai.Minimax;

import game.Logic;
import game.Move;
import game.State;

import java.util.HashMap;

import static misc.Globals.BLACK;
import static misc.Globals.RED;

public class FindWinnerStrategy {
    private static boolean cutOff = false;
    private static int team = RED;
    private static HashMap<Long, MinimaxPlay> transTable = new HashMap<>();

    private static Move makeMove(State state) {
        if (state.getLegalMoves().size() == 1) {
            return state.getLegalMoves().get(0);
        }
        long startTime = System.currentTimeMillis();
        Move move = iterativeDeepeningMinimax(state).move;
        System.out.println("TIME SPENT: " + (System.currentTimeMillis() - startTime));
        return move;

    }

    private static MinimaxPlay iterativeDeepeningMinimax(State state) {
        int CURR_MAX_DEPTH = 0;
        cutOff = false;
        MinimaxPlay bestPlay = null;
        while (!cutOff) {
            Node simNode = new Node(state); // Start from fresh (Don't reuse previous game tree in new iterations)
            CURR_MAX_DEPTH++;
            System.out.println("CURRENT MAX DEPTH: " + CURR_MAX_DEPTH);
            MinimaxPlay play = minimax(simNode, CURR_MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (play.score == 1000 || play.score == -1000) cutOff = true;
            bestPlay = play;
        }
        System.out.println("Score: " + bestPlay.score + ", Final Depth: " + CURR_MAX_DEPTH + ", Play:  oldRow: " + bestPlay.move.oldRow + ", oldCol: " +
                bestPlay.move.oldCol + ", newRow: " + bestPlay.move.newRow + ", newCol: " + bestPlay.move.newCol + ", team: " + bestPlay.move.team);

        return bestPlay;
    }

    public static MinimaxPlay minimax(Node node, int depth, int alpha, int beta) {
        Move bestMove = null;
        int score;
        int bestScore = (node.getState().getTurn() == team) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        if (Logic.gameOver(node.getState()) || depth <= 0) {
            return new MinimaxPlay(bestMove, heuristic(node.getState()), depth);
        }
        MinimaxPlay transpoPlay;
        transpoPlay = transTable.get(node.getHashCode());
        if (transpoPlay != null && depth <= transpoPlay.depth) {
            return transpoPlay;
        }
        for (Node child : node.getChildren()) {
            //if(transSet.contains(child.getHashCode())) continue;
            //transSet.add(child.getHashCode());
            score = minimax(child, depth - 1, alpha, beta).score;
            if (node.getState().getTurn() == team) {
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = child.getState().getMove();
                }
                alpha = Math.max(score, alpha);
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    bestMove = child.getState().getMove();
                }
                beta = Math.min(score, beta);
            }
            if (beta <= alpha) break;
        }
        if (transpoPlay == null || depth > transpoPlay.depth) {
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
        Move move = makeMove(state);
    }
}

