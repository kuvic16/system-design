# Job Application API

A Spring Boot REST API for managing job applications.

## Project Structure

```
src/
├── main/
│   ├── java/com/example/jobapplication/
│   │   ├── JobApplicationApiApplication.java  (Main Entry Point)
│   │   ├── controller/
│   │   │   └── JobApplicationController.java  (REST Endpoints)
│   │   ├── service/
│   │   │   └── JobApplicationService.java     (Business Logic)
│   │   ├── repository/
│   │   │   └── JobApplicationRepository.java  (Data Access)
│   │   └── entity/
│   │       └── JobApplication.java            (Data Model)
│   └── resources/
│       └── application.properties              (Configuration)
└── test/
    └── java/com/example/jobapplication/       (Test Classes)
```

## Technology Stack

- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Data JPA**
- **H2 Database** (In-memory for development)
- **Lombok** (Reduce boilerplate code)
- **Jakarta Validation** (Input validation)

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build the Project
```bash
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080`

## API Endpoints

### 1. Create Job Application
**POST** `/api/job-applications`

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "jobId": 1,
  "resumeUrl": "https://example.com/resume.pdf"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "jobId": 1,
  "resumeUrl": "https://example.com/resume.pdf"
}
```

### 2. Get All Job Applications
**GET** `/api/job-applications`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "jobId": 1,
    "resumeUrl": "https://example.com/resume.pdf"
  },
  {
    "id": 2,
    "name": "Jane Smith",
    "email": "jane@example.com",
    "jobId": 2,
    "resumeUrl": "https://example.com/resume2.pdf"
  }
]
```

### 3. Get Job Application by ID
**GET** `/api/job-applications/{id}`

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "jobId": 1,
  "resumeUrl": "https://example.com/resume.pdf"
}
```

### 4. Update Job Application
**PUT** `/api/job-applications/{id}`

**Request Body:**
```json
{
  "name": "John Doe Updated",
  "email": "newemail@example.com",
  "jobId": 2,
  "resumeUrl": "https://example.com/resume-updated.pdf"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "John Doe Updated",
  "email": "newemail@example.com",
  "jobId": 2,
  "resumeUrl": "https://example.com/resume-updated.pdf"
}
```

### 5. Delete Job Application
**DELETE** `/api/job-applications/{id}`

**Response (204 No Content)**

## Validation

All fields are validated:
- `name` - Required, non-blank
- `email` - Required, non-blank, must be valid email format
- `jobId` - Required
- `resumeUrl` - Required, non-blank

## Database Configuration

The application uses an in-memory H2 database for development. To use a different database (like PostgreSQL):

1. Update `pom.xml` to add the database driver dependency
2. Modify `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/jobdb
   spring.datasource.username=postgres
   spring.datasource.password=password
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.hibernate.ddl-auto=update
   ```

## H2 Console (Development)

H2 console is available at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:jobdb`
- User Name: `sa`
- Password: (leave blank)

## Error Handling

- **400 Bad Request** - Validation errors or invalid input
- **404 Not Found** - Resource not found
- **500 Internal Server Error** - Server-side error

## Future Enhancements

- [ ] Add pagination and sorting
- [ ] Add filtering capabilities
- [ ] Add Spring Security for authentication
- [ ] Add API documentation (Swagger/OpenAPI)
- [ ] Add exception handling with custom error responses
- [ ] Add logging and monitoring
- [ ] Add unit and integration tests
- [ ] Add caching
- [ ] Add async processing

## License

This project is open source and available under the MIT License.

