@echo off
echo ========================================
echo    Student Tracker - Quick Launcher
echo ========================================
echo.

:: Check if compiled classes exist
if not exist "target\classes\com\laplateforme\tracker\server\TrackerHttpServer.class" (
    echo ERROR: Application not compiled!
    echo Please run run-application.bat first to compile the project
    pause
    exit /b 1
)

:: Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher and try again
    pause
    exit /b 1
)

echo ========================================
echo    Step 1: Starting Backend Server
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
echo    Step 2: Starting GUI Application
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
java -cp target/classes com.laplateforme.tracker.gui.StudentTrackerApp

echo.
echo ========================================
echo    Application has been closed
echo ========================================
echo.
echo Note: The backend server may still be running.
echo To stop it, close the backend server window.
echo.
pause 