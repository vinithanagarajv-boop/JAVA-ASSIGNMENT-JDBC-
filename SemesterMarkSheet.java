import java.util.ArrayList;
import java.util.List;

/**
 * Kongu Engineering College (Autonomous) - Regulation 2024
 * SemesterMarkSheet Model representing the formal student grade sheet report.
 */
public class SemesterMarkSheet {
    private Student student;
    private int semester;
    private List<EvaluationResult> courseResults = new ArrayList<>();
    private int totalRegisteredCredits;
    private int totalEarnedCredits;
    private double sgpa;
    private double equivalentPercentage;
    private String overallResult; // "ALL PASS", "RE-APPEARANCE (ARREAR)", "PREVENTED (SA)"

    public SemesterMarkSheet() {
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public List<EvaluationResult> getCourseResults() {
        return courseResults;
    }

    public void setCourseResults(List<EvaluationResult> courseResults) {
        this.courseResults = courseResults;
    }

    public void addCourseResult(EvaluationResult res) {
        this.courseResults.add(res);
    }

    public int getTotalRegisteredCredits() {
        return totalRegisteredCredits;
    }

    public void setTotalRegisteredCredits(int totalRegisteredCredits) {
        this.totalRegisteredCredits = totalRegisteredCredits;
    }

    public int getTotalEarnedCredits() {
        return totalEarnedCredits;
    }

    public void setTotalEarnedCredits(int totalEarnedCredits) {
        this.totalEarnedCredits = totalEarnedCredits;
    }

    public double getSgpa() {
        return sgpa;
    }

    public void setSgpa(double sgpa) {
        this.sgpa = sgpa;
    }

    public double getEquivalentPercentage() {
        return equivalentPercentage;
    }

    public void setEquivalentPercentage(double equivalentPercentage) {
        this.equivalentPercentage = equivalentPercentage;
    }

    public String getOverallResult() {
        return overallResult;
    }

    public void setOverallResult(String overallResult) {
        this.overallResult = overallResult;
    }

    public void computeSummary() {
        this.totalRegisteredCredits = 0;
        this.totalEarnedCredits = 0;
        boolean hasArrear = false;
        boolean hasSA = false;

        for (EvaluationResult r : courseResults) {
            totalRegisteredCredits += r.getCredits();
            if ("PASS".equalsIgnoreCase(r.getStatus())) {
                totalEarnedCredits += r.getCredits();
            } else if ("SA".equalsIgnoreCase(r.getGrade())) {
                hasSA = true;
            } else {
                hasArrear = true;
            }
        }

        this.sgpa = EvaluationEngine.calculateSGPA(courseResults);
        this.equivalentPercentage = EvaluationEngine.calculatePercentage(this.sgpa);

        if (hasSA) {
            this.overallResult = "PREVENTED (SA)";
        } else if (hasArrear) {
            this.overallResult = "RE-APPEARANCE";
        } else {
            this.overallResult = "ALL CLEAR / PASS";
        }
    }
}
