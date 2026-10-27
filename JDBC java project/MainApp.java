import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Kongu Engineering College (Autonomous) - Regulation 2024
 * MainApp: Menu-Driven Interface for Student Academic & Continuous Evaluation System.
 */
public class MainApp {

    private static final StudentDAO studentDAO = new StudentDAO();
    private static final AcademicDAO academicDAO = new AcademicDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("          KONGU ENGINEERING COLLEGE (AUTONOMOUS) - ERODE 638060");
        System.out.println("       STUDENT ACADEMIC & CONTINUOUS EVALUATION SYSTEM (REGULATION 2024)");
        System.out.println("================================================================================");

        // Verify and initialize DB
        try {
            DBConnection.initializeDatabaseIfRequired();
            System.out.println("[+] Database Connection Established (kec_eval_db)");
        } catch (Exception e) {
            System.err.println("[!] DB Init warning: " + e.getMessage());
        }

        // Check if demo mode requested via command line
        if (args.length > 0 && args[0].equalsIgnoreCase("--demo")) {
            runAutomatedDemonstration();
            return;
        }

        boolean running = true;
        while (running) {
            printMainMenu();
            System.out.print("Enter your choice (1-8): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    handleStudentCourseManagement();
                    break;
                case "2":
                    handleAttendanceManagement();
                    break;
                case "3":
                    handlePhasedMarksEntry();
                    break;
                case "4":
                    handleStudentMarkSheet();
                    break;
                case "5":
                    handleAttendanceDefaulters();
                    break;
                case "6":
                    handleClassPerformanceSummary();
                    break;
                case "7":
                    runAutomatedDemonstration();
                    break;
                case "8":
                    System.out.println("\n[+] Exiting KEC Academic Evaluation System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid choice. Please enter a number between 1 and 8.");
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                     MAIN MENU");
        System.out.println("=".repeat(60));
        System.out.println("  1. Student & Course Management (Data Entry)");
        System.out.println("  2. Course Enrollment & Attendance Tracking");
        System.out.println("  3. Incremental Marks Entry (Phase 1, 2, 3)");
        System.out.println("  4. Generate Student Semester Mark Sheet (SGPA & %)");
        System.out.println("  5. Generate Attendance Defaulter Report (SA Flagged)");
        System.out.println("  6. Generate Class Performance & Result Summary");
        System.out.println("  7. Run End-to-End Demonstration (Sample Proofs)");
        System.out.println("  8. Exit System");
        System.out.println("=".repeat(60));
    }

    // ========================================================================
    // 1. Student & Course Management
    // ========================================================================

    private static void handleStudentCourseManagement() {
        System.out.println("\n--- [1] STUDENT & COURSE MANAGEMENT ---");
        System.out.println("1. Register New Student");
        System.out.println("2. Add New Course to Catalog");
        System.out.println("3. View All Registered Students");
        System.out.println("4. View Course Catalog");
        System.out.println("5. Back to Main Menu");
        System.out.print("Select an option (1-5): ");
        String sub = scanner.nextLine().trim();

        switch (sub) {
            case "1":
                registerStudent();
                break;
            case "2":
                addCourse();
                break;
            case "3":
                viewAllStudents();
                break;
            case "4":
                viewCourseCatalog();
                break;
            default:
                break;
        }
    }

    private static void registerStudent() {
        try {
            System.out.println("\n--- Student Registration ---");
            System.out.print("Enter Roll Number (e.g., 24ITR010): ");
            String rollNo = scanner.nextLine().trim();
            if (rollNo.isEmpty()) {
                System.out.println("[!] Roll number cannot be empty.");
                return;
            }

            Student existing = studentDAO.getStudentByRollNo(rollNo);
            if (existing != null) {
                System.out.println("[!] Student with Roll Number " + rollNo + " already exists!");
                return;
            }

            System.out.print("Enter Student Full Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter Department (e.g., IT, CSE, ECE, MECH, AIDS): ");
            String dept = scanner.nextLine().trim().toUpperCase();

            System.out.print("Enter Current Semester (1-8): ");
            int sem = Integer.parseInt(scanner.nextLine().trim());

            Student s = new Student(rollNo, name, dept, sem);
            boolean success = studentDAO.addStudent(s);
            if (success) {
                System.out.println("[+] Student registered successfully: " + s);
            } else {
                System.out.println("[!] Failed to register student.");
            }
        } catch (Exception e) {
            System.err.println("[!] Error registering student: " + e.getMessage());
        }
    }

    private static void addCourse() {
        try {
            System.out.println("\n--- Add Course to Catalog ---");
            System.out.print("Enter Course Code (e.g., 24ITC31): ");
            String code = scanner.nextLine().trim().toUpperCase();

            Course existing = academicDAO.getCourse(code);
            if (existing != null) {
                System.out.println("[!] Course code " + code + " already exists in catalog!");
                return;
            }

            System.out.print("Enter Course Title: ");
            String title = scanner.nextLine().trim();

            System.out.print("Enter Credits (1-6): ");
            int credits = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Course Type (1: THEORY, 2: PRACTICAL, 3: EMBEDDED): ");
            String tOpt = scanner.nextLine().trim();
            CourseType type = CourseType.THEORY;
            if (tOpt.equals("2") || tOpt.equalsIgnoreCase("PRACTICAL")) {
                type = CourseType.PRACTICAL;
            } else if (tOpt.equals("3") || tOpt.equalsIgnoreCase("EMBEDDED")) {
                type = CourseType.EMBEDDED;
            }

            System.out.print("Enter Semester (1-8): ");
            int sem = Integer.parseInt(scanner.nextLine().trim());

            Course c = new Course(code, title, credits, type, sem);
            boolean success = academicDAO.addCourse(c);
            if (success) {
                System.out.println("[+] Course added successfully to Catalog: " + c);
                System.out.printf("    Rules: CIA: %.0f%% | ESE: %.0f%% | ESE Min Pass: %.1f marks\n",
                        c.getCourseType().getCiaWeightage() * 100,
                        c.getCourseType().getEseWeightage() * 100,
                        c.getCourseType().getEseMinScaledScore());
            } else {
                System.out.println("[!] Failed to add course.");
            }
        } catch (Exception e) {
            System.err.println("[!] Error adding course: " + e.getMessage());
        }
    }

    private static void viewAllStudents() {
        try {
            List<Student> list = studentDAO.getAllStudents();
            System.out.println("\n" + "-".repeat(60));
            System.out.printf("%-10s | %-25s | %-6s | %-8s\n", "Roll No", "Student Name", "Dept", "Semester");
            System.out.println("-".repeat(60));
            for (Student s : list) {
                System.out.printf("%-10s | %-25s | %-6s | Sem %d\n", s.getRollNo(), s.getName(), s.getDepartment(), s.getSemester());
            }
            System.out.println("-".repeat(60));
            System.out.println("Total Students Registered: " + list.size());
        } catch (Exception e) {
            System.err.println("[!] Error retrieving students: " + e.getMessage());
        }
    }

    private static void viewCourseCatalog() {
        try {
            List<Course> list = academicDAO.getAllCourses();
            System.out.println("\n" + "-".repeat(85));
            System.out.printf("%-10s | %-38s | %-4s | %-9s | %-8s\n", "Code", "Course Title", "Cred", "Type", "Semester");
            System.out.println("-".repeat(85));
            for (Course c : list) {
                System.out.printf("%-10s | %-38s | %-4d | %-9s | Sem %d\n",
                        c.getCourseCode(), c.getTitle(), c.getCredits(), c.getCourseType(), c.getSemester());
            }
            System.out.println("-".repeat(85));
            System.out.println("Total Courses in Catalog: " + list.size());
        } catch (Exception e) {
            System.err.println("[!] Error retrieving courses: " + e.getMessage());
        }
    }

    // ========================================================================
    // 2. Attendance Tracking & Medical Condonation
    // ========================================================================

    private static void handleAttendanceManagement() {
        System.out.println("\n--- [2] ENROLLMENT & ATTENDANCE TRACKING ---");
        System.out.println("1. Enroll Student into Course");
        System.out.println("2. Update Continuous Attendance (Held vs Attended)");
        System.out.println("3. Manage Medical Condonation (65% - 74.9%)");
        System.out.println("4. View All Enrollments");
        System.out.println("5. Back to Main Menu");
        System.out.print("Select an option (1-5): ");
        String opt = scanner.nextLine().trim();

        switch (opt) {
            case "1":
                enrollStudentInCourse();
                break;
            case "2":
                updateAttendanceRecord();
                break;
            case "3":
                manageMedicalCondonation();
                break;
            case "4":
                viewAllEnrollments();
                break;
            default:
                break;
        }
    }

    private static void enrollStudentInCourse() {
        try {
            System.out.println("\n--- Enroll Student in Course ---");
            System.out.print("Enter Roll Number: ");
            String rollNo = scanner.nextLine().trim().toUpperCase();

            Student student = studentDAO.getStudentByRollNo(rollNo);
            if (student == null) {
                System.out.println("[!] Student not found with Roll Number: " + rollNo);
                return;
            }

            System.out.print("Enter Course Code: ");
            String courseCode = scanner.nextLine().trim().toUpperCase();

            Course course = academicDAO.getCourse(courseCode);
            if (course == null) {
                System.out.println("[!] Course not found in catalog: " + courseCode);
                return;
            }

            int enrollId = studentDAO.enrollStudent(rollNo, courseCode, student.getSemester());
            System.out.println("[+] Student " + rollNo + " successfully enrolled in " + courseCode + " (Enrollment ID: " + enrollId + ")");
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                System.out.println("[!] Student is already enrolled in this course!");
            } else {
                System.err.println("[!] Enrollment error: " + e.getMessage());
            }
        }
    }

