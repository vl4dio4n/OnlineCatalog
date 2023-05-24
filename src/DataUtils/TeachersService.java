package DataUtils;

import Application.Colors;
import Faculty.Admin;
import Faculty.SubjectTeacher;
import Faculty.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class TeachersService {
    private final Connection connection;
    private static TeachersService teachersService;

    private TeachersService(Connection connection) {
        this.connection = connection;
    }

    public static TeachersService getTeachersService(Connection connection) {
        if(teachersService == null)
            teachersService = new TeachersService(connection);
        return teachersService;
    }

    public void createTeacher(Teacher teacher) {
        UsersService usersService = UsersService.getUsersService(null);

        usersService.createUser(teacher.getUsername(), teacher.getEncryptedPassword(), "teacher");
        int userId = usersService.getUserId(teacher.getUsername());

        String sql = "INSERT INTO \"Teachers\"(teacher_id, first_name, last_name, dob) VALUES(?, ?, ?, ?);";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, teacher.getFirstName());
            pstmt.setString(3, teacher.getLastName());
            pstmt.setString(4, teacher.getDob());
            pstmt.execute();
            pstmt.close();

        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to create new teacher. Keep Calm !!!" + Colors.ANSI_RESET);
        }
    }

    public Set<Teacher> getAllTeachers(){
        Set<Teacher> teachers = new HashSet<>();
        String sql = "SELECT u.username, u.user_id, u.password, t.first_name, t.last_name, t.dob FROM \"Teachers\" t JOIN \"Users\" u ON u.user_id = t.teacher_id;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                Teacher teacher = new Teacher(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"));
                teacher.setFirstName(rs.getString("first_name"));
                teacher.setLastName(rs.getString("last_name"));
                teacher.setDob(rs.getString("dob"));
                teachers.add(teacher);
            }
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get teachers. Keep Calm !!!" + Colors.ANSI_RESET);
        }

        return teachers;
    }

    public void addSubject2Teacher(SubjectTeacher subject, int teacherId){
        GroupsService groupsService = GroupsService.getGroupsService(null);
        SubjectsService subjectsService = SubjectsService.getSubjectsService(null);

        int groupId = groupsService.getGroupId(subject.getGroup().getName(), subject.getGroup().getSeriesName(), subject.getGroup().getAcademicYear());
        int subjectId = subjectsService.getSubjectId(subject.getName());

        String sql = "INSERT INTO \"TeacherGroupSubject\" (teacher_id, group_id, subject_id, semester, study_year) VALUES (?, ?, ?, ?, ?);";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, teacherId);
            pstmt.setInt(2, groupId);
            pstmt.setInt(3, subjectId);
            pstmt.setInt(4, subject.getSemester());
            pstmt.setInt(5, subject.getStudyYear());
            pstmt.execute();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(Colors.ANSI_RED + "!!! Failed to create new entry in TeacherGroupSubject. Keep Calm !!!" + Colors.ANSI_RESET);
        }
    }

    public Teacher getTeacherById(int teacherId){
        SubjectsService subjectsService = SubjectsService.getSubjectsService(null);

        String sql =
                "SELECT u.user_id, u.username, u.password, t.first_name, t.last_name, t.dob\n" +
                "FROM \"Users\" u\n" +
                "JOIN \"Teachers\" t ON t.teacher_id = u.user_id\n" +
                "WHERE t.teacher_id = ?;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                Teacher teacher = new Teacher(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"));
                teacher.setFirstName(rs.getString("first_name"));
                teacher.setLastName(rs.getString("last_name"));
                teacher.setDob(rs.getString("dob"));

                Set<SubjectTeacher> subjects = subjectsService.getSubjectsTeacher(teacherId);
                for(SubjectTeacher subject: subjects)
                    teacher.addSubject(subject);
                return teacher;
            }
            return null;
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get teacher by id. Keep Calm !!!" + Colors.ANSI_RESET);
        }
        return null;
    }
}
