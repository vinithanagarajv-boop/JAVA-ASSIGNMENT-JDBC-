import java.util.List;
import java.util.Map;

/**
 * Kongu Engineering College (Autonomous) - Regulation 2024
 * ConsoleReportFormatter: Formats Academic Reports into Clean Boxed Text Tables.
 */
public class ConsoleReportFormatter {

    public static void printSemesterMarkSheet(SemesterMarkSheet sheet) {
        if (sheet == null || sheet.getStudent() == null) {
            System.out.println("\n[!] No academic records found for the specified student and semester.");
            return;
        }

        Student s = sheet.getStudent();
        System.out.println("\n" + "=".repeat(120));
        System.out.println("                   KONGU ENGINEERING COLLEGE (AUTONOMOUS), PERUNDURAI, ERODE - 638060");
        System.out.println("                             OFFICE OF THE CONTROLLER OF EXAMINATIONS");
        System.out.println("                        ACADEMIC GRADE SHEET - REGULATION 2024 (R2024)");
        System.out.println("=".repeat(120));
        System.out.printf("  Roll Number : %-15s   Student Name : %-30s   Semester   : %d\n", 
                s.getRollNo(), s.getName(), sheet.getSemester());
        System.out.printf("  Department  : %-15s   Degree       : B.Tech / B.E.                   Academic Yr: 2024-2025\n", 
                s.getDepartment());
        System.out.println("-".repeat(120));

        System.out.printf("| %-8s | %-28s | %-4s | %-8s | %-5s | %-5s | %-5s | %-7s | %-6s | %-6s | %-5s | %-3s | %-2s | %-10s |\n",
                "Code", "Course Title", "Type", "Attd %", "CIA1", "CIA2", "Asgn", "CIA(Sc)", "ESE(R)", "ESE(Sc)", "Total", "Grd", "GP", "Status");
        System.out.println("|" + "-".repeat(10) + "|" + "-".repeat(30) + "|" + "-".repeat(6) + "|" + "-".repeat(10) + "|"
                + "-".repeat(7) + "|" + "-".repeat(7) + "|" + "-".repeat(7) + "|" + "-".repeat(9) + "|"
                + "-".repeat(8) + "|" + "-".repeat(8) + "|" + "-".repeat(7) + "|" + "-".repeat(5) + "|" + "-".repeat(4) + "|" + "-".repeat(12) + "|");

        for (EvaluationResult r : sheet.getCourseResults()) {
            String shortTitle = r.getCourseTitle().length() > 28 ? r.getCourseTitle().substring(0, 25) + "..." : r.getCourseTitle();
            String typeStr = r.getCourseType() == CourseType.THEORY ? "THY" : (r.getCourseType() == CourseType.PRACTICAL ? "PRC" : "EMB");
            String cia1Str = r.getCia1() != null ? String.format("%.1f", r.getCia1()) : "-";
            String cia2Str = r.getCia2() != null ? String.format("%.1f", r.getCia2()) : "-";
            String asgnStr = r.getAssignment() != null ? String.format("%.1f", r.getAssignment()) : "-";
            String ciaScStr = r.getCiaScaled() != null ? String.format("%.1f", r.getCiaScaled()) : "-";
            String eseRawStr = r.isAbsentEse() ? "AB" : (r.getEseRaw() != null ? String.format("%.1f", r.getEseRaw()) : "-");
            String eseScStr = r.getEseScaled() != null ? String.format("%.1f", r.getEseScaled()) : "-";
            String totalStr = r.getTotalMarks() != null ? String.format("%.0f", r.getTotalMarks()) : "-";
            String gpStr = String.valueOf(r.getGradePoint());

            System.out.printf("| %-8s | %-28s | %-4s | %6.1f%%  | %-5s | %-5s | %-5s | %-7s | %-6s | %-6s | %-5s | %-3s | %-2s | %-10s |\n",
                    r.getCourseCode(), shortTitle, typeStr, r.getAttendancePercentage(),
                    cia1Str, cia2Str, asgnStr, ciaScStr, eseRawStr, eseScStr, totalStr,
                    r.getGrade(), gpStr, r.getStatus());
        }

        System.out.println("=".repeat(120));
        System.out.printf("  TOTAL CREDITS REGISTERED : %-4d            TOTAL CREDITS EARNED : %-4d\n", 
                sheet.getTotalRegisteredCredits(), sheet.getTotalEarnedCredits());
        System.out.printf("  SEMESTER GRADE POINT AVERAGE (SGPA) : %5.2f / 10.00\n", sheet.getSgpa());
        System.out.printf("  EQUIVALENT PERCENTAGE               : %5.2f %%\n", sheet.getEquivalentPercentage());
        System.out.printf("  OVERALL SEMESTER STATUS             : %s\n", sheet.getOverallResult());
        System.out.println("=".repeat(120));
        System.out.println("  Weightage Rules: THEORY: CIA 40% (Min ESE 27/60) | PRACTICAL: CIA 60% (Min ESE 20/40) | EMBEDDED: CIA 50% (Min ESE 22.5/50)");
        System.out.println("  Grades: O (91-100), A+ (81-90), A (71-80), B+ (61-70), B (50-60), RA (Re-Appearance <50), SA (Shortage), AB (Absent)");
        System.out.println("=".repeat(120) + "\n");
    }

