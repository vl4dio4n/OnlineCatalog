package Faculty;

public non-sealed class SubjectTeacher extends Subject{
    private Group group;
    private int studyYear;
    private int semester;

    public Group getGroup() {
        return group;
    }

    public int getStudyYear() {
        return studyYear;
    }

    public int getSemester() {
        return semester;
    }

    public SubjectTeacher(String name, int credits, Group group, int studyYear, int semester){
        super(name, credits);
        this.group = group;
        this.studyYear = studyYear;
        this.semester = semester;
    }

    @Override
    public String toString() {
        return this.name + "," + this.credits + "," + this.group.getName() + "," + this.group.getSeriesName() + "," +
                this.group.getAcademicYear() + "," + this.studyYear + "," + this.semester + "\n";
    }
}
