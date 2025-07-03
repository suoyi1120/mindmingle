# MindMingle Backend

A Spring Boot backend application focused on improving users' mental wellbeing and self-confidence through mind-enhancing features.

## Tech Stack

- **Framework**: Spring Boot 3.4.3
- **Language**: Java 17
- **Build Tool**: Maven
- **Security**: Spring Security with JWT authentication (jjwt 0.11.5)
- **Database**: PostgreSQL with Hibernate JPA
- **Cloud Storage**: Azure Blob Storage
- **AI Integration**: Google Gemini API
- **Additional Libraries**:
  - Lombok for code generation
  - ModelMapper 3.1.1 for object mapping
  - Apache Tika 2.9.2 for file type detection
  - Spring Boot Validation for input validation

## Prerequisites

- **Java**: Java 17 or higher
- **Build Tool**: Maven 3.6+
- **Database**: PostgreSQL (Azure PostgreSQL or local instance)
- **Cloud Storage**: Azure Storage Account with Blob service
- **AI Service**: Google Gemini API key
- **Operating System**: Windows, macOS, or Linux

## Project Structure

```
mindmingle/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/group02/mindmingle/
│   │   │       ├── common/                 # Common utilities and shared components
│   │   │       │   ├── exception/          # Custom exception classes
│   │   │       │   └── security/           # Security-related utilities
│   │   │       ├── config/                 # Spring configuration classes
│   │   │       ├── controller/             # REST API controllers
│   │   │       ├── dto/                    # Data Transfer Objects
│   │   │       │   ├── auth/               # Authentication DTOs
│   │   │       │   ├── challenge/          # Challenge-related DTOs
│   │   │       │   ├── common/             # Common DTOs
│   │   │       │   ├── community/          # Community feature DTOs
│   │   │       │   ├── game/               # Game-related DTOs
│   │   │       │   ├── gemini/             # Gemini AI DTOs
│   │   │       │   ├── gpt/                # GPT-related DTOs
│   │   │       │   └── user/               # User management DTOs
│   │   │       ├── exception/              # Global exception handling
│   │   │       ├── mapper/                 # Entity-DTO mapping utilities
│   │   │       ├── model/                  # JPA Entity classes
│   │   │       ├── repository/             # Data access layer (JPA repositories)
│   │   │       ├── scheduler/              # Scheduled tasks and jobs
│   │   │       ├── service/                # Business logic layer
│   │   │       │   └── impl/               # Service implementations
│   │   │       └── MindmingleApplication.java # Main Spring Boot application class
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/              # Database migration scripts
│   │       ├── prompt/                     # AI prompt templates
│   │       ├── static/
│   │       │   └── games/                  # Static game resources
│   │       └── application.yml             # Application configuration
│   └── test/
│       └── java/
│           └── com/group02/mindmingle/
│               └── service/                # Unit and integration tests
├── target/                                 # Maven build output directory
├── logs/                                   # Application log files
├── pom.xml                                 # Maven project configuration
└── README.md                               # Project documentation
```

### Key Directories Explained

#### Source Code (`src/main/java/`)
- **`common/`**: Shared utilities, exceptions, and security components used across the application
- **`config/`**: Spring Boot configuration classes for security, database, and other services
- **`controller/`**: REST API endpoints that handle HTTP requests and responses
- **`dto/`**: Data Transfer Objects organized by feature domain (auth, challenges, community, etc.)
- **`exception/`**: Global exception handlers and custom exception classes
- **`mapper/`**: Utility classes for converting between entities and DTOs (using ModelMapper)
- **`model/`**: JPA entity classes representing database tables
- **`repository/`**: Data access layer using Spring Data JPA repositories
- **`scheduler/`**: Scheduled tasks and background job implementations
- **`service/`**: Business logic layer with service interfaces and implementations

#### Resources (`src/main/resources/`)
- **`db/migration/`**: Database schema migration scripts for version control
- **`prompt/`**: Template files for AI prompt generation
- **`static/games/`**: Static resources for game features
- **`application.yml`**: Main application configuration file

#### Test Code (`src/test/`)
- **`service/`**: Unit tests and integration tests for service layer components

### Architecture Pattern

The project follows a **layered architecture** pattern:

1. **Controller Layer**: Handles HTTP requests and responses
2. **Service Layer**: Contains business logic and orchestrates data flow
3. **Repository Layer**: Manages data persistence and database operations
4. **Model Layer**: Defines data structures and entity relationships

### Feature Organization

The codebase is organized by feature domains:
- **Authentication & Security**: User login, registration, JWT handling
- **Challenges**: Daily mental wellness challenges and activities
- **Community**: Social features and user interactions
- **Games**: Mental wellness games and activities
- **AI Integration**: Gemini API integration for AI-powered features
- **User Management**: Profile management and user data