    public static void printDefaulterReport(List<DefaulterRecord> defaulters, Integer semester, String dept) {
        System.out.println("\n" + "=".repeat(110));
        System.out.println("                   KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024");
        System.out.printf("                    ATTENDANCE DEFAULTER REPORT (SEMESTER: %s | DEPT: %s)\n",
                semester != null ? semester.toString() : "ALL", dept != null ? dept : "ALL");
        System.out.println("            Mandatory Threshold: >= 75% Eligible | 65-74.9% Needs Condonation | < 65% Barred (SA)");
        System.out.println("=".repeat(110));

        if (defaulters.isEmpty()) {
            System.out.println("  [+] No attendance defaulters found! All enrolled students satisfy eligibility criteria.");
            System.out.println("=".repeat(110) + "\n");
            return;
        }

        System.out.printf("| %-10s | %-20s | %-6s | %-9s | %-6s | %-8s | %-8s | %-5s | %-24s |\n",
                "Roll No", "Student Name", "Dept", "Course", "Held", "Attended", "Attd %", "Grade", "Action / Status");
        System.out.println("|" + "-".repeat(12) + "|" + "-".repeat(22) + "|" + "-".repeat(8) + "|" + "-".repeat(11) + "|"
                + "-".repeat(8) + "|" + "-".repeat(10) + "|" + "-".repeat(10) + "|" + "-".repeat(7) + "|" + "-".repeat(26) + "|");

        for (DefaulterRecord d : defaulters) {
            String name = d.getStudentName().length() > 20 ? d.getStudentName().substring(0, 17) + "..." : d.getStudentName();
            String reason = d.getReason().length() > 24 ? d.getReason().substring(0, 21) + "..." : d.getReason();
            System.out.printf("| %-10s | %-20s | %-6s | %-9s | %-6d | %-8d | %6.1f%%  | %-5s | %-24s |\n",
                    d.getRollNo(), name, d.getDepartment(), d.getCourseCode(),
                    d.getClassesHeld(), d.getClassesAttended(), d.getAttendancePercentage(),
                    d.getFlaggedGrade(), reason);
        }

        System.out.println("=".repeat(110));
        System.out.printf("  Total Defaulter Cases Flagged with SA Grade: %d\n", defaulters.size());
        System.out.println("=".repeat(110) + "\n");
    }

    public static void printClassSummaryReport(ClassSummaryReport summary) {
        System.out.println("\n" + "=".repeat(90));
        System.out.println("              KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024");
        System.out.printf("                    CLASS PERFORMANCE & RESULT SUMMARY REPORT\n");
        System.out.printf("                       Department: %-6s | Semester: %d\n", 
                summary.getDepartment(), summary.getSemester());
        System.out.println("=".repeat(90));

        System.out.printf("  Total Enrolled Students  : %-4d\n", summary.getTotalStudents());
        System.out.printf("  All-Clear Passed Count   : %-4d\n", summary.getTotalPassed());
        System.out.printf("  Re-Appearance (RA) Count : %-4d\n", summary.getTotalReappearance());
        System.out.printf("  Attendance Shortage (SA) : %-4d\n", summary.getTotalAttendanceShortage());
        System.out.printf("  Absent in ESE (AB) Count : %-4d\n", summary.getTotalAbsent());
        System.out.println("-".repeat(90));
        System.out.printf("  CLASS PASS PERCENTAGE    : %6.2f %%\n", summary.getPassPercentage());
        System.out.printf("  CLASS AVERAGE SGPA       : %6.2f / 10.00 (Equivalent: %.2f %%)\n", 
                summary.getAverageSgpa(), summary.getAverageSgpa() * 10.0);
        System.out.println("-".repeat(90));
        System.out.println("  10-Point Grade Distribution Across All Course Enrollments:");
        Map<String, Integer> dist = summary.getGradeDistribution();
        System.out.printf("    [O] Outstanding (91-100) : %-4d    [B+] Good (61-70)    : %-4d\n", dist.getOrDefault("O", 0), dist.getOrDefault("B+", 0));
        System.out.printf("    [A+] Excellent  (81-90)  : %-4d    [B]  Average (50-60) : %-4d\n", dist.getOrDefault("A+", 0), dist.getOrDefault("B", 0));
        System.out.printf("    [A] Very Good   (71-80)  : %-4d    [RA] Arrear (<50)    : %-4d\n", dist.getOrDefault("A", 0), dist.getOrDefault("RA", 0));
        System.out.printf("    [SA] Shortage of Attd    : %-4d    [AB] Absent in ESE   : %-4d\n", dist.getOrDefault("SA", 0), dist.getOrDefault("AB", 0));
        System.out.println("=".repeat(90) + "\n");
    }
}
