package Application;

import Faculty.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public non-sealed class AppServiceTeacher extends AppService{
    private static Map<String, String> welcomeOptions;

    static {
        welcomeOptions = new HashMap<>();
        welcomeOptions.put("1", "show-subjects");
        welcomeOptions.put("quit", "quit");
        welcomeOptions.put("back", "back");
    }

    public static String welcome(){
        System.out.println(Colors.ANSI_BLUE + "What action do you want to take?" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_BLUE + "     1. Show Subjects I Teach" + Colors.ANSI_RESET);

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

    public static String showSubjects(Teacher teacher){
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
        System.out.println(Colors.ANSI_BLUE + "Enter the id of a course to see the students enrolled, or type back/quit" + Colors.ANSI_RESET);

        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Option: " + Colors.ANSI_RESET);
            try {
                String input = scanner.nextLine();
                if(input.equals("quit") || input.equals("back"))
                    return input;
                int id = Integer.parseInt(input);
                if(id >= 1 && id <= counter){
                    for(SubjectTeacher s: teacher.getSubjects()){
                        id --;
                        if(id == 0){
                            return "show-students-course," + s.getGroup().getName() + "," + s.getGroup().getSeriesName()
                                    + "," + s.getGroup().getAcademicYear() + "," + s.getName() + "," + s.getCredits() + "," + s.getStudyYear() + "," + s.getSemester();
                        }
                    }
                }
                System.out.printf(Colors.ANSI_RED + "Please provide an id in range %d - %d\n" + Colors.ANSI_RESET, 1, counter);
            } catch (NumberFormatException e){
                System.out.println(Colors.ANSI_RED + "Please provide a valid id" + Colors.ANSI_RESET);
            }
        }
    }

    private static void getStudentSubjects(Student student, String path){
        try(Scanner scanner = new Scanner(new File(path))){
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();

                Scanner rowScanner = new Scanner(line);
                rowScanner.useDelimiter(",");

                String subjectName = rowScanner.next();
                int credits = Integer.parseInt(rowScanner.next());
                String groupName = rowScanner.next();
                String seriesName = rowScanner.next();
                String academicYear = rowScanner.next();
                int studyYear = Integer.parseInt(rowScanner.next());
                int semester = Integer.parseInt(rowScanner.next());
                int mark = Integer.parseInt(rowScanner.next());

                SubjectStudent subject = new SubjectStudent(subjectName, credits, new Group(groupName, seriesName, academicYear), studyYear, semester);
                subject.setMark(mark);

                student.addSubject(subject);
            }
        } catch (FileNotFoundException ignored){}
    }

    private static Set<Student> getStudents(SubjectTeacher subject){
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

                        if(student.getGroup().equals(subject.getGroup())){
                            getStudentSubjects(student, "resources/students/" + file.getName() + "/" + subject.getStudyYear() + "_" + subject.getSemester());
                            students.add(student);
                            break;
                        }
                    }

                } catch (FileNotFoundException ignored){}
            }
        }

        return students;
    }


    private static void updateFile(String path, Student student){
        StringBuilder sb = new StringBuilder();
        for(SubjectStudent subject: student.getSubjects()){
            sb.append(subject.toString());
        }

        try{
            Files.write(Paths.get(path), sb.toString().getBytes(), StandardOpenOption.WRITE);
        } catch (IOException ignored){}
    }

    private static void setMark(Set<Student> students, SubjectTeacher subject, int id, int mark){
        for(Student student: students){
            id --;
            if(id == 0){
                for(SubjectStudent s: student.getSubjects()){
                    if(s.getName().equals(subject.getName()) && s.getCredits() == subject.getCredits()){
                        s.setMark(mark);
                        String path = "resources/students/" + student.getRegistrationNumber() + "/" + subject.getStudyYear() + "_" + subject.getSemester();
                        updateFile(path, student);
                    }
                }
            }
        }
    }

    public static String showStudentsCourse(SubjectTeacher subject){
        Set<Student> students = getStudents(subject);

        System.out.printf(Colors.ANSI_BLUE + "\nShow Students from group %s %s %s at subject %s, study year %d, semester %d\n" + Colors.ANSI_RESET, subject.getGroup().getName(), subject.getGroup().getSeriesName(), subject.getGroup().getAcademicYear(), subject.getName(), subject.getStudyYear(), subject.getSemester());

        String[] headers = {"Id", "Reg. Number", "Email", "First Name", "Last Name", "Mark"};
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, headers);
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);

        int counter = 0;
        for (Student s: students) {
            counter ++;
            int mark = 0;
            for(SubjectStudent sub: s.getSubjects())
                if(sub.getName().equals(subject.getName()) && sub.getCredits() == subject.getCredits()){
                    mark = sub.getMark();
                    break;
                }
            System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET,
                    counter, s.getRegistrationNumber(), s.getEmail(), s.getFirstName(), s.getLastName(), mark);
        }

        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_BLUE + "\nEnter the id of a student to set the mark or type back/quit" + Colors.ANSI_RESET);

        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Student Id: " + Colors.ANSI_RESET);
            try {
                String input = scanner.nextLine();
                if(input.equals("quit") || input.equals("back"))
                    return input;
                int id = Integer.parseInt(input);
                if(id >= 1 && id <= counter){
                    System.out.print(Colors.ANSI_PURPLE + ">>> Mark: " + Colors.ANSI_RESET);
                    String markStr = scanner.nextLine();
                    try{
                        int mark = Integer.parseInt(markStr);
                        if(mark >= 1 && mark <= 10){
                            setMark(students, subject, id, mark);
                            System.out.println(Colors.ANSI_BLUE + "Student's mark was set successfully!" + Colors.ANSI_RESET);
                            return "back";
                        } else {
                            System.out.println(Colors.ANSI_RED + "The mark must be in range 1-10\n" + Colors.ANSI_RESET);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(Colors.ANSI_RED + "Please provide an integer for the mark\n" + Colors.ANSI_RESET);
                    }
                } else {
                    System.out.printf(Colors.ANSI_RED + "Please provide an id in range %d - %d\n" + Colors.ANSI_RESET, 1, counter);
                }
            } catch (NumberFormatException e){
                System.out.println(Colors.ANSI_RED + "Please provide a valid id\n" + Colors.ANSI_RESET);
            }
        }
    }
}
