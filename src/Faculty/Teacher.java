package Faculty;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public non-sealed class Teacher extends User{
    private String firstName;
    private String lastName;
    private String dob;
    private Set<SubjectTeacher> subjects;

    public Teacher(String email, String password){
        super(email, password);
        this.subjects = new HashSet<>();
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDob() {
        return dob;
    }

    public Set<SubjectTeacher> getSubjects() {
        return subjects;
    }

    public void addSubject(SubjectTeacher subject){
        this.subjects.add(subject);
    }

    @Override
    public void init(){
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

                    if(this.email.equals(email) && this.encryptedPassword.equals(encryptedPassword)) {
                        this.firstName = rowScanner.next();
                        this.lastName = rowScanner.next();
                        this.dob = rowScanner.next();

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

                            this.addSubject(new SubjectTeacher(subjectName, credits, new Group(groupName, seriesName, academicYear), studyYear, semester));
                        }
                        break;
                    }
                } catch (FileNotFoundException ignored){}
            }
        }
    }

    @Override
    public String toString() {
        return this.email + "," + this.encryptedPassword + "," + this.firstName + "," + this.lastName + "," + this.dob + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return email.equals(teacher.email) && encryptedPassword.equals(teacher.encryptedPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, encryptedPassword);
    }
}