    private static void updateAttendanceRecord() {
        try {
            System.out.println("\n--- Update Attendance Tracking ---");
            System.out.print("Enter Student Roll Number: ");
            String rollNo = scanner.nextLine().trim().toUpperCase();

            System.out.print("Enter Course Code: ");
            String courseCode = scanner.nextLine().trim().toUpperCase();

            Enrollment enroll = studentDAO.getEnrollment(rollNo, courseCode);
            if (enroll == null) {
                System.out.println("[!] No enrollment found for " + rollNo + " in " + courseCode);
                return;
            }

            System.out.printf("Current Status: Held: %d, Attended: %d (%.1f%%)\n",
                    enroll.getClassesHeld(), enroll.getClassesAttended(), enroll.getAttendancePercentage());

            System.out.print("Enter Total Classes Held: ");
            int held = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Total Classes Attended: ");
            int attended = Integer.parseInt(scanner.nextLine().trim());

            boolean condonation = enroll.isMedicalCondonation();
            double pct = held > 0 ? (attended * 100.0) / held : 0.0;
            System.out.printf("New Attendance Percentage: %.1f%%\n", pct);

            if (pct >= 65.0 && pct < 75.0) {
                System.out.print("Attendance is in conditional range (65-74.9%). Has medical condonation been approved? (Y/N): ");
                String yn = scanner.nextLine().trim();
                condonation = yn.equalsIgnoreCase("Y");
            }

            boolean ok = studentDAO.updateAttendance(enroll.getEnrollmentId(), held, attended, condonation);
            if (ok) {
                academicDAO.recalculateAndStoreEvaluation(enroll.getEnrollmentId());
                System.out.println("[+] Attendance updated and evaluation recalculated successfully.");
            }
        } catch (Exception e) {
            System.err.println("[!] Error updating attendance: " + e.getMessage());
        }
    }

