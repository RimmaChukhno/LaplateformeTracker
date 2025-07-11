@echo off
echo ========================================
echo    Student Tracker Application Launcher
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

echo Checking Java version...
java -version
echo.

echo Checking Maven version...
mvn -version
echo.

echo ========================================
echo    Step 1: Compiling the project
echo ========================================
echo.

:: Clean and compile the project
echo Cleaning previous builds...
call mvn clean

echo Compiling the project...
call mvn compile

if errorlevel 1 (
    echo ERROR: Compilation failed!
    echo Please check the error messages above and fix any issues
    pause
    exit /b 1
)

echo.
echo ========================================
echo    Step 2: Starting Backend Server
echo ========================================
echo.

:: Start the backend server in a new window
echo Starting backend server on port 8080...
start "Student Tracker Backend Server" cmd /k "echo Backend Server is starting... && echo. && echo Server will be available at: http://localhost:8080 && echo. && echo Press Ctrl+C to stop the server && echo. && java -cp target/classes com.laplateforme.tracker.server.TrackerHttpServer"

:: Wait a moment for the server to start
echo Waiting for backend server to start...
timeout /t 3 /nobreak >nul

echo.
echo ========================================
echo    Step 3: Starting GUI Application
echo ========================================
echo.

:: Start the GUI application
echo Starting JavaFX GUI application...
echo.
echo Default login credentials:
echo Username: admin
echo Password: admin123
echo.

:: Run the GUI application
call mvn javafx:run

echo.
echo ========================================
echo    Application has been closed
echo ========================================
echo.
echo Note: The backend server may still be running.
echo To stop it, close the backend server window.
echo.
pause 