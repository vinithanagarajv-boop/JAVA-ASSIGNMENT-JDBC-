# Kongu Engineering College (Autonomous) - Regulation 2024 (R2024)
## Student Academic & Continuous Evaluation System using Java JDBC

---

### Project Deliverables Checklist
- [x] **Database Schema Script** ([`kec_schema.sql`](file:///d:/java%20assignment/kec_schema.sql)): DDL statements for `students`, `courses`, `enrollments`, and `assessment_marks` with constraints, foreign keys, and seed data.
- [x] **Source Code (.java files)**:
  - [`DBConnection.java`](file:///d:/java%20assignment/DBConnection.java): JDBC connection management and schema verification.
  - [`StudentDAO.java`](file:///d:/java%20assignment/StudentDAO.java): Student registration, enrollment, and attendance CRUD queries.
  - [`AcademicDAO.java`](file:///d:/java%20assignment/AcademicDAO.java): Course catalog, phased incremental marks entry, and report queries.
  - [`EvaluationEngine.java`](file:///d:/java%20assignment/EvaluationEngine.java): KEC R2024 evaluation rules (THEORY, PRACTICAL, EMBEDDED), attendance eligibility, and 10-point grading engine.
  - [`CourseType.java`](file:///d:/java%20assignment/CourseType.java), [`Student.java`](file:///d:/java%20assignment/Student.java), [`Course.java`](file:///d:/java%20assignment/Course.java), [`Enrollment.java`](file:///d:/java%20assignment/Enrollment.java), [`AssessmentMark.java`](file:///d:/java%20assignment/AssessmentMark.java): Domain entity models.
  - [`SemesterMarkSheet.java`](file:///d:/java%20assignment/SemesterMarkSheet.java), [`DefaulterRecord.java`](file:///d:/java%20assignment/DefaulterRecord.java), [`ClassSummaryReport.java`](file:///d:/java%20assignment/ClassSummaryReport.java): Report models.
  - [`ConsoleReportFormatter.java`](file:///d:/java%20assignment/ConsoleReportFormatter.java): Clean ASCII/boxed console reporting.
  - [`HtmlReportGenerator.java`](file:///d:/java%20assignment/HtmlReportGenerator.java): Official autonomous Grade Card HTML generator.
  - [`MainApp.java`](file:///d:/java%20assignment/MainApp.java): Interactive menu-driven interface and automated demo runner.
- [x] **Execution Documentation & Proofs** ([`docs/EXECUTION_DOCUMENTATION.md`](./docs/EXECUTION_DOCUMENTATION.md)):
  - Adding a student and course proof
  - Incremental marks entry (CIA1, CIA2, Assignment, ESE) proof
  - Generated Grade Sheet with calculated SGPA proof
- [x] **Official Project Report PDF** ([`Project_Report_VINITHA_N_25ITR174.pdf`](./Project_Report_VINITHA_N_25ITR174.pdf)): Complete 28-page project submission report by **Vinitha N (25ITR174)** for 24ITT32 - Programming in Java.

---

### Evaluation Rules & Weightage Engine (KEC R2024)

1. **THEORY**:
   - CIA Weightage: **40%** | ESE Weightage: **60%**
   - ESE Minimum Threshold: **45%** (27.0 / 60 marks)
   - Overall Pass Minimum: **50 Marks**

2. **PRACTICAL**:
   - CIA Weightage: **60%** | ESE Weightage: **40%**
   - ESE Minimum Threshold: **50%** (20.0 / 40 marks)
   - Overall Pass Minimum: **50 Marks**

3. **EMBEDDED**:
   - CIA Weightage: **50%** | ESE Weightage: **50%**
   - ESE Minimum Threshold: **45%** (22.5 / 50 marks)
   - Overall Pass Minimum: **50 Marks**

4. **Attendance Filter**:
   - $\ge 75\%$: Eligible for ESE
   - $65\% \text{ to } 74.9\%$: Conditionally eligible (Requires Medical Condonation approval)
   - $< 65\%$: Award **SA** (Shortage of Attendance) grade (0 Grade Points), Barred from ESE.

5. **10-Point Grading System**:
   - **O** (91–100): GP 10
   - **A+** (81–90): GP 9
   - **A** (71–80): GP 8
   - **B+** (61–70): GP 7
   - **B** (50–60): GP 6
   - **RA** ($<50$ or failed ESE min): GP 0 (Re-Appearance)
   - **SA** (Attendance Shortage): GP 0
   - **AB** (Absent in ESE): GP 0

6. **SGPA & Percentage**:
   - $\text{SGPA} = \sum(\text{Credits} \times \text{GP}) / \sum(\text{Credits})$
   - $\text{Equivalent Percentage} = \text{SGPA} \times 10.0$

---

### Quick Start Commands

```powershell
# 1. Compile
.\compile.bat

# 2. Run Interactive CLI Menu
.\run.bat

# 3. Run Automated Demonstration & Reports
.\run_demo.bat
```
