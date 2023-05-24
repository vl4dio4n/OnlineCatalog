package DataUtils;

import Application.Colors;
import Faculty.Group;
import Faculty.Series;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class GroupsService {
    private final Connection connection;
    private static GroupsService groupsService;

    private GroupsService(Connection connection) {
        this.connection = connection;
    }

    public static GroupsService getGroupsService(Connection connection) {
        if(groupsService == null)
            groupsService = new GroupsService(connection);
        return groupsService;
    }

    public void createGroup(String groupName, int seriesId) {
        String sql = "INSERT INTO \"Groups\"(series_id, group_name) VALUES (?, ?);";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, seriesId);
            pstmt.setString(2, groupName);
            pstmt.execute();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to create new group. Keep Calm !!!" + Colors.ANSI_RESET);
        }
    }

    public Set<Group> getGroupsBySeriesId(int seriesId) {
        String sql = "SELECT g.group_id, g.series_id, g.group_name, s.series_name, s.academic_year FROM \"Groups\" g JOIN \"Series\" s ON s.series_id = g.series_id WHERE g.series_id = ?;";
        Set<Group> groups = new HashSet<>();

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, seriesId);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                groups.add(new Group(rs.getInt("group_id"), rs.getString("group_name"), rs.getString("series_name"),  rs.getString("academic_year")));
            }

            pstmt.close();
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get groups by series id. Keep Calm !!!" + Colors.ANSI_RESET);
        }

        return groups;
    }


    public int getGroupId(String groupName, String seriesName, String academicYear) {
        String sql = "SELECT g.group_id FROM \"Groups\" g JOIN \"Series\" s ON s.series_id = g.series_id WHERE g.group_name = ? AND s.series_name = ? AND s.academic_year = ?;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, groupName);
            pstmt.setString(2, seriesName);
            pstmt.setString(3, academicYear);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                return rs.getInt("group_id");
            }
            pstmt.close();
            return -1;
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get group id. Keep Calm !!!" + Colors.ANSI_RESET);
            return -1;
        }
    }

    public Group getGroupById(int groupId){
        String sql =
                "SELECT g.group_id, g.group_name, s.series_name, s.academic_year\n" +
                "FROM \"Groups\" g\n" +
                "JOIN \"Series\" s ON s.series_id = g.group_id\n" +
                "WHERE g.group_id = ?;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next())
                return new Group(rs.getInt("group_id"), rs.getString("group_name"), rs.getString("series_name"),  rs.getString("academic_year"));

            return null;
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get group by id. Keep Calm !!!" + Colors.ANSI_RESET);
            return null;
        }
    }

}