    private static void manageMedicalCondonation() {
        try {
            System.out.println("\n--- Manage Medical Condonation ---");
            System.out.print("Enter Student Roll Number: ");
            String rollNo = scanner.nextLine().trim().toUpperCase();

            System.out.print("Enter Course Code: ");
            String courseCode = scanner.nextLine().trim().toUpperCase();

            Enrollment enroll = studentDAO.getEnrollment(rollNo, courseCode);
            if (enroll == null) {
                System.out.println("[!] No enrollment found.");
                return;
            }

            double pct = enroll.getAttendancePercentage();
            System.out.printf("Student: %s (%s) | Course: %s | Attendance: %.1f%% | Condonation: %s\n",
                    enroll.getStudentName(), enroll.getRollNo(), enroll.getCourseCode(),
                    pct, enroll.isMedicalCondonation() ? "APPROVED" : "NOT APPROVED");

            if (pct < 65.0) {
                System.out.println("[!] Regulation 2024 Rule: Attendance < 65% CANNOT be condoned. Student is permanently Barred (SA) from ESE.");
                return;
            } else if (pct >= 75.0) {
                System.out.println("[+] Student has >= 75% attendance. Condonation is not required (Already Eligible).");
                return;
            }

            System.out.print("Do you wish to APPROVE medical condonation? (Y/N): ");
            String yn = scanner.nextLine().trim();
            boolean approve = yn.equalsIgnoreCase("Y");

            studentDAO.setMedicalCondonation(enroll.getEnrollmentId(), approve);
            academicDAO.recalculateAndStoreEvaluation(enroll.getEnrollmentId());
            System.out.println("[+] Medical Condonation status updated to: " + (approve ? "APPROVED" : "REVOKED"));
        } catch (Exception e) {
            System.err.println("[!] Error updating condonation: " + e.getMessage());
        }
    }

