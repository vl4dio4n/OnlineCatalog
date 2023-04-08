package Application;

import Faculty.Admin;
import Faculty.Student;
import Faculty.Teacher;
import Faculty.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public sealed class AppService permits AppServiceTeacher, AppServiceAdmin, AppServiceStudent {

    private static List<User> getUsers(){
        List<User> users = new ArrayList<>();

        try(Scanner scanner = new Scanner(new File("resources/credentials.csv"))){
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();

                Scanner rowScanner = new Scanner(line);
                rowScanner.useDelimiter(",");

                String email = rowScanner.next();
                String encrypted_password = rowScanner.next();

                users.add(new User(email, encrypted_password));
            }

            return users;
        } catch (FileNotFoundException e){
            return users;
        }
    }

    private static User getUserType(String email, String password){
        try(Scanner scanner = new Scanner(new File("resources/users-type.csv"))){
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();

                Scanner rowScanner = new Scanner(line);
                rowScanner.useDelimiter(",");

                String emailHandle = rowScanner.next();
                String userType = rowScanner.next();

                if(emailHandle.equals(email)){
                    return switch (userType) {
                        case "admin" -> new Admin(email, password);
                        case "student" -> new Student(email, password);
                        default -> new Teacher(email, password);
                    };
                }
            }
        } catch (FileNotFoundException e){
            return null;
        }
        return null;
    }

    public static User authenticate(String email, String password){
        List<User> users = getUsers();
        for(User user: users){
            if(user.signIn(email, password)){
                return getUserType(email, PasswordEncryptionService.encryptPassword(password));
            }
        }
        return null;
    }

    public static boolean signUp(String email, String password){
        String encrypted_password = PasswordEncryptionService.encryptPassword(password);
        try{
            Files.write(Paths.get("resources/credentials.csv"), (email + "," + encrypted_password + "\n").getBytes(), StandardOpenOption.APPEND);
            return true;
        } catch (IOException e){
            return false;
        }
    }

}
