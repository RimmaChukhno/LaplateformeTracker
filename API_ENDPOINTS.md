# Student Tracker API Documentation

## Base URL
`http://localhost:8080`

## Authentication Endpoints

### POST /login
Authenticate a user
- **Body**: `username=admin&password=admin123`
- **Response**: `200 OK` or `401 Unauthorized`

## Student Management Endpoints

### GET /students
Get all students
- **Response**: `200 OK` - List of all students

### GET /students/{id}
Get student by ID
- **Parameters**: `id` (integer)
- **Response**: `200 OK` - Student object or `404 Not Found`

### POST /students
Add a new student
- **Body**: `firstName=John&lastName=Doe&age=20&grade=15.5`
- **Response**: `201 Created` or `400 Bad Request`

### PUT /students/{id}
Update an existing student
- **Parameters**: `id` (integer)
- **Body**: `firstName=John&lastName=Doe&age=20&grade=15.5`
- **Response**: `200 OK` or `404 Not Found`

### DELETE /students/{id}
Delete a student
- **Parameters**: `id` (integer)
- **Response**: `200 OK` or `404 Not Found`

## Advanced Features Endpoints

### GET /students?search={criteria}&value={value}
Search students by criteria
- **Parameters**: 
  - `search`: `first_name`, `last_name`, `age`, or `grade`
  - `value`: The value to search for
- **Examples**:
  - `GET /students?search=first_name&value=John`
  - `GET /students?search=age&value=20`
  - `GET /students?search=grade&value=15.5`
- **Response**: `200 OK` - List of matching students

### GET /students?sort={field}&order={direction}
Sort students by field
- **Parameters**:
  - `sort`: `id`, `first_name`, `last_name`, `age`, or `grade`
  - `order`: `asc` or `desc`
- **Examples**:
  - `GET /students?sort=first_name&order=asc`
  - `GET /students?sort=grade&order=desc`
- **Response**: `200 OK` - Sorted list of students

### GET /students?page={page}&size={size}
Get paginated students
- **Parameters**:
  - `page`: Page number (1-based)
  - `size`: Number of students per page
- **Example**: `GET /students?page=1&size=10`
- **Response**: `200 OK` - Paginated list of students

### GET /students/statistics
Get student statistics
- **Response**: `200 OK` - JSON with total students and average grade
- **Example Response**: `{"totalStudents":25,"averageGrade":14.75}`

### GET /students/export?format={format}&filename={filename}
Export students to file
- **Parameters**:
  - `format`: `csv` or `json`
  - `filename`: Output filename
- **Examples**:
  - `GET /students/export?format=csv&filename=students.csv`
  - `GET /students/export?format=json&filename=students.json`
- **Response**: `200 OK` - Export confirmation

### POST /students/import
Import students from file
- **Body**: `format=csv&filename=students.csv`
- **Parameters**:
  - `format`: `csv` or `json`
  - `filename`: Input filename
- **Response**: `200 OK` - Import confirmation with count

## System Endpoints

### GET /
Welcome message
- **Response**: `200 OK` - "Welcome to the Student Tracker API"

### GET /health
Health check
- **Response**: `200 OK` - "Server is running"

## Error Responses

- `400 Bad Request`: Invalid parameters or missing required fields
- `404 Not Found`: Resource not found
- `405 Method Not Allowed`: HTTP method not supported
- `500 Internal Server Error`: Server error

## Security Features

- Input validation to prevent SQL injection
- Parameter sanitization
- Error handling with appropriate HTTP status codes
- Content-Type headers for JSON responses

## Database Schema

```sql
CREATE TABLE student (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    age INTEGER NOT NULL CHECK (age > 0),
    grade DECIMAL(4,2) NOT NULL CHECK (grade >= 0 AND grade <= 20)
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);
```

## Testing Examples

### Using curl

```bash
# Get all students
curl http://localhost:8080/students

# Search for students with first name "John"
curl "http://localhost:8080/students?search=first_name&value=John"

# Sort students by grade descending
curl "http://localhost:8080/students?sort=grade&order=desc"

# Get first page of 5 students
curl "http://localhost:8080/students?page=1&size=5"

# Get statistics
curl http://localhost:8080/students/statistics

# Export to CSV
curl "http://localhost:8080/students/export?format=csv&filename=students.csv"

# Add a new student
curl -X POST http://localhost:8080/students \
  -d "firstName=Jane&lastName=Doe&age=22&grade=16.5"
``` 