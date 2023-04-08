package Faculty;

public non-sealed class SubjectStudent extends Subject{
    private Group group;
    private int studyYear;
    private int semester;
    private int mark;

    public SubjectStudent(String name, int credits, Group group, int studyYear, int semester){
        super(name, credits);
        this.group = group;
        this.studyYear = studyYear;
        this.semester = semester;
        this.mark = 0;
    }

    public Group getGroup() {
        return group;
    }

    public int getStudyYear() {
        return studyYear;
    }

    public int getSemester() {
        return semester;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return this.name + "," + this.credits + "," + this.group.getName() + "," + this.group.getSeriesName() + "," +
                this.group.getAcademicYear() + "," + this.studyYear + "," + this.semester + "," + this.mark + "\n";
    }
}
