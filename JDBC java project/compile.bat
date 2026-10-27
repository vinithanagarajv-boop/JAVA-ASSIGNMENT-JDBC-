@echo off
echo ============================================================================
echo   Kongu Engineering College (Autonomous) - Regulation 2024
echo   Compiling Student Academic and Continuous Evaluation System...
echo ============================================================================

if not exist bin mkdir bin

javac -cp "lib\mysql-connector-j-8.3.0.jar;." -d bin *.java

if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] Compilation finished successfully. All class files are in 'bin\'.
) else (
    echo [ERROR] Compilation failed. Please check error messages above.
)
pause
