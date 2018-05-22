package ai.Minimax;

import game.Logic;
import game.Move;
import game.State;

import java.util.ArrayList;

import static misc.Globals.BLACK;
import static misc.Globals.RED;

public class Node {
    private State state;
    private long hash;

    // Starting Root state
    public Node(State startState) {
        this.state = new State(startState);
        this.hash = initHashCode();
    }

    // Non-root state
    public Node(Node parent, Move m) {
        this.state = new State(parent.state);
        hash = parent.hash;

        this.state.setMove(m);
        Logic.doTurn(m, this.state);
        updateHashCode(parent.state);
    }

    // Duplicate constructor, for "root" state
    public Node(Node node) {
        this.state = new State(node.state);
        hash = node.hash;
        this.state.setMove(node.state.getMove());
    }

    public Node getNextNode(Move m) {
        return new Node(this, m);
    }

    public ArrayList<Node> getChildren() {
        ArrayList<Node> children = new ArrayList<>();
        for (Move m : state.getLegalMoves()) {
            Node child = new Node(this, m);
            children.add(child);
        }
        return children;
    }

    public State getState() {
        return state;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) return false;
        return (((Node) obj).getHashCode() == getHashCode());
    }

    @Override
    public int hashCode() {
        return (int) hash;
    }

    public long getHashCode() {
        return this.hash;
    }


    private long initHashCode() {
        long hash = 0L;
        int[][] board = state.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != 0) {
                    int k = board[i][j]; // team occupying spot
                    hash = hash ^ Zobrist.board[i][j][k];
                }
            }
        }
        hash = hash ^ Zobrist.turn[state.getTurn()];
        hash = hash ^ Zobrist.redPoints[state.getScore(RED)];
        hash = hash ^ Zobrist.blackPoints[state.getScore(BLACK)];
        return hash;
    }

    public void updateHashCode(State parent) {
        int[][] board = state.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != parent.getBoard()[i][j]) {
                    int k_parent = parent.getBoard()[i][j]; // team occupying spot
                    int k = board[i][j];
                    if (k_parent != 0) hash ^= Zobrist.board[i][j][k_parent];
                    if (k != 0) hash ^= Zobrist.board[i][j][k];
                }
            }
        }
        if (state.getTurn() != parent.getTurn()) {
            hash ^= Zobrist.turn[parent.getTurn()];
            hash ^= Zobrist.turn[state.getTurn()];
        }
        if (state.getScore(RED) != parent.getScore(RED)) {
            hash ^= Zobrist.redPoints[parent.getScore(RED)];
            hash ^= Zobrist.redPoints[state.getScore(RED)];
        }
        if (state.getScore(BLACK) != parent.getScore(BLACK)) {
            hash ^= Zobrist.blackPoints[parent.getScore(BLACK)];
            hash ^= Zobrist.blackPoints[state.getScore(BLACK)];
        }
    }
}
