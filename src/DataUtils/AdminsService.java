package DataUtils;

import Application.Colors;
import Faculty.Admin;
import Faculty.Series;
import Faculty.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class AdminsService {
    private final Connection connection;
    private static AdminsService adminsService;

    private AdminsService(Connection connection) {
        this.connection = connection;
    }

    public static AdminsService getAdminsService(Connection connection) {
        if(adminsService == null)
            adminsService = new AdminsService(connection);
        return adminsService;
    }

    public void createAdmin(Admin admin) {
        UsersService usersService = UsersService.getUsersService(null);

        usersService.createUser(admin.getUsername(), admin.getEncryptedPassword(), "admin");
        int userId = usersService.getUserId(admin.getUsername());

        String sql = "INSERT INTO \"Administrators\"(admin_id, first_name, last_name) VALUES(?, ?, ?);";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, admin.getFirstName());
            pstmt.setString(3, admin.getLastName());
            pstmt.execute();
            pstmt.close();

        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to create new admin. Keep Calm !!!" + Colors.ANSI_RESET);
        }
    }

    public Set<Admin> getAllAdmins(){
        Set<Admin> admins = new HashSet<>();
        String sql = "SELECT u.username, u.user_id, u.password, a.first_name, a.last_name FROM \"Administrators\" a JOIN \"Users\" u ON u.user_id = a.admin_id;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                Admin admin = new Admin(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"));
                admin.setFirstName(rs.getString("first_name"));
                admin.setLastName(rs.getString("last_name"));
                admins.add(admin);
            }
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get admins. Keep Calm !!!" + Colors.ANSI_RESET);
        }

        return admins;
    }
}
