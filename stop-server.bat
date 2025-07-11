@echo off
echo ========================================
echo    Stopping Student Tracker Server
echo ========================================
echo.

echo Looking for running backend server processes...

:: Find and kill Java processes running the backend server
for /f "tokens=2" %%i in ('tasklist /fi "imagename eq java.exe" /fo table /nh ^| findstr "java.exe"') do (
    echo Found Java process: %%i
    taskkill /PID %%i /F >nul 2>&1
    if errorlevel 1 (
        echo Could not stop process %%i
    ) else (
        echo Successfully stopped process %%i
    )
)

echo.
echo All backend server processes have been stopped.
echo.
pause 