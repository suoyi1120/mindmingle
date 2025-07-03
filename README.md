# MindMingle Backend - Azure Web App Deployment Guide

This guide provides detailed instructions for deploying the MindMingle Spring Boot backend application to Azure Web App using GitHub Actions.

## Deployment Overview

The deployment process automatically triggers when code is pushed to the `release/v1.0.0` branch and uses GitHub Actions to build the Spring Boot JAR file and deploy it to Azure Web App.

## Prerequisites

Before starting the deployment setup, ensure you have:

- **GitHub Repository**: With admin access to configure secrets
- **Azure Subscription**: Active Azure subscription with appropriate permissions
- **Azure CLI**: Installed locally for initial setup
- **Java 17**: For local testing (optional)
- **Maven**: For local builds (optional)

## Step 1: Create Azure Web App Service

### 1.1 Create Resource Group (if needed)
```bash
az group create --name mindmingle-rg --location "Australia Southeast"
```

### 1.2 Create App Service Plan
```bash
az appservice plan create \
  --name mindmingle-backend-plan \
  --resource-group mindmingle-rg \
  --location "Australia Southeast" \
  --sku B3 \
  --is-linux true
```

### 1.3 Create Azure Web App
```bash
az webapp create \
  --resource-group mindmingle-rg \
  --plan mindmingle-backend-plan \
  --name mindmingle-backend \
  --runtime "JAVA:17-java17" \
  --startup-file ""
```

### 1.4 Configure Web App Settings
```bash
# Enable Always On (recommended for production)
az webapp config set \
  --resource-group mindmingle-rg \
  --name mindmingle-backend \
  --always-on true

# Configure Java settings
az webapp config set \
  --resource-group mindmingle-rg \
  --name mindmingle-backend \
  --java-version "17" \
  --java-container "Java SE" \
  --java-container-version "17"
```

## Step 2: Create Azure PostgreSQL Database

### 2.1 Create PostgreSQL Server
```bash
az postgres flexible-server create \
  --resource-group mindmingle-rg \
  --name mindmingle5703-new \
  --location "Australia Southeast" \
  --admin-user mindmingle \
  --admin-password "mm123456!" \
  --sku-name Standard_B1ms \
  --tier Burstable \
  --version 13
```

### 2.2 Configure Firewall Rules
```bash
# Allow Azure services
az postgres flexible-server firewall-rule create \
  --resource-group mindmingle-rg \
  --name mindmingle5703-new \
  --rule-name AllowAzureServices \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 0.0.0.0

# Allow your IP (for development)
az postgres flexible-server firewall-rule create \
  --resource-group mindmingle-rg \
  --name mindmingle5703-new \
  --rule-name AllowMyIP \
  --start-ip-address YOUR_IP \
  --end-ip-address YOUR_IP
```

### 2.3 Create Database
```bash
az postgres flexible-server db create \
  --resource-group mindmingle-rg \
  --server-name mindmingle5703-new \
  --database-name postgres
```

## Step 3: Create Azure Blob Storage

### 3.1 Create Storage Account
```bash
az storage account create \
  --name mindminglefile \
  --resource-group mindmingle-rg \
  --location "Australia Southeast" \
  --sku Standard_LRS \
  --kind StorageV2
```

### 3.2 Create Blob Container
```bash
az storage container create \
  --name generated-file \
  --account-name mindminglefile \
  --auth-mode login \
  --public-access blob
```

### 3.3 Get Storage Connection String
```bash
az storage account show-connection-string \
  --name mindminglefile \
  --resource-group mindmingle-rg
```

## Step 4: Create Azure Service Principal

### 4.1 Create Service Principal
```bash
az ad sp create-for-rbac \
  --name "mindmingle-backend-sp" \
  --role contributor \
  --scopes /subscriptions/{subscription-id}/resourceGroups/mindmingle-rg \
  --sdk-auth
```