    private static void viewAllEnrollments() {
        try {
            List<Enrollment> list = studentDAO.getAllEnrollments();
            System.out.println("\n" + "-".repeat(85));
            System.out.printf("%-4s | %-10s | %-9s | %-6s | %-8s | %-8s | %-20s\n",
                    "ID", "Roll No", "Course", "Held", "Attended", "Attd %", "Eligibility Status");
            System.out.println("-".repeat(85));
            for (Enrollment e : list) {
                System.out.printf("%-4d | %-10s | %-9s | %-6d | %-8d | %6.1f%%  | %-20s\n",
                        e.getEnrollmentId(), e.getRollNo(), e.getCourseCode(),
                        e.getClassesHeld(), e.getClassesAttended(), e.getAttendancePercentage(),
                        e.getEligibilityStatus());
            }
            System.out.println("-".repeat(85));
            System.out.println("Total Course Enrollments: " + list.size());
        } catch (Exception e) {
            System.err.println("[!] Error retrieving enrollments: " + e.getMessage());
        }
    }

    // ========================================================================
    // 3. Incremental Phased Marks Entry ("Then and Now")
    // ========================================================================

    private static void handlePhasedMarksEntry() {
        System.out.println("\n--- [3] INCREMENTAL MARKS ENTRY (THEN AND NOW) ---");
        System.out.println("1. Phase 1 (Mid-Semester)     : CIA Test 1 (0-100) & CIA Test 2 (0-100)");
        System.out.println("2. Phase 2 (End-of-Semester)  : Assignments / Quizzes / Lab Evaluations (0-100)");
        System.out.println("3. Phase 3 (Post-Semester)    : End Semester Examination (ESE 0-100) / Mark Absent");
        System.out.println("4. Back to Main Menu");
        System.out.print("Select Assessment Phase (1-4): ");
        String phase = scanner.nextLine().trim();

        switch (phase) {
            case "1":
                enterPhase1Marks();
                break;
            case "2":
                enterPhase2Marks();
                break;
            case "3":
                enterPhase3Marks();
                break;
            default:
                break;
        }
    }

    private static void enterPhase1Marks() {
        try {
            System.out.println("\n--- Phase 1: Mid-Semester CIA Tests Entry ---");
            System.out.print("Enter Student Roll Number: ");
            String rollNo = scanner.nextLine().trim().toUpperCase();

            System.out.print("Enter Course Code: ");
            String courseCode = scanner.nextLine().trim().toUpperCase();

            Enrollment enroll = studentDAO.getEnrollment(rollNo, courseCode);
            if (enroll == null) {
                System.out.println("[!] Enrollment not found!");
                return;
            }

            System.out.printf("Student: %s (%s) | Course: %s (%s)\n",
                    enroll.getStudentName(), enroll.getRollNo(), enroll.getCourseCode(), enroll.getCourseType());

            System.out.print("Enter CIA Test 1 Marks (0.0 - 100.0): ");
            double cia1 = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter CIA Test 2 Marks (0.0 - 100.0): ");
            double cia2 = Double.parseDouble(scanner.nextLine().trim());

            boolean ok = academicDAO.recordPhase1Marks(enroll.getEnrollmentId(), cia1, cia2);
            if (ok) {
                System.out.println("[+] Phase 1 marks recorded successfully!");
                double avg = (cia1 + cia2) / 2.0;
                System.out.printf("    Current Mid-Semester Internal Average: %.2f / 100\n", avg);
            }
        } catch (Exception e) {
            System.err.println("[!] Error entering Phase 1 marks: " + e.getMessage());
        }
    }

