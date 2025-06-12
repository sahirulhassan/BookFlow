package pk.edu.uitu.bookflow;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String url = "jdbc:sqlite:bookflow.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }
}
