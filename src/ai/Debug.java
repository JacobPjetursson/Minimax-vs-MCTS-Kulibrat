package ai;

import ai.Minimax.Minimax;
import ai.Minimax.Node;
import ai.Minimax.Zobrist;
import game.Move;
import game.State;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static misc.Globals.BLACK;
import static misc.Globals.RED;

public class Debug {
    public static void main(String[] args) {
        Zobrist.initialize();

        Point p1 = new Point(-1, -1);
        Point p2 = new Point(1, 1);
        Point p3 = new Point(2, 3);
        ArrayList<Point> points1 = new ArrayList<>();
        ArrayList<Point> points2 = new ArrayList<>();
        points1.add(p1);
        points1.add(p2);
        points1.add(p3);

        points2.add(p1);
        points2.add(p2);
        points2.add(p3);

        int[][] hej = {{1, 2, 3}, {4, 5, 6}};
        int[][] hejhej = {{1, 2, 3}, {4, 5, 6}};

        State baseState = new State(5);

        Node state1 = new Node(new State(5));
        state1.getState().setBoardEntry(0, 0, 2);
        state1.getState().setBoardEntry(3, 0, 1);
        state1.updateHashCode(baseState);

        Node state2 = new Node(new State(5));
        state2.getState().setBoardEntry(3, 0, 1);
        state2.getState().setBoardEntry(0, 0, 2);
        state2.updateHashCode(baseState);

        Node state3 = new Node(state2);


        Node state4 = new Node(state1, new Move(-1, -1, 3, 1, 1));
        Node state5 = new Node(state2, new Move(-1, -1, 3, 1, 1));
        Node state6 = state1.getNextNode(new Move(-1, -1, 3, 1, 1));

        Node state7 = new Node(new State(5));
        state7.getState().setBoardEntry(0, 0, 2);
        state7.getState().setBoardEntry(3, 0, 1);
        state7.getState().setBoardEntry(3, 1, 1);
        state7.getState().setTurn(BLACK);
        state7.updateHashCode(baseState);

        Node state8 = new Node(new State(state7.getState()));
        state8.getState().addPoint(RED);
        state8.updateHashCode(state7.getState());

        Node state9 = new Node(state7);
        state9.getState().setScore(RED, 1);
        state9.updateHashCode(state7.getState());


        System.out.println(state1.getHashCode() + "  " + state1.hashCode());
        System.out.println(state2.getHashCode() + "  " + state2.hashCode());
        System.out.println(state3.getHashCode() + "  " + state3.hashCode());
        System.out.println();

        System.out.println(state4.getHashCode() + "  " + state4.hashCode());
        System.out.println(state5.getHashCode() + "  " + state5.hashCode());
        System.out.println(state6.getHashCode() + "  " + state6.hashCode());
        System.out.println(state7.getHashCode() + "  " + state7.hashCode());
        System.out.println();


        System.out.println(state8.getHashCode() + "  " + state8.hashCode());
        System.out.println(state9.getHashCode() + "  " + state9.hashCode());
        System.out.println();

        HashSet<State> transSet = new HashSet<>();
        transSet.add(state1.getState());
        transSet.add(state2.getState());
        transSet.add(state3.getState());
        transSet.add(state4.getState());
        transSet.add(state5.getState());
        transSet.add(state6.getState());
        transSet.add(state7.getState());
        transSet.add(state8.getState());
        transSet.add(state9.getState());
        System.out.println(transSet.size());

        int depth = 20;
        System.out.println(--depth);
        System.out.println(depth);
        System.out.println(10 + 10);

    }

    private static boolean isReflection(State state1, State state2) {
        for (int i = 0; i < state1.getBoard().length; i++) {
            for (int j = 0; j < state1.getBoard()[i].length; j++) {
                if (j == 0) {
                    if (state1.getBoard()[i][0] != state2.getBoard()[i][2]) return false;
                } else if (j == 1) {
                    if (state1.getBoard()[i][1] != state2.getBoard()[i][1]) return false;
                }
            }
        }
        return true;
    }
}