    private static void enterPhase2Marks() {
        try {
            System.out.println("\n--- Phase 2: End-of-Semester CIA (Assignments/Quizzes/Labs) ---");
            System.out.print("Enter Student Roll Number: ");
            String rollNo = scanner.nextLine().trim().toUpperCase();

            System.out.print("Enter Course Code: ");
            String courseCode = scanner.nextLine().trim().toUpperCase();

            Enrollment enroll = studentDAO.getEnrollment(rollNo, courseCode);
            if (enroll == null) {
                System.out.println("[!] Enrollment not found!");
                return;
            }

            System.out.print("Enter Assignment / Quiz / Lab Evaluation Marks (0.0 - 100.0): ");
            double asgn = Double.parseDouble(scanner.nextLine().trim());

            boolean ok = academicDAO.recordPhase2Marks(enroll.getEnrollmentId(), asgn);
            if (ok) {
                AssessmentMark m = academicDAO.getMarksByEnrollment(enroll.getEnrollmentId());
                System.out.println("[+] Phase 2 marks recorded successfully!");
                System.out.printf("    Final CIA Internal: %.2f / 100 | Scaled for %s: %.2f marks\n",
                        EvaluationEngine.computeRawCia100(m.getCia1Marks(), m.getCia2Marks(), m.getAssignmentMarks()),
                        enroll.getCourseType(), m.getCiaScaled());
            }
        } catch (Exception e) {
            System.err.println("[!] Error entering Phase 2 marks: " + e.getMessage());
        }
    }

    private static void enterPhase3Marks() {
        try {
            System.out.println("\n--- Phase 3: Post-Semester End Semester Examination (ESE) ---");
            System.out.print("Enter Student Roll Number: ");
            String rollNo = scanner.nextLine().trim().toUpperCase();

            System.out.print("Enter Course Code: ");
            String courseCode = scanner.nextLine().trim().toUpperCase();

            Enrollment enroll = studentDAO.getEnrollment(rollNo, courseCode);
            if (enroll == null) {
                System.out.println("[!] Enrollment not found!");
                return;
            }

            // Attendance eligibility check
            if (!enroll.isEligibleForEse()) {
                System.out.println("\n[!] ATTENDANCE ELIGIBILITY ALERT:");
                System.out.printf("    Student has %.1f%% attendance and is BARRED from appearing in ESE!\n",
                        enroll.getAttendancePercentage());
                System.out.println("    Under KEC Regulation 2024, Grade 'SA' (Shortage of Attendance) is permanently awarded.");
                academicDAO.recordPhase3Marks(enroll.getEnrollmentId(), false, null);
                return;
            }

            System.out.print("Was the student ABSENT in ESE? (Y/N): ");
            String absentStr = scanner.nextLine().trim();
            boolean isAbsent = absentStr.equalsIgnoreCase("Y");

            Double eseMarks = null;
            if (!isAbsent) {
                System.out.print("Enter ESE Raw Marks (0.0 - 100.0): ");
                eseMarks = Double.parseDouble(scanner.nextLine().trim());
            }

            boolean ok = academicDAO.recordPhase3Marks(enroll.getEnrollmentId(), isAbsent, eseMarks);
            if (ok) {
                AssessmentMark m = academicDAO.getMarksByEnrollment(enroll.getEnrollmentId());
                System.out.println("[+] Phase 3 ESE recorded and final grade evaluated!");
                System.out.printf("    Course: %s | Total: %.1f | Grade: %s | Grade Point: %d | Status: %s\n",
                        courseCode, m.getTotalMarks(), m.getGrade(), m.getGradePoint(), m.getStatus());
            }
        } catch (Exception e) {
            System.err.println("[!] Error entering Phase 3 marks: " + e.getMessage());
        }
    }

