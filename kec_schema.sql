-- ============================================================================
-- Kongu Engineering College (Autonomous), Perundurai, Erode - 638060
-- Regulation 2024 (R2024) - Academic & Continuous Evaluation System
-- Database Schema Script: kec_schema.sql
-- ============================================================================

CREATE DATABASE IF NOT EXISTS kec_eval_db;
USE kec_eval_db;

-- Drop tables in reverse foreign-key order
DROP TABLE IF EXISTS assessment_marks;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS students;

-- ============================================================================
-- 1. Students Table
-- Roll Number format: e.g., 24ITR001, 24CSR015
-- ============================================================================
CREATE TABLE students (
    roll_no VARCHAR(15) NOT NULL,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(20) NOT NULL,
    semester INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (roll_no),
    CONSTRAINT chk_student_sem CHECK (semester BETWEEN 1 AND 8)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- 2. Courses Table
-- Course types: THEORY, PRACTICAL, EMBEDDED
-- ============================================================================
CREATE TABLE courses (
    course_code VARCHAR(15) NOT NULL,
    title VARCHAR(150) NOT NULL,
    credits INT NOT NULL,
    course_type ENUM('THEORY', 'PRACTICAL', 'EMBEDDED') NOT NULL,
    semester INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (course_code),
    CONSTRAINT chk_course_credits CHECK (credits BETWEEN 1 AND 6),
    CONSTRAINT chk_course_sem CHECK (semester BETWEEN 1 AND 8)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- 3. Enrollments & Attendance Tracking Table
-- Continuously tracks total classes held vs. classes attended
-- medical_condonation: Flag for attendance between 65% and 74.9%
-- ============================================================================
CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    roll_no VARCHAR(15) NOT NULL,
    course_code VARCHAR(15) NOT NULL,
    semester INT NOT NULL,
    classes_held INT DEFAULT 0,
    classes_attended INT DEFAULT 0,
    medical_condonation BOOLEAN DEFAULT FALSE,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_student_course (roll_no, course_code),
    CONSTRAINT fk_enroll_student FOREIGN KEY (roll_no) 
        REFERENCES students(roll_no) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_enroll_course FOREIGN KEY (course_code) 
        REFERENCES courses(course_code) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_attendance_values CHECK (classes_held >= 0 AND classes_attended >= 0 AND classes_attended <= classes_held)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- 4. Assessment Marks Table (Phased / Incremental Entry)
-- Phase 1 (Mid-Semester): cia1_marks (0-100), cia2_marks (0-100)
-- Phase 2 (End of Semester CIA): assignment_marks (0-100)
-- Phase 3 (Post Semester): is_absent_ese (BOOLEAN), ese_marks (0-100)
-- Cached computed fields for quick reporting (kept in sync via EvaluationEngine / DAO)
-- ============================================================================
CREATE TABLE assessment_marks (
    mark_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL UNIQUE,
    cia1_marks DECIMAL(5, 2) NULL,
    cia2_marks DECIMAL(5, 2) NULL,
    assignment_marks DECIMAL(5, 2) NULL,
    is_absent_ese BOOLEAN DEFAULT FALSE,
    ese_marks DECIMAL(5, 2) NULL,
    cia_scaled DECIMAL(5, 2) NULL,
    ese_scaled DECIMAL(5, 2) NULL,
    total_marks DECIMAL(5, 2) NULL,
    grade VARCHAR(5) NULL,
    grade_point INT NULL,
    status VARCHAR(20) NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_marks_enrollment FOREIGN KEY (enrollment_id) 
        REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    CONSTRAINT chk_cia1 CHECK (cia1_marks IS NULL OR (cia1_marks >= 0 AND cia1_marks <= 100)),
    CONSTRAINT chk_cia2 CHECK (cia2_marks IS NULL OR (cia2_marks >= 0 AND cia2_marks <= 100)),
    CONSTRAINT chk_assign CHECK (assignment_marks IS NULL OR (assignment_marks >= 0 AND assignment_marks <= 100)),
    CONSTRAINT chk_ese CHECK (ese_marks IS NULL OR (ese_marks >= 0 AND ese_marks <= 100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Initial Sample Seed Data for Demonstration & Testing
-- ============================================================================

-- 1. Insert Sample Students (Department of Information Technology & Computer Science)
INSERT INTO students (roll_no, name, department, semester) VALUES
('24ITR001', 'Arun Kumar S', 'IT', 1),
('24ITR002', 'Bhavani Devi M', 'IT', 1),
('24ITR003', 'Chandru P', 'IT', 1),
('24ITR004', 'Dharani V', 'IT', 1),
('24ITR005', 'Elango K', 'IT', 1),
('24CSR001', 'Gokulnath R', 'CSE', 1),
('24CSR002', 'Harini S', 'CSE', 1);

-- 2. Insert Sample Courses (KEC R2024 Semester 1 Curriculum)
-- THEORY: 40% CIA, 60% ESE (ESE Min 45% = 27/60)
-- PRACTICAL: 60% CIA, 40% ESE (ESE Min 50% = 20/40)
-- EMBEDDED: 50% CIA, 50% ESE (ESE Min 45% = 22.5/50)
INSERT INTO courses (course_code, title, credits, course_type, semester) VALUES
('24MAC11', 'Matrices and Calculus', 4, 'THEORY', 1),
('24PHC11', 'Engineering Physics', 3, 'THEORY', 1),
('24CSC11', 'Problem Solving and Python Programming', 4, 'EMBEDDED', 1),
('24EEL11', 'Basic Electrical and Electronics Engineering Practice', 2, 'PRACTICAL', 1),
('24ITC31', 'Object Oriented Programming using Java', 3, 'THEORY', 3);

-- 3. Enroll Students in Semester 1 Courses with Attendance
-- Classes held: 60
-- 24ITR001: 54/60 = 90% (Eligible)
-- 24ITR002: 48/60 = 80% (Eligible)
-- 24ITR003: 42/60 = 70% (Conditionally eligible - Approved medical condonation)
-- 24ITR004: 40/60 = 66.7% (Conditionally eligible - NOT approved -> Barred / SA)
-- 24ITR005: 32/60 = 53.3% (<65% Barred / SA Defaulter)
INSERT INTO enrollments (roll_no, course_code, semester, classes_held, classes_attended, medical_condonation) VALUES
-- 24ITR001 (Arun Kumar S)
('24ITR001', '24MAC11', 1, 60, 56, FALSE),
('24ITR001', '24PHC11', 1, 45, 42, FALSE),
('24ITR001', '24CSC11', 1, 60, 55, FALSE),
('24ITR001', '24EEL11', 1, 30, 28, FALSE),

-- 24ITR002 (Bhavani Devi M)
('24ITR002', '24MAC11', 1, 60, 50, FALSE),
('24ITR002', '24PHC11', 1, 45, 38, FALSE),
('24ITR002', '24CSC11', 1, 60, 48, FALSE),
('24ITR002', '24EEL11', 1, 30, 25, FALSE),

-- 24ITR003 (Chandru P) - 70% attendance with medical condonation approved
('24ITR003', '24MAC11', 1, 60, 42, TRUE),
('24ITR003', '24PHC11', 1, 45, 32, TRUE),
('24ITR003', '24CSC11', 1, 60, 42, TRUE),

-- 24ITR004 (Dharani V) - 66.7% attendance WITHOUT condonation -> Barred / SA
('24ITR004', '24MAC11', 1, 60, 40, FALSE),
('24ITR004', '24PHC11', 1, 45, 30, FALSE),

-- 24ITR005 (Elango K) - 53.3% attendance (< 65% Defaulter) -> Barred / SA
('24ITR005', '24MAC11', 1, 60, 32, FALSE),
('24ITR005', '24PHC11', 1, 45, 24, FALSE);

-- 4. Insert Initial Phased Assessment Marks
-- 24ITR001: Excellent Marks across all phases (Grade O / A+)
-- Enrollment 1: 24MAC11 (Theory: CIA 40%, ESE 60%)
-- CIA1: 92, CIA2: 95, Assign: 90 -> Raw CIA = 92.5 -> Scaled CIA = 37.0. ESE: 94 -> Scaled ESE = 56.4 -> Total = 93.4 (Grade O, GP 10)
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, is_absent_ese, ese_marks, cia_scaled, ese_scaled, total_marks, grade, grade_point, status) VALUES
(1, 92.00, 95.00, 90.00, FALSE, 94.00, 37.00, 56.40, 93.40, 'O', 10, 'PASS'),
(2, 85.00, 88.00, 85.00, FALSE, 86.00, 34.40, 51.60, 86.00, 'A+', 9, 'PASS'),
(3, 88.00, 92.00, 90.00, FALSE, 90.00, 45.00, 45.00, 90.00, 'A+', 9, 'PASS'),
(4, 95.00, 96.00, 98.00, FALSE, 92.00, 57.70, 36.80, 94.50, 'O', 10, 'PASS'),

-- 24ITR002: Good marks, but Absent in 24PHC11 ESE
(5, 78.00, 75.00, 80.00, FALSE, 72.00, 31.00, 43.20, 74.20, 'A', 8, 'PASS'),
(6, 70.00, 72.00, 75.00, TRUE, NULL, 28.80, 0.00, 28.80, 'AB', 0, 'FAIL (AB)'),
(7, 65.00, 70.00, 68.00, FALSE, 64.00, 33.70, 32.00, 65.70, 'B+', 7, 'PASS'),
(8, 80.00, 85.00, 82.00, FALSE, 80.00, 49.30, 32.00, 81.30, 'A+', 9, 'PASS'),

-- 24ITR003: Conditionally approved, Pass with Average
(9, 60.00, 58.00, 65.00, FALSE, 52.00, 24.30, 31.20, 55.50, 'B', 6, 'PASS'),
(10, 55.00, 52.00, 58.00, FALSE, 46.00, 21.90, 27.60, 49.50, 'RA', 0, 'FAIL (RA)'),
(11, 62.00, 60.00, 64.00, FALSE, 58.00, 30.90, 29.00, 59.90, 'B', 6, 'PASS'),

-- 24ITR004: Barred due to attendance without condonation -> SA
(12, 70.00, 68.00, 72.00, FALSE, NULL, 28.00, 0.00, 28.00, 'SA', 0, 'PREVENTED (SA)'),
(13, 65.00, 60.00, 70.00, FALSE, NULL, 25.90, 0.00, 25.90, 'SA', 0, 'PREVENTED (SA)'),

-- 24ITR005: Barred < 65% -> SA
(14, 50.00, 45.00, 55.00, FALSE, NULL, 19.90, 0.00, 19.90, 'SA', 0, 'PREVENTED (SA)'),
(15, 48.00, 42.00, 50.00, FALSE, NULL, 18.60, 0.00, 18.60, 'SA', 0, 'PREVENTED (SA)');
