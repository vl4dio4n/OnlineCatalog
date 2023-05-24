package Application;

import DataUtils.StudentService;
import DataUtils.TeachersService;
import Faculty.*;


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
        TeachersService teachersService = TeachersService.getTeachersService(null);
        teacher = teachersService.getTeacherById(teacher.getUserId());

        System.out.printf(Colors.ANSI_BLUE + "\nShow Info of Teacher: %s %s\n" + Colors.ANSI_RESET, teacher.getFirstName(), teacher.getLastName());

        String[] headers = {"Id", "Subject Name", "Credits", "Group Name", "Series Name", "Academic Year", "Study Year", "Semester"};
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s | %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, headers);
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);

        int counter = 0;
        SubjectTeacher[] subjectsArray = teacher.getSubjects().toArray(new SubjectTeacher[teacher.getSubjects().size()]);
        for (SubjectTeacher s: subjectsArray) {
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
                    SubjectTeacher s = subjectsArray[id - 1];
                    return "show-students-course," + s.getGroup().getName() + "," + s.getGroup().getSeriesName()
                            + "," + s.getGroup().getAcademicYear() + "," + s.getName() + "," + s.getCredits() + "," + s.getStudyYear() + "," + s.getSemester() + "," + s.getGroup().getGroupId();
                }
                System.out.printf(Colors.ANSI_RED + "Please provide an id in range %d - %d\n" + Colors.ANSI_RESET, 1, counter);
            } catch (NumberFormatException e){
                System.out.println(Colors.ANSI_RED + "Please provide a valid id" + Colors.ANSI_RESET);
            }
        }
    }


    public static String showStudentsCourse(SubjectTeacher subject){
        StudentService studentService = StudentService.getStudentService(null);

        Set<Student> students = studentService.getStudentsByGroupId(subject.getGroup().getGroupId());

        System.out.printf(Colors.ANSI_BLUE + "\nShow Students from group %s %s %s at subject %s, study year %d, semester %d\n" + Colors.ANSI_RESET, subject.getGroup().getName(), subject.getGroup().getSeriesName(), subject.getGroup().getAcademicYear(), subject.getName(), subject.getStudyYear(), subject.getSemester());

        String[] headers = {"Id", "Reg. Number", "Email", "First Name", "Last Name", "Mark"};
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, headers);
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);

        Set<Integer> ids = new HashSet<>();
        for (Student s: students) {
            ids.add(s.getUserId());

            int mark = studentService.getMark(s.getUserId(), subject.getName());

            System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET,
                    s.getUserId(), s.getRegistrationNumber(), s.getUsername(), s.getFirstName(), s.getLastName(), mark);
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
                if(ids.contains(id)){
                    System.out.print(Colors.ANSI_PURPLE + ">>> Mark: " + Colors.ANSI_RESET);
                    String markStr = scanner.nextLine();
                    try{
                        int mark = Integer.parseInt(markStr);
                        if(mark >= 1 && mark <= 10){
                            studentService.setMark(id, subject.getName(), mark);
                            System.out.println(Colors.ANSI_BLUE + "Student's mark was set successfully!" + Colors.ANSI_RESET);
                            return "back";
                        } else {
                            System.out.println(Colors.ANSI_RED + "The mark must be in range 1-10\n" + Colors.ANSI_RESET);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(Colors.ANSI_RED + "Please provide an integer for the mark\n" + Colors.ANSI_RESET);
                    }
                } else {
                    System.out.printf(Colors.ANSI_RED + "Please provide a valid id\n" + Colors.ANSI_RESET);
                }
            } catch (NumberFormatException e){
                System.out.println(Colors.ANSI_RED + "Please provide a valid id\n" + Colors.ANSI_RESET);
            }
        }
    }
}
