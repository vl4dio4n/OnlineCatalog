import Application.Application;
import Application.Colors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    public static Connection getConnection () {
        try {
            String url = "jdbc:postgresql://localhost/OnlineCatalog";
            String user = "vl4dio4n";
            String password = "Elena1973";

            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.printf(Colors.ANSI_RED + "Connection to database failed\n" + Colors.ANSI_RESET);
            return null;
        }
    }
    public static void main(String[] args) {
        Connection connection = Main.getConnection();

        Application app = Application.getApplication(connection);
        app.runner();

        try {
            assert connection != null;
            connection.close();
        } catch (SQLException e) {
            System.out.printf(Colors.ANSI_RED + "Failed to close database connection\n" + Colors.ANSI_RESET);
        }
    }
}