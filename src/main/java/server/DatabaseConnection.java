package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private final String jdbUrl = "jdbc:postgresql://localhost/OOPP";
    private final String username = "postgres";
    private final String password = "postgres";
    private Connection conn = null;

    public static void main(String[] args) {
        DatabaseConnection connection = new DatabaseConnection();
        connection.connect();
    }

    /**
     * This method makes a connection with the database.
     *
     * @return the connection with the database
     */
    public Connection connect() {
        try {

            //Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection(jdbUrl, username, password);
            System.out.println("Database connection established");
            // return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }


}

