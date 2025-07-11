# Student Tracker - JavaFX GUI Application

## Overview

A modern, professional JavaFX GUI application for managing student records. The application provides a beautiful, user-friendly interface that connects to the Student Tracker backend API.

## Features

### 🔐 Authentication
- **Login Page**: Secure user authentication with username/password
- **Registration Page**: New user account creation with validation
- **Session Management**: Automatic session handling

### 📊 Dashboard
- **Welcome Header**: Personalized greeting with logout functionality
- **Search Bar**: Advanced search with multiple criteria (First Name, Last Name, Age, Grade)
- **Student Table**: Professional data table with sorting and selection
- **Pagination**: Efficient handling of large datasets
- **Status Bar**: Real-time feedback and status updates

### 👥 Student Management
- **Add Student**: Modal dialog with form validation
- **Update Student**: Edit existing student records
- **Delete Student**: Confirmation dialog for safe deletion
- **Real-time Updates**: Automatic data refresh after operations

### 📈 Analytics
- **Statistics Dashboard**: Average grade and total student count
- **Grade Distribution Chart**: Bar chart showing grade ranges
- **Real-time Data**: Live statistics from the database

### 📁 Import/Export
- **CSV Import**: Import student data from CSV files
- **JSON Import**: Import student data from JSON files
- **CSV Export**: Export data to CSV format
- **JSON Export**: Export data to JSON format
- **Timestamped Files**: Automatic file naming with timestamps

## Design & Styling

### 🎨 Color Scheme
- **Light Grey**: `#f5f5f5` (Background)
- **Dark Grey**: `#34495e` (Headers, Buttons)
- **Accent Blue**: `#3498db` (Primary actions)
- **Success Green**: `#27ae60` (Add operations)
- **Warning Orange**: `#f39c12` (Update operations)
- **Danger Red**: `#e74c3c` (Delete operations)

### 🎯 Professional Features
- **Rounded Buttons**: Modern button design with hover effects
- **Glowing Effects**: Subtle animations on button interactions
- **Responsive Layout**: Adapts to different window sizes
- **Professional Typography**: Segoe UI font family
- **Consistent Spacing**: Well-organized layout with proper padding

## Technical Architecture

### 🏗️ Application Structure
```
src/main/java/com/laplateforme/tracker/gui/
├── StudentTrackerApp.java      # Main application class
├── ApiService.java             # Backend API communication
├── LoginView.java              # Login interface
├── RegisterView.java           # Registration interface
├── DashboardView.java          # Main dashboard
├── AnalyticsView.java          # Statistics and charts
└── StudentDialog.java          # Add/Edit student dialog
```

### 🔌 API Integration
- **RESTful Communication**: HTTP requests to backend API
- **Async Operations**: Background thread processing
- **Error Handling**: Comprehensive error management
- **Data Parsing**: Custom response parsing for Student objects

### 🎨 Styling System
- **CSS-based**: External stylesheet for consistent theming
- **Modular Design**: Reusable CSS classes
- **Responsive**: Adapts to different screen sizes
- **Professional**: Modern, clean appearance

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Student Tracker backend server running on `localhost:8080`

### Running the Application

1. **Start the Backend Server**:
   ```bash
   # In the project directory
   java -cp target/classes com.laplateforme.tracker.server.TrackerHttpServer
   ```

2. **Launch the GUI Application**:
   ```bash
   # Using Maven
   mvn javafx:run
   
   # Or using Java directly
   java -cp target/classes com.laplateforme.tracker.gui.StudentTrackerApp
   ```

### Default Credentials
- **Username**: `admin`
- **Password**: `admin123`

## Usage Guide

### 1. Authentication
- Launch the application
- Enter your credentials or create a new account
- Click "Sign In" to access the dashboard

### 2. Managing Students
- **Add**: Click "Add Student" button, fill the form, click "Add"
- **Update**: Select a student from the table, click "Update", modify fields, click "Update"
- **Delete**: Select a student, click "Delete", confirm the action

### 3. Searching
- Choose search criteria from the dropdown
- Enter search term in the text field
- Click "Search" or press Enter
- Use "Clear" to reset search results

### 4. Analytics
- Click "Analytics" button to open statistics window
- View average grade and total student count
- Examine grade distribution chart

### 5. Import/Export
- **Import**: Click "Import", select CSV/JSON file, confirm
- **Export**: Click "Export", choose format and location, save

## API Endpoints Used

The GUI connects to these backend endpoints:

- `POST /login` - User authentication
- `POST /register` - User registration
- `GET /students` - List all students
- `POST /students` - Add new student
- `PUT /students/{id}` - Update student
- `DELETE /students/{id}` - Delete student
- `GET /students?search=...` - Search students
- `GET /students/statistics` - Get statistics
- `GET /students/export` - Export data
- `POST /students/import` - Import data

## Error Handling

The application provides comprehensive error handling:

- **Network Errors**: Connection timeout and server unavailable
- **Validation Errors**: Form validation with user-friendly messages
- **API Errors**: Backend error responses with status codes
- **File Errors**: Import/export operation failures

## Performance Features

- **Background Processing**: All API calls run in background threads
- **UI Responsiveness**: Non-blocking operations maintain UI responsiveness
- **Efficient Data Loading**: Pagination for large datasets
- **Memory Management**: Proper cleanup of resources

## Security Features

- **Input Validation**: Client-side form validation
- **Data Sanitization**: Proper encoding of user inputs
- **Secure Communication**: HTTP requests with proper headers
- **Session Management**: Proper authentication flow

## Future Enhancements

Potential improvements for future versions:

- **Real-time Updates**: WebSocket integration for live data
- **Advanced Filtering**: Multiple criteria filtering
- **Bulk Operations**: Select multiple students for batch operations
- **Data Visualization**: More advanced charts and graphs
- **User Preferences**: Customizable interface settings
- **Offline Mode**: Local data caching for offline use

## Troubleshooting

### Common Issues

1. **Connection Failed**
   - Ensure backend server is running on port 8080
   - Check firewall settings
   - Verify network connectivity

2. **Login Failed**
   - Verify username and password
   - Check if user account exists
   - Ensure backend authentication is working

3. **Import/Export Issues**
   - Verify file format (CSV/JSON)
   - Check file permissions
   - Ensure sufficient disk space

4. **Performance Issues**
   - Close other applications
   - Check available memory
   - Restart the application

## Contributing

To contribute to the GUI application:

1. Follow the existing code style
2. Add proper error handling
3. Include CSS styling for new components
4. Test thoroughly before submitting
5. Update documentation as needed

## License

This GUI application is part of the Student Tracker project and follows the same licensing terms. 