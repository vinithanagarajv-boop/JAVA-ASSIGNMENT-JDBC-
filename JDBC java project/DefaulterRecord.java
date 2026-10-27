/**
 * Kongu Engineering College (Autonomous) - Regulation 2024
 * DefaulterRecord Model representing students with attendance < 65% or
 * lacking approved medical condonation, flagged with SA grade.
 */
public class DefaulterRecord {
    private String rollNo;
    private String studentName;
    private String department;
    private String courseCode;
    private String courseTitle;
    private int classesHeld;
    private int classesAttended;
    private double attendancePercentage;
    private boolean medicalCondonation;
    private String flaggedGrade; // 'SA'
    private String reason;

    public DefaulterRecord() {
        this.flaggedGrade = "SA";
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
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

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
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

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public boolean isMedicalCondonation() {
        return medicalCondonation;
    }

    public void setMedicalCondonation(boolean medicalCondonation) {
        this.medicalCondonation = medicalCondonation;
    }

    public String getFlaggedGrade() {
        return flaggedGrade;
    }

    public void setFlaggedGrade(String flaggedGrade) {
        this.flaggedGrade = flaggedGrade;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
