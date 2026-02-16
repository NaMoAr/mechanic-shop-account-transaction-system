import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DbClient {
    private final Connection connection;

    public DbClient(String dbName, String dbPort, String user, String password) throws SQLException {
        String url = "jdbc:postgresql://localhost:" + dbPort + "/" + dbName;
        this.connection = DriverManager.getConnection(url, user, password);
    }

    public Connection getConnection() {
        return connection;
    }

    public int executeUpdate(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            return stmt.executeUpdate(sql);
        }
    }

    public int executeUpdate(String sql, SqlConsumer<PreparedStatement> binder) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            binder.accept(stmt);
            return stmt.executeUpdate();
        }
    }

    public int executeQueryCount(String sql, SqlConsumer<PreparedStatement> binder) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            binder.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                }
                return count;
            }
        }
    }

    public List<List<String>> executeQuery(String sql, SqlConsumer<PreparedStatement> binder) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            binder.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                return readResult(rs);
            }
        }
    }

    public int printQuery(String sql, SqlConsumer<PreparedStatement> binder) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            binder.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columns = rsmd.getColumnCount();
                int rows = 0;
                boolean header = true;

                while (rs.next()) {
                    if (header) {
                        for (int i = 1; i <= columns; i++) {
                            System.out.print(rsmd.getColumnName(i) + "\t");
                        }
                        System.out.println();
                        header = false;
                    }
                    for (int i = 1; i <= columns; i++) {
                        System.out.print(rs.getString(i) + "\t");
                    }
                    System.out.println();
                    rows++;
                }
                return rows;
            }
        }
    }

    public int nextSequenceValue(String sequenceName) throws SQLException {
        String sql = "SELECT nextval(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, sequenceName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Could not read sequence: " + sequenceName);
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }

    private List<List<String>> readResult(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        List<List<String>> rows = new ArrayList<List<String>>();

        while (rs.next()) {
            List<String> record = new ArrayList<String>();
            for (int i = 1; i <= columns; i++) {
                record.add(rs.getString(i));
            }
            rows.add(record);
        }
        return rows;
    }
}
