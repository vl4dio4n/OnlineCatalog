package Faculty;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public non-sealed class Admin extends User{
    private String firstName;
    private String lastName;
    public Admin(int userId, String email, String password){
        super(userId, email, password);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public void init(){
        try(Scanner scanner = new Scanner(new File("resources/admins.csv"))){
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();

                Scanner rowScanner = new Scanner(line);
                rowScanner.useDelimiter(",");

                String email = rowScanner.next();
                String encryptedPassword = rowScanner.next();

                if(this.username.equals(email) && this.encryptedPassword.equals(encryptedPassword)){
                    this.firstName = rowScanner.next();
                    this.lastName = rowScanner.next();
                }
            }
        } catch (FileNotFoundException ignored){}
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return this.username + "," + this.encryptedPassword + "," + this.firstName + "," + this.lastName + "\n";
    }
}
