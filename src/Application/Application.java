package Application;

import Faculty.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import java.util.Stack;

public class Application {
    private static Application application;
    private User activeUser;
    private Class<? extends AppService> activeService;
    private Stack<String> navigationStack;

    private Application(){
        this.activeService = AppService.class;
        this.navigationStack = new Stack<String>();
        this.navigationStack.push("sign_in");
    }

    public static Application getApplication(){
        if(application == null)
            application = new Application();
        return application;
    }

    private void signIn(){
        Scanner scanner = new Scanner(System.in);

        System.out.println(Colors.ANSI_BLUE + "Log In into your account" + Colors.ANSI_RESET);
        System.out.print(Colors.ANSI_PURPLE + ">>> Email: " + Colors.ANSI_RESET);
        String email = scanner.nextLine();
        System.out.print(Colors.ANSI_PURPLE + ">>> Password: " + Colors.ANSI_RESET);
        String password = scanner.nextLine();

        String[] args = {email, password};
        try {
            this.activeUser = (User) this.activeService.getMethod("authenticate", String.class, String.class).invoke(null, args);
            this.activeUser.init();

            if (this.activeUser == null) {
                System.out.println(Colors.ANSI_RED + "Wrong email or password. Try again!\n" + Colors.ANSI_RESET);
            } else {
                System.out.println(Colors.ANSI_BLUE + "Sign In completed successfully!\n" + Colors.ANSI_RESET);
                System.out.println(Colors.ANSI_GREEN + "Welcome, " + this.activeUser.getEmail() + "\n" + Colors.ANSI_RESET);
                this.navigationStack.push("welcome");

                if(this.activeUser instanceof Admin){
                    this.activeService = AppServiceAdmin.class;
                } else if(this.activeUser instanceof Student){
                    this.activeService = AppServiceStudent.class;
                } else {
                    this.activeService = AppServiceTeacher.class;
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println(Colors.ANSI_RED + "!!! An internal error occurred. Keep Calm !!!" + Colors.ANSI_RESET);
        }
    }

    private void invokeMethod(String methodName, String... args){
        try {
            String action = null;
            if(args.length == 0){
                action = (String) this.activeService.getMethod(methodName).invoke(null);
            } else if(args.length == 1){
                action = (String) this.activeService.getMethod(methodName, String.class).invoke(null, args);
            } else if(args.length == 2){
                action = (String) this.activeService.getMethod(methodName, String.class, String.class).invoke(null, args);
            } else if(args.length == 3){
                action = (String) this.activeService.getMethod(methodName, String.class, String.class, String.class).invoke(null, args);
            } else if(args.length == 7){
                SubjectTeacher subject = new SubjectTeacher(args[3], Integer.parseInt(args[4]), new Group(args[0], args[1], args[2]), Integer.parseInt(args[5]), Integer.parseInt(args[6]));
                action = (String) this.activeService.getMethod(methodName, SubjectTeacher.class).invoke(null, subject);
            }
            this.navigationStack.push(action);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println(Colors.ANSI_RED + "!!! An internal error occurred. Keep Calm !!!" + Colors.ANSI_RESET);
        }
    }

    private void invokeMethodTeacher(String methodName, Teacher teacher){
        try {
            String action = (String) this.activeService.getMethod(methodName, Teacher.class).invoke(null, teacher);
            this.navigationStack.push(action);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println(Colors.ANSI_RED + "!!! An internal error occurred. Keep Calm !!!" + Colors.ANSI_RESET);
        }
    }

    private void invokeMethodStudent(String methodName, String registrationNumber){
        try {
            String action = (String) this.activeService.getMethod(methodName, String.class).invoke(null, registrationNumber);
            this.navigationStack.push(action);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println(Colors.ANSI_RED + "!!! An internal error occurred. Keep Calm !!!" + Colors.ANSI_RESET);
        }
    }

    public void runner(){
        System.out.println(Colors.ANSI_GREEN + "                             Online Catalog is up and running...\n" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_YELLOW + "                                 !!! Navigation Rules !!!" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_YELLOW + "- In order to close the application, type 'quit' when '>>> Option: ' prompt is displayed" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_YELLOW + "- In order to navigate to a previous view, type 'back' when '>>> Option: ' prompt is displayed" + Colors.ANSI_RESET);
        System.out.println(Colors.ANSI_YELLOW + "- For any other cases when you want to force quit the app, press 'Ctrl' + 'C'\n" + Colors.ANSI_RESET);

        while(true){

            String currentView = this.navigationStack.peek();

            if(currentView.equals("sign_in")) {
                this.signIn();
            } else if(currentView.equals("welcome")){
                this.invokeMethod("welcome");
            } else if(currentView.equals("create-series")){
                this.invokeMethod("createSeries");
            } else if(currentView.equals("show-series")){
                this.invokeMethod("showSeries");
            } else if(currentView.startsWith("show-groups")){
                String[] args = currentView.replace("show-groups,", "").split(",");
                this.invokeMethod("showGroups", args[0], args[1]);
            } else if(currentView.equals("create-group")){
                this.invokeMethod("createGroup");
            } else if(currentView.startsWith("show-students-course")){
                String[] args = currentView.replace("show-students-course,", "").split(",");
                this.invokeMethod("showStudentsCourse", args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            } else if(currentView.startsWith("show-students")){
                String[] args = currentView.replace("show-students,", "").split(",");
                this.invokeMethod("showStudents", args[0], args[1], args[2]);
            } else if(currentView.equals("add-subject")){
                this.invokeMethod("createSubject");
            } else if(currentView.equals("add-teacher")){
                this.invokeMethod("createTeacher");
            } else if(currentView.equals("add-subject-to-teacher")){
                this.invokeMethod("addSubject2Teacher");
            } else if(currentView.equals("show-teachers")){
                this.invokeMethod("showTeachers");
            } else if(currentView.startsWith("show-teacher")){
                String[] args = {currentView.replace("show-teacher,", "")};
                this.invokeMethod("showTeacher", args[0]);
            } else if(currentView.equals("add-student")){
                this.invokeMethod("createStudent");
            } else if(currentView.equals("add-admin")) {
                this.invokeMethod("createAdmin");
            } else if(currentView.equals("show-admins")){
                this.invokeMethod("showAdmins");
            } else if(currentView.equals("show-subjects")){
                this.invokeMethodTeacher("showSubjects", (Teacher) this.activeUser);
            } else if(currentView.equals("dashboard")){
                this.invokeMethodStudent("dashboard", ((Student) this.activeUser).getRegistrationNumber());
            } else if(currentView.equals("back")){
                this.navigationStack.pop();
                this.navigationStack.pop();
            } else if(currentView.equals("quit")){
                break;
            }
        }
    }
}