    // ========================================================================
    // 4. Expected Reporting Outputs
    // ========================================================================

    private static void handleStudentMarkSheet() {
        try {
            System.out.println("\n--- [4] STUDENT SEMESTER MARK SHEET ---");
            System.out.print("Enter Student Roll Number: ");
            String rollNo = scanner.nextLine().trim().toUpperCase();

            System.out.print("Enter Semester (e.g., 1): ");
            int sem = Integer.parseInt(scanner.nextLine().trim());

            SemesterMarkSheet sheet = academicDAO.getStudentSemesterMarkSheet(rollNo, sem);
            if (sheet == null) {
                System.out.println("[!] Student not found with Roll Number: " + rollNo);
                return;
            }

            ConsoleReportFormatter.printSemesterMarkSheet(sheet);

            // Export option
            System.out.print("Do you want to export this Mark Sheet as an official HTML Report? (Y/N): ");
            String exp = scanner.nextLine().trim();
            if (exp.equalsIgnoreCase("Y")) {
                String outPath = "reports/" + rollNo + "_Sem" + sem + "_MarkSheet.html";
                String absPath = HtmlReportGenerator.generateMarkSheetHtml(sheet, outPath);
                System.out.println("[+] Official HTML Mark Sheet exported to: " + absPath);
            }
        } catch (Exception e) {
            System.err.println("[!] Error generating mark sheet: " + e.getMessage());
        }
    }

    private static void handleAttendanceDefaulters() {
        try {
            System.out.println("\n--- [5] ATTENDANCE DEFAULTER REPORT (< 65% / SHORTAGE) ---");
            System.out.print("Filter by Semester (press Enter for ALL): ");
            String semStr = scanner.nextLine().trim();
            Integer sem = semStr.isEmpty() ? null : Integer.parseInt(semStr);

            System.out.print("Filter by Department (press Enter for ALL): ");
            String dept = scanner.nextLine().trim();
            if (dept.isEmpty()) dept = "ALL";

            List<DefaulterRecord> defaulters = academicDAO.getAttendanceDefaulters(sem, dept);
            ConsoleReportFormatter.printDefaulterReport(defaulters, sem, dept);
        } catch (Exception e) {
            System.err.println("[!] Error generating defaulter report: " + e.getMessage());
        }
    }

    private static void handleClassPerformanceSummary() {
        try {
            System.out.println("\n--- [6] CLASS PERFORMANCE & RESULT SUMMARY ---");
            System.out.print("Enter Department (e.g., IT, CSE, or ALL): ");
            String dept = scanner.nextLine().trim();
            if (dept.isEmpty()) dept = "IT";

            System.out.print("Enter Semester: ");
            int sem = Integer.parseInt(scanner.nextLine().trim());

            ClassSummaryReport summary = academicDAO.getClassPerformanceSummary(sem, dept);
            ConsoleReportFormatter.printClassSummaryReport(summary);
        } catch (Exception e) {
            System.err.println("[!] Error generating class summary: " + e.getMessage());
        }
    }

    // ========================================================================
    // 7. Automated Demonstration (Proofs of All Scenarios)
    // ========================================================================