## Configuration

### Database Configuration

The application is configured to use Azure PostgreSQL by default:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://mindmingle5703-new.postgres.database.azure.com/postgres
    username: mindmingle
    password: mm123456!
    driver-class-name: org.postgresql.Driver
```

#### For Local PostgreSQL Setup:
Update `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mindmingle
    username: your_username
    password: your_password
```

### Environment Variables

For security, it's recommended to use environment variables for sensitive data:

```bash
# Database credentials
DB_URL=jdbc:postgresql://your-db-host:5432/mindmingle
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT Secret
JWT_SECRET=your_jwt_secret_key

# Gemini API Key
GEMINI_API_KEY=your_gemini_api_key

# Azure Storage
AZURE_STORAGE_CONNECTION_STRING=your_azure_connection_string
```

### Azure Blob Storage Configuration

The application is configured for Azure Blob Storage:

```yaml
azure:
  storage:
    connection-string: DefaultEndpointsProtocol=https;AccountName=mindminglefile;AccountKey=YOUR_SECRET_KEY;EndpointSuffix=core.windows.net
    container-name: generated-file
```

### Gemini AI Configuration

Configure your Google Gemini API:

```yaml
gemini:
  api:
    url: https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro-preview-05-06:generateContent?key=${GEMINI_API_KEY}
```

## Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd backend/mindmingle
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Configure Database
- Ensure PostgreSQL is running (local or accessible cloud instance)
- Create database if using local setup:
```sql
CREATE DATABASE mindmingle;
```

### 4. Set Environment Variables
Create a `.env` file or set environment variables as needed.

### 5. Run the Application

#### Using Maven:
```bash
mvn spring-boot:run
```

#### Using Java directly:
```bash
# Build the project first
mvn clean package

# Run the generated JAR file
java -jar target/mindmingle-0.0.1-SNAPSHOT.jar
```

#### Using IDE:
Run the `MindmingleApplication.java` file located at:
```
src/main/java/com/group02/mindmingle/MindmingleApplication.java
```

The application will start on `http://localhost:8080`

When the application starts successfully, you'll see output similar to:
```
🚀 Spring Boot 运行地址: http://localhost:8080
🔗 连接的数据库: jdbc:postgresql://mindmingle5703-new.postgres.database.azure.com/postgres
```

## Key Features

- **User Authentication**: JWT-based authentication system with 15-day token expiration
- **File Upload**: Support for file uploads up to 20MB via Azure Blob Storage
- **Database Management**: Automatic schema updates with Hibernate DDL auto-update
- **AI Integration**: Gemini API for AI-powered features
- **RESTful APIs**: Comprehensive REST API endpoints
- **Security**: Spring Security with JWT token-based authentication
- **File Type Detection**: Apache Tika for automatic file type detection

## Application Configuration Details

### Server Configuration
- **Port**: 8080 (configurable)
- **Max File Size**: 20MB
- **Max Request Size**: 50MB

### Database Configuration
- **Hibernate DDL**: Auto-update mode
- **SQL Dialect**: PostgreSQL
- **Show SQL**: Enabled for development

### JWT Configuration
- **Expiration**: 1,296,000 seconds (15 days)
- **Secret**: Configurable via environment variables

## Development Configuration

For development environment:
- Thymeleaf template caching is disabled for hot reload
- SQL logging is enabled for debugging (DEBUG level)
- Hibernate DDL auto-update is enabled
- Circular references are allowed

## Production Deployment

For production deployment:
1. Set `spring.jpa.hibernate.ddl-auto` to `validate` or `none`
2. Use environment variables for all sensitive configurations
3. Enable SSL/HTTPS
4. Configure proper logging levels
5. Set up monitoring and health checks
6. Update PostgreSQL dependency to address security vulnerabilities

## API Documentation

The application exposes RESTful APIs. Base API URL: `http://localhost:8080/api`

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify database credentials and connection URL
   - Ensure PostgreSQL is running and accessible
   - Check network connectivity to Azure PostgreSQL

2. **Azure Blob Storage Issues**
   - Check Azure connection string and container permissions
   - Verify container exists and is accessible
   - Ensure proper Azure credentials are configured

3. **JWT Authentication Issues**
   - Ensure JWT secret is properly configured
   - Check token expiration settings (default: 15 days)

4. **File Upload Issues**
   - Check file size limits (max 20MB)
   - Verify Azure Blob Storage configuration

### Security Note

**Important**: The PostgreSQL dependency has known security vulnerabilities. Consider updating to the latest version:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.6</version> <!-- or latest secure version -->
    <scope>runtime</scope>
</dependency>
```

### Logs

Application logs are available at the console level. Key log levels:
- Spring Framework: INFO
- Hibernate SQL: DEBUG
- SQL Parameters: TRACE

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

[Add your license information here]

