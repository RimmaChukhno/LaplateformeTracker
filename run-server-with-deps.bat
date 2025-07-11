@echo off
echo ========================================
echo    Student Tracker Server with Dependencies
echo ========================================
echo.

:: Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher and try again
    pause
    exit /b 1
)

:: Check if Maven is installed
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven 3.6+ and try again
    pause
    exit /b 1
)

echo Compiling project with dependencies...
call mvn clean compile

if errorlevel 1 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo    Starting Server with Dependencies
echo ========================================
echo.

:: Run the server with Maven exec plugin to include all dependencies
echo Starting server on port 8080...
echo Server will be available at: http://localhost:8080
echo Press Ctrl+C to stop the server
echo.

call mvn exec:java

pause 