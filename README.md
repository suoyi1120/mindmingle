# MindMingle Backend

A Spring Boot backend application focused on improving users' mental wellbeing and self-confidence through mind-enhancing features.

## Tech Stack

- **Framework**: Spring Boot 3.4.3
- **Language**: Java 17
- **Build Tool**: Maven
- **Security**: Spring Security with JWT authentication
- **Database**: PostgreSQL with Hibernate JPA
- **Cloud Storage**: Azure Blob Storage
- **AI Integration**: Google Gemini API

## Prerequisites

- **Java**: Java 17 or higher
- **Build Tool**: Maven 3.6+
- **Database**: PostgreSQL (Azure PostgreSQL or local instance)
- **Cloud Storage**: Azure Storage Account with Blob service
- **AI Service**: Google Gemini API key
- **Operating System**: Windows, macOS, or Linux

## Azure Web App Deployment

The application is configured for automatic deployment to Azure Web App using GitHub Actions. The deployment process is triggered when code is pushed to the `release/v1.0.0` branch.

### Deployment Configuration

The deployment workflow (`.github/workflows/deploy-backend.yml`) is configured with the following settings:

- **Target Azure Web App**: `mindmingle-backend`
- **Java Version**: Java 17 (Temurin distribution)
- **Build Tool**: Maven with wrapper (`mvnw`)
- **Deployment Artifact**: JAR file from `target/*.jar`
- **Trigger Branch**: `release/v1.0.0`
- **Monitored Files**: `src/**` and `pom.xml`

### Prerequisites for Deployment

Before deploying to Azure, ensure you have:

1. **Azure Web App Service**: Created and configured in Azure Portal
2. **GitHub Secrets**: Required secrets configured in your GitHub repository
3. **Release Branch**: Code pushed to the `release/v1.0.0` branch

### Required GitHub Secrets

Configure the following secrets in your GitHub repository (Settings → Secrets and variables → Actions):

#### AZURE_CREDENTIALS
```json
{
  "clientId": "your-client-id",
  "clientSecret": "your-client-secret",
  "subscriptionId": "your-subscription-id",
  "tenantId": "your-tenant-id"
}
```

#### Environment Variables for Application
```bash
# Database Configuration
DB_URL=jdbc:postgresql://mindmingle5703-new.postgres.database.azure.com/postgres
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password

# JWT Configuration
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=1296000

# Google Gemini API
GEMINI_API_KEY=your_gemini_api_key

# Azure Storage
AZURE_STORAGE_ENDPOINT=your_azure_storage_endpoint
AZURE_STORAGE_CONTAINER=generated-file

# Frontend URL
FRONTEND_URL=http://localhost:3000
```

### Deployment Process

The automated deployment follows these steps:

1. **Code Checkout**: Latest code from `release/v1.0.0` branch is checked out
2. **Java Setup**: Java 17 (Temurin) is installed with Maven cache
3. **Azure Login**: Authenticates using `AZURE_CREDENTIALS` secret
4. **Build Application**: 
   - Makes `mvnw` executable
   - Runs `./mvnw -B package -DskipTests`
   - Generates JAR file in `target/` directory
5. **Deploy to Azure**: Uploads JAR file to Azure Web App
6. **Cleanup**: Logs out from Azure CLI

### Manual Deployment Trigger

You can also trigger the deployment manually:

1. Go to your GitHub repository
2. Navigate to **Actions** tab
3. Select **Deploy Backend to Azure App Service** workflow
4. Click **Run workflow** button
5. Select `release/v1.0.0` branch and run

### Monitoring Deployment

Monitor your deployment through:

- **GitHub Actions**: Check workflow status and logs
- **Azure Portal**: Monitor App Service metrics and logs
- **Application Logs**: View real-time logs in Azure Portal

### Azure Web App Configuration

Ensure your Azure Web App is configured with:

- **Runtime Stack**: Java 17
- **Web Server**: Embedded (Spring Boot)
- **Platform**: Linux
- **SKU**: Basic (B3) or higher for production

### Environment Variables in Azure

Configure the following application settings in Azure Web App:

```bash
DB_URL=jdbc:postgresql://mindmingle5703-new.postgres.database.azure.com/postgres
DB_USERNAME=mindmingle
DB_PASSWORD=your_secure_password
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=1296000
GEMINI_API_KEY=your_gemini_api_key
AZURE_STORAGE_ENDPOINT=your_azure_storage_endpoint
AZURE_STORAGE_CONTAINER=generated-file
FRONTEND_URL=https://your-frontend-domain.com
```

### Troubleshooting Deployment

Common deployment issues and solutions:

1. **Build Failures**:
   - Check Java version compatibility
   - Verify Maven dependencies in `pom.xml`
   - Review build logs in GitHub Actions

2. **Authentication Errors**:
   - Verify `AZURE_CREDENTIALS` secret format
   - Check Azure service principal permissions
   - Ensure subscription and tenant IDs are correct

3. **Application Startup Issues**:
   - Check environment variables in Azure Web App
   - Review application logs in Azure Portal
   - Verify database connectivity

4. **Deployment Package Issues**:
   - Ensure JAR file is generated correctly
   - Check Maven build configuration
   - Verify target directory structure

### Health Check

After deployment, verify the application is running:

- **Health Endpoint**: `https://your-app-name.azurewebsites.net/actuator/health`
- **Application URL**: `https://mindmingle-backend.azurewebsites.net`
- **API Base URL**: `https://mindmingle-backend.azurewebsites.net/api`

The application startup includes diagnostic information:
```
🚀 Spring Boot 运行地址: https://mindmingle-backend.azurewebsites.net
🔗 连接的数据库: jdbc:postgresql://mindmingle5703-new.postgres.database.azure.com/postgres
```

