import java.util.List;

/**
 * Kongu Engineering College (Autonomous) - Regulation 2024 (R2024)
 * Evaluation Rules & Weightage Engine:
 * - THEORY:    CIA 40% | ESE 60% | ESE Min Threshold 45% (27/60)   | Overall Pass Min 50 Marks
 * - PRACTICAL: CIA 60% | ESE 40% | ESE Min Threshold 50% (20/40)   | Overall Pass Min 50 Marks
 * - EMBEDDED:  CIA 50% | ESE 50% | ESE Min Threshold 45% (22.5/50) | Overall Pass Min 50 Marks
 *
 * Attendance Eligibility:
 * - >= 75%: Eligible for ESE
 * - 65% to 74.9%: Conditionally eligible (Requires Medical Condonation approval)
 * - < 65%: Award SA grade (GP: 0), Barred from ESE
 *
 * 10-Point Grading:
 * - O (91-100)  -> GP: 10
 * - A+ (81-90)  -> GP: 9
 * - A (71-80)   -> GP: 8
 * - B+ (61-70)  -> GP: 7
 * - B (50-60)   -> GP: 6
 * - RA (<50 or ESE failed) -> GP: 0
 * - SA (Shortage of Attendance) -> GP: 0
 * - AB (Absent in ESE) -> GP: 0
 */
public class EvaluationEngine {

    /**
     * Compute raw CIA marks out of 100 based on incremental Phase 1 and Phase 2.
     * KEC Model: CIA1 (35%) + CIA2 (35%) + Assignment/Quiz (30%)
     */
    public static double computeRawCia100(Double cia1, Double cia2, Double assignment) {
        double c1 = (cia1 != null) ? cia1 : 0.0;
        double c2 = (cia2 != null) ? cia2 : 0.0;
        double as = (assignment != null) ? assignment : 0.0;

        if (cia1 != null && cia2 != null && assignment != null) {
            return (c1 * 0.35) + (c2 * 0.35) + (as * 0.30);
        } else if (cia1 != null && cia2 != null) {
            // Mid-semester partial scale: average of CIA 1 and 2
            return (c1 + c2) / 2.0;
        } else if (cia1 != null) {
            return c1;
        }
        return 0.0;
    }

    /**
     * Scales CIA internal mark according to course type weightage.
     */
    public static double scaleCia(double rawCia100, CourseType type) {
        return Math.round((rawCia100 * type.getCiaWeightage()) * 100.0) / 100.0;
    }

    /**
     * Scales ESE mark according to course type weightage.
     */
    public static double scaleEse(double rawEse100, CourseType type) {
        return Math.round((rawEse100 * type.getEseWeightage()) * 100.0) / 100.0;
    }

