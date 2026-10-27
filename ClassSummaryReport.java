import java.util.HashMap;
import java.util.Map;

/**
 * Kongu Engineering College (Autonomous) - Regulation 2024
 * ClassSummaryReport Model representing department-wise and semester-wise
 * academic performance, pass/fail counts, average SGPA, and grade distribution.
 */
public class ClassSummaryReport {
    private String department;
    private int semester;
    private int totalStudents;
    private int totalPassed;
    private int totalReappearance; // RA
    private int totalAttendanceShortage; // SA
    private int totalAbsent; // AB
    private double averageSgpa;
    private double passPercentage;
    private Map<String, Integer> gradeDistribution = new HashMap<>();

    public ClassSummaryReport() {
        gradeDistribution.put("O", 0);
        gradeDistribution.put("A+", 0);
        gradeDistribution.put("A", 0);
        gradeDistribution.put("B+", 0);
        gradeDistribution.put("B", 0);
        gradeDistribution.put("RA", 0);
        gradeDistribution.put("SA", 0);
        gradeDistribution.put("AB", 0);
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getTotalPassed() {
        return totalPassed;
    }

    public void setTotalPassed(int totalPassed) {
        this.totalPassed = totalPassed;
    }

    public int getTotalReappearance() {
        return totalReappearance;
    }

    public void setTotalReappearance(int totalReappearance) {
        this.totalReappearance = totalReappearance;
    }

    public int getTotalAttendanceShortage() {
        return totalAttendanceShortage;
    }

    public void setTotalAttendanceShortage(int totalAttendanceShortage) {
        this.totalAttendanceShortage = totalAttendanceShortage;
    }

    public int getTotalAbsent() {
        return totalAbsent;
    }

    public void setTotalAbsent(int totalAbsent) {
        this.totalAbsent = totalAbsent;
    }

    public double getAverageSgpa() {
        return averageSgpa;
    }

    public void setAverageSgpa(double averageSgpa) {
        this.averageSgpa = averageSgpa;
    }

    public double getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(double passPercentage) {
        this.passPercentage = passPercentage;
    }

    public Map<String, Integer> getGradeDistribution() {
        return gradeDistribution;
    }

    public void setGradeDistribution(Map<String, Integer> gradeDistribution) {
        this.gradeDistribution = gradeDistribution;
    }

    public void incrementGrade(String grade) {
        if (grade != null && gradeDistribution.containsKey(grade)) {
            gradeDistribution.put(grade, gradeDistribution.get(grade) + 1);
        }
    }
}
