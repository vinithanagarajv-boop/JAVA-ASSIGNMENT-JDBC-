import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Kongu Engineering College (Autonomous) - Regulation 2024
 * HtmlReportGenerator: Generates formal, publication-quality HTML Mark Sheets
 * suitable for viewing in browser, printing, and saving screenshot proofs.
 */
public class HtmlReportGenerator {

    public static String generateMarkSheetHtml(SemesterMarkSheet sheet, String outputPath) throws IOException {
        Student s = sheet.getStudent();
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n");
        sb.append("  <meta charset=\"UTF-8\">\n");
        sb.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        sb.append("  <title>KEC Mark Sheet - ").append(s.getRollNo()).append("</title>\n");
        sb.append("  <link href=\"https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;700;800&family=JetBrains+Mono:wght@400;600&display=swap\" rel=\"stylesheet\">\n");
        sb.append("  <style>\n");
        sb.append("    :root {\n");
        sb.append("      --primary: #1e3a8a;\n");
        sb.append("      --primary-light: #3b82f6;\n");
        sb.append("      --accent: #d97706;\n");
        sb.append("      --bg: #0f172a;\n");
        sb.append("      --card-bg: rgba(30, 41, 59, 0.85);\n");
        sb.append("      --text: #f8fafc;\n");
        sb.append("      --text-muted: #94a3b8;\n");
        sb.append("      --border: rgba(148, 163, 184, 0.15);\n");
        sb.append("      --pass: #10b981;\n");
        sb.append("      --fail: #ef4444;\n");
        sb.append("      --warning: #f59e0b;\n");
        sb.append("    }\n");
        sb.append("    * { box-sizing: border-box; margin: 0; padding: 0; }\n");
        sb.append("    body {\n");
        sb.append("      font-family: 'Outfit', sans-serif;\n");
        sb.append("      background: radial-gradient(circle at top left, #1e293b, #0f172a 80%);\n");
        sb.append("      color: var(--text);\n");
        sb.append("      padding: 40px 20px;\n");
        sb.append("      min-height: 100vh;\n");
        sb.append("    }\n");
        sb.append("    .container {\n");
        sb.append("      max-width: 1050px;\n");
        sb.append("      margin: 0 auto;\n");
        sb.append("      background: var(--card-bg);\n");
        sb.append("      backdrop-filter: blur(16px);\n");
        sb.append("      border: 1px solid var(--border);\n");
        sb.append("      border-radius: 20px;\n");
        sb.append("      padding: 40px;\n");
        sb.append("      box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5), 0 0 40px rgba(59, 130, 246, 0.1);\n");
        sb.append("    }\n");
        sb.append("    .header {\n");
        sb.append("      text-align: center;\n");
        sb.append("      border-bottom: 2px solid var(--border);\n");
        sb.append("      padding-bottom: 25px;\n");
        sb.append("      margin-bottom: 30px;\n");
        sb.append("    }\n");
        sb.append("    .header .college-title {\n");
        sb.append("      font-size: 26px;\n");
        sb.append("      font-weight: 800;\n");
        sb.append("      letter-spacing: 1px;\n");
        sb.append("      color: #60a5fa;\n");
        sb.append("      text-transform: uppercase;\n");
        sb.append("      margin-bottom: 6px;\n");
        sb.append("    }\n");
        sb.append("    .header .sub-title {\n");
        sb.append("      font-size: 14px;\n");
        sb.append("      color: var(--text-muted);\n");
        sb.append("      margin-bottom: 12px;\n");
        sb.append("    }\n");
        sb.append("    .header .badge {\n");
        sb.append("      display: inline-block;\n");
        sb.append("      background: linear-gradient(135deg, #2563eb, #1d4ed8);\n");
        sb.append("      color: #fff;\n");
        sb.append("      padding: 6px 16px;\n");
        sb.append("      border-radius: 9999px;\n");
        sb.append("      font-size: 12px;\n");
        sb.append("      font-weight: 600;\n");
        sb.append("      letter-spacing: 0.5px;\n");
        sb.append("      text-transform: uppercase;\n");
        sb.append("    }\n");
        sb.append("    .info-grid {\n");
        sb.append("      display: grid;\n");
        sb.append("      grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));\n");
        sb.append("      gap: 16px;\n");
        sb.append("      background: rgba(15, 23, 42, 0.6);\n");
        sb.append("      padding: 20px;\n");
        sb.append("      border-radius: 14px;\n");
        sb.append("      border: 1px solid var(--border);\n");
        sb.append("      margin-bottom: 30px;\n");
        sb.append("    }\n");
        sb.append("    .info-item .label {\n");
        sb.append("      font-size: 12px;\n");
        sb.append("      text-transform: uppercase;\n");
        sb.append("      color: var(--text-muted);\n");
        sb.append("      margin-bottom: 4px;\n");
        sb.append("      font-weight: 600;\n");
        sb.append("    }\n");
        sb.append("    .info-item .value {\n");
        sb.append("      font-size: 16px;\n");
        sb.append("      font-weight: 700;\n");
        sb.append("      color: #f1f5f9;\n");
        sb.append("    }\n");
        sb.append("    table {\n");
        sb.append("      width: 100%;\n");
        sb.append("      border-collapse: collapse;\n");
        sb.append("      margin-bottom: 30px;\n");
        sb.append("      font-size: 13px;\n");
        sb.append("    }\n");
        sb.append("    th {\n");
        sb.append("      background: rgba(37, 99, 235, 0.2);\n");
        sb.append("      color: #93c5fd;\n");
        sb.append("      padding: 12px 10px;\n");
        sb.append("      text-align: left;\n");
        sb.append("      font-weight: 700;\n");
        sb.append("      border-bottom: 2px solid var(--border);\n");
        sb.append("      white-space: nowrap;\n");
        sb.append("    }\n");
        sb.append("    td {\n");
        sb.append("      padding: 12px 10px;\n");
        sb.append("      border-bottom: 1px solid var(--border);\n");
        sb.append("      color: #e2e8f0;\n");
        sb.append("    }\n");
        sb.append("    tr:hover td { background: rgba(255, 255, 255, 0.02); }\n");
        sb.append("    .mono { font-family: 'JetBrains Mono', monospace; }\n");
        sb.append("    .grade-badge {\n");
        sb.append("      display: inline-block;\n");
        sb.append("      padding: 3px 10px;\n");
        sb.append("      border-radius: 6px;\n");
        sb.append("      font-weight: 800;\n");
        sb.append("      font-size: 12px;\n");
        sb.append("      text-align: center;\n");
        sb.append("    }\n");
        sb.append("    .grade-O  { background: rgba(16, 185, 129, 0.2); color: #34d399; border: 1px solid #10b981; }\n");
        sb.append("    .grade-AP { background: rgba(59, 130, 246, 0.2); color: #60a5fa; border: 1px solid #3b82f6; }\n");
        sb.append("    .grade-A  { background: rgba(99, 102, 241, 0.2); color: #818cf8; border: 1px solid #6366f1; }\n");
        sb.append("    .grade-BP { background: rgba(234, 179, 8, 0.2); color: #fde047; border: 1px solid #eab308; }\n");
        sb.append("    .grade-B  { background: rgba(249, 115, 22, 0.2); color: #fb923c; border: 1px solid #f97316; }\n");
        sb.append("    .grade-RA { background: rgba(239, 68, 68, 0.2); color: #f87171; border: 1px solid #ef4444; }\n");
        sb.append("    .grade-SA { background: rgba(168, 85, 247, 0.2); color: #c084fc; border: 1px solid #a855f7; }\n");
        sb.append("    .grade-AB { background: rgba(100, 116, 139, 0.3); color: #cbd5e1; border: 1px solid #64748b; }\n");
        sb.append("    .status-pass { color: var(--pass); font-weight: 700; }\n");
        sb.append("    .status-fail { color: var(--fail); font-weight: 700; }\n");
        sb.append("    .status-sa   { color: #c084fc; font-weight: 700; }\n");
        sb.append("    .summary-cards {\n");
        sb.append("      display: grid;\n");
        sb.append("      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));\n");
        sb.append("      gap: 16px;\n");
        sb.append("      margin-bottom: 30px;\n");
        sb.append("    }\n");
        sb.append("    .stat-card {\n");
        sb.append("      background: rgba(15, 23, 42, 0.7);\n");
        sb.append("      border: 1px solid var(--border);\n");
        sb.append("      border-radius: 12px;\n");
        sb.append("      padding: 16px;\n");
        sb.append("      text-align: center;\n");
        sb.append("    }\n");
        sb.append("    .stat-card .num {\n");
        sb.append("      font-size: 26px;\n");
        sb.append("      font-weight: 800;\n");
        sb.append("      color: #60a5fa;\n");
        sb.append("      font-family: 'JetBrains Mono', monospace;\n");
        sb.append("    }\n");
        sb.append("    .stat-card .desc {\n");
        sb.append("      font-size: 12px;\n");
        sb.append("      color: var(--text-muted);\n");
        sb.append("      text-transform: uppercase;\n");
        sb.append("      margin-top: 4px;\n");
        sb.append("      font-weight: 600;\n");
        sb.append("    }\n");
        sb.append("    .rules-footer {\n");
        sb.append("      background: rgba(15, 23, 42, 0.4);\n");
        sb.append("      border-radius: 12px;\n");
        sb.append("      padding: 16px;\n");
        sb.append("      font-size: 11px;\n");
        sb.append("      color: var(--text-muted);\n");
        sb.append("      line-height: 1.6;\n");
        sb.append("    }\n");
        sb.append("    .footer-signs {\n");
        sb.append("      display: flex;\n");
        sb.append("      justify-content: space-between;\n");
        sb.append("      margin-top: 30px;\n");
        sb.append("      padding-top: 25px;\n");
        sb.append("      border-top: 1px solid var(--border);\n");
        sb.append("      font-size: 13px;\n");
        sb.append("      color: var(--text-muted);\n");
        sb.append("    }\n");
        sb.append("  </style>\n");
        sb.append("</head>\n<body>\n");
        sb.append("  <div class=\"container\">\n");
        sb.append("    <div class=\"header\">\n");
        sb.append("      <div class=\"college-title\">Kongu Engineering College</div>\n");
        sb.append("      <div class=\"sub-title\">(Autonomous) | Affiliated to Anna University | Perundurai, Erode - 638060</div>\n");
        sb.append("      <div class=\"badge\">Regulation 2024 (R2024) - Semester Grade Sheet</div>\n");
        sb.append("    </div>\n");

        sb.append("    <div class=\"info-grid\">\n");
        sb.append("      <div class=\"info-item\"><div class=\"label\">Roll Number</div><div class=\"value mono\">").append(s.getRollNo()).append("</div></div>\n");
        sb.append("      <div class=\"info-item\"><div class=\"label\">Student Name</div><div class=\"value\">").append(s.getName()).append("</div></div>\n");
        sb.append("      <div class=\"info-item\"><div class=\"label\">Department</div><div class=\"value\">").append(s.getDepartment()).append("</div></div>\n");
        sb.append("      <div class=\"info-item\"><div class=\"label\">Semester</div><div class=\"value mono\">").append(sheet.getSemester()).append("</div></div>\n");
        sb.append("    </div>\n");

        sb.append("    <table>\n");
        sb.append("      <thead>\n");
        sb.append("        <tr>\n");
        sb.append("          <th>Code</th><th>Course Title</th><th>Type</th><th>Cr</th><th>Attd %</th>\n");
        sb.append("          <th>CIA1</th><th>CIA2</th><th>Asgn</th><th>CIA Sc</th><th>ESE Raw</th><th>ESE Sc</th><th>Total</th><th>Grade</th><th>GP</th><th>Status</th>\n");
        sb.append("        </tr>\n");
        sb.append("      </thead>\n");
        sb.append("      <tbody>\n");

        for (EvaluationResult r : sheet.getCourseResults()) {
            String grClass = "grade-" + r.getGrade().replace("+", "P");
            String statClass = "PASS".equalsIgnoreCase(r.getStatus()) ? "status-pass" : ("PREVENTED (SA)".equalsIgnoreCase(r.getStatus()) ? "status-sa" : "status-fail");

            sb.append("        <tr>\n");
            sb.append("          <td class=\"mono\"><strong>").append(r.getCourseCode()).append("</strong></td>\n");
            sb.append("          <td>").append(r.getCourseTitle()).append("</td>\n");
            sb.append("          <td>").append(r.getCourseType()).append("</td>\n");
            sb.append("          <td class=\"mono\">").append(r.getCredits()).append("</td>\n");
            sb.append("          <td class=\"mono\">").append(String.format("%.1f%%", r.getAttendancePercentage())).append("</td>\n");
            sb.append("          <td class=\"mono\">").append(r.getCia1() != null ? String.format("%.0f", r.getCia1()) : "-").append("</td>\n");
            sb.append("          <td class=\"mono\">").append(r.getCia2() != null ? String.format("%.0f", r.getCia2()) : "-").append("</td>\n");
            sb.append("          <td class=\"mono\">").append(r.getAssignment() != null ? String.format("%.0f", r.getAssignment()) : "-").append("</td>\n");
            sb.append("          <td class=\"mono\"><strong>").append(r.getCiaScaled() != null ? String.format("%.1f", r.getCiaScaled()) : "-").append("</strong></td>\n");
            sb.append("          <td class=\"mono\">").append(r.isAbsentEse() ? "AB" : (r.getEseRaw() != null ? String.format("%.0f", r.getEseRaw()) : "-")).append("</td>\n");
            sb.append("          <td class=\"mono\"><strong>").append(r.getEseScaled() != null ? String.format("%.1f", r.getEseScaled()) : "-").append("</strong></td>\n");
            sb.append("          <td class=\"mono\" style=\"font-size:14px; font-weight:800;\">").append(r.getTotalMarks() != null ? String.format("%.0f", r.getTotalMarks()) : "-").append("</td>\n");
            sb.append("          <td><span class=\"grade-badge ").append(grClass).append("\">").append(r.getGrade()).append("</span></td>\n");
            sb.append("          <td class=\"mono\" style=\"font-weight:700;\">").append(r.getGradePoint()).append("</td>\n");
            sb.append("          <td class=\"").append(statClass).append("\">").append(r.getStatus()).append("</td>\n");
            sb.append("        </tr>\n");
        }

        sb.append("      </tbody>\n");
        sb.append("    </table>\n");

        sb.append("    <div class=\"summary-cards\">\n");
        sb.append("      <div class=\"stat-card\"><div class=\"num\">").append(sheet.getTotalRegisteredCredits()).append("</div><div class=\"desc\">Credits Registered</div></div>\n");
        sb.append("      <div class=\"stat-card\"><div class=\"num\">").append(sheet.getTotalEarnedCredits()).append("</div><div class=\"desc\">Credits Earned</div></div>\n");
        sb.append("      <div class=\"stat-card\"><div class=\"num\" style=\"color:#34d399;\">").append(String.format("%.2f", sheet.getSgpa())).append("</div><div class=\"desc\">SGPA (Out of 10)</div></div>\n");
        sb.append("      <div class=\"stat-card\"><div class=\"num\" style=\"color:#fde047;\">").append(String.format("%.2f%%", sheet.getEquivalentPercentage())).append("</div><div class=\"desc\">Equivalent Percentage</div></div>\n");
        sb.append("      <div class=\"stat-card\"><div class=\"num\" style=\"font-size:18px;\">").append(sheet.getOverallResult()).append("</div><div class=\"desc\">Overall Semester Result</div></div>\n");
        sb.append("    </div>\n");

        sb.append("    <div class=\"rules-footer\">\n");
        sb.append("      <strong>Regulation 2024 Evaluation Framework:</strong><br>\n");
        sb.append("      &bull; <strong>THEORY:</strong> CIA 40% | ESE 60% | ESE Minimum Threshold = 45% (27/60) | Overall Pass Minimum = 50 Marks.<br>\n");
        sb.append("      &bull; <strong>PRACTICAL:</strong> CIA 60% | ESE 40% | ESE Minimum Threshold = 50% (20/40) | Overall Pass Minimum = 50 Marks.<br>\n");
        sb.append("      &bull; <strong>EMBEDDED:</strong> CIA 50% | ESE 50% | ESE Minimum Threshold = 45% (22.5/50) | Overall Pass Minimum = 50 Marks.<br>\n");
        sb.append("      &bull; <strong>Attendance Rules:</strong> &ge; 75% Eligible | 65-74.9% Conditionally Eligible (Requires Medical Condonation) | &lt; 65% Barred with Grade SA.<br>\n");
        sb.append("      &bull; <strong>SGPA Formula:</strong> SGPA = &sum;(Course Credits &times; Grade Points) / &sum;(Total Enrolled Credits) | Equivalent % = SGPA &times; 10.\n");
        sb.append("    </div>\n");

        sb.append("    <div class=\"footer-signs\">\n");
        sb.append("      <div>Prepared by: Academic Evaluation System</div>\n");
        sb.append("      <div>Verified by: Head of Department</div>\n");
        sb.append("      <div>Controller of Examinations (Autonomous)</div>\n");
        sb.append("    </div>\n");

        sb.append("  </div>\n");
        sb.append("</body>\n</html>\n");

        File file = new File(outputPath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(sb.toString());
        }
        return file.getAbsolutePath();
    }
}
