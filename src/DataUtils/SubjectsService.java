package DataUtils;

import Application.Colors;
import Faculty.Group;
import Faculty.Series;
import Faculty.Subject;
import Faculty.SubjectTeacher;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class SubjectsService {

    private final Connection connection;
    private static SubjectsService subjectsService;

    private SubjectsService(Connection connection) {
        this.connection = connection;
    }

    public static SubjectsService getSubjectsService(Connection connection) {
        if(subjectsService == null)
            subjectsService = new SubjectsService(connection);
        return subjectsService;
    }

    public Set<Subject> getAllSubjects(){
        String sql = "SELECT * FROM \"Subjects\";";

        Set<Subject> subjects = new HashSet<>();

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                subjects.add(new Subject(rs.getInt("subject_id"), rs.getString("subject_name"), rs.getInt("credits")));
            }

            pstmt.close();
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get all subjects. Keep Calm !!!" + Colors.ANSI_RESET);
        }

        return subjects;
    }

    public void createSubject(Subject subject) {
        String sql = "INSERT INTO \"Subjects\"(subject_name, credits) VALUES (?, ?);";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, subject.getName());
            pstmt.setInt(2, subject.getCredits());
            pstmt.execute();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to create new subject. Keep Calm !!!" + Colors.ANSI_RESET);
        }
    }

    public int getSubjectId(String subjectName){
        String sql = "SELECT s.subject_id FROM \"Subjects\" s WHERE s.subject_name = ?;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, subjectName);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next())
                return rs.getInt("subject_id");

            return -1;
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get subject id. Keep Calm !!!" + Colors.ANSI_RESET);
            return -1;
        }
    }

    public Set<SubjectTeacher> getSubjectsTeacher(int teacherId){
        Set<SubjectTeacher> subjects = new HashSet<>();

        String sql =
                "SELECT g.group_id, g.group_name, s.series_name, s.academic_year, tgs.semester, tgs.study_year, sb.subject_id, sb.subject_name, sb.credits\n" +
                "FROM \"TeacherGroupSubject\" tgs\n" +
                "JOIN \"Subjects\" sb ON tgs.subject_id = sb.subject_id\n" +
                "JOIN \"Groups\" g ON tgs.group_id = g.group_id\n" +
                "JOIN \"Series\" s ON g.series_id = s.series_id\n" +
                "WHERE tgs.teacher_id = ?;";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                int subjectId = rs.getInt("subject_id");
                String subjectName = rs.getString("subject_name");
                int credits = rs.getInt("credits");
                Group group = new Group(rs.getInt("group_id"), rs.getString("group_name"), rs.getString("series_name"), rs.getString("academic_year"));
                int semester = rs.getInt("semester");
                int studyYear = rs.getInt("study_year");

                SubjectTeacher subject = new SubjectTeacher(subjectId, subjectName, credits, group, semester, studyYear);
                subjects.add(subject);
            }
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get subject id. Keep Calm !!!" + Colors.ANSI_RESET);
        }

        return subjects;
    }

}
