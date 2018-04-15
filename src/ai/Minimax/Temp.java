package ai.Minimax;

import ai.AI;
import game.Logic;
import game.Move;
import game.State;

import java.util.HashMap;
import java.util.HashSet;

import static misc.Globals.BLACK;
import static misc.Globals.RED;

public class Temp extends AI {
    private HashMap<Long, MinimaxPlay> transTable = new HashMap<>();
    private int CURR_MAX_DEPTH;
    private Node prevBestNode;

    public Temp(int team)  {
        super(team);
    }

    public Move makeMove(State state) {
        if (state.getLegalMoves().size() == 1) {
            return state.getLegalMoves().get(0);
        }
        long startTime = System.currentTimeMillis();
        Move move = iterativeDeepeningMinimax(state).move;
        System.out.println("TIME SPENT: " + (System.currentTimeMillis() - startTime));
        return move;

    }

    private MinimaxPlay iterativeDeepeningMinimax(State state) {
        CURR_MAX_DEPTH = 0;
        prevBestNode = null;
        boolean done = false;
        transTable = new HashMap<>();
        MinimaxPlay bestPlay = null;
        long startTime = System.currentTimeMillis();
        while (!done) {
            Node simNode = new Node(state); // Start from fresh (Don't reuse previous game tree in new iterations)
            CURR_MAX_DEPTH+=1;
            MinimaxPlay play = minimax(simNode, CURR_MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE);
            System.out.println("CURRENT MAX DEPTH: " + CURR_MAX_DEPTH);
            System.out.println("CURRENT TABLE SIZE: " + transTable.size());

            if (Math.abs(play.score) >= 1000) done = true;
            bestPlay = play;
        }
        System.out.println("Score: " + bestPlay.score + ", Final Depth: " + CURR_MAX_DEPTH +
                ", Final table size: " + transTable.size() + ", Play:  oldRow: " + bestPlay.move.oldRow + ", oldCol: " +
                bestPlay.move.oldCol + ", newRow: " + bestPlay.move.newRow + ", newCol: " +
                bestPlay.move.newCol + ", team: " + bestPlay.move.team);
        System.out.println("TIME SPENT: " + (System.currentTimeMillis() - startTime));

        return bestPlay;
    }

    public MinimaxPlay minimax(Node node, int depth, int alpha, int beta) {
        Move bestMove = null;
        int score;
        int bestScore = (node.getState().getTurn() == team) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        if (Logic.gameOver(node.getState()) || depth <= 0) {
            return new MinimaxPlay(bestMove, heuristic(node.getState(), depth), depth);
        }
        MinimaxPlay transpoPlay;
        transpoPlay = transTable.get(node.getHashCode());
        if (transpoPlay != null && (depth <= transpoPlay.depth || Math.abs(transpoPlay.score) >= 1000)) {
            return transpoPlay;
        }

        if (depth == CURR_MAX_DEPTH && prevBestNode != null) {
            score = minimax(prevBestNode, depth - 1, alpha, beta).score;
            if (node.getState().getTurn() == team) {
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = prevBestNode.getState().getMove();
                }
                alpha = Math.max(score, alpha);
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    bestMove = prevBestNode.getState().getMove();
                }
                beta = Math.min(score, beta);
            }
        }

        for (Node child : node.getChildren()) {
            if (depth == CURR_MAX_DEPTH) if (child.equals(prevBestNode)) continue;
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
        if (depth == CURR_MAX_DEPTH)
            prevBestNode = node.getNextNode(bestMove);
        if (transpoPlay == null || depth > transpoPlay.depth) {
            transTable.put(node.getHashCode(), new MinimaxPlay(bestMove, bestScore, depth));
        }
        return new MinimaxPlay(bestMove, bestScore, depth);
    }

    private int heuristic(State state, int depth) {
        int opponent = (team == RED) ? BLACK : RED;
        int winner = Logic.getWinner(state);

        if (winner == team) return 1000;
        else if (winner == opponent) return -1000;
        return 0;
    }
}

