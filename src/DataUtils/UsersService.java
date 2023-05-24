package DataUtils;

import Faculty.Admin;
import Faculty.Student;
import Faculty.Teacher;
import Faculty.User;
import Application.Colors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersService {
    private final Connection connection;
    private static UsersService usersService;
    private UsersService(Connection connection) {
        this.connection = connection;
    }

    public static UsersService getUsersService(Connection connection) {
        if(usersService == null)
            usersService = new UsersService(connection);
        return usersService;
    }

    public User getUser(String username, String encryptedPassword){
        String sql = "SELECT u.user_id, u.username, u.password, u.role FROM \"Users\" u WHERE u.username = ? AND u.password = ?;";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, encryptedPassword);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                int userId = rs.getInt("user_id");
                String role = rs.getString("role");
                pstmt.close();

                return switch (role) {
                    case "admin" -> new Admin(userId, username, encryptedPassword);
                    case "student" -> new Student(userId, username, encryptedPassword);
                    default -> new Teacher(userId, username, encryptedPassword);
                };
            }

            pstmt.close();
            return null;

        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get user from DB. Keep Calm !!!" + Colors.ANSI_RESET);
            return null;
        }
    }

    public void createUser(String username, String encryptedPassword, String role){
        String sql = "INSERT INTO \"Users\"(username, password, role) VALUES (?, ?, ?);";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, encryptedPassword);
            pstmt.setString(3, role);
            pstmt.execute();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to create new user. Keep Calm !!!" + Colors.ANSI_RESET);
        }
    }

    public int getUserId(String username) {
        String sql = "SELECT u.user_id FROM \"Users\" u WHERE u.username = ?;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                return rs.getInt("user_id");
            }
            pstmt.close();
            return -1;
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get user id. Keep Calm !!!" + Colors.ANSI_RESET);
            return -1;
        }
    }

}
