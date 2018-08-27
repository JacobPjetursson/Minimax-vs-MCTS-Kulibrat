package FFT;

import ai.Minimax.MinimaxPlay;
import game.Logic;
import game.Move;
import game.State;

import java.util.*;

import static misc.Globals.RED;

public class FFTMinimax {
    private int team;
    private HashMap<State, MinimaxPlay> lookupTable;
    private HashMap<Integer, ArrayList<Statement>> statements;

    private static int OUTCOME_RED_WIN = 0;
    private static int OUTCOME_BLACK_WIN = 1;
    private static int OUTCOME_DRAW = 2;
    private static int OUTCOME_RED_WIN_DRAW = 3;
    private static int OUTCOME_BLACK_WIN_DRAW = 3;
    private int CURR_MAX_DEPTH;
    private int COMMON_PIECES = 1;
    private int unevaluatedNodes = 0;

    FFTMinimax(int team) {
        this.team = team;
        lookupTable = new HashMap<>();
        statements = new HashMap<>();

    }

    void makeFFT(State state) {
        iterativeDeepeningMinimax(state);



        System.out.println("LOOKUP SIZE: " + lookupTable.size());

        for (Map.Entry<State, MinimaxPlay> entry : lookupTable.entrySet()) {
            State key = entry.getKey();
            MinimaxPlay value = entry.getValue();
            if (value.score < -1000) {
                int moves = movesFromTerminal(value.score, OUTCOME_BLACK_WIN);
                if (statements.get(moves) == null) {
                    ArrayList<Statement> list = new ArrayList<>();
                    list.add(new Statement(key, value.move));
                    statements.put(moves, list);
                } else {
                    statements.get(moves).add(new Statement(key, value.move));
                }
            }
        }
        // Sort the statements, so those with few clauses appear first in the list.
        // This is to reduce the amount of unimportant clauses when grouping the statements
        sortStatements();
        HashMap<Integer, ArrayList<ArrayList<Statement>>> similars = new HashMap<>();

        // Finding similiarities in statements and grouping them together
        for (Map.Entry<Integer, ArrayList<Statement>> entry : statements.entrySet()) {
            for (Statement st : entry.getValue()) {
                boolean match = false;
                if (similars.get(entry.getKey()) != null) {
                    for (ArrayList<Statement> sims : similars.get(entry.getKey())) {
                        Statement common = findCommon(sims);
                        if (isMatch(common, st)) {
                            sims.add(st);
                            match = true;
                            break;
                        }
                    }
                }
                if (!match) {
                    ArrayList<Statement> newSim = new ArrayList<>();
                    newSim.add(st);
                    if (similars.get(entry.getKey()) == null) {
                        ArrayList<ArrayList<Statement>> lists = new ArrayList<>();
                        lists.add(newSim);
                        similars.put(entry.getKey(), lists);
                    } else {
                        similars.get(entry.getKey()).add(newSim);
                    }
                }

            }
        }
        System.out.println("SIMILARS SIZE: " + similars.size());
/*
        boolean done1 = false;
        int i1 = 1;
        while (!done1) {
            System.out.println("STEP: " + (i1/2 + 1) );
            for (ArrayList<Statement> sList : similars.get(i1)) {
                Statement s = sList.get(0);
                //s.printStatement();
                s.printBoard();
            }
            if (similars.get(i1 + 2) == null)
                done1= true;
            else
                i1+=2;
        }
*/
        // Finding the common clauses in all statements, and making new statements with only these clauses
        HashMap<Integer, ArrayList<Statement>> commons = new HashMap<>();
        for (Map.Entry<Integer, ArrayList<ArrayList<Statement>>> entry : similars.entrySet()) {
            for (ArrayList<Statement> sims : entry.getValue()) {
                // The statement that shares the most with all other statements, and no more
                Statement common = findCommon(sims);
                if (commons.get(entry.getKey()) == null) {
                    ArrayList<Statement> newCommons = new ArrayList<>();
                    newCommons.add(common);
                    commons.put(entry.getKey(), newCommons);
                } else {
                    commons.get(entry.getKey()).add(common);
                }
            }
        }


        System.out.println("COMMONS SIZE: " + commons.size());
        boolean done = false;
        int i = 1;
        while (!done) {
            System.out.println("STEP: " + (i/2 + 1) );
            for (Statement s : commons.get(i)) {
                //s.printStatement();
                s.printBoard();
            }
            if (commons.get(i + 2) == null)
                done = true;
            else
                i+=2;
        }

    }

    private Statement findCommon(ArrayList<Statement> sims) {
        Statement stmt;
        ArrayList<Clause> commonClauses = new ArrayList<>();
        Statement s = sims.get(0);
        ArrayList<Clause> clauses = s.symmetryStatements.get(0);
        for (Clause c : clauses) {
            if (c.action) {
                commonClauses.add(c);
            }
        }
        for (Clause c : clauses) {
            if (c.action)
                continue;
            boolean common = true;
            for (Statement s1 : sims) {
                boolean common1 = false;
                for (ArrayList<Clause> clauses1 : s1.symmetryStatements) {
                    if (clauses1.contains(c) && clauses1.containsAll(commonClauses)) {
                        //if (clauses1.contains(c)) {
                        common1= true;
                        break;
                    }
                }

                if (!common1) {
                    common = false;
                    break;
                }
            }
            if (common) {
                commonClauses.add(c);
            }
        }
        stmt = new Statement(commonClauses);

        return stmt;
    }