### 4.2 Save the Output
Save the JSON output from the previous command. It should look like:
```json
{
  "clientId": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "clientSecret": "your-client-secret",
  "subscriptionId": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "tenantId": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "activeDirectoryEndpointUrl": "https://login.microsoftonline.com",
  "resourceManagerEndpointUrl": "https://management.azure.com/",
  "activeDirectoryGraphResourceId": "https://graph.windows.net/",
  "sqlManagementEndpointUrl": "https://management.core.windows.net:8443/",
  "galleryEndpointUrl": "https://gallery.azure.com/",
  "managementEndpointUrl": "https://management.core.windows.net/"
}
```

## Step 5: Configure GitHub Actions Secrets

### 5.1 Access GitHub Repository Settings
1. Go to your GitHub repository
2. Click on **Settings** tab
3. Navigate to **Secrets and variables** → **Actions**

### 5.2 Add Repository Secrets

#### AZURE_CREDENTIALS
- **Name**: `AZURE_CREDENTIALS`
- **Value**: The entire JSON output from Step 4.2
```json
{
  "clientId": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "clientSecret": "your-client-secret",
  "subscriptionId": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "tenantId": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
  "activeDirectoryEndpointUrl": "https://login.microsoftonline.com",
  "resourceManagerEndpointUrl": "https://management.azure.com/",
  "activeDirectoryGraphResourceId": "https://graph.windows.net/",
  "sqlManagementEndpointUrl": "https://management.core.windows.net:8443/",
  "galleryEndpointUrl": "https://gallery.azure.com/",
  "managementEndpointUrl": "https://management.core.windows.net/"
}
```

#### Database Configuration Secrets
- **Name**: `DB_USERNAME`
- **Value**: `mindmingle`

- **Name**: `DB_PASSWORD`
- **Value**: `mm123456!`

- **Name**: `DB_URL`
- **Value**: `jdbc:postgresql://mindmingle5703-new.postgres.database.azure.com/postgres`

#### JWT Configuration Secrets
- **Name**: `JWT_SECRET`
- **Value**: `your-secure-jwt-secret-key-here-make-it-long-and-random`

- **Name**: `JWT_EXPIRATION`
- **Value**: `1296000`

#### Google Gemini API Secret
- **Name**: `GEMINI_API_KEY`
- **Value**: `your-google-gemini-api-key`

#### Azure Storage Secrets
- **Name**: `AZURE_STORAGE_ENDPOINT`
- **Value**: `https://mindminglefile.blob.core.windows.net`

- **Name**: `AZURE_STORAGE_CONTAINER`
- **Value**: `generated-file`

#### Frontend URL Secret
- **Name**: `FRONTEND_URL`
- **Value**: `https://mindmingle-frontend.azurewebsites.net`

### 5.3 Verify Secrets Configuration
After adding all secrets, you should see:
- ✅ AZURE_CREDENTIALS (Updated X minutes ago)
- ✅ DB_USERNAME (Updated X minutes ago)
- ✅ DB_PASSWORD (Updated X minutes ago)
- ✅ DB_URL (Updated X minutes ago)
- ✅ JWT_SECRET (Updated X minutes ago)
- ✅ JWT_EXPIRATION (Updated X minutes ago)
- ✅ GEMINI_API_KEY (Updated X minutes ago)
- ✅ AZURE_STORAGE_ENDPOINT (Updated X minutes ago)
- ✅ AZURE_STORAGE_CONTAINER (Updated X minutes ago)
- ✅ FRONTEND_URL (Updated X minutes ago)

## Step 6: Deployment Workflow Configuration

### 6.1 Workflow File Location
The deployment workflow is located at:
```
.github/workflows/deploy-backend.yml
```

### 6.2 Key Workflow Settings
```yaml
# Trigger configuration
on:
  push:
    branches:
      - release/v1.0.0  # Deploys when pushing to this branch
    paths:
      - 'src/**'        # Only trigger on source code changes
      - 'pom.xml'       # Or when dependencies change
  workflow_dispatch:     # Allows manual deployment

# Environment variables
env:
  AZURE_WEBAPP_NAME: 'mindmingle-backend'  # Your Azure Web App name
  JAVA_VERSION: '17'                       # Java version
```

