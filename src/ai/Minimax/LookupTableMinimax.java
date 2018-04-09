package ai.Minimax;

import ai.AI;
import game.Logic;
import game.Move;
import game.State;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static misc.Globals.BLACK;
import static misc.Globals.RED;

public class LookupTableMinimax extends AI {
    private boolean useDB = true;
    private int CURR_MAX_DEPTH;
    private HashMap<Long, MinimaxPlay> lookupTable;

    private String JDBC_URL = "jdbc:derby:lookupDB;create=true";
    private Connection conn;

    public LookupTableMinimax(int team, State state, boolean overwriteDBTable) {
        super(team);
        lookupTable = new HashMap<>();
        if (useDB) {
            conn = null;
            try {
                conn = getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (overwriteDBTable) {
                System.out.println("Rebuilding lookup table. This will take some time.");
                buildLookupTable(state);
                try {
                    fillTable(state.getPointsToWin());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public Move makeMove(State state) {
        System.out.println("Finding best move");
        if (state.getLegalMoves().size() == 1) {
            return state.getLegalMoves().get(0);
        }
        // table lookup
        Node simNode = new Node(state);
        Move move = null;
        if (useDB) {
            try {
                move = queryData(simNode.getHashCode(), state.getPointsToWin());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            MinimaxPlay play = iterativeDeepeningMinimax(state);
            move = play.move;
            System.out.print("BEST PLAY SCORE: " + play.score + "   ");
        }
        if (move == null) {
            System.err.println("DB Table is empty!");
            System.exit(0);
        }
        System.out.println("BEST MOVE:  " + "oldRow: " + move.oldRow +
                ", oldCol: " + move.oldCol + ", newRow: " + move.newRow + ", newCol: " + move.newCol);
        return move;
    }

    private void buildLookupTable(State state) {
        long startTime = System.currentTimeMillis();
        iterativeDeepeningMinimax(state);
        System.out.println("Lookup table successfully built. Time spent: " + (System.currentTimeMillis() - startTime));
    }

    private MinimaxPlay iterativeDeepeningMinimax(State state) {
        CURR_MAX_DEPTH = 25; //
        boolean done = false;
        MinimaxPlay play = null;
        while (!done) {
            Node simNode = new Node(state); // Start from fresh (Don't reuse previous game tree in new iterations)
            CURR_MAX_DEPTH += 2;
            int prevSize = lookupTable.size();
            play = minimax(simNode, CURR_MAX_DEPTH);
            System.out.println("CURRENT MAX DEPTH: " + CURR_MAX_DEPTH + ", TABLE SIZE: " + lookupTable.size());
            if (lookupTable.size() == prevSize) done = true;
        }
        return play;
    }

    public MinimaxPlay minimax(Node node, int depth) {
        Move bestMove = null;
        int bestScore = (node.getState().getTurn() == team) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int score;

        if (Logic.gameOver(node.getState()) || depth == 0) {
            return new MinimaxPlay(bestMove, heuristic(node.getState(), depth), depth);
        }
        MinimaxPlay transpoPlay = lookupTable.get(node.getHashCode());
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
            lookupTable.put(node.getHashCode(), new MinimaxPlay(bestMove, bestScore, depth));
        }
        return new MinimaxPlay(bestMove, bestScore, depth);
    }

    private int heuristic(State state, int depth) {
        // AI plays optimally
        int m = 2000;
        int n = (CURR_MAX_DEPTH - depth);
        int opponent = (team == RED) ? BLACK : RED;
        int winner = Logic.getWinner(state);

        if (winner == team) return m - n;
        else if (winner == opponent) return -(m - n);
        return 0;
    }

    private Connection getConnection() throws SQLException {
        System.out.println("Connecting to database. This might take some time");
        Connection conn = DriverManager.getConnection(
                JDBC_URL);
        System.out.println("Connection successful");
        return conn;
    }

    private void fillTable(int pointsToWin) throws SQLException {
        System.out.println("Inserting data into table. This will take some time");
        String tableName = "plays_" + pointsToWin;
        long startTime = System.currentTimeMillis();
        //conn.createStatement().execute("drop table " + tableName);
        //conn.createStatement().execute("create table " + tableName +
        //        "(id bigint primary key, oldRow smallint, oldCol smallint, newRow smallint, newCol smallint, team smallint)");
        conn.createStatement().execute("truncate table " + tableName);

        final int batchSize = 1000;
        int count = 0;
        PreparedStatement stmt = conn.prepareStatement("insert into " + tableName + " values (?, ?, ?, ?, ?, ?)");
        for (Map.Entry<Long, MinimaxPlay> entry : lookupTable.entrySet()) {
            Long key = entry.getKey();
            Move value = entry.getValue().move;
            stmt.setLong(1, key);
            stmt.setInt(2, value.oldRow);
            stmt.setInt(3, value.oldCol);
            stmt.setInt(4, value.newRow);
            stmt.setInt(5, value.newCol);
            stmt.setInt(6, value.team);

            stmt.addBatch();
            if (++count % batchSize == 0) {
                stmt.executeBatch();
            }
        }
        stmt.executeBatch();
        stmt.close();
        System.out.println("Data inserted successfully. Time spent: " + (System.currentTimeMillis() - startTime));
    }

    private Move queryData(Long key, int pointsToWin) throws SQLException {
        Move move = null;
        String tableName = "plays_" + pointsToWin;
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("select oldRow, oldCol, newRow, newCol, team from " + tableName + " where id=" + key);
        while (resultSet.next()) {
            move = new Move(resultSet.getInt(1), resultSet.getInt(2),
                    resultSet.getInt(3), resultSet.getInt(4), resultSet.getInt(5));
        }
        statement.close();
        return move;
    }

}

