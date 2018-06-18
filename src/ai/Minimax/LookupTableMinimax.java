package ai.Minimax;

import ai.AI;
import game.Logic;
import game.Move;
import game.State;
import misc.Globals;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static misc.Globals.BLACK;
import static misc.Globals.RED;

public class LookupTableMinimax extends AI {
    private boolean useDB = true;
    private int CURR_MAX_DEPTH;
    private int unevaluatedNodes = 0;
    private HashMap<Long, MinimaxPlay> lookupTable;
    private String JDBC_URL;
    private Connection conn;

    public LookupTableMinimax(int team, State state, boolean overwriteDB) {
        super(team);
        lookupTable = new HashMap<>();
        JDBC_URL = Globals.JDBC_URL;
        if (!overwriteDB)
            checkConnection(state);
        if (useDB) {
            conn = getConnection(state.getScoreLimit());
            if (overwriteDB) {
                this.team = RED;
                System.out.println("Rebuilding lookup table. This will take some time.");
                buildLookupTable(state);
                try {
                    fillTable(state.getScoreLimit());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                this.team = team;
            }
        }
    }

    // This function fetches the best move from the DB, if it exists
    public Move makeMove(State state) {
        String teamstr = (team == BLACK) ? "BLACK" : "RED";
        System.out.println("Finding best play for " + teamstr);
        if (state.getLegalMoves().size() == 1) {
            return state.getLegalMoves().get(0);
        }
        // table lookup
        Node simNode = new Node(state);
        MinimaxPlay play;
        if (useDB) {
            play = queryData(simNode.getHashCode(), state.getScoreLimit());
        } else {
            play = iterativeDeepeningMinimax(state);
        }
        if (play == null) {
            System.err.println("DB Table is empty and needs to be rebuilt. Exiting");
            System.exit(0);
        }
        Move move = play.move;
        String winner = (play.score >= 1000) ? "RED" : (play.score == 0) ? "DRAW" : "BLACK";
        System.out.print("BEST PLAY:  " + "oldRow: " + move.oldRow +
                ", oldCol: " + move.oldCol + ", newRow: " + move.newRow + ", newCol: " + move.newCol +
                ", WINNER IS: " + winner);
        System.out.println(" in " + (play.score >= 1000 ? 2000 - play.score : (play.score == 0) ? "∞" : 2000 + play.score) + " moves!");
        return move;
    }

    // This function builds the lookup table from scratch
    private void buildLookupTable(State state) {
        long startTime = System.currentTimeMillis();
        iterativeDeepeningMinimax(state);
        System.out.println("Lookup table successfully built. Time spent: " + (System.currentTimeMillis() - startTime));
    }

    // Runs an iterative deepening minimax as the exhaustive brute-force for the lookupDB. The data is saved in the transpo table
    private MinimaxPlay iterativeDeepeningMinimax(State state) {
        CURR_MAX_DEPTH = 0;
        boolean done = false;
        MinimaxPlay play = null;
        int doneCounter = 0;
        while (!done) {
            Node simNode = new Node(state); // Start from fresh (Don't reuse previous game tree in new iterations)
            int prevSize = lookupTable.size();
            int prevUnevaluatedNodes = unevaluatedNodes;
            unevaluatedNodes = 0;
            CURR_MAX_DEPTH += 1;
            play = minimax(simNode, CURR_MAX_DEPTH);
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
    private MinimaxPlay minimax(Node node, int depth) {
        Move bestMove = null;
        int bestScore = (node.getState().getTurn() == team) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int score;
        if (Logic.gameOver(node.getState()) || depth == 0) {
            return new MinimaxPlay(bestMove, heuristic(node.getState()), depth);
        }
        MinimaxPlay transpoPlay = lookupTable.get(node.getHashCode());
        if (transpoPlay != null && depth <= transpoPlay.depth) {
            return transpoPlay;
        }
        boolean evaluated = true;
        for (Node child : node.getChildren()) {
            score = minimax(child, depth - 1).score;
            if (score > 1000) score--;
            else if (score < -1000) score++;
            else evaluated = false;

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
            lookupTable.put(node.getHashCode(),
                    new MinimaxPlay(bestMove, bestScore, depth));
        }
        if (!evaluated) unevaluatedNodes++;
        return new MinimaxPlay(bestMove, bestScore, depth);
    }

    // Heuristic function which values red with 2000 for a win, and -2000 for a loss. All other nodes are 0
    private int heuristic(State state) {
        int opponent = (team == RED) ? BLACK : RED;
        if (Logic.gameOver(state)) {
            int winner = Logic.getWinner(state);
            if (winner == team)
                return 2000;
            else if (winner == opponent)
                return -2000;
        }
        return 0;
    }

    // Connects to the DB
    private Connection getConnection(int scoreLimit) {
        System.out.println("Connecting to database. This might take some time");
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                    JDBC_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Connection successful");
        // Creating the table, if it does not exist already
        String tableName = "plays_" + scoreLimit;
        try {
            conn.createStatement().execute("create table " + tableName +
                    "(id bigint primary key, oldRow smallint, oldCol smallint, newRow smallint, newCol smallint, team smallint, score smallint)");
        } catch (SQLException e) {
            System.out.println("Table '" + tableName + "' exists in the DB");
        }
        return conn;
    }

    // Copies the transposition table into the database
    private void fillTable(int scoreLimit) throws SQLException {
        System.out.println("Inserting data into table. This will take some time");
        String tableName = "plays_" + scoreLimit;
        long startTime = System.currentTimeMillis();
        conn.createStatement().execute("truncate table " + tableName);

        final int batchSize = 1000;
        int count = 0;
        PreparedStatement stmt = conn.prepareStatement("insert into " + tableName + " values (?, ?, ?, ?, ?, ?, ?)");
        for (Map.Entry<Long, MinimaxPlay> entry : lookupTable.entrySet()) {
            Long key = entry.getKey();
            MinimaxPlay value = entry.getValue();
            stmt.setLong(1, key);
            stmt.setInt(2, value.move.oldRow);
            stmt.setInt(3, value.move.oldCol);
            stmt.setInt(4, value.move.newRow);
            stmt.setInt(5, value.move.newCol);
            stmt.setInt(6, value.move.team);
            stmt.setInt(7, value.score);

            stmt.addBatch();
            if (++count % batchSize == 0) {
                stmt.executeBatch();
            }
        }
        stmt.executeBatch();
        stmt.close();
        System.out.println("Data inserted successfully. Time spent: " + (System.currentTimeMillis() - startTime));
    }

    // Fetches the best state, given a key and a score limit
    private MinimaxPlay queryData(Long key, int scoreLimit) {
        MinimaxPlay play = null;
        String tableName = "plays_" + scoreLimit;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select oldRow, oldCol, newRow, newCol, team, score from " + tableName + " where id=" + key);
            while (resultSet.next()) {
                Move move = new Move(resultSet.getInt(1), resultSet.getInt(2),
                        resultSet.getInt(3), resultSet.getInt(4), resultSet.getInt(5));
                int score = resultSet.getInt(6);
                play = new MinimaxPlay(move, score, 0);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Table '" + tableName + "' does not exist! Exiting.");
            System.exit(0);
        }
        return play;
    }

    // Checks whether the database table to be used exists and is not empty.
    // If not, exits the program and tells the user to rebuild the database
    private void checkConnection(State state) {
        int scoreLimit = state.getScoreLimit();
        System.out.println("Connecting to database. This might take some time");
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                    JDBC_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Connection successful");

        String tableName = "plays_" + scoreLimit;
        long key = new Node(state).getHashCode();
        boolean error = false;
        // Try query to check for table existance
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select oldRow, oldCol, newRow, newCol, team, score from "
                    + tableName + " where id=" + key);
            if (!resultSet.next()) {
                System.err.println("The database table '" + tableName + "' is incomplete.");
                error = true;
            }
            statement.close();
        } catch (SQLException e) {
            System.err.println("Table '" + tableName + "' does not exist in the database.");
            error = true;
        }
        if (error) {
            System.err.println("Please rebuild the database. Exiting");
            System.exit(-1);
        }
    }
}