### 6.3 Deployment Steps Overview
1. **Checkout Code**: Retrieves latest code from `release/v1.0.0` branch
2. **Setup Java**: Installs Java 17 (Temurin) with Maven cache
3. **Azure Login**: Authenticates using service principal credentials
4. **Make mvnw Executable**: Ensures Maven wrapper has proper permissions
5. **Build Application**: Runs `./mvnw -B package -DskipTests`
6. **Deploy to Azure**: Uploads JAR file to Azure Web App
7. **Cleanup**: Logs out from Azure CLI

## Step 7: Azure Web App Configuration

### 7.1 Configure Application Settings in Azure Portal

1. **Navigate to Azure Portal** → **App Services** → **mindmingle-backend**
2. **Go to Configuration** → **Application settings**
3. **Add the following settings**:

```bash
# Database Configuration
DB_URL = jdbc:postgresql://mindmingle5703-new.postgres.database.azure.com/postgres
DB_USERNAME = mindmingle
DB_PASSWORD = mm123456!

# JWT Configuration
JWT_SECRET = your-secure-jwt-secret-key-here-make-it-long-and-random
JWT_EXPIRATION = 1296000

# Google Gemini API
GEMINI_API_KEY = your-google-gemini-api-key

# Azure Storage Configuration
AZURE_STORAGE_ENDPOINT = https://mindminglefile.blob.core.windows.net
AZURE_STORAGE_CONTAINER = generated-file

# Frontend URL Configuration
FRONTEND_URL = https://mindmingle-frontend.azurewebsites.net

# Java Configuration
JAVA_OPTS = -Dserver.port=80
```

### 7.2 Configure General Settings

1. **Platform**: Linux
2. **Runtime stack**: Java 17
3. **Java web server**: Java SE (Embedded Web Server)
4. **Always On**: On (recommended for production)
5. **ARR affinity**: Off (recommended for stateless apps)

### 7.3 Configure Startup Command

Set the startup command to:
```bash
java -jar /home/site/wwwroot/mindmingle-0.0.1-SNAPSHOT.jar
```

## Step 8: Deployment Process

### 8.1 Automatic Deployment
1. **Push code to release branch**:
   ```bash
   git checkout release/v1.0.0
   git add .
   git commit -m "Deploy to production"
   git push origin release/v1.0.0
   ```

2. **Monitor deployment**:
   - Go to **GitHub** → **Actions** tab
   - Watch the "Deploy Backend to Azure App Service" workflow

### 8.2 Manual Deployment
1. **Navigate to GitHub Actions**:
   - Go to repository → **Actions** tab
   - Select **Deploy Backend to Azure App Service**

2. **Trigger manual deployment**:
   - Click **Run workflow**
   - Select `release/v1.0.0` branch
   - Click **Run workflow** button

### 8.3 Deployment Status Monitoring

#### GitHub Actions Monitoring
- **Workflow Status**: Success/Failure indication
- **Step-by-step Logs**: Detailed logs for each deployment step
- **Build Artifacts**: JAR file generation logs
- **Duration**: Total deployment time

#### Azure Portal Monitoring
- **Deployment Center**: View deployment history and logs
- **Log Stream**: Real-time application logs
- **Metrics**: Performance and resource usage metrics

## Step 9: Post-Deployment Verification

### 9.1 Application URLs
- **Production URL**: `https://mindmingle-backend.azurewebsites.net`
- **API Base URL**: `https://mindmingle-backend.azurewebsites.net/api`
- **Health Check**: `https://mindmingle-backend.azurewebsites.net/actuator/health`

### 9.2 Health Checks
1. **Application Startup**: Check application logs for successful startup
2. **Database Connectivity**: Verify database connection in logs
3. **API Endpoints**: Test key API endpoints
4. **File Upload**: Test Azure Blob Storage integration

