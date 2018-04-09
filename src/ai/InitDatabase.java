package ai;

import game.Move;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InitDatabase {
    static final String JDBC_URL = "jdbc:derby:lookupDB;create=true";
    static HashMap<Long, Move> map = new HashMap<>();
    static ArrayList<Long> keys = new ArrayList<>();

    public static void main(String args[]) throws SQLException {
        fillMap();
        long startTime = System.currentTimeMillis();
        Connection conn = getConnection();
        System.out.println("TIME SPENT CONNECTING: " + (System.currentTimeMillis() - startTime));

        for(int i = 1; i <= 10; i++) {
        String tableName = "plays_" + i;
        conn.createStatement().execute("drop table " + tableName);
        conn.createStatement().execute("create table " + tableName +
                "(id bigint primary key, oldRow smallint, oldCol smallint, newRow smallint, newCol smallint, team smallint)");
        }

        getCurrentDBTables(conn);
    }


    private static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(
                JDBC_URL);
        System.out.println("Connected to database");
        return conn;
    }

    private static void fillTable(Connection conn) throws SQLException {
        System.out.println("INSERTING VALUES INTO DB");
        String tableName = "testTable";
        conn.createStatement().execute("truncate table " + tableName);

        PreparedStatement stmt = conn.prepareStatement("insert into " + tableName + " values (?, ?, ?, ?, ?, ?)");
        final int batchSize = 1000;
        int count = 0;
        for (Map.Entry<Long, Move> entry : map.entrySet()) {
            Long key = entry.getKey();
            Move value = entry.getValue();

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
        System.out.println("Table filled successfully");
    }

    private static Move queryData(Connection conn, Long key) throws SQLException {
        Move move = null;
        String tableName = "testTable";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("select oldRow, oldCol, newRow, newCol, team from " + tableName + " where id=" + key);
        while (resultSet.next()) {
            move = new Move(resultSet.getInt(1), resultSet.getInt(2),
                    resultSet.getInt(3), resultSet.getInt(4), resultSet.getInt(5));
        }
        statement.close();
        //conn.close();
        return move;
    }

    private static void fillMap() {
        Random r = new Random();
        r.setSeed(0);
        for (int i = 0; i < 2000; i++) {
            long rLong = (long) (r.nextDouble() * Long.MAX_VALUE);
            keys.add(rLong);
            map.put(rLong, new Move(-1, -1, -1, -1, 2));
        }
    }

    private static void getCurrentDBTables(Connection conn) throws SQLException {
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getTables(null, null, "%", null);
        while (rs.next()) {
            System.out.println(rs.getString(3));
        }
    }
}
