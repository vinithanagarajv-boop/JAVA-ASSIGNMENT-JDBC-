/**
 * Kongu Engineering College (Autonomous) - Regulation 2024
 * Course Model representing academic courses in the catalog.
 */
public class Course {
    private String courseCode;
    private String title;
    private int credits;
    private CourseType courseType;
    private int semester;

    public Course() {
    }

    public Course(String courseCode, String title, int credits, CourseType courseType, int semester) {
        this.courseCode = courseCode;
        this.title = title;
        this.credits = credits;
        this.courseType = courseType;
        this.semester = semester;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-40s | Cr: %d | %-9s | Sem: %d", 
                courseCode, title, credits, courseType, semester);
    }
}