    /**
     * Evaluates a single course enrollment under KEC R2024 regulations.
     */
    public static EvaluationResult evaluateCourse(Enrollment enrollment, AssessmentMark mark, Course course) {
        EvaluationResult result = new EvaluationResult();
        result.setCourseCode(course.getCourseCode());
        result.setCourseTitle(course.getTitle());
        result.setCredits(course.getCredits());
        result.setCourseType(course.getCourseType());

        double attendancePct = enrollment.getAttendancePercentage();
        result.setAttendancePercentage(attendancePct);
        result.setMedicalCondonation(enrollment.isMedicalCondonation());

        CourseType type = course.getCourseType();
        Double cia1 = mark != null ? mark.getCia1Marks() : null;
        Double cia2 = mark != null ? mark.getCia2Marks() : null;
        Double assign = mark != null ? mark.getAssignmentMarks() : null;
        boolean isAbsent = mark != null && mark.isAbsentEse();
        Double rawEse = mark != null ? mark.getEseMarks() : null;

        result.setCia1(cia1);
        result.setCia2(cia2);
        result.setAssignment(assign);
        result.setAbsentEse(isAbsent);
        result.setEseRaw(rawEse);

        // 1. Calculate Internal CIA
        double rawCia100 = computeRawCia100(cia1, cia2, assign);
        double ciaScaled = scaleCia(rawCia100, type);
        result.setRawCia100(rawCia100);
        result.setCiaScaled(ciaScaled);

        // 2. Attendance Rule Verification
        // <65% -> Barred (SA)
        // 65% - 74.9% -> Conditionally eligible (Needs medical condonation)
        // >= 75% -> Eligible
        boolean isEligible = false;
        if (attendancePct >= 75.0) {
            isEligible = true;
        } else if (attendancePct >= 65.0) {
            isEligible = enrollment.isMedicalCondonation();
        } else {
            isEligible = false;
        }
        result.setEligible(isEligible);

        // Rule: If not eligible due to attendance shortage -> Award SA
        if (!isEligible) {
            result.setGrade("SA");
            result.setGradePoint(0);
            result.setEseScaled(0.0);
            result.setTotalMarks(ciaScaled);
            result.setStatus("PREVENTED (SA)");
            result.setRemarks(attendancePct < 65.0 
                ? "Barred: Attendance < 65% (Shortage)" 
                : "Barred: 65-74.9% without approved medical condonation");
            return result;
        }

        // Rule: If marked Absent in ESE -> Award AB
        if (isAbsent) {
            result.setGrade("AB");
            result.setGradePoint(0);
            result.setEseScaled(0.0);
            result.setTotalMarks(ciaScaled);
            result.setStatus("FAIL (AB)");
            result.setRemarks("Absent in End Semester Examination");
            return result;
        }

        // If ESE marks not yet entered (Phase 1 or Phase 2 evaluation in progress)
        if (rawEse == null) {
            result.setGrade("PEND");
            result.setGradePoint(0);
            result.setEseScaled(null);
            result.setTotalMarks(ciaScaled);
            result.setStatus("INCOMPLETE");
            result.setRemarks("ESE Marks Pending");
            return result;
        }

        // 3. ESE Scaling and Minimum Threshold Check
        double eseScaled = scaleEse(rawEse, type);
        result.setEseScaled(eseScaled);

        double total = Math.round(ciaScaled + eseScaled);
        result.setTotalMarks(total);

        double eseMinThreshold = type.getEseMinScaledScore(); // 27 (Theory), 20 (Practical), 22.5 (Embedded)
        double passMinTotal = type.getPassMinimumTotal();     // 50.0

        boolean failedEseThreshold = eseScaled < eseMinThreshold;
        boolean failedTotalPassMin = total < passMinTotal;

        if (failedEseThreshold || failedTotalPassMin) {
            result.setGrade("RA");
            result.setGradePoint(0);
            result.setStatus("FAIL (RA)");
            if (failedEseThreshold && failedTotalPassMin) {
                result.setRemarks(String.format("Failed ESE Min (%.1f/%.1f) & Total Pass Min (%.1f/50)", 
                    eseScaled, eseMinThreshold, total));
            } else if (failedEseThreshold) {
                result.setRemarks(String.format("Failed ESE Min Threshold: %.1f < %.1f", 
                    eseScaled, eseMinThreshold));
            } else {
                result.setRemarks(String.format("Failed Overall Pass Min: %.1f < 50", total));
            }
            return result;
        }

        // 4. 10-Point Grade Assignment
        // O: 91-100 (GP 10), A+: 81-90 (GP 9), A: 71-80 (GP 8), B+: 61-70 (GP 7), B: 50-60 (GP 6)
        if (total >= 91.0) {
            result.setGrade("O");
            result.setGradePoint(10);
        } else if (total >= 81.0) {
            result.setGrade("A+");
            result.setGradePoint(9);
        } else if (total >= 71.0) {
            result.setGrade("A");
            result.setGradePoint(8);
        } else if (total >= 61.0) {
            result.setGrade("B+");
            result.setGradePoint(7);
        } else { // 50 to 60
            result.setGrade("B");
            result.setGradePoint(6);
        }

        result.setStatus("PASS");
        result.setRemarks("Successfully Passed");
        return result;
    }

    /**
     * Calculates SGPA:
     * SGPA = Sum(Credits * Grade Points Earned) / Sum(Total Enrolled Credits)
     */
    public static double calculateSGPA(List<EvaluationResult> results) {
        if (results == null || results.isEmpty()) return 0.0;

        int totalCredits = 0;
        int weightedPoints = 0;

        for (EvaluationResult r : results) {
            totalCredits += r.getCredits();
            weightedPoints += (r.getCredits() * r.getGradePoint());
        }

        if (totalCredits == 0) return 0.0;
        double sgpa = (double) weightedPoints / totalCredits;
        return Math.round(sgpa * 100.0) / 100.0;
    }

    /**
     * Calculates Equivalent Percentage:
     * Percentage = SGPA * 10.0
     */
    public static double calculatePercentage(double sgpa) {
        return Math.round((sgpa * 10.0) * 100.0) / 100.0;
    }
}
