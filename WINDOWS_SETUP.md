# Student Tracker - Windows Setup Guide

## Quick Start

### Option 1: Full Setup (Recommended for first time)
1. Double-click `run-application.bat`
2. Wait for compilation and startup
3. Use the GUI with credentials: `admin` / `admin123`

### Option 2: Quick Launch (After first setup)
1. Double-click `run-application-quick.bat`
2. Application starts immediately
3. Use the GUI with credentials: `admin` / `admin123`

### Stop the Application
- Close the GUI window to stop the application
- Double-click `stop-server.bat` to stop the backend server

## Prerequisites

### Required Software
1. **Java 17 or higher**
   - Download from: https://adoptium.net/
   - Add Java to your system PATH
   - Verify installation: `java -version`

2. **Maven 3.6+**
   - Download from: https://maven.apache.org/download.cgi
   - Extract to a folder (e.g., `C:\Program Files\Apache\maven`)
   - Add Maven to your system PATH
   - Verify installation: `mvn -version`

3. **PostgreSQL Database**
   - Download from: https://www.postgresql.org/download/windows/
   - Install with default settings
   - Default credentials: `postgres` / `1111`
   - The application will create the database automatically

### System Requirements
- Windows 10 or higher
- 4GB RAM minimum
- 1GB free disk space
- Internet connection (for dependencies)

## Installation Steps

### Step 1: Install Java
1. Download OpenJDK 17 from https://adoptium.net/
2. Run the installer
3. Follow the installation wizard
4. Verify installation by opening Command Prompt and typing:
   ```cmd
   java -version
   ```

### Step 2: Install Maven
1. Download Maven from https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH:
   - Open System Properties → Advanced → Environment Variables
   - Add `C:\Program Files\Apache\maven\bin` to PATH
4. Verify installation:
   ```cmd
   mvn -version
   ```

### Step 3: Install PostgreSQL
1. Download PostgreSQL from https://www.postgresql.org/download/windows/
2. Run the installer
3. Use default settings:
   - Port: 5432
   - Username: postgres
   - Password: 1111
4. Complete the installation

### Step 4: Download and Setup Project
1. Download the project files
2. Extract to a folder (e.g., `C:\StudentTracker`)
3. Open Command Prompt in the project folder
4. Run the application:
   ```cmd
   run-application.bat
   ```

## Scripts Overview

### `run-application.bat`
- **Purpose**: Full setup and launch
- **Actions**:
  - Checks Java and Maven installation
  - Cleans and compiles the project
  - Starts backend server in new window
  - Launches GUI application
- **Use when**: First time setup or after code changes

### `run-application-quick.bat`
- **Purpose**: Quick launch without recompilation
- **Actions**:
  - Checks if project is compiled
  - Starts backend server
  - Launches GUI application
- **Use when**: Daily usage (faster startup)

### `stop-server.bat`
- **Purpose**: Stop backend server processes
- **Actions**:
  - Finds running Java processes
  - Terminates backend server
- **Use when**: Need to stop server manually

## Troubleshooting

### Common Issues

#### 1. "Java is not installed or not in PATH"
**Solution**:
- Install Java 17+ from https://adoptium.net/
- Add Java to system PATH
- Restart Command Prompt

#### 2. "Maven is not installed or not in PATH"
**Solution**:
- Install Maven from https://maven.apache.org/download.cgi
- Add Maven bin folder to system PATH
- Restart Command Prompt

#### 3. "Compilation failed"
**Solution**:
- Check Java and Maven versions
- Ensure all project files are present
- Check for syntax errors in code

#### 4. "Connection failed" in GUI
**Solution**:
- Ensure PostgreSQL is running
- Check if backend server started successfully
- Verify database credentials in code

#### 5. "Port 8080 already in use"
**Solution**:
- Close other applications using port 8080
- Or modify the port in `TrackerHttpServer.java`
- Use `stop-server.bat` to kill existing processes

#### 6. "Database connection failed"
**Solution**:
- Ensure PostgreSQL is installed and running
- Verify credentials: username=`postgres`, password=`1111`
- Check if PostgreSQL service is started

### Database Setup
The application automatically creates the database and tables. If you need to reset:

1. Open pgAdmin (comes with PostgreSQL)
2. Connect to localhost
3. Create new database: `student_tracker`
4. Or let the application create it automatically

### Manual Database Creation
```sql
-- Connect to PostgreSQL and run:
CREATE DATABASE student_tracker;
\c student_tracker;

-- Tables will be created automatically by the application
```

## Performance Tips

### For Better Performance
1. **Close unnecessary applications** before running
2. **Use SSD** for faster file access
3. **Increase Java heap size** if needed:
   ```cmd
   set JAVA_OPTS=-Xmx2g
   run-application.bat
   ```

### For Development
1. Use `run-application-quick.bat` for faster startup
2. Keep backend server running between GUI restarts
3. Use `stop-server.bat` only when needed

## File Structure
```
StudentTracker/
├── run-application.bat          # Full setup and launch
├── run-application-quick.bat    # Quick launch
├── stop-server.bat              # Stop backend server
├── src/                         # Source code
├── target/                      # Compiled classes
├── pom.xml                      # Maven configuration
└── README files
```

## Support

### Getting Help
1. Check this guide for common issues
2. Verify all prerequisites are installed
3. Check the console output for error messages
4. Ensure database is running and accessible

### Logs and Debugging
- Backend server logs appear in the server window
- GUI errors appear in the main console
- Database logs can be viewed in pgAdmin

### Contact
For additional support, check the project documentation or contact the development team. 