import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Kongu Engineering College (Autonomous) - Regulation 2024
 * StudentDAO: Data Access Object for Student Registration,
 * Course Enrollment, and Attendance Tracking.
 */
public class StudentDAO {

    /**
     * Adds a new student to the database.
     */
    public boolean addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (roll_no, name, department, semester) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getRollNo().trim().toUpperCase());
            ps.setString(2, student.getName().trim());
            ps.setString(3, student.getDepartment().trim().toUpperCase());
            ps.setInt(4, student.getSemester());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Finds a student by roll number.
     */
    public Student getStudentByRollNo(String rollNo) throws SQLException {
        String sql = "SELECT roll_no, name, department, semester FROM students WHERE roll_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getString("roll_no"),
                            rs.getString("name"),
                            rs.getString("department"),
                            rs.getInt("semester")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all registered students.
     */
    public List<Student> getAllStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT roll_no, name, department, semester FROM students ORDER BY roll_no ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getInt("semester")
                ));
            }
        }
        return list;
    }

    /**
     * Retrieves students by department.
     */
    public List<Student> getStudentsByDepartment(String dept) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT roll_no, name, department, semester FROM students WHERE department = ? ORDER BY roll_no ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Student(
                            rs.getString("roll_no"),
                            rs.getString("name"),
                            rs.getString("department"),
                            rs.getInt("semester")
                    ));
                }
            }
        }
        return list;
    }

    /**
     * Enrolls a student into a course for a semester.
     * Automatically initializes an assessment_marks row for incremental entry.
     */
    public int enrollStudent(String rollNo, String courseCode, int semester) throws SQLException {
        String insertEnroll = "INSERT INTO enrollments (roll_no, course_code, semester, classes_held, classes_attended, medical_condonation) "
                + "VALUES (?, ?, ?, 0, 0, FALSE)";
        String initMarks = "INSERT INTO assessment_marks (enrollment_id) VALUES (?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int enrollmentId = -1;
            try (PreparedStatement ps = conn.prepareStatement(insertEnroll, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, rollNo.trim().toUpperCase());
                ps.setString(2, courseCode.trim().toUpperCase());
                ps.setInt(3, semester);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        enrollmentId = rs.getInt(1);
                    }
                }
            }

            if (enrollmentId > 0) {
                try (PreparedStatement psMarks = conn.prepareStatement(initMarks)) {
                    psMarks.setInt(1, enrollmentId);
                    psMarks.executeUpdate();
                }
            }

            conn.commit();
            return enrollmentId;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Updates continuous attendance figures for an enrollment.
     */
    public boolean updateAttendance(int enrollmentId, int classesHeld, int classesAttended, boolean condonation) throws SQLException {
        if (classesAttended > classesHeld) {
            throw new IllegalArgumentException("Classes attended (" + classesAttended + ") cannot exceed classes held (" + classesHeld + ")");
        }
        String sql = "UPDATE enrollments SET classes_held = ?, classes_attended = ?, medical_condonation = ? WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classesHeld);
            ps.setInt(2, classesAttended);
            ps.setBoolean(3, condonation);
            ps.setInt(4, enrollmentId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Approves or revokes medical condonation for attendance between 65% and 74.9%.
     */
    public boolean setMedicalCondonation(int enrollmentId, boolean approved) throws SQLException {
        String sql = "UPDATE enrollments SET medical_condonation = ? WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, approved);
            ps.setInt(2, enrollmentId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Finds an enrollment by student roll number and course code.
     */
    public Enrollment getEnrollment(String rollNo, String courseCode) throws SQLException {
        String sql = "SELECT e.enrollment_id, e.roll_no, e.course_code, e.semester, e.classes_held, e.classes_attended, e.medical_condonation, "
                + "s.name as student_name, s.department, c.title as course_title, c.credits, c.course_type "
                + "FROM enrollments e "
                + "JOIN students s ON e.roll_no = s.roll_no "
                + "JOIN courses c ON e.course_code = c.course_code "
                + "WHERE e.roll_no = ? AND e.course_code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo.trim().toUpperCase());
            ps.setString(2, courseCode.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEnrollment(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all course enrollments for a student in a semester.
     */
    public List<Enrollment> getEnrollmentsByStudent(String rollNo, int semester) throws SQLException {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.roll_no, e.course_code, e.semester, e.classes_held, e.classes_attended, e.medical_condonation, "
                + "s.name as student_name, s.department, c.title as course_title, c.credits, c.course_type "
                + "FROM enrollments e "
                + "JOIN students s ON e.roll_no = s.roll_no "
                + "JOIN courses c ON e.course_code = c.course_code "
                + "WHERE e.roll_no = ? AND e.semester = ? "
                + "ORDER BY e.course_code ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo.trim().toUpperCase());
            ps.setInt(2, semester);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEnrollment(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves all enrollments across the system.
     */
    public List<Enrollment> getAllEnrollments() throws SQLException {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.roll_no, e.course_code, e.semester, e.classes_held, e.classes_attended, e.medical_condonation, "
                + "s.name as student_name, s.department, c.title as course_title, c.credits, c.course_type "
                + "FROM enrollments e "
                + "JOIN students s ON e.roll_no = s.roll_no "
                + "JOIN courses c ON e.course_code = c.course_code "
                + "ORDER BY e.roll_no, e.course_code";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapEnrollment(rs));
            }
        }
        return list;
    }

    private Enrollment mapEnrollment(ResultSet rs) throws SQLException {
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