### 9.3 Verify Application Startup
Look for these log messages in Azure Portal:
```
🚀 Spring Boot 运行地址: https://mindmingle-backend.azurewebsites.net
🔗 连接的数据库: jdbc:postgresql://mindmingle5703-new.postgres.database.azure.com/postgres
```

## Step 10: Troubleshooting Common Issues

### 10.1 Build Failures

**Problem**: Maven build fails during deployment
**Solutions**:
1. Check Java version compatibility (should be Java 17)
2. Verify Maven dependencies in `pom.xml`
3. Review GitHub Actions build logs
4. Ensure `mvnw` wrapper has proper permissions

**Debug Commands**:
```bash
# Check Java version in GitHub Actions logs
java -version
mvn -version

# Verify build locally
./mvnw clean package -DskipTests

# Check for dependency conflicts
./mvnw dependency:tree
```

### 10.2 Authentication Errors

**Problem**: Azure authentication fails
**Solutions**:
1. Verify AZURE_CREDENTIALS secret format
2. Check service principal permissions
3. Ensure subscription and tenant IDs are correct
4. Regenerate service principal if needed

**Debug Steps**:
```bash
# Test service principal locally
az login --service-principal \
  --username $CLIENT_ID \
  --password $CLIENT_SECRET \
  --tenant $TENANT_ID

# Verify permissions
az role assignment list --assignee $CLIENT_ID

# Check resource group access
az group show --name mindmingle-rg
```

### 10.3 Database Connection Issues

**Problem**: Application cannot connect to PostgreSQL database
**Solutions**:
1. Verify database connection string format
2. Check firewall rules on PostgreSQL server
3. Ensure database credentials are correct
4. Test database connectivity

**Debug Steps**:
```bash
# Test database connection
az postgres flexible-server connect \
  --name mindmingle5703-new \
  --admin-user mindmingle \
  --admin-password "mm123456!" \
  --database-name postgres

# Check firewall rules
az postgres flexible-server firewall-rule list \
  --resource-group mindmingle-rg \
  --name mindmingle5703-new

# Test from Azure Web App
# Add temporary firewall rule for Azure services
az postgres flexible-server firewall-rule create \
  --resource-group mindmingle-rg \
  --name mindmingle5703-new \
  --rule-name AllowAzureWebApp \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 0.0.0.0
```

### 10.4 Application Startup Issues

**Problem**: Spring Boot application fails to start
**Solutions**:
1. Check environment variables in Azure Web App
2. Review application logs in Azure Portal
3. Verify JAR file was deployed correctly
4. Check for port conflicts

**Debug Commands**:
```bash
# Check Azure Web App logs
az webapp log tail --resource-group mindmingle-rg --name mindmingle-backend

# Download log files
az webapp log download --resource-group mindmingle-rg --name mindmingle-backend

# Check application settings
az webapp config appsettings list \
  --resource-group mindmingle-rg \
  --name mindmingle-backend

# Restart the web app
az webapp restart --resource-group mindmingle-rg --name mindmingle-backend
```

### 10.5 Azure Blob Storage Issues

**Problem**: File upload to Azure Blob Storage fails
**Solutions**:
1. Verify storage account connection string
2. Check container permissions
3. Ensure storage account exists and is accessible
4. Test storage connectivity

**Debug Steps**:
```bash
# Test storage account connectivity
az storage account show \
  --name mindminglefile \
  --resource-group mindmingle-rg

# List containers
az storage container list \
  --account-name mindminglefile \
  --auth-mode login

# Test blob upload
az storage blob upload \
  --account-name mindminglefile \
  --container-name generated-file \
  --name test.txt \
  --file test.txt \
  --auth-mode login
```

### 10.6 Performance Issues

**Problem**: Slow application performance
**Solutions**:
1. Scale up Azure App Service plan
2. Monitor database performance
3. Enable Application Insights
4. Optimize database queries