    private int movesFromTerminal(int value, int outcome) {
        if (outcome == OUTCOME_RED_WIN)
            return 2000 - value;
        else if (outcome == OUTCOME_BLACK_WIN)
            return 2000 + value;
        else if (outcome == OUTCOME_DRAW)
            return value;
        else if (outcome == OUTCOME_RED_WIN_DRAW) {
            if (value > 1000)
                return 2000 - value;
            else
                return value;
        } else if (outcome == OUTCOME_BLACK_WIN_DRAW) {
            if (value < -1000)
                return 2000 + value;
            else
                return value;
        }
        else
            return value;
    }

    private class SortByStatementAmount implements Comparator<Statement> {

        public int compare(Statement a, Statement b) {
            return a.clauses.size() - b.clauses.size();
        }
    }

    private void sortStatements() {
        for (Map.Entry<Integer, ArrayList<Statement>> entry : statements.entrySet()) {
            Collections.sort(entry.getValue(), new SortByStatementAmount());
        }
    }

    private boolean isMatch(Statement st1, Statement st2) {
        ArrayList<Clause> stmnt2 = st2.symmetryStatements.get(0);
        for (ArrayList<Clause> stmnt1 : st1.symmetryStatements) {
            boolean containsAction = false;
            int commonPieces = 0;
            for (Clause c1 : stmnt1) {
                if (c1.action && stmnt2.contains(c1)) {
                    containsAction = true;
                }
                if (!c1.action && stmnt2.contains(c1))
                    commonPieces++;
            }
            if (containsAction && commonPieces >= COMMON_PIECES)
                return true;
        }
        return false;
    }

    // Runs an iterative deepening minimax as the exhaustive brute-force for the lookupDB. The data is saved in the transpo table
    private MinimaxPlay iterativeDeepeningMinimax(State state) {
        CURR_MAX_DEPTH = 0;
        boolean done = false;
        MinimaxPlay play = null;
        int doneCounter = 0;
        while (!done) {
            State simState = new State(state); // Start from fresh (Don't reuse previous game tree in new iterations)
            int prevSize = lookupTable.size();
            int prevUnevaluatedNodes = unevaluatedNodes;
            unevaluatedNodes = 0;
            CURR_MAX_DEPTH += 1;
            play = minimax(simState, CURR_MAX_DEPTH);
            System.out.println("CURRENT MAX DEPTH: " + CURR_MAX_DEPTH + ", LOOKUP TABLE SIZE: " + lookupTable.size() + ", UNEVALUATED NODES: " + unevaluatedNodes);
            if (lookupTable.size() == prevSize && unevaluatedNodes == prevUnevaluatedNodes) {
                System.out.println("State space explored, and unevaluated nodes unchanged between runs. I'm done");
                doneCounter++;
            } else
                doneCounter = 0;
            if (doneCounter == 2) done = true;

            if (Math.abs(play.score) >= 1000) {
                String player = (team == RED) ? "RED" : "BLACK";
                String opponent = (player.equals("RED")) ? "BLACK" : "RED";
                String winner = (play.score >= 1000) ? player : opponent;
                System.out.println("A SOLUTION HAS BEEN FOUND, WINNING STRAT GOES TO: " + winner);
            }
        }
        return play;
    }

    // Is called for every depth limit of the iterative deepening function. Classic minimax with no pruning
    private MinimaxPlay minimax(State state, int depth) {
        Move bestMove = null;
        int bestScore = (state.getTurn() == team) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int score;
        if (Logic.gameOver(state) || depth == 0) {
            return new MinimaxPlay(bestMove, heuristic(state), depth);
        }
        MinimaxPlay transpoPlay = lookupTable.get(state);
        if (transpoPlay != null && depth <= transpoPlay.depth) {
            return transpoPlay;
        }
        boolean evaluated = true;
        for (State child : state.getChildren()) {
            score = minimax(child, depth - 1).score;
            if (score > 1000) score--;
            else if (score < -1000) score++;
            else {
                evaluated = false;
                score++;
            }

            if (state.getTurn() == team) {
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = child.getMove();
                }
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    bestMove = child.getMove();
                }
            }
        }
        if (transpoPlay == null || depth > transpoPlay.depth) {
            lookupTable.put(state,
                    new MinimaxPlay(bestMove, bestScore, depth));
        }
        if (!evaluated) unevaluatedNodes++;
        return new MinimaxPlay(bestMove, bestScore, depth);
    }

    private int heuristic(State state) {
        int opponent = (team == 1) ? 2 : 1;
        int winner = Logic.getWinner(state);
        if(winner == team)
            return 2000;
        else if (winner == opponent)
            return -2000;
        return 0;
    }

}
