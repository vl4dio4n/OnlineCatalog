package Application;

import DataUtils.StudentService;
import Faculty.*;

import java.util.*;

public non-sealed class AppServiceStudent extends AppService{
    public static String welcome(){
        System.out.println(Colors.ANSI_BLUE + "Press 'd' to view dashboard or type back/quit" + Colors.ANSI_RESET);
        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.print(Colors.ANSI_PURPLE + ">>> Option: " + Colors.ANSI_RESET);
            String input = scanner.nextLine();
            if(input.equals("d"))
                return "dashboard";
            if(input.equals("quit") || input.equals("back"))
                return input;
            System.out.println(Colors.ANSI_RED + "Please type d/back/quit" + Colors.ANSI_RESET);
        }
    }

    static void showSubjects(List<SubjectStudent> subjects, String name){
        System.out.printf(Colors.ANSI_BLUE + "\nStudy Year %s, Semester %s\n" + Colors.ANSI_RESET, name.split("_")[0], name.split("_")[1]);

        String[] headers = {"Id", "Subject Name", "Credits", "Mark"};
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, headers);
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);

        int counter = 0;
        double sum = 0;
        for (SubjectStudent s: subjects) {
            counter ++;
            sum += s.getMark();
            System.out.format(Colors.ANSI_CYAN + "| %-13s | %-13s | %-13s | %-13s |%n" + Colors.ANSI_RESET, counter, s.getName(), s.getCredits(), s.getMark());
        }
        System.out.format(Colors.ANSI_CYAN + "+---------------+---------------+---------------+---------------+%n" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_BLUE + "Average: " + (sum / counter) + Colors.ANSI_RESET);
    }

    public static String dashboard(Integer studentId){
        StudentService studentService = StudentService.getStudentService(null);
        TreeMap<String, List<SubjectStudent>> map = studentService.getStudentSubjects(studentId);

        for (Map.Entry<String, List<SubjectStudent>> entry: map.entrySet()) {
            String key = entry.getKey();
            List<SubjectStudent> subjects = entry.getValue();
            showSubjects(subjects, key);
        }

        return "back";
    }
}
