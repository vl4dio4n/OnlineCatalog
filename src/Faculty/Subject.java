package Faculty;

import java.util.Objects;

public sealed class Subject permits SubjectTeacher, SubjectStudent {
    protected String name;
    protected int credits;

    public Subject(String name, int credits){
        this.name = name;
        this.credits = credits;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    @Override
    public String toString() {
        return this.name + "," + this.credits + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return credits == subject.credits && name.equals(subject.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, credits);
    }
}