    public static void runAutomatedDemonstration() {
        System.out.println("\n" + "#".repeat(80));
        System.out.println("  RUNNING AUTOMATED DEMONSTRATION & GENERATING EXECUTION PROOFS");
        System.out.println("  KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024");
        System.out.println("#".repeat(80));

        try {
            // Step 1: Demonstrate adding a new student and a new course
            System.out.println("\n[DEMO STEP 1] Adding a New Student & New Course:");
            String demoRoll = "24ITR099";
            Student newStudent = new Student(demoRoll, "Naveen Kumar M", "IT", 1);
            if (studentDAO.getStudentByRollNo(demoRoll) == null) {
                studentDAO.addStudent(newStudent);
                System.out.println("  -> Successfully added new student: " + newStudent);
            } else {
                System.out.println("  -> Demo student already exists: " + demoRoll);
            }

            String demoCourseCode = "24ITC32";
            Course newCourse = new Course(demoCourseCode, "Data Structures and Algorithms", 4, CourseType.EMBEDDED, 1);
            if (academicDAO.getCourse(demoCourseCode) == null) {
                academicDAO.addCourse(newCourse);
                System.out.println("  -> Successfully added new course: " + newCourse);
            } else {
                System.out.println("  -> Demo course already exists: " + demoCourseCode);
            }

            // Step 2: Enroll and record attendance
            System.out.println("\n[DEMO STEP 2] Enrolling Student & Recording Attendance:");
            Enrollment enroll = studentDAO.getEnrollment(demoRoll, demoCourseCode);
            int enrollId;
            if (enroll == null) {
                enrollId = studentDAO.enrollStudent(demoRoll, demoCourseCode, 1);
                System.out.println("  -> Enrolled " + demoRoll + " in " + demoCourseCode + " (ID: " + enrollId + ")");
            } else {
                enrollId = enroll.getEnrollmentId();
            }
            studentDAO.updateAttendance(enrollId, 60, 52, false); // 52/60 = 86.7% (Eligible)
            System.out.println("  -> Updated attendance: 52 attended out of 60 held (86.7% - Eligible for ESE)");

            // Step 3: Phased Incremental Marks Entry ("Then and Now")
            System.out.println("\n[DEMO STEP 3] Phased Incremental Marks Entry:");
            System.out.println("  Phase 1 (Mid-Semester): Recording CIA 1 = 88.0, CIA 2 = 92.0");
            academicDAO.recordPhase1Marks(enrollId, 88.0, 92.0);

            System.out.println("  Phase 2 (End-of-Semester): Recording Assignment/Lab = 85.0");
            academicDAO.recordPhase2Marks(enrollId, 85.0);

            System.out.println("  Phase 3 (Post-Semester ESE): Recording ESE Mark = 82.0");
            academicDAO.recordPhase3Marks(enrollId, false, 82.0);

            AssessmentMark mark = academicDAO.getMarksByEnrollment(enrollId);
            System.out.printf("  -> Evaluation Computed: Scaled CIA: %.2f | Scaled ESE: %.2f | Total: %.0f | Grade: %s | GP: %d | Status: %s\n",
                    mark.getCiaScaled(), mark.getEseScaled(), mark.getTotalMarks(),
                    mark.getGrade(), mark.getGradePoint(), mark.getStatus());

            // Step 4: Display Student Semester Mark Sheet for Arun Kumar S (24ITR001)
            System.out.println("\n[DEMO STEP 4] Formal Academic Grade Sheet for 24ITR001 (Arun Kumar S):");
            SemesterMarkSheet sheet1 = academicDAO.getStudentSemesterMarkSheet("24ITR001", 1);
            ConsoleReportFormatter.printSemesterMarkSheet(sheet1);
            String htmlPath1 = "reports/24ITR001_Sem1_MarkSheet.html";
            HtmlReportGenerator.generateMarkSheetHtml(sheet1, htmlPath1);
            System.out.println("  -> HTML Grade Sheet Generated: " + new File(htmlPath1).getAbsolutePath());

            // Step 5: Display Attendance Defaulter Report
            System.out.println("\n[DEMO STEP 5] Attendance Defaulter Report (Attendance < 65% or Unapproved Condonation):");
            List<DefaulterRecord> defaulters = academicDAO.getAttendanceDefaulters(1, "IT");
            ConsoleReportFormatter.printDefaulterReport(defaulters, 1, "IT");

            // Step 6: Display Class Performance & Result Summary
            System.out.println("\n[DEMO STEP 6] Class Performance & Department Result Summary (IT - Semester 1):");
            ClassSummaryReport summary = academicDAO.getClassPerformanceSummary(1, "IT");
            ConsoleReportFormatter.printClassSummaryReport(summary);

            System.out.println("\n[+] DEMONSTRATION COMPLETED SUCCESSFULLY!");
        } catch (Exception e) {
            System.err.println("[!] Demo error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
