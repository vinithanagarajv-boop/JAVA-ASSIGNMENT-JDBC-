import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Kongu Engineering College (Autonomous) - Regulation 2024
 * AcademicDAO: Data Access Object for Course Catalog Management,
 * Phased Incremental Marks Entry (Phase 1, Phase 2, Phase 3),
 * Evaluation Engine synchronization, and Formal Academic Reporting.
 */
public class AcademicDAO {

    // ========================================================================
    // 1. Course Catalog Management
    // ========================================================================

    public boolean addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_code, title, credits, course_type, semester) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getCourseCode().trim().toUpperCase());
            ps.setString(2, course.getTitle().trim());
            ps.setInt(3, course.getCredits());
            ps.setString(4, course.getCourseType().name());
            ps.setInt(5, course.getSemester());
            return ps.executeUpdate() > 0;
        }
    }

    public Course getCourse(String courseCode) throws SQLException {
        String sql = "SELECT course_code, title, credits, course_type, semester FROM courses WHERE course_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                            rs.getString("course_code"),
                            rs.getString("title"),
                            rs.getInt("credits"),
                            CourseType.fromString(rs.getString("course_type")),
                            rs.getInt("semester")
                    );
                }
            }
        }
        return null;
    }

    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT course_code, title, credits, course_type, semester FROM courses ORDER BY course_code";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Course(
                        rs.getString("course_code"),
                        rs.getString("title"),
                        rs.getInt("credits"),
                        CourseType.fromString(rs.getString("course_type")),
                        rs.getInt("semester")
                ));
            }
        }
        return list;
    }

    public List<Course> getCoursesBySemester(int semester) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT course_code, title, credits, course_type, semester FROM courses WHERE semester = ? ORDER BY course_code";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, semester);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Course(
                            rs.getString("course_code"),
                            rs.getString("title"),
                            rs.getInt("credits"),
                            CourseType.fromString(rs.getString("course_type")),
                            rs.getInt("semester")
                    ));
                }
            }
        }
        return list;
    }

    // ========================================================================
    // 2. Incremental Phased Marks Entry ("Then and Now")
    // ========================================================================

    /**
     * Phase 1 (Mid-Semester): Record CIA Test 1 (0-100) and CIA Test 2 (0-100).
     */
    public boolean recordPhase1Marks(int enrollmentId, double cia1, double cia2) throws SQLException {
        if (cia1 < 0 || cia1 > 100 || cia2 < 0 || cia2 > 100) {
            throw new IllegalArgumentException("CIA marks must be within the range [0.0 - 100.0]");
        }
        String sql = "INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks) "
                + "VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE cia1_marks = VALUES(cia1_marks), cia2_marks = VALUES(cia2_marks)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setDouble(2, cia1);
            ps.setDouble(3, cia2);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                recalculateAndStoreEvaluation(enrollmentId);
            }
            return ok;
        }
    }

    /**
     * Phase 2 (End of Semester CIA): Record Assignments / Quizzes / Lab Evaluations (0-100).
     */
    public boolean recordPhase2Marks(int enrollmentId, double assignment) throws SQLException {
        if (assignment < 0 || assignment > 100) {
            throw new IllegalArgumentException("Assignment/Quiz mark must be within the range [0.0 - 100.0]");
        }
        String sql = "INSERT INTO assessment_marks (enrollment_id, assignment_marks) "
                + "VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE assignment_marks = VALUES(assignment_marks)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setDouble(2, assignment);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                recalculateAndStoreEvaluation(enrollmentId);
            }
            return ok;
        }
    }

    /**
     * Phase 3 (Post Semester): Record End Semester Examination (ESE) Marks (0-100) or Mark Absent (AB).
     * Enforces attendance eligibility rules before allowing ESE marks.
     */
    public boolean recordPhase3Marks(int enrollmentId, boolean isAbsent, Double eseMarks) throws SQLException {
        // Enforce attendance eligibility rule first!
        Enrollment enrollment = getEnrollmentById(enrollmentId);
        if (enrollment == null) {
            throw new IllegalArgumentException("Enrollment ID " + enrollmentId + " does not exist.");
        }

        if (!enrollment.isEligibleForEse()) {
            // Cannot sit for ESE: automatically awarded SA grade
            isAbsent = false;
            eseMarks = null;
        } else if (!isAbsent) {
            if (eseMarks == null || eseMarks < 0 || eseMarks > 100) {
                throw new IllegalArgumentException("ESE marks must be between 0.0 and 100.0 when student is present.");
            }
        }

        String sql = "INSERT INTO assessment_marks (enrollment_id, is_absent_ese, ese_marks) "
                + "VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE is_absent_ese = VALUES(is_absent_ese), ese_marks = VALUES(ese_marks)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setBoolean(2, isAbsent);
            if (isAbsent || eseMarks == null) {
                ps.setNull(3, Types.DECIMAL);
            } else {
                ps.setDouble(3, eseMarks);
            }
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                recalculateAndStoreEvaluation(enrollmentId);
            }
            return ok;
        }
    }

    /**
     * Retrieves assessment marks row for an enrollment.
     */
    public AssessmentMark getMarksByEnrollment(int enrollmentId) throws SQLException {
        String sql = "SELECT mark_id, enrollment_id, cia1_marks, cia2_marks, assignment_marks, "
                + "is_absent_ese, ese_marks, cia_scaled, ese_scaled, total_marks, grade, grade_point, status "
                + "FROM assessment_marks WHERE enrollment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AssessmentMark m = new AssessmentMark(rs.getInt("enrollment_id"));
                    m.setMarkId(rs.getInt("mark_id"));

                    BigDecimal c1 = rs.getBigDecimal("cia1_marks");
                    m.setCia1Marks(c1 != null ? c1.doubleValue() : null);

                    BigDecimal c2 = rs.getBigDecimal("cia2_marks");
                    m.setCia2Marks(c2 != null ? c2.doubleValue() : null);

                    BigDecimal as = rs.getBigDecimal("assignment_marks");
                    m.setAssignmentMarks(as != null ? as.doubleValue() : null);

                    m.setAbsentEse(rs.getBoolean("is_absent_ese"));

                    BigDecimal ese = rs.getBigDecimal("ese_marks");
                    m.setEseMarks(ese != null ? ese.doubleValue() : null);

                    BigDecimal cs = rs.getBigDecimal("cia_scaled");
                    m.setCiaScaled(cs != null ? cs.doubleValue() : null);

                    BigDecimal es = rs.getBigDecimal("ese_scaled");
                    m.setEseScaled(es != null ? es.doubleValue() : null);

                    BigDecimal tot = rs.getBigDecimal("total_marks");
                    m.setTotalMarks(tot != null ? tot.doubleValue() : null);

                    m.setGrade(rs.getString("grade"));
                    int gp = rs.getInt("grade_point");
                    m.setGradePoint(rs.wasNull() ? null : gp);
                    m.setStatus(rs.getString("status"));
                    return m;
                }
            }
        }
        return null;
    }

    /**
     * Synchronizes evaluation engine with database records.
     */
    public void recalculateAndStoreEvaluation(int enrollmentId) throws SQLException {
        Enrollment enrollment = getEnrollmentById(enrollmentId);
        if (enrollment == null) return;

        Course course = getCourse(enrollment.getCourseCode());
        if (course == null) return;

        AssessmentMark mark = getMarksByEnrollment(enrollmentId);
        EvaluationResult eval = EvaluationEngine.evaluateCourse(enrollment, mark, course);

        String updateSql = "UPDATE assessment_marks SET "
                + "cia_scaled = ?, ese_scaled = ?, total_marks = ?, grade = ?, grade_point = ?, status = ? "
                + "WHERE enrollment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            if (eval.getCiaScaled() != null) ps.setDouble(1, eval.getCiaScaled()); else ps.setNull(1, Types.DECIMAL);
            if (eval.getEseScaled() != null) ps.setDouble(2, eval.getEseScaled()); else ps.setNull(2, Types.DECIMAL);
            if (eval.getTotalMarks() != null) ps.setDouble(3, eval.getTotalMarks()); else ps.setNull(3, Types.DECIMAL);
            ps.setString(4, eval.getGrade());
            ps.setInt(5, eval.getGradePoint());
            ps.setString(6, eval.getStatus());
            ps.setInt(7, enrollmentId);
            ps.executeUpdate();
        }
    }

    // ========================================================================
    // 3. Expected Reporting Outputs
    // ========================================================================

    /**
     * Report 1: Student Semester Mark Sheet
     * Generates a comprehensive formal academic report with internal scores,
     * external scores, total, grade, pass/fail status, SGPA, and percentage.
     */
    public SemesterMarkSheet getStudentSemesterMarkSheet(String rollNo, int semester) throws SQLException {
        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.getStudentByRollNo(rollNo);
        if (student == null) {
            return null;
        }

        SemesterMarkSheet sheet = new SemesterMarkSheet();
        sheet.setStudent(student);
        sheet.setSemester(semester);

        List<Enrollment> enrollments = studentDAO.getEnrollmentsByStudent(rollNo, semester);
        for (Enrollment e : enrollments) {
            Course c = getCourse(e.getCourseCode());
            AssessmentMark m = getMarksByEnrollment(e.getEnrollmentId());
            EvaluationResult res = EvaluationEngine.evaluateCourse(e, m, c);
            sheet.addCourseResult(res);
        }

        sheet.computeSummary();
        return sheet;
    }

    /**
     * Report 2: Attendance Defaulter Report
     * Lists all students whose attendance is < 65% (or 65-74.9% without approved condonation),
     * flagged with grade SA.
     */
    public List<DefaulterRecord> getAttendanceDefaulters(Integer semester, String department) throws SQLException {
        List<DefaulterRecord> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT e.enrollment_id, e.roll_no, s.name as student_name, s.department, "
                + "e.course_code, c.title as course_title, e.classes_held, e.classes_attended, e.medical_condonation "
                + "FROM enrollments e "
                + "JOIN students s ON e.roll_no = s.roll_no "
                + "JOIN courses c ON e.course_code = c.course_code "
                + "WHERE ( (e.classes_held > 0 AND (e.classes_attended * 100.0 / e.classes_held) < 65.0) "
                + "     OR (e.classes_held > 0 AND (e.classes_attended * 100.0 / e.classes_held) < 75.0 AND e.medical_condonation = FALSE) ) "
        );

        if (semester != null) {
            sql.append(" AND e.semester = ?");
        }
        if (department != null && !department.trim().isEmpty() && !department.equalsIgnoreCase("ALL")) {
            sql.append(" AND s.department = ?");
        }
        sql.append(" ORDER BY s.department, e.roll_no, e.course_code");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (semester != null) {
                ps.setInt(idx++, semester);
            }
            if (department != null && !department.trim().isEmpty() && !department.equalsIgnoreCase("ALL")) {
                ps.setString(idx++, department.trim().toUpperCase());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DefaulterRecord d = new DefaulterRecord();
                    d.setRollNo(rs.getString("roll_no"));
                    d.setStudentName(rs.getString("student_name"));
                    d.setDepartment(rs.getString("department"));
                    d.setCourseCode(rs.getString("course_code"));
                    d.setCourseTitle(rs.getString("course_title"));
                    int held = rs.getInt("classes_held");
                    int attended = rs.getInt("classes_attended");
                    d.setClassesHeld(held);
                    d.setClassesAttended(attended);
                    double pct = held > 0 ? (attended * 100.0) / held : 0.0;
                    d.setAttendancePercentage(Math.round(pct * 10.0) / 10.0);
                    d.setMedicalCondonation(rs.getBoolean("medical_condonation"));
                    d.setFlaggedGrade("SA");
                    if (pct < 65.0) {
                        d.setReason(String.format("Critical Shortage (%.1f%% < 65.0%%). Barred from ESE.", pct));
                    } else {
                        d.setReason(String.format("Borderline Shortage (%.1f%%). Medical Condonation NOT approved.", pct));
                    }
                    list.add(d);
                }
            }
        }
        return list;
    }

    /**
     * Report 3: Class Performance & Result Summary
     * Department-wise pass/fail count, class average SGPA, and grade distribution.
     */
    public ClassSummaryReport getClassPerformanceSummary(int semester, String department) throws SQLException {
        ClassSummaryReport summary = new ClassSummaryReport();
        summary.setDepartment(department);
        summary.setSemester(semester);

        StudentDAO studentDAO = new StudentDAO();
        List<Student> students = department.equalsIgnoreCase("ALL")
                ? studentDAO.getAllStudents()
                : studentDAO.getStudentsByDepartment(department);

        List<Student> semStudents = new ArrayList<>();
        for (Student s : students) {
            if (s.getSemester() == semester) {
                semStudents.add(s);
            }
        }
        summary.setTotalStudents(semStudents.size());

        if (semStudents.isEmpty()) {
            return summary;
        }

        double totalSgpaSum = 0.0;
        int sgpaCount = 0;
        int passCount = 0;
        int raCount = 0;
        int saCount = 0;
        int abCount = 0;

        for (Student s : semStudents) {
            SemesterMarkSheet sheet = getStudentSemesterMarkSheet(s.getRollNo(), semester);
            if (sheet == null || sheet.getCourseResults().isEmpty()) {
                continue;
            }

            boolean studentAllPass = true;
            boolean hasSa = false;
            boolean hasAb = false;

            for (EvaluationResult r : sheet.getCourseResults()) {
                String gr = r.getGrade();
                summary.incrementGrade(gr);

                if ("SA".equalsIgnoreCase(gr)) {
                    hasSa = true;
                    studentAllPass = false;
                } else if ("AB".equalsIgnoreCase(gr)) {
                    hasAb = true;
                    studentAllPass = false;
                } else if ("RA".equalsIgnoreCase(gr)) {
                    studentAllPass = false;
                }
            }

            if (studentAllPass) {
                passCount++;
            } else if (hasSa) {
                saCount++;
            } else if (hasAb) {
                abCount++;
            } else {
                raCount++;
            }

            totalSgpaSum += sheet.getSgpa();
            sgpaCount++;
        }

        summary.setTotalPassed(passCount);
        summary.setTotalReappearance(raCount);
        summary.setTotalAttendanceShortage(saCount);
        summary.setTotalAbsent(abCount);

        double avgSgpa = sgpaCount > 0 ? (totalSgpaSum / sgpaCount) : 0.0;
        summary.setAverageSgpa(Math.round(avgSgpa * 100.0) / 100.0);

        double passPct = semStudents.size() > 0 ? (passCount * 100.0 / semStudents.size()) : 0.0;
        summary.setPassPercentage(Math.round(passPct * 100.0) / 100.0);

        return summary;
    }

    private Enrollment getEnrollmentById(int enrollmentId) throws SQLException {
        String sql = "SELECT e.enrollment_id, e.roll_no, e.course_code, e.semester, e.classes_held, e.classes_attended, e.medical_condonation, "
                + "s.name as student_name, s.department, c.title as course_title, c.credits, c.course_type "
                + "FROM enrollments e "
                + "JOIN students s ON e.roll_no = s.roll_no "
                + "JOIN courses c ON e.course_code = c.course_code "
                + "WHERE e.enrollment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Enrollment e = new Enrollment(
                            rs.getInt("enrollment_id"),
                            rs.getString("roll_no"),
                            rs.getString("course_code"),
                            rs.getInt("semester"),
                            rs.getInt("classes_held"),
                            rs.getInt("classes_attended"),
                            rs.getBoolean("medical_condonation")
                    );
                    e.setStudentName(rs.getString("student_name"));
                    e.setDepartment(rs.getString("department"));
                    e.setCourseTitle(rs.getString("course_title"));
                    e.setCourseCredits(rs.getInt("credits"));
                    e.setCourseType(CourseType.fromString(rs.getString("course_type")));
                    return e;
                }
            }
        }
        return null;
    }
}
