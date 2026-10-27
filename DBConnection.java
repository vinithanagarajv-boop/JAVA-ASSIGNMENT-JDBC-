import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Kongu Engineering College (Autonomous) - Regulation 2024
 * DBConnection: JDBC Connection Management for kec_eval_db.
 * Handles driver registration, connection pooling/lifecycle, and schema verification.
 */
public class DBConnection {

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "kec_eval_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static String url;
    private static String user;
    private static String password;

    static {
        // Allow runtime overrides via System properties or Environment variables
        String host = System.getProperty("DB_HOST", System.getenv().getOrDefault("DB_HOST", DEFAULT_HOST));
        String port = System.getProperty("DB_PORT", System.getenv().getOrDefault("DB_PORT", DEFAULT_PORT));
        String dbName = System.getProperty("DB_NAME", System.getenv().getOrDefault("DB_NAME", DEFAULT_DB));
        user = System.getProperty("DB_USER", System.getenv().getOrDefault("DB_USER", DEFAULT_USER));
        password = System.getProperty("DB_PASS", System.getenv().getOrDefault("DB_PASS", DEFAULT_PASSWORD));

        url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                host, port, dbName);

        try {
            // Load MySQL Connector/J driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                // Fallback for older drivers
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("[DBConnection] WARNING: MySQL JDBC Driver not found on classpath!");
            }
        }
    }

    /**
     * Obtains a new JDBC Connection.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Tests database connectivity.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[DBConnection] Connection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Safely closes resources.
     */
    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Verifies that the required tables exist, or creates them if missing.
     */
    public static void initializeDatabaseIfRequired() {
        String testQuery = "SELECT 1 FROM students LIMIT 1";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeQuery(testQuery);
        } catch (SQLException e) {
            System.out.println("[DBConnection] Tables not found. Initializing schema...");
            createSchemaTables();
        }
    }

    private static void createSchemaTables() {
        String createStudents = "CREATE TABLE IF NOT EXISTS students ("
                + "roll_no VARCHAR(15) PRIMARY KEY, "
                + "name VARCHAR(100) NOT NULL, "
                + "department VARCHAR(20) NOT NULL, "
                + "semester INT NOT NULL"
                + ") ENGINE=InnoDB;";

        String createCourses = "CREATE TABLE IF NOT EXISTS courses ("
                + "course_code VARCHAR(15) PRIMARY KEY, "
                + "title VARCHAR(150) NOT NULL, "
                + "credits INT NOT NULL, "
                + "course_type ENUM('THEORY', 'PRACTICAL', 'EMBEDDED') NOT NULL, "
                + "semester INT NOT NULL"
                + ") ENGINE=InnoDB;";

        String createEnrollments = "CREATE TABLE IF NOT EXISTS enrollments ("
                + "enrollment_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "roll_no VARCHAR(15) NOT NULL, "
                + "course_code VARCHAR(15) NOT NULL, "
                + "semester INT NOT NULL, "
                + "classes_held INT DEFAULT 0, "
                + "classes_attended INT DEFAULT 0, "
                + "medical_condonation BOOLEAN DEFAULT FALSE, "
                + "UNIQUE KEY uq_student_course (roll_no, course_code), "
                + "FOREIGN KEY (roll_no) REFERENCES students(roll_no) ON DELETE CASCADE, "
                + "FOREIGN KEY (course_code) REFERENCES courses(course_code) ON DELETE CASCADE"
                + ") ENGINE=InnoDB;";

        String createMarks = "CREATE TABLE IF NOT EXISTS assessment_marks ("
                + "mark_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "enrollment_id INT NOT NULL UNIQUE, "
                + "cia1_marks DECIMAL(5,2) NULL, "
                + "cia2_marks DECIMAL(5,2) NULL, "
                + "assignment_marks DECIMAL(5,2) NULL, "
                + "is_absent_ese BOOLEAN DEFAULT FALSE, "
                + "ese_marks DECIMAL(5,2) NULL, "
                + "cia_scaled DECIMAL(5,2) NULL, "
                + "ese_scaled DECIMAL(5,2) NULL, "
                + "total_marks DECIMAL(5,2) NULL, "
                + "grade VARCHAR(5) NULL, "
                + "grade_point INT NULL, "
                + "status VARCHAR(20) NULL, "
                + "FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE"
                + ") ENGINE=InnoDB;";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createStudents);
            stmt.executeUpdate(createCourses);
            stmt.executeUpdate(createEnrollments);
            stmt.executeUpdate(createMarks);
            System.out.println("[DBConnection] Schema created successfully.");
        } catch (SQLException ex) {
            System.err.println("[DBConnection] Error creating schema tables: " + ex.getMessage());
        }
    }
}
