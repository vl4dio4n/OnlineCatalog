package Application;

import Faculty.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

    private static int getNextId(String path){
        int id = 0;
        File dir = new File(path);
        File[] files = dir.listFiles();
        if(files != null){
            for(File file: files){
                id = Math.max(id, Integer.parseInt(file.getName()));
            }
        }
        return id + 1;
    }

    private static void createFile(String path){
        File file = new File(path);
        if(!file.exists()){
            try{
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.flush();
                fos.close();
            } catch (IOException ignored) {}
        }
    }

    private static void createDir(String path){
        File folder = new File(path);
        folder.mkdirs();
    }

    private static void addUser(User user, String type){
        try{
            Files.write(Paths.get("resources/credentials.csv"), (user.getEmail() + "," + user.getEncryptedPassword() + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ignored){}

        try{
            Files.write(Paths.get("resources/users-type.csv"), (user.getEmail() + "," + type + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ignored){}
    }

    private static Set<Series> getSeries(){
        Set<Series> series = new HashSet<>();

        try(Scanner scanner = new Scanner(new File("resources/series.csv"))){
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();

                Scanner rowScanner = new Scanner(line);
                rowScanner.useDelimiter(",");

                String seriesName = rowScanner.next();
                String academicYear = rowScanner.next();

                Series newSeries = new Series(seriesName, academicYear);

                while(rowScanner.hasNext()){
                    String groupName = rowScanner.next().replace("\"", "");
                    if(groupName.length() > 0){
                        newSeries.addGroup(new Group(groupName, seriesName, academicYear));
                    }
                }

                series.add(newSeries);
            }
        } catch (FileNotFoundException e){
            return null;
        }

        return series;
    }

    private static void addSeries(Series series){
        try{
            Files.write(Paths.get("resources/series.csv"), (series.getName() + "," + series.getAcademicYear() + ",\"\"\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ignored){}
    }

    private static void addGroup(Set<Series> series, Group group){
        StringBuilder sb = new StringBuilder();
        for(Series s: series){
            sb.append(s.toString());
        }

        try{
            Files.write(Paths.get("resources/series.csv"), sb.toString().getBytes(), StandardOpenOption.WRITE);
        } catch (IOException ignored){}

        try{
            Files.write(Paths.get("resources/groups.csv"), group.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ignored){}
    }

    public static String createSeries(){
        Scanner scanner = new Scanner(System.in);
        Set<Series> series = getSeries();

        System.out.println(Colors.ANSI_BLUE + "\nCreate Series" + Colors.ANSI_RESET);

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Name: " + Colors.ANSI_RESET);
            String seriesName = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Academic Year: " + Colors.ANSI_RESET);
            String academicYear = scanner.nextLine();

            Series newSeries = new Series(seriesName, academicYear);

            if(series.contains(newSeries) ){
                System.out.println(Colors.ANSI_RED + "The series name for this academic year is already used" + Colors.ANSI_RESET);
                System.out.println(Colors.ANSI_RED + "Please choose another name or change the year\n" + Colors.ANSI_RESET);
            } else {
                System.out.println(Colors.ANSI_BLUE + "Series created successfully!\n" + Colors.ANSI_RESET);
                addSeries(newSeries);
                return "back";
            }
        }
    }

    public static String showSeries(){
        Scanner scanner = new Scanner(System.in);
        Set<Series> series = getSeries();
        Map<Integer, Series> map = new HashMap<>();

        System.out.println(Colors.ANSI_BLUE + "\nShow Series" + Colors.ANSI_RESET);

        String[] headers = {"Id", "Name", "Academic Year"};
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, headers);
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);

        int counter = 0;
        for (Series s: series) {
            counter ++;
            System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, counter, s.getName(), s.getAcademicYear());
            map.put(counter, s);
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
                if(id >= 1 && id <= counter)
                    return "show-groups," + map.get(id).getName() + "," + map.get(id).getAcademicYear();
                System.out.printf(Colors.ANSI_RED + "Please provide an id in range %d - %d\n" + Colors.ANSI_RESET, 1, counter);
            } catch (NumberFormatException e){
                System.out.println(Colors.ANSI_RED + "Please provide a valid id" + Colors.ANSI_RESET);
            }
        }
    }

    public static String createGroup(){
        Scanner scanner = new Scanner(System.in);
        Set<Series> series = getSeries();

        System.out.println(Colors.ANSI_BLUE + "\nCreate Group" + Colors.ANSI_RESET);

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Group Name: " + Colors.ANSI_RESET);
            String groupName = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Series Name: " + Colors.ANSI_RESET);
            String seriesName = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> Academic Year: " + Colors.ANSI_RESET);
            String academicYear = scanner.nextLine();

            Group newGroup = new Group(groupName, seriesName, academicYear);
            Series newSeries = new Series(seriesName, academicYear);

            if(groupName.length() == 0){
                System.out.println(Colors.ANSI_RED + "Please provide a name for the group\n" + Colors.ANSI_RESET);
            } else if(series.contains(newSeries) ){
                for(Series s: series){
                    if(s.equals(newSeries)){
                        Set<Group> groups = s.getGroups();
                        if(groups.contains(newGroup)){
                            System.out.println(Colors.ANSI_RED + "The group name for this academic year and series is already used" + Colors.ANSI_RESET);
                            System.out.println(Colors.ANSI_RED + "Please choose another name, year or series\n" + Colors.ANSI_RESET);
                        } else {
                            System.out.println(Colors.ANSI_BLUE + "Group created successfully!\n" + Colors.ANSI_RESET);
                            s.addGroup(newGroup);
                            addGroup(series, newGroup);
                            return "back";
                        }
                        break;
                    }
                }
            } else {
                System.out.println(Colors.ANSI_RED + "Cannot create group because series doesn't exist" + Colors.ANSI_RESET);
                System.out.println(Colors.ANSI_RED + "Please associate the group with one of the existing series" + Colors.ANSI_RESET);
            }
        }
    }

    public static String showGroups(String seriesName, String academicYear){
        Scanner scanner = new Scanner(System.in);
        Set<Series> series = getSeries();
        Map<Integer, Group> map = new HashMap<>();
        Series newSeries = new Series(seriesName, academicYear);
        Set<Group> groups = new HashSet<>();

        for(Series s: series){
            if(s.equals(newSeries)){
                groups = s.getGroups();
                break;
            }
        }

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
                    return "show-students," + map.get(id).getName() + "," + map.get(id).getSeriesName() + "," + map.get(id).getAcademicYear();
                System.out.printf(Colors.ANSI_RED + "Please provide an id in range %d - %d\n" + Colors.ANSI_RESET, 1, counter);
            } catch (NumberFormatException e){
                System.out.println(Colors.ANSI_RED + "Please provide a valid id" + Colors.ANSI_RESET);
            }
        }
    }

    private static Set<Subject> getSubjects(){
        Set<Subject> subjects = new HashSet<>();

        try(Scanner scanner = new Scanner(new File("resources/subjects.csv"))){
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();

                Scanner rowScanner = new Scanner(line);
                rowScanner.useDelimiter(",");

                String subjectName = rowScanner.next();
                int credits = Integer.parseInt(rowScanner.next());

                subjects.add(new Subject(subjectName, credits));
            }
        } catch (FileNotFoundException e){
            return null;
        }

        return subjects;
    }

    private static void addSubject(Subject subject){
        try{
            Files.write(Paths.get("resources/subjects.csv"), subject.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ignored){}
    }

    public static String createSubject(){
        Scanner scanner = new Scanner(System.in);
        Set<Subject> subjects = getSubjects();

        System.out.println(Colors.ANSI_BLUE + "\nCreate Subject" + Colors.ANSI_RESET);

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Subject Name: " + Colors.ANSI_RESET);
            String subjectName = scanner.nextLine();
            System.out.print(Colors.ANSI_PURPLE + ">>> No. of Credits: " + Colors.ANSI_RESET);
            try {
                int credits = Integer.parseInt(scanner.nextLine());
                Subject newSubject = new Subject(subjectName, credits);
                if(subjects.contains(newSubject)){
                    System.out.println(Colors.ANSI_RED + "This subject already exists. Try a different name or number of credits.\n" + Colors.ANSI_RESET);
                } else {
                    System.out.println(Colors.ANSI_BLUE + "Subject created successfully!\n" + Colors.ANSI_RESET);
                    addSubject(newSubject);
                    return "back";
                }
            } catch(NumberFormatException e){
                System.out.println(Colors.ANSI_RED + "Please provide a valid number!\n" + Colors.ANSI_RESET);
            }
        }
    }

    private static void addTeacher(Teacher teacher, int id){
        try{
            Files.write(Paths.get("resources/teachers/" + id), teacher.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ignored){}
        addUser(teacher, "teacher");
    }

    public static String createTeacher(){
        int id = getNextId("resources/teachers");
        createFile("resources/teachers/" + id);

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
                Teacher newTeacher = new Teacher(email, encryptedPassword);
                newTeacher.setFirstName(firstName);
                newTeacher.setLastName(lastName);
                newTeacher.setDob(dob);

                addTeacher(newTeacher, id);
                System.out.println(Colors.ANSI_BLUE + "Teacher added successfully!\n" + Colors.ANSI_RESET);
                return "back";
            }
        }
    }

    private static TreeMap<Integer, Teacher> getTeachers(){
        TreeMap<Integer, Teacher> teachers = new TreeMap<>();
        File dir = new File("resources/teachers");
        File[] files = dir.listFiles();
        if(files != null){
            for(File file: files){
                try(Scanner scanner = new Scanner(new File("resources/teachers/" + file.getName()))){
                    String line = scanner.nextLine();

                    Scanner rowScanner = new Scanner(line);
                    rowScanner.useDelimiter(",");

                    String email = rowScanner.next();
                    String encryptedPassword = rowScanner.next();
                    Teacher teacher = new Teacher(email, encryptedPassword);
                    teacher.setFirstName(rowScanner.next());
                    teacher.setLastName(rowScanner.next());
                    teacher.setDob(rowScanner.next());

                    teachers.put(Integer.parseInt(file.getName()), teacher);
                } catch (FileNotFoundException e){
                    return teachers;
                }
            }
        }
        return teachers;
    }

    private static Teacher getTeacher(String fileName){
        try(Scanner scanner = new Scanner(new File("resources/teachers/" + fileName))){
            String line = scanner.nextLine();

            Scanner rowScanner = new Scanner(line);
            rowScanner.useDelimiter(",");

            String email = rowScanner.next();
            String encryptedPassword = rowScanner.next();
            Teacher teacher = new Teacher(email, encryptedPassword);
            teacher.setFirstName(rowScanner.next());
            teacher.setLastName(rowScanner.next());
            teacher.setDob(rowScanner.next());

            while(scanner.hasNextLine()){
                line = scanner.nextLine();

                rowScanner = new Scanner(line);
                rowScanner.useDelimiter(",");

                String subjectName = rowScanner.next();
                int credits = Integer.parseInt(rowScanner.next());
                String groupName = rowScanner.next();
                String seriesName = rowScanner.next();
                String academicYear = rowScanner.next();
                int studyYear = Integer.parseInt(rowScanner.next());
                int semester = Integer.parseInt(rowScanner.next());

                teacher.addSubject(new SubjectTeacher(subjectName, credits, new Group(groupName, seriesName, academicYear), studyYear, semester));
            }

            return teacher;
        } catch (FileNotFoundException e){
            return null;
        }
    }

    private static int showTeachersTable(){
        TreeMap<Integer, Teacher> teachers = getTeachers();

        System.out.println(Colors.ANSI_BLUE + "\nShow Teachers" + Colors.ANSI_RESET);

        String[] headers = {"Id", "Email", "First Name", "Last Name", "Date of Birth"};
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, headers);
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);

        int counter = 0;
        for (Map.Entry<Integer, Teacher> entry: teachers.entrySet()) {
            counter = entry.getKey();
            Teacher t = entry.getValue();
            System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, counter, t.getEmail(), t.getFirstName(), t.getLastName(), t.getDob());
        }

        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        return counter;
    }

    public static String showTeachers(){
        Scanner scanner = new Scanner(System.in);
        int counter = showTeachersTable();
        System.out.println(Colors.ANSI_BLUE + "Enter the id of a professor to see what subjects teaches or type back/quit" + Colors.ANSI_RESET);

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Option: " + Colors.ANSI_RESET);
            try {
                String input = scanner.nextLine();
                if(input.equals("quit") || input.equals("back"))
                    return input;
                int id = Integer.parseInt(input);
                if(id >= 1 && id <= counter)
                    return "show-teacher," + id;
                System.out.printf(Colors.ANSI_RED + "Please provide an id in range %d - %d\n" + Colors.ANSI_RESET, 1, counter);
            } catch (NumberFormatException e){
                System.out.println(Colors.ANSI_RED + "Please provide a valid id" + Colors.ANSI_RESET);
            }
        }
    }

    private static void addSubject2TeacherFile(int id, SubjectTeacher subject){
        try{
            Files.write(Paths.get("resources/teachers/" + id), subject.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ignored){}
    }

    public static String addSubject2Teacher(){
        System.out.println(Colors.ANSI_BLUE + "\nEnter the id of a professor to add a subject to it" + Colors.ANSI_RESET);

        Scanner scanner = new Scanner(System.in);
        int counter = showTeachersTable();

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Teacher Id: " + Colors.ANSI_RESET);
            try {
                String input = scanner.nextLine();
                if(input.equals("quit") || input.equals("back"))
                    return input;
                int id = Integer.parseInt(input);
                if(id >= 1 && id <= counter){
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

                        SubjectTeacher subject = new SubjectTeacher(subjectName, credits, new Group(groupName, seriesName, academicYear), studyYear, semester);

                        addSubject2TeacherFile(id, subject);
                        updateStudents(subject);
                        System.out.println(Colors.ANSI_BLUE + "Subject added to teacher successfully!\n" + Colors.ANSI_RESET);
                        return "back";
                    } catch (NumberFormatException e) {
                        System.out.println(Colors.ANSI_RED + "Check if the values introduced for credits, semester or study year are valid integers\n" + Colors.ANSI_RESET);
                    }
                } else {
                    System.out.printf(Colors.ANSI_RED + "Please provide an id in range %d - %d\n" + Colors.ANSI_RESET, 1, counter);
                }
            } catch (NumberFormatException e){
                System.out.println(Colors.ANSI_RED + "Please provide a valid id\n" + Colors.ANSI_RESET);
            }
        }
    }

    public static String showTeacher(String fileName){
        Teacher teacher = getTeacher(fileName);

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

    private static void updateStudents(SubjectTeacher subject){
        File dir = new File("resources/students");
        File[] files = dir.listFiles();
        if(files != null){
            for(File file: files){
                String path = "resources/students/" + file.getName() + "/info";
                try(Scanner scanner = new Scanner(new File(path))){
                    while(scanner.hasNextLine()){
                        String line = scanner.nextLine();

                        Scanner rowScanner = new Scanner(line);
                        rowScanner.useDelimiter(",");

                        String email = rowScanner.next();
                        String encryptedPassword = rowScanner.next();
                        Student student = new Student(email, encryptedPassword);
                        student.setFirstName(rowScanner.next());
                        student.setLastName(rowScanner.next());
                        student.setRegistrationNumber(rowScanner.next());
                        student.setGroup(new Group(rowScanner.next(), rowScanner.next(), rowScanner.next()));

                        if(student.getGroup().equals(subject.getGroup())){
                            addSubjectToStudent(subject, student, subject.getStudyYear(), subject.getSemester());
                        }
                    }

                } catch (FileNotFoundException ignored){}
            }
        }
    }

    private static void addSubjectToStudent(SubjectTeacher subject, Student student, int studyYear, int semester){
        String path = "resources/students/" + student.getRegistrationNumber() + "/" + studyYear + "_" + semester;
        createFile(path);
        SubjectStudent newSubject = new SubjectStudent(subject.getName(), subject.getCredits(), subject.getGroup(),
                subject.getStudyYear(), subject.getSemester());
        try{
            Files.write(Paths.get(path), newSubject.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ignored){}
    }

    private static void addStudent(Student student){
        createDir("resources/students/" + student.getRegistrationNumber());
        createFile("resources/students/" + student.getRegistrationNumber() + "/" + "info");

        try{
            Files.write(Paths.get("resources/students/" + student.getRegistrationNumber() + "/" + "info"), student.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ignored){}

        addUser(student, "student");

        Set<Teacher> teachers = new HashSet<>();
        File dir = new File("resources/teachers");
        File[] files = dir.listFiles();
        if(files != null){
            for(File file: files){
                teachers.add(getTeacher(file.getName()));
            }
        }

        for(Teacher teacher: teachers){
            Set<SubjectTeacher> subjects = teacher.getSubjects();
            for(SubjectTeacher subject: subjects){
                if(subject.getGroup().equals(student.getGroup())){
                    int studyYear = subject.getStudyYear();
                    int semester = subject.getSemester();
                    addSubjectToStudent(subject, student, studyYear, semester);
                }
            }
        }
    }

    public static String createStudent(){
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
                Student newStudent = new Student(email, encryptedPassword);
                newStudent.setFirstName(firstName);
                newStudent.setLastName(lastName);
                newStudent.setRegistrationNumber(registrationNumber);
                newStudent.setGroup(new Group(groupName, seriesName, academicYear));

                addStudent(newStudent);
                System.out.println(Colors.ANSI_BLUE + "Student added successfully!\n" + Colors.ANSI_RESET);
                return "back";
            }
        }
    }

    private static Set<Student> getStudents(Group group){
        Set<Student> students = new HashSet<>();

        File dir = new File("resources/students");
        File[] files = dir.listFiles();
        if(files != null){
            for(File file: files){
                String path = "resources/students/" + file.getName() + "/info";
                try(Scanner scanner = new Scanner(new File(path))){
                    while(scanner.hasNextLine()){
                        String line = scanner.nextLine();

                        Scanner rowScanner = new Scanner(line);
                        rowScanner.useDelimiter(",");

                        String email = rowScanner.next();
                        String encryptedPassword = rowScanner.next();
                        Student student = new Student(email, encryptedPassword);
                        student.setFirstName(rowScanner.next());
                        student.setLastName(rowScanner.next());
                        student.setRegistrationNumber(rowScanner.next());
                        student.setGroup(new Group(rowScanner.next(), rowScanner.next(), rowScanner.next()));

                        if(student.getGroup().equals(group)){
                            students.add(student);
                            break;
                        }
                    }

                } catch (FileNotFoundException ignored){}
            }
        }

        return students;
    }

    public static String showStudents(String groupName, String seriesName, String academicYear){
        Set<Student> students = getStudents(new Group(groupName, seriesName, academicYear));

        System.out.printf(Colors.ANSI_BLUE + "\nShow Students from group: %s %s %s\n" + Colors.ANSI_RESET, groupName, seriesName, academicYear);

        String[] headers = {"Id", "Reg. Number", "Email", "First Name", "Last Name"};
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, headers);
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);

        int counter = 0;
        for (Student s: students) {
            counter ++;
            System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET,
                    counter, s.getRegistrationNumber(), s.getEmail(), s.getFirstName(), s.getLastName());
        }

        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        return "back";
    }

    private static void addAdmin(Admin admin){
        try{
            Files.write(Paths.get("resources/admins.csv"), admin.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ignored){}

        addUser(admin, "admin");
    }

    public static String createAdmin(){
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
                Admin admin = new Admin(email, encryptedPassword);
                admin.setFirstName(firstName);
                admin.setLastName(lastName);

                addAdmin(admin);
                System.out.println(Colors.ANSI_BLUE + "Administrator added successfully!\n" + Colors.ANSI_RESET);
                return "back";
            }
        }
    }

    private static Set<Admin> getAdmins(){
        Set<Admin> admins = new HashSet<>();

        try(Scanner scanner = new Scanner(new File("resources/admins.csv"))){
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();

                Scanner rowScanner = new Scanner(line);
                rowScanner.useDelimiter(",");

                String email = rowScanner.next();
                String encryptedPassword = rowScanner.next();
                Admin admin = new Admin(email, encryptedPassword);
                admin.setFirstName(rowScanner.next());
                admin.setLastName(rowScanner.next());

                admins.add(admin);
            }

        } catch (FileNotFoundException ignored){}

        return  admins;
    }

    public static String showAdmins(){
        Set<Admin> admins = getAdmins();

        System.out.printf(Colors.ANSI_BLUE + "\nShow Administrators\n" + Colors.ANSI_RESET);

        String[] headers = {"Id", "Email", "First Name", "Last Name"};
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, headers);
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);

        int counter = 0;
        for (Admin a: admins) {
            counter ++;
            System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET,
                    counter, a.getEmail(), a.getFirstName(), a.getLastName());
        }

        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        return "back";
    }
}