**Optimization Commands**:
```bash
# Scale up App Service plan
az appservice plan update \
  --name mindmingle-backend-plan \
  --resource-group mindmingle-rg \
  --sku P1V2

# Enable Application Insights
az monitor app-insights component create \
  --app mindmingle-backend-insights \
  --location "Australia Southeast" \
  --resource-group mindmingle-rg \
  --application-type web

# Monitor database performance
az postgres flexible-server parameter list \
  --resource-group mindmingle-rg \
  --server-name mindmingle5703-new
```

## Step 11: Advanced Configuration

### 11.1 Custom Domain Setup (Optional)

1. **Add custom domain**:
   ```bash
   az webapp config hostname add \
     --hostname api.yourdomain.com \
     --resource-group mindmingle-rg \
     --webapp-name mindmingle-backend
   ```

2. **Configure SSL certificate**:
   - Use Azure managed certificate or
   - Upload custom SSL certificate

### 11.2 Scaling Configuration

1. **Auto-scaling setup**:
   ```bash
   az monitor autoscale create \
     --resource-group mindmingle-rg \
     --resource mindmingle-backend \
     --resource-type Microsoft.Web/sites \
     --name mindmingle-backend-autoscale \
     --min-count 1 \
     --max-count 3 \
     --count 1
   ```

2. **Add scaling rules**:
   ```bash
   # Scale out when CPU > 70%
   az monitor autoscale rule create \
     --resource-group mindmingle-rg \
     --autoscale-name mindmingle-backend-autoscale \
     --condition "Percentage CPU > 70 avg 5m" \
     --scale out 1
   ```

### 11.3 Backup Configuration

1. **Configure automated backups**:
   ```bash
   az webapp config backup create \
     --resource-group mindmingle-rg \
     --webapp-name mindmingle-backend \
     --backup-name daily-backup \
     --storage-account-url $STORAGE_URL
   ```

### 11.4 Monitoring and Alerts

1. **Set up alerts**:
   ```bash
   # CPU usage alert
   az monitor metrics alert create \
     --name "High CPU Usage" \
     --resource-group mindmingle-rg \
     --scopes /subscriptions/{sub-id}/resourceGroups/mindmingle-rg/providers/Microsoft.Web/sites/mindmingle-backend \
     --condition "avg Percentage CPU > 80" \
     --description "Alert when CPU usage is high"
   ```

## Deployment Checklist

- [ ] Azure Resource Group created
- [ ] Azure Web App Service created and configured
- [ ] PostgreSQL database created and configured
- [ ] Azure Blob Storage created and configured
- [ ] Service Principal created with appropriate permissions
- [ ] GitHub repository secrets configured (all 10 secrets)
- [ ] Workflow file committed to repository
- [ ] Release branch `release/v1.0.0` created
- [ ] Code pushed to release branch
- [ ] Deployment workflow executed successfully
- [ ] Application accessible via Azure Web App URL
- [ ] Database connectivity verified
- [ ] File upload functionality tested
- [ ] API endpoints responding correctly
- [ ] Performance and logs monitored

## Support and Maintenance

### Regular Maintenance Tasks
1. **Monitor deployment logs** weekly
2. **Update dependencies** monthly (check for security patches)
3. **Review database performance** weekly
4. **Monitor storage usage** monthly
5. **Update secrets** as needed (JWT secret rotation)
6. **Backup configuration** before major changes

### Emergency Procedures
1. **Rollback deployment** if critical issues occur
2. **Scale up resources** during high traffic
3. **Contact Azure support** for infrastructure issues
4. **Check GitHub Actions** for build failures
5. **Review application logs** for runtime errors
6. **Database failover** procedures if needed

### Security Best Practices
1. **Rotate secrets** regularly (JWT, database passwords)
2. **Monitor access logs** for suspicious activity
3. **Keep dependencies updated** for security patches
4. **Use managed identities** where possible
5. **Enable Azure Security Center** recommendations
6. **Regular security audits** of the application

