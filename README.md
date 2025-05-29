# MindMingle Backend

A Spring Boot backend application focused on improving users' mental wellbeing and self-confidence through mind-enhancing features.

## Tech Stack

- **Framework**: Spring Boot (Java)
- **Security**: Spring Security with JWT authentication
- **Database**: PostgreSQL with Hibernate JPA
- **Cloud Storage**: Azure Blob Storage
- **AI Integration**: Google Gemini API

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- PostgreSQL database (local or cloud)
- Azure Storage Account (optional, for file storage)
- Google Gemini API key (optional, for AI features)

## Configuration

### Database Configuration

The application is configured to use Azure PostgreSQL by default. You can choose between cloud and local database:

#### Option 1: Azure PostgreSQL (Default)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://mindmingle5703-new.postgres.database.azure.com/postgres
    username: mindmingle
    password: mm123456!
```

#### Option 2: Local PostgreSQL
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

Configure Azure Blob Storage for file uploads:

1. Create an Azure Storage Account
2. Update the connection string in `application.yml`:
```yaml
azure:
  storage:
    connection-string: DefaultEndpointsProtocol=https;AccountName=your_account;AccountKey=YOUR_SECRET_KEY;EndpointSuffix=core.windows.net
    container-name: your-container-name
```

### Gemini AI Configuration

To enable AI features, configure your Google Gemini API:

1. Get your API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Set the environment variable:
```bash
export GEMINI_API_KEY=your_api_key_here
```

## Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd mindmingle/backend/mindmingle
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
```bash
# Using Maven
mvn spring-boot:run

# Or using Java
java -jar target/mindmingle-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## Key Features

- **User Authentication**: JWT-based authentication system
- **File Upload**: Support for file uploads up to 20MB via Azure Blob Storage
- **Database Management**: Automatic schema updates with Hibernate
- **AI Integration**: Gemini API for AI-powered features
- **RESTful APIs**: Comprehensive REST API endpoints

## Development Configuration

For development environment:
- Thymeleaf template caching is disabled for hot reload
- SQL logging is enabled for debugging
- Hibernate DDL auto-update is enabled

## Production Deployment

For production deployment:
1. Set `spring.jpa.hibernate.ddl-auto` to `validate` or `none`
2. Use environment variables for all sensitive configurations
3. Enable SSL/HTTPS
4. Configure proper logging levels
5. Set up monitoring and health checks

## API Documentation

The application exposes RESTful APIs. Access the API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html` (if configured)
- Base API URL: `http://localhost:8080/api`

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify database credentials and connection URL
   - Ensure PostgreSQL is running and accessible

2. **Azure Blob Storage Issues**
   - Check Azure connection string and container permissions
   - Verify container exists and is accessible

3. **JWT Authentication Issues**
   - Ensure JWT secret is properly configured
   - Check token expiration settings (default: 15 days)

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

