package Faculty;

import java.util.List;
import java.util.Objects;

public class Group implements Comparable<Group> {
    private int groupId;
    private String name;
    private String seriesName;
    private String academicYear;
    private List<Student> students;

    public Group(int groupId,  String name, String seriesName, String academicYear){
        this.groupId = groupId;
        this.name = name;
        this.seriesName = seriesName;
        this.academicYear = academicYear;
    }

    public void addStudent(Student student){
        students.add(student);
    }

    public int getGroupId() { return groupId; }

    public String getName() {
        return name;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public List<Student> getStudents() {
        return students;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.name + "," + this.seriesName + "," + this.academicYear + ",\"");
        sb.append("\"\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return name.equals(group.name) && seriesName.equals(group.seriesName) && academicYear.equals(group.academicYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, seriesName, academicYear);
    }

    public int compareTo(Group ob) {
        return this.groupId - ob.groupId;
    }
}
