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

public class SeriesService {
    private final Connection connection;
    private static SeriesService seriesService;

    private SeriesService(Connection connection) {
        this.connection = connection;
    }

    public static SeriesService getSeriesService(Connection connection) {
        if(seriesService == null)
            seriesService = new SeriesService(connection);
        return seriesService;
    }

    public void createSeries(Series series) {
        String sql = "INSERT INTO \"Series\"(series_name, academic_year) VALUES (?, ?);";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, series.getName());
            pstmt.setString(2, series.getAcademicYear());
            pstmt.execute();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to create new series. Keep Calm !!!" + Colors.ANSI_RESET);
        }
    }

    public Set<Series> getAllSeries() {
        String sql = "SELECT * FROM \"Series\" s ORDER BY s.series_id;";
        Set<Series> series = new HashSet<>();

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                series.add(new Series(rs.getInt("series_id"), rs.getString("series_name"), rs.getString("academic_year")));
            }

            pstmt.close();
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get all series. Keep Calm !!!" + Colors.ANSI_RESET);
        }

        return series;
    }

    public Series getSeries(String seriesName, String academicYear){
        GroupsService groupsService = GroupsService.getGroupsService(null);
        String sql = "SELECT s.series_id, s.series_name, s.academic_year FROM \"Series\" s WHERE s.series_name = ? AND s.academic_year = ?;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, seriesName);
            pstmt.setString(2, academicYear);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                Series series = new Series(rs.getInt("series_id"), rs.getString("series_name"), rs.getString("academic_year"));
                Set<Group> groups = groupsService.getGroupsBySeriesId(series.getSeriesId());

                for(Group group: groups){
                    group.setSeriesName(seriesName);
                    group.setAcademicYear(academicYear);
                    series.addGroup(group);
                }

                pstmt.close();
                return series;
            }

            pstmt.close();
            return null;
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get series. Keep Calm !!!" + Colors.ANSI_RESET);
            return null;
        }
    }

    public void deleteSeriesById(int seriesId){
        String sql = "DELETE FROM \"Series\" WHERE series_id = ?;";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, seriesId);
            pstmt.execute();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to delete series by id. Keep Calm !!!" + Colors.ANSI_RESET);
        }
    }
}
