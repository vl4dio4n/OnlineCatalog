package DataUtils;

import Application.Colors;
import Faculty.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StudentService {
    private final Connection connection;
    private static StudentService studentService;
    private StudentService(Connection connection) {
        this.connection = connection;
    }

    public static StudentService getStudentService(Connection connection) {
        if(studentService == null)
            studentService = new StudentService(connection);
        return studentService;
    }

    public void createStudent(Student student) {
        GroupsService groupsService = GroupsService.getGroupsService(null);
        UsersService usersService = UsersService.getUsersService(null);

        int groupId = groupsService.getGroupId(student.getGroup().getName(), student.getGroup().getSeriesName(), student.getGroup().getAcademicYear());
        if(groupId == -1){
            System.out.println(Colors.ANSI_RED + "Group or series doesn't exit!" + Colors.ANSI_RESET);
            return;
        }

        usersService.createUser(student.getUsername(), student.getEncryptedPassword(), "student");
        int userId = usersService.getUserId(student.getUsername());

        String sql = "INSERT INTO \"Students\"(student_id, first_name, last_name, registration_number) VALUES(?, ?, ?, ?);";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getLastName());
            pstmt.setString(4, student.getRegistrationNumber());
            pstmt.execute();
            pstmt.close();

        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to create new student. Keep Calm !!!" + Colors.ANSI_RESET);
        }

        sql = "INSERT INTO \"StudentGroups\"(stduent_id, group_id) VALUES(?, ?);";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, groupId);
            pstmt.execute();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to create StudentGroups entry. Keep Calm !!!" + Colors.ANSI_RESET);
        }

        sql =
            "SELECT tgs.subject_id, tgs.semester, s.academic_year\n" +
            "FROM \"TeacherGroupSubject\" tgs\n" +
            "JOIN \"Groups\" g ON g.group_id = tgs.group_id\n" +
            "JOIN \"Series\" s ON s.series_id = g.series_id\n" +
            "WHERE tgs.group_id = ?;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                String insertStmt = "INSERT INTO \"StudentSubject\"(student_id, subject_id, academic_year, semester) VALUES (?, ?, ?, ?);";
                try {
                    PreparedStatement insertPstmt = connection.prepareStatement(insertStmt);
                    insertPstmt.setInt(1, userId);
                    insertPstmt.setInt(2, rs.getInt("subject_id"));
                    insertPstmt.setString(3, rs.getString("academic_year"));
                    insertPstmt.setInt(4, rs.getInt("semester"));
                    insertPstmt.execute();
                    insertPstmt.close();
                } catch (SQLException e) {
                    System.out.println(Colors.ANSI_RED + "!!! Failed to create StudentSubject entry. Keep Calm !!!" + Colors.ANSI_RESET);
                }
            }
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get subjects for specific group. Keep Calm !!!" + Colors.ANSI_RESET);
        }
    }

    public Set<Student> getStudentsByGroupId(int groupId){
        GroupsService groupsService = GroupsService.getGroupsService(null);

        Set<Student> students = new HashSet<>();

        Group group = groupsService.getGroupById(groupId);
        if (group == null)
            return null;

        String sql =
                "SELECT u.user_id, u.username, u.password, s.first_name, s.last_name, s.registration_number\n" +
                "FROM \"Users\" u\n" +
                "JOIN \"Students\" s ON s.student_id = u.user_id\n" +
                "JOIN \"StudentGroups\" sg ON sg.stduent_id = s.student_id\n" +
                "WHERE sg.group_id = ?;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                Student student = new Student(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setRegistrationNumber(rs.getString("registration_number"));
                student.setGroup(group);
                students.add(student);
            }
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get students. Keep Calm !!!" + Colors.ANSI_RESET);
        }

        return students;
    }

    public void addSubject2Student(Student student, SubjectTeacher subject){
        SubjectsService subjectsService = SubjectsService.getSubjectsService(null);

        int subjectId = subjectsService.getSubjectId(subject.getName());

        String sql = "INSERT INTO \"StudentSubject\"(student_id, subject_id, academic_year, semester) VALUES (?, ?, ?, ?);";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, student.getUserId());
            pstmt.setInt(2, subjectId);
            pstmt.setString(3, subject.getGroup().getAcademicYear());
            pstmt.setInt(4, subject.getSemester());
            pstmt.execute();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to create StudentSubject entry. Keep Calm !!!" + Colors.ANSI_RESET);
        }

    }

    public TreeMap<String, List<SubjectStudent>> getStudentSubjects(int studentId) {
        TreeMap<String, List<SubjectStudent>> subjectsMap = new TreeMap<>();

        String sql =
                "SELECT g.group_id, g.group_name, s.series_name, s.academic_year, tgs.study_year, ss.semester, ss.mark, sb.subject_id, sb.subject_name, sb.credits\n" +
                "FROM \"StudentSubject\" ss \n" +
                "JOIN \"Students\" st ON st.student_id = ss.student_id\n" +
                "JOIN \"StudentGroups\" sg ON sg.stduent_id = st.student_id\n" +
                "JOIN \"Groups\" g ON g.group_id = sg.group_id\n" +
                "JOIN \"Series\" s ON s.series_id = g.series_id\n" +
                "JOIN \"TeacherGroupSubject\" tgs ON tgs.subject_id = ss.subject_id AND tgs.group_id = g.group_id\n" +
                "JOIN \"Subjects\" sb ON sb.subject_id = ss.subject_id\n" +
                "WHERE ss.student_id = ?;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                int subjectId = rs.getInt("subject_id");
                String subjectName = rs.getString("subject_name");
                int credits = rs.getInt("credits");
                Group group = new Group(rs.getInt("group_id"), rs.getString("group_name"), rs.getString("series_name"), rs.getString("academic_year"));
                int semester = rs.getInt("semester");
                int studyYear = rs.getInt("study_year");

                SubjectStudent subject = new SubjectStudent(subjectId, subjectName, credits, group, semester, studyYear);
                subject.setMark(rs.getInt("mark"));

                String key = studyYear + "_" + semester;

                if(!subjectsMap.containsKey(key))
                    subjectsMap.put(key, new ArrayList<>());
                subjectsMap.get(key).add(subject);

            }
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get student subjects. Keep Calm !!!" + Colors.ANSI_RESET);
        }

        return subjectsMap;
    }

    public void setMark(int studentId, String subjectName, int mark) {
        SubjectsService subjectsService = SubjectsService.getSubjectsService(null);

        int subjectId = subjectsService.getSubjectId(subjectName);

        String sql = "UPDATE \"StudentSubject\" SET mark = ? WHERE student_id = ? AND subject_id = ?;";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, mark);
            pstmt.setInt(2, studentId);
            pstmt.setInt(3, subjectId);
            pstmt.execute();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to update student's mark. Keep Calm !!!" + Colors.ANSI_RESET);
        }
    }

    public int getMark(int studentId, String subjectName){
        SubjectsService subjectsService = SubjectsService.getSubjectsService(null);
        int subjectId = subjectsService.getSubjectId(subjectName);

        String sql = "SELECT ss.mark FROM \"StudentSubject\" ss WHERE ss.student_id = ? AND ss.subject_id = ?;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next())
                return rs.getInt("mark");

            return -1;
        } catch (SQLException e) {
            System.out.println(Colors.ANSI_RED + "!!! Failed to get student's mark. Keep Calm !!!" + Colors.ANSI_RESET);
            return -1;
        }

    }
}
