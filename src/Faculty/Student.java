package Faculty;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public non-sealed class Student extends User{
    private String firstName;
    private String lastName;
    private String registrationNumber;
    private Group group;
    private Set<SubjectStudent> subjects;

    public Student(String email, String password) {
        super(email, password);
        this.subjects = new HashSet<>();
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public Group getGroup() {
        return group;
    }

    public Set<SubjectStudent> getSubjects() {
        return subjects;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void addSubject(SubjectStudent subject){
        this.subjects.add(subject);
    }

    @Override
    public void init(){
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
                        if(this.email.equals(email) && this.encryptedPassword.equals(encryptedPassword)){
                            rowScanner.next();
                            rowScanner.next();
                            this.registrationNumber = rowScanner.next();
                            return;
                        }
                    }
                } catch (FileNotFoundException ignored){}
            }
        }
    }

    @Override
    public String toString() {
        return this.email + "," + this.encryptedPassword + "," + this.firstName + "," + this.lastName + "," +
                this.registrationNumber + "," + this.group.getName() + "," + this.group.getSeriesName() + "," + this.group.getAcademicYear() + "\n";
    }
}
