/**
 * Kongu Engineering College (Autonomous) - Regulation 2024
 * Enrollment Model representing student registration in a course with continuous
 * attendance tracking and medical condonation status.
 */
public class Enrollment {
    private int enrollmentId;
    private String rollNo;
    private String courseCode;
    private int semester;
    private int classesHeld;
    private int classesAttended;
    private boolean medicalCondonation;

    // Optional joined fields for reporting
    private String studentName;
    private String department;
    private String courseTitle;
    private int courseCredits;
    private CourseType courseType;

    public Enrollment() {
    }

    public Enrollment(String rollNo, String courseCode, int semester) {
        this.rollNo = rollNo;
        this.courseCode = courseCode;
        this.semester = semester;
        this.classesHeld = 0;
        this.classesAttended = 0;
        this.medicalCondonation = false;
    }

    public Enrollment(int enrollmentId, String rollNo, String courseCode, int semester, 
                      int classesHeld, int classesAttended, boolean medicalCondonation) {
        this.enrollmentId = enrollmentId;
        this.rollNo = rollNo;
        this.courseCode = courseCode;
        this.semester = semester;
        this.classesHeld = classesHeld;
        this.classesAttended = classesAttended;
        this.medicalCondonation = medicalCondonation;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getClassesHeld() {
        return classesHeld;
    }

    public void setClassesHeld(int classesHeld) {
        this.classesHeld = classesHeld;
    }

    public int getClassesAttended() {
        return classesAttended;
    }

    public void setClassesAttended(int classesAttended) {
        this.classesAttended = classesAttended;
    }

    public boolean isMedicalCondonation() {
        return medicalCondonation;
    }

    public void setMedicalCondonation(boolean medicalCondonation) {
        this.medicalCondonation = medicalCondonation;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public int getCourseCredits() {
        return courseCredits;
    }

    public void setCourseCredits(int courseCredits) {
        this.courseCredits = courseCredits;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    /**
     * Calculates Attendance Percentage (0.0 to 100.0)
     */
    public double getAttendancePercentage() {
        if (classesHeld <= 0) return 100.0;
        return (classesAttended * 100.0) / classesHeld;
    }

    /**
     * Determines eligibility for End Semester Examination under KEC R2024:
     * - >= 75%: Eligible
     * - 65% to 74.9%: Conditionally eligible (Requires approved medical condonation)
     * - < 65%: Barred (SA)
     */
    public boolean isEligibleForEse() {
        double pct = getAttendancePercentage();
        if (pct >= 75.0) {
            return true;
        } else if (pct >= 65.0) {
            return medicalCondonation;
        } else {
            return false;
        }
    }

    public String getEligibilityStatus() {
        double pct = getAttendancePercentage();
        if (pct >= 75.0) {
            return "Eligible";
        } else if (pct >= 65.0) {
            return medicalCondonation ? "Eligible (Condonation Approved)" : "Conditionally Eligible (Needs Condonation)";
        } else {
            return "Barred (<65% Shortage)";
        }
    }
}
