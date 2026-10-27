/**
 * Kongu Engineering College (Autonomous) - Regulation 2024
 * AssessmentMark Model representing incremental phased marks:
 * Phase 1: CIA 1 (0-100), CIA 2 (0-100)
 * Phase 2: Assignment / Quiz / Lab Evaluations (0-100)
 * Phase 3: ESE Marks (0-100) and Absent Status (AB)
 */
public class AssessmentMark {
    private int markId;
    private int enrollmentId;
    
    // Phase 1 (Mid-Semester)
    private Double cia1Marks;
    private Double cia2Marks;

    // Phase 2 (End of Semester CIA)
    private Double assignmentMarks;

    // Phase 3 (Post Semester ESE)
    private boolean absentEse;
    private Double eseMarks;

    // Computed / Cached Evaluation Fields
    private Double ciaScaled;
    private Double eseScaled;
    private Double totalMarks;
    private String grade;
    private Integer gradePoint;
    private String status;

    public AssessmentMark() {
    }

    public AssessmentMark(int enrollmentId) {
        this.enrollmentId = enrollmentId;
        this.absentEse = false;
    }

    public int getMarkId() {
        return markId;
    }

    public void setMarkId(int markId) {
        this.markId = markId;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Double getCia1Marks() {
        return cia1Marks;
    }

    public void setCia1Marks(Double cia1Marks) {
        this.cia1Marks = cia1Marks;
    }

    public Double getCia2Marks() {
        return cia2Marks;
    }

    public void setCia2Marks(Double cia2Marks) {
        this.cia2Marks = cia2Marks;
    }

    public Double getAssignmentMarks() {
        return assignmentMarks;
    }

    public void setAssignmentMarks(Double assignmentMarks) {
        this.assignmentMarks = assignmentMarks;
    }

    public boolean isAbsentEse() {
        return absentEse;
    }

    public void setAbsentEse(boolean absentEse) {
        this.absentEse = absentEse;
    }

    public Double getEseMarks() {
        return eseMarks;
    }

    public void setEseMarks(Double eseMarks) {
        this.eseMarks = eseMarks;
    }

    public Double getCiaScaled() {
        return ciaScaled;
    }

    public void setCiaScaled(Double ciaScaled) {
        this.ciaScaled = ciaScaled;
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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(Integer gradePoint) {
        this.gradePoint = gradePoint;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean hasPhase1() {
        return cia1Marks != null && cia2Marks != null;
    }

    public boolean hasPhase2() {
        return hasPhase1() && assignmentMarks != null;
    }

    public boolean hasPhase3() {
        return absentEse || eseMarks != null;
    }
}
