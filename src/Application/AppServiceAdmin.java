package Application;

import DataUtils.*;
import Faculty.*;

import java.util.*;

public non-sealed class AppServiceAdmin extends AppService{
    private static Map<String, String> welcomeOptions;

    static {
        welcomeOptions = new HashMap<>();
        welcomeOptions.put("1", "add-student");
        welcomeOptions.put("2", "add-teacher");
        welcomeOptions.put("3", "add-admin");
        welcomeOptions.put("4", "add-subject");
        welcomeOptions.put("5", "add-subject-to-teacher");
        welcomeOptions.put("6", "create-group");
        welcomeOptions.put("7", "create-series");
        welcomeOptions.put("8", "show-admins");
        welcomeOptions.put("9", "show-teachers");
        welcomeOptions.put("10", "show-series");
        welcomeOptions.put("quit", "quit");
        welcomeOptions.put("back", "back");
    }

    public static String welcome(){
        System.out.println(Colors.ANSI_BLUE + "What action do you want to take?" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_BLUE + "     1. Add Student" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_BLUE + "     2. Add Teacher" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_BLUE + "     3. Add Administrator" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_BLUE + "     4. Add Subject" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_BLUE + "     5. Add Subject to Teacher" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_BLUE + "     6. Create Group" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_BLUE + "     7. Create Series" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_BLUE + "     8. Show Administrators" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_BLUE + "     9. Show Teachers" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_BLUE + "    10. Show Series\n" + Colors.ANSI_RESET);

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Option: " + Colors.ANSI_RESET);
            Scanner scanner = new Scanner(System.in);
            String option = scanner.nextLine();
            if(welcomeOptions.containsKey(option)){
                return welcomeOptions.get(option);
            } else {
                System.out.println(Colors.ANSI_RED + "Invalid option. Please choose one from the above." + Colors.ANSI_RESET);
            }
        }
    }


    public static String createSeries(){
        SeriesService seriesService = SeriesService.getSeriesService(null);

        Scanner scanner = new Scanner(System.in);
        Set<Series> series = seriesService.getAllSeries();

        System.out.println(Colors.ANSI_BLUE + "\nCreate Series" + Colors.ANSI_RESET);

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Name: " + Colors.ANSI_RESET);
            String seriesName = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Academic Year: " + Colors.ANSI_RESET);
            String academicYear = scanner.nextLine();

            Series newSeries = new Series(-1, seriesName, academicYear);

            if(series.contains(newSeries) ){
                System.out.println(Colors.ANSI_RED + "The series name for this academic year is already used" + Colors.ANSI_RESET);
                System.out.println(Colors.ANSI_RED + "Please choose another name or change the year\n" + Colors.ANSI_RESET);
            } else {
                System.out.println(Colors.ANSI_BLUE + "Series created successfully!\n" + Colors.ANSI_RESET);
                seriesService.createSeries(newSeries);
                return "back";
            }
        }
    }

    public static String showSeries(){
        SeriesService seriesService = SeriesService.getSeriesService(null);

        Scanner scanner = new Scanner(System.in);

        Set<Series> series = seriesService.getAllSeries();
        List<Series> sortedSeries = new ArrayList<>(series);
        Collections.sort(sortedSeries);

        Map<Integer, Series> map = new HashMap<>();

        System.out.println(Colors.ANSI_BLUE + "\nShow Series" + Colors.ANSI_RESET);

        String[] headers = {"Id", "Name", "Academic Year"};
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, headers);
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);

        for (Series s: sortedSeries) {
            System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, s.getSeriesId(), s.getName(), s.getAcademicYear());
            map.put(s.getSeriesId(), s);
        }

        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_BLUE + "Enter the id of a series to see the groups associated to it, or type back/quit" + Colors.ANSI_RESET);

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Option: " + Colors.ANSI_RESET);
            try {
                String input = scanner.nextLine();
                if(input.equals("quit") || input.equals("back"))
                    return input;
                int id = Integer.parseInt(input);
                if(id >= 1 && id <= map.size())
                    return "show-groups," + id;
                System.out.printf(Colors.ANSI_RED + "Please provide an id in range %d - %d\n" + Colors.ANSI_RESET, 1, map.size());
            } catch (NumberFormatException e){
                System.out.println(Colors.ANSI_RED + "Please provide a valid id" + Colors.ANSI_RESET);
            }
        }
    }

    public static String createGroup(){
        GroupsService groupsService = GroupsService.getGroupsService(null);
        SeriesService seriesService = SeriesService.getSeriesService(null);

        Scanner scanner = new Scanner(System.in);

        System.out.println(Colors.ANSI_BLUE + "\nCreate Group" + Colors.ANSI_RESET);

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Group Name: " + Colors.ANSI_RESET);
            String groupName = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Series Name: " + Colors.ANSI_RESET);
            String seriesName = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Academic Year: " + Colors.ANSI_RESET);
            String academicYear = scanner.nextLine();

            Group newGroup = new Group(-1, groupName, seriesName, academicYear);
            Series series = seriesService.getSeries(seriesName, academicYear);

            if(groupName.length() == 0){
                System.out.println(Colors.ANSI_RED + "Please provide a name for the group\n" + Colors.ANSI_RESET);
            } else if(series != null){
                Set<Group> groups = series.getGroups();
                if(groups.contains(newGroup)){
                    System.out.println(Colors.ANSI_RED + "The group name for this academic year and series is already used" + Colors.ANSI_RESET);
                    System.out.println(Colors.ANSI_RED + "Please choose another name, year or series\n" + Colors.ANSI_RESET);
                } else {
                    System.out.println(Colors.ANSI_BLUE + "Group created successfully!\n" + Colors.ANSI_RESET);
                    groupsService.createGroup(groupName, series.getSeriesId());
                    return "back";
                }
            } else {
                System.out.println(Colors.ANSI_RED + "Cannot create group because series doesn't exist" + Colors.ANSI_RESET);
                System.out.println(Colors.ANSI_RED + "Please associate the group with one of the existing series" + Colors.ANSI_RESET);
            }
        }
    }

    public static String showGroups(String seriesId){
        GroupsService groupsService = GroupsService.getGroupsService(null);

        Scanner scanner = new Scanner(System.in);
        Set<Group> groups = groupsService.getGroupsBySeriesId(Integer.parseInt(seriesId));
        Map<Integer, Group> map = new HashMap<>();

        System.out.println(Colors.ANSI_BLUE + "\nShow Groups" + Colors.ANSI_RESET);

        String[] headers = {"Id", "Group Name", "Series Name", "Academic Year"};
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, headers);
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);

        int counter = 0;
        for (Group g: groups) {
            counter ++;
            System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, counter, g.getName(), g.getSeriesName(), g.getAcademicYear());
            map.put(counter, g);
        }

        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_BLUE + "Enter the id of a group to see the students inside it, or type back/quit" + Colors.ANSI_RESET);

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Option: " + Colors.ANSI_RESET);
            try {
                String input = scanner.nextLine();
                if(input.equals("quit") || input.equals("back"))
                    return input;
                int id = Integer.parseInt(input);
                if(id >= 1 && id <= counter)
                    return "show-students," + map.get(id).getGroupId();
                System.out.printf(Colors.ANSI_RED + "Please provide an id in range %d - %d\n" + Colors.ANSI_RESET, 1, counter);
            } catch (NumberFormatException e){
                System.out.println(Colors.ANSI_RED + "Please provide a valid id" + Colors.ANSI_RESET);
            }
        }
    }

    public static String createSubject(){
        SubjectsService subjectsService = SubjectsService.getSubjectsService(null);

        Scanner scanner = new Scanner(System.in);
        Set<Subject> subjects = subjectsService.getAllSubjects();

        System.out.println(Colors.ANSI_BLUE + "\nCreate Subject" + Colors.ANSI_RESET);

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Subject Name: " + Colors.ANSI_RESET);
            String subjectName = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> No. of Credits: " + Colors.ANSI_RESET);
            try {
                int credits = Integer.parseInt(scanner.nextLine());
                Subject newSubject = new Subject(-1, subjectName, credits);
                if(subjects.contains(newSubject)){
                    System.out.println(Colors.ANSI_RED + "This subject already exists. Try a different name or number of credits.\n" + Colors.ANSI_RESET);
                } else {
                    System.out.println(Colors.ANSI_BLUE + "Subject created successfully!\n" + Colors.ANSI_RESET);
                    subjectsService.createSubject(newSubject);;
                    return "back";
                }
            } catch(NumberFormatException e){
                System.out.println(Colors.ANSI_RED + "Please provide a valid number!\n" + Colors.ANSI_RESET);
            }
        }
    }

    public static String createTeacher(){
        TeachersService teachersService = TeachersService.getTeachersService(null);

        Scanner scanner = new Scanner(System.in);
        System.out.println(Colors.ANSI_BLUE + "\nAdd Teacher" + Colors.ANSI_RESET);

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Email: " + Colors.ANSI_RESET);
            String email = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Password: " + Colors.ANSI_RESET);
            String encryptedPassword = PasswordEncryptionService.encryptPassword(scanner.nextLine());
            System.out.print(Colors.ANSI_PURPLE + ">>> First Name: " + Colors.ANSI_RESET);
            String firstName = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Last Name: " + Colors.ANSI_RESET);
            String lastName = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Date of Birth: " + Colors.ANSI_RESET);
            String dob = scanner.nextLine();

            if(email.length() == 0 || encryptedPassword.length() == 0 || firstName.length() == 0 || lastName.length() == 0 || dob.length() == 0){
                System.out.println(Colors.ANSI_RED + "Please provide every field with information!\n" + Colors.ANSI_RESET);
            } else {
                Teacher newTeacher = new Teacher(-1, email, encryptedPassword);
                newTeacher.setFirstName(firstName);
                newTeacher.setLastName(lastName);
                newTeacher.setDob(dob);

                teachersService.createTeacher(newTeacher);
                System.out.println(Colors.ANSI_BLUE + "Teacher added successfully!\n" + Colors.ANSI_RESET);
                return "back";
            }
        }
    }


    private static Set<Integer> showTeachersTable(){
        TeachersService teachersService = TeachersService.getTeachersService(null);

        Set<Teacher> teachers = teachersService.getAllTeachers();
        TreeMap<Integer, Teacher> teachersMap = new TreeMap<>();

        for(Teacher t: teachers)
            teachersMap.put(t.getUserId(), t);

        System.out.println(Colors.ANSI_BLUE + "\nShow Teachers" + Colors.ANSI_RESET);

        String[] headers = {"Id", "Email", "First Name", "Last Name", "Date of Birth"};
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, headers);
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);

        Set<Integer> ids = new HashSet<Integer>();
        for (Map.Entry<Integer, Teacher> entry: teachersMap.entrySet()) {
            int counter = entry.getKey();
            ids.add(counter);
            Teacher t = entry.getValue();
            System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, counter, t.getUsername(), t.getFirstName(), t.getLastName(), t.getDob());
        }

        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        return ids;
    }

    public static String showTeachers(){
        Scanner scanner = new Scanner(System.in);
        Set<Integer> ids = showTeachersTable();
        System.out.println(Colors.ANSI_BLUE + "Enter the id of a professor to see what subjects teaches or type back/quit" + Colors.ANSI_RESET);

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Option: " + Colors.ANSI_RESET);
            try {
                String input = scanner.nextLine();
                if(input.equals("quit") || input.equals("back"))
                    return input;
                int id = Integer.parseInt(input);
                if(ids.contains(id))
                    return "show-teacher," + id;
                System.out.printf(Colors.ANSI_RED + "Please provide a valid id\n" + Colors.ANSI_RESET, 1);
            } catch (NumberFormatException e){
                System.out.println(Colors.ANSI_RED + "Please provide a valid id" + Colors.ANSI_RESET);
            }
        }
    }

    public static String addSubject2Teacher(){
        TeachersService teachersService = TeachersService.getTeachersService(null);
        GroupsService groupsService = GroupsService.getGroupsService(null);
        StudentService studentService = StudentService.getStudentService(null);

        System.out.println(Colors.ANSI_BLUE + "\nEnter the id of a professor to add a subject to it" + Colors.ANSI_RESET);

        Scanner scanner = new Scanner(System.in);
        Set<Integer> ids = showTeachersTable();

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Teacher Id: " + Colors.ANSI_RESET);
            try {
                String input = scanner.nextLine();
                if(input.equals("quit") || input.equals("back"))
                    return input;
                int id = Integer.parseInt(input);
                if(ids.contains(id)){
                    System.out.print(Colors.ANSI_PURPLE + ">>> Subject Name: " + Colors.ANSI_RESET);
                    String subjectName = scanner.nextLine();
                    System.out.print(Colors.ANSI_PURPLE + ">>> No. of Credits: " + Colors.ANSI_RESET);
                    String creditsStr = scanner.nextLine();
                    System.out.print(Colors.ANSI_PURPLE + ">>> Group Name: " + Colors.ANSI_RESET);
                    String groupName = scanner.nextLine();
                    System.out.print(Colors.ANSI_PURPLE + ">>> Series Name: " + Colors.ANSI_RESET);
                    String seriesName = scanner.nextLine();
                    System.out.print(Colors.ANSI_PURPLE + ">>> Academic Year: " + Colors.ANSI_RESET);
                    String academicYear = scanner.nextLine();
                    System.out.print(Colors.ANSI_PURPLE + ">>> Study Year: " + Colors.ANSI_RESET);
                    String studyYearStr = scanner.nextLine();
                    System.out.print(Colors.ANSI_PURPLE + ">>> Semester: " + Colors.ANSI_RESET);
                    String semesterStr = scanner.nextLine();
                    try{
                        int credits = Integer.parseInt(creditsStr);
                        int studyYear = Integer.parseInt(studyYearStr);
                        int semester = Integer.parseInt(semesterStr);

                        SubjectTeacher subject = new SubjectTeacher(-1, subjectName, credits, new Group(-1, groupName, seriesName, academicYear), studyYear, semester);

                        teachersService.addSubject2Teacher(subject, id);

                        int groupId = groupsService.getGroupId(groupName, seriesName, academicYear);
                        Set<Student> students = studentService.getStudentsByGroupId(groupId);
                        for(Student student: students)
                            studentService.addSubject2Student(student, subject);

                        System.out.println(Colors.ANSI_BLUE + "Subject added to teacher successfully!\n" + Colors.ANSI_RESET);
                        return "back";
                    } catch (NumberFormatException e) {
                        System.out.println(Colors.ANSI_RED + "Check if the values introduced for credits, semester or study year are valid integers\n" + Colors.ANSI_RESET);
                    }
                } else {
                    System.out.printf(Colors.ANSI_RED + "Please provide a valid id\n" + Colors.ANSI_RESET);
                }
            } catch (NumberFormatException e){
                System.out.println(Colors.ANSI_RED + "Please provide a valid id\n" + Colors.ANSI_RESET);
            }
        }
    }

    public static String showTeacher(String teacherId){
        TeachersService teachersService = TeachersService.getTeachersService(null);

        Teacher teacher = teachersService.getTeacherById(Integer.parseInt(teacherId));

        System.out.printf(Colors.ANSI_BLUE + "\nShow Info of Teacher: %s %s\n" + Colors.ANSI_RESET, teacher.getFirstName(), teacher.getLastName());

        String[] headers = {"Id", "Subject Name", "Credits", "Group Name", "Series Name", "Academic Year", "Study Year", "Semester"};
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s | %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, headers);
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);

        int counter = 0;
        for (SubjectTeacher s: teacher.getSubjects()) {
            counter ++;
            System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s | %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET,
                    counter, s.getName(), s.getCredits(), s.getGroup().getName(), s.getGroup().getSeriesName(), s.getGroup().getAcademicYear(), s.getStudyYear(), s.getSemester());
        }

        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);

        return "back";
    }


    public static String createStudent(){
        StudentService studentService = StudentService.getStudentService(null);

        Scanner scanner = new Scanner(System.in);
        System.out.println(Colors.ANSI_BLUE + "\nAdd Student" + Colors.ANSI_RESET);

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Email: " + Colors.ANSI_RESET);
            String email = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Password: " + Colors.ANSI_RESET);
            String encryptedPassword = PasswordEncryptionService.encryptPassword(scanner.nextLine());
            System.out.print(Colors.ANSI_PURPLE + ">>> First Name: " + Colors.ANSI_RESET);
            String firstName = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Last Name: " + Colors.ANSI_RESET);
            String lastName = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Registration Number: " + Colors.ANSI_RESET);
            String registrationNumber = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Group Name: " + Colors.ANSI_RESET);
            String groupName = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Series Name: " + Colors.ANSI_RESET);
            String seriesName = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Academic Year: " + Colors.ANSI_RESET);
            String academicYear = scanner.nextLine();

            if(email.length() == 0 || encryptedPassword.length() == 0 || firstName.length() == 0 || lastName.length() == 0 || registrationNumber.length() == 0
                    || groupName.length() == 0 || seriesName.length() == 0 || academicYear.length() == 0){
                System.out.println(Colors.ANSI_RED + "Please provide every field with information!\n" + Colors.ANSI_RESET);
            } else {
                Student newStudent = new Student(-1, email, encryptedPassword);
                newStudent.setFirstName(firstName);
                newStudent.setLastName(lastName);
                newStudent.setRegistrationNumber(registrationNumber);
                newStudent.setGroup(new Group(-1, groupName, seriesName, academicYear));

                studentService.createStudent(newStudent);
                System.out.println(Colors.ANSI_BLUE + "Student added successfully!\n" + Colors.ANSI_RESET);
                return "back";
            }
        }
    }


    public static String showStudents(String groupId){
        GroupsService groupsService = GroupsService.getGroupsService(null);
        StudentService studentsService = StudentService.getStudentService(null);

        Set<Student> students = studentsService.getStudentsByGroupId(Integer.parseInt(groupId));
        Group group = groupsService.getGroupById(Integer.parseInt(groupId));

        System.out.printf(Colors.ANSI_BLUE + "\nShow Students from group: %s %s %s\n" + Colors.ANSI_RESET, group.getName(), group.getSeriesName(), group.getAcademicYear());

        String[] headers = {"Id", "Reg. Number", "Email", "First Name", "Last Name"};
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, headers);
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);

        int counter = 0;
        for (Student s: students) {
            counter ++;
            System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET,
                    counter, s.getRegistrationNumber(), s.getUsername(), s.getFirstName(), s.getLastName());
        }

        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        return "back";
    }

    public static String createAdmin(){
        AdminsService adminsService = AdminsService.getAdminsService(null);

        Scanner scanner = new Scanner(System.in);

        System.out.printf(Colors.ANSI_BLUE + "\nAdd a new Administrator\n");

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Email: " + Colors.ANSI_RESET);
            String email = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Password: " + Colors.ANSI_RESET);
            String encryptedPassword = PasswordEncryptionService.encryptPassword(scanner.nextLine());
            System.out.print(Colors.ANSI_PURPLE + ">>> First Name: " + Colors.ANSI_RESET);
            String firstName = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Last Name: " + Colors.ANSI_RESET);
            String lastName = scanner.nextLine();

            if(email.length() == 0 || encryptedPassword.length() == 0 || firstName.length() == 0 || lastName.length() == 0){
                System.out.println(Colors.ANSI_RED + "Please provide every field with information!\n" + Colors.ANSI_RESET);
            } else {
                Admin admin = new Admin(-1, email, encryptedPassword);
                admin.setFirstName(firstName);
                admin.setLastName(lastName);

                adminsService.createAdmin(admin);
                System.out.println(Colors.ANSI_BLUE + "Administrator added successfully!\n" + Colors.ANSI_RESET);
                return "back";
            }
        }
    }

    public static String showAdmins(){
        AdminsService adminsService = AdminsService.getAdminsService(null);
        Set<Admin> admins = adminsService.getAllAdmins();

        System.out.printf(Colors.ANSI_BLUE + "\nShow Administrators\n" + Colors.ANSI_RESET);

        String[] headers = {"Id", "Email", "First Name", "Last Name"};
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, headers);
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);

        int counter = 0;
        for (Admin a: admins) {
            counter ++;
            System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET,
                    counter, a.getUsername(), a.getFirstName(), a.getLastName());
        }

        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        return "back";
    }
}
