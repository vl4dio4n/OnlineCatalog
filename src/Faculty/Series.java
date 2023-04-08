package Faculty;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Series {
    private String name;
    private String academicYear;
    private Set<Group> groups;

    public Series(String name, String academicYear){
        this.name = name;
        this.academicYear = academicYear;
        this.groups = new HashSet<>();
    }

    public void addGroup(Group group){
        groups.add(group);
    }

    public String getName() {
        return name;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Series series = (Series) o;
        return name.equals(series.name) && academicYear.equals(series.academicYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, academicYear);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.name + "," + this.academicYear + ",\"");
        boolean isFirst = true;
        for(Group group: groups){
            if(!isFirst)
                sb.append(",");
            sb.append(group.getName());
            isFirst = false;
        }
        sb.append("\"\n");
        return sb.toString();
    }
}
