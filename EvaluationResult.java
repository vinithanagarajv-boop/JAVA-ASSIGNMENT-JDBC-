/**
 * Kongu Engineering College (Autonomous) - Regulation 2024
 * EvaluationResult Model encapsulating the calculated assessment output
 * for an individual course enrollment.
 */
public class EvaluationResult {
    private String courseCode;
    private String courseTitle;
    private int credits;
    private CourseType courseType;
    private double attendancePercentage;
    private boolean eligible;
    private boolean medicalCondonation;

    // Phased Marks
    private Double cia1;
    private Double cia2;
    private Double assignment;
    private Double rawCia100;
    private Double ciaScaled;
    private Double eseRaw;
    private Double eseScaled;
    private Double totalMarks;

    private boolean absentEse;
    private String grade;
    private int gradePoint;
    private String status; // 'PASS', 'FAIL (RA)', 'SA', 'FAIL (AB)', 'INCOMPLETE'
    private String remarks;

    public EvaluationResult() {
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

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public boolean isMedicalCondonation() {
        return medicalCondonation;
    }

    public void setMedicalCondonation(boolean medicalCondonation) {
        this.medicalCondonation = medicalCondonation;
    }

    public Double getCia1() {
        return cia1;
    }

    public void setCia1(Double cia1) {
        this.cia1 = cia1;
    }

    public Double getCia2() {
        return cia2;
    }

    public void setCia2(Double cia2) {
        this.cia2 = cia2;
    }

    public Double getAssignment() {
        return assignment;
    }

    public void setAssignment(Double assignment) {
        this.assignment = assignment;
    }

    public Double getRawCia100() {
        return rawCia100;
    }

    public void setRawCia100(Double rawCia100) {
        this.rawCia100 = rawCia100;
    }

    public Double getCiaScaled() {
        return ciaScaled;
    }

    public void setCiaScaled(Double ciaScaled) {
        this.ciaScaled = ciaScaled;
    }

    public Double getEseRaw() {
        return eseRaw;
    }

    public void setEseRaw(Double eseRaw) {
        this.eseRaw = eseRaw;
    }

    public Double getEseScaled() {
        return eseScaled;
    }

    public void setEseScaled(Double eseScaled) {
        this.eseScaled = eseScaled;
    }

    public Double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Double totalMarks) {
        this.totalMarks = totalMarks;
    }

    public boolean isAbsentEse() {
        return absentEse;
    }

    public void setAbsentEse(boolean absentEse) {
        this.absentEse = absentEse;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(int gradePoint) {
        this.gradePoint = gradePoint;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getCreditPoints() {
        return credits * gradePoint;
    }
}
