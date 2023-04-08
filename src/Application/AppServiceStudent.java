package Application;

import Faculty.*;

import java.io.File;
import java.io.FileNotFoundException;
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

    private static List<SubjectStudent> getSubjects(String path){
        List<SubjectStudent> subjects = new ArrayList<>();

        try(Scanner scanner = new Scanner(new File(path))){
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();

                Scanner rowScanner = new Scanner(line);
                rowScanner.useDelimiter(",");

                String subjectName = rowScanner.next();
                int credits = Integer.parseInt(rowScanner.next());
                Group group = new Group(rowScanner.next(), rowScanner.next(), rowScanner.next());
                int studyYear = Integer.parseInt(rowScanner.next());
                int semester = Integer.parseInt(rowScanner.next());
                int mark = Integer.parseInt(rowScanner.next());

                SubjectStudent newSubject = new SubjectStudent(subjectName, credits, group, studyYear, semester);
                newSubject.setMark(mark);
                subjects.add(newSubject);
            }

        } catch (FileNotFoundException ignored){}

        return subjects;
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

    public static String dashboard(String registrationNumber){
        TreeMap<String, List<SubjectStudent>> map = new TreeMap<>();

        File dir = new File("resources/students/" + registrationNumber);
        File[] files = dir.listFiles();
        if(files != null){
            for(File file: files)
                if(!file.getName().equals("info")){
                    List<SubjectStudent> subjects = getSubjects("resources/students/" + registrationNumber + "/" + file.getName());
                    map.put(file.getName(), subjects);
                }
        }

        for (Map.Entry<String, List<SubjectStudent>> entry: map.entrySet()) {
            String fileName = entry.getKey();
            List<SubjectStudent> subjects = entry.getValue();
            showSubjects(subjects, fileName);
        }

        return "back";
    }
}
