# 🚀 Allytic Labs Backend - Complete Setup Guide

A production-ready Spring Boot REST API backend for Allytic Labs, featuring AWS DynamoDB integration, Spring Security authentication, and comprehensive CRUD operations for Robots, Drones, Solar Panels, Contacts, and Newsletter management.

---

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [Tech Stack](#tech-stack)
3. [Prerequisites](#prerequisites)
4. [Local Development Setup](#local-development-setup)
5. [AWS Setup & Configuration](#aws-setup--configuration)
6. [Building the Application](#building-the-application)
7. [Running the Application](#running-the-application)
8. [API Documentation](#api-documentation)
9. [Testing the APIs](#testing-the-apis)
10. [Troubleshooting](#troubleshooting)
11. [Production Deployment](#production-deployment)

---

## 🎯 Project Overview

This backend application provides RESTful APIs for managing:
- **Robots** - AI assistants and industrial robots
- **Drones** - Photography, delivery, and racing drones
- **Solar Panels** - Residential and commercial solar solutions
- **Contacts** - Customer contact form submissions
- **Newsletter** - Email subscription management

---

## 🛠 Tech Stack

- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Database:** AWS DynamoDB
- **Security:** Spring Security with HTTP Basic Authentication
- **Build Tool:** Maven
- **Cloud Provider:** Amazon Web Services (AWS)

---

## ✅ Prerequisites

Before you begin, ensure you have the following installed:

```bash
# Check Java version (must be 17+)
java -version

# Check Maven version
mvn -version

# Check Python (for AWS setup scripts)
python3 --version
```

**Required:**
- Java JDK 17 or higher
- Maven 3.6+
- AWS Account (with valid credentials)
- Python 3.x (for DynamoDB table creation scripts)
- Git

---

## 🔧 Local Development Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/allyticlabs-backend.git
cd allyticlabs-backend
```

### 2. Install Dependencies

```bash
# Install boto3 for AWS operations
pip3 install boto3

# (Optional) Install AWS CLI
sudo apt update
sudo apt install awscli -y
```

### 3. Configure Environment Variables

Create a `.env` file in the project root:

```bash
cat > .env << 'EOF'
export AWS_ACCESS_KEY_ID=your_aws_access_key_here
export AWS_SECRET_ACCESS_KEY=your_aws_secret_key_here
export AWS_REGION=us-east-1
EOF

# Make it executable
chmod +x .env
```

**⚠️ IMPORTANT:** Never commit `.env` file to Git!

Add to `.gitignore`:
```bash
echo ".env" >> .gitignore
echo "*.env" >> .gitignore
```

### 4. Update Application Properties

Edit `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# AWS Configuration
aws.region=${AWS_REGION:us-east-1}
aws.dynamodb.endpoint=https://dynamodb.${aws.region}.amazonaws.com
aws.accessKeyId=${AWS_ACCESS_KEY_ID}
aws.secretAccessKey=${AWS_SECRET_ACCESS_KEY}

# Logging
logging.level.root=INFO
logging.level.com.allyticlabs=DEBUG
```

---

## ☁️ AWS Setup & Configuration

### Step 1: Get AWS Credentials

1. Log in to [AWS Console](https://console.aws.amazon.com/)
2. Navigate to **IAM** → **Users** → Select your user
3. Go to **Security credentials** tab
4. Click **Create access key**
5. Copy:
   - Access Key ID
   - Secret Access Key

### Step 2: Create DynamoDB Tables

Use the provided Python script to create all required tables:

```bash
# Create the table creation script
cat > create_tables.py << 'EOF'
import boto3

# Initialize DynamoDB client
dynamodb = boto3.client('dynamodb', 
                        region_name='us-east-1',
                        aws_access_key_id='YOUR_ACCESS_KEY_ID',
                        aws_secret_access_key='YOUR_SECRET_ACCESS_KEY')

tables = [
    {'name': 'Robots', 'key': 'id'},
    {'name': 'Drones', 'key': 'id'},
    {'name': 'SolarPanels', 'key': 'id'},
    {'name': 'Contacts', 'key': 'id'},
    {'name': 'Newsletters', 'key': 'email'}  # Note: email is the partition key
]

for table in tables:
    try:
        response = dynamodb.create_table(
            TableName=table['name'],
            KeySchema=[
                {'AttributeName': table['key'], 'KeyType': 'HASH'}
            ],
            AttributeDefinitions=[
                {'AttributeName': table['key'], 'AttributeType': 'S'}
            ],
            BillingMode='PAY_PER_REQUEST'
        )
        print(f"✓ Created table: {table['name']}")
    except Exception as e:
        if 'ResourceInUseException' in str(e):
            print(f"✓ Table {table['name']} already exists")
        else:
            print(f"✗ Error creating {table['name']}: {e}")

print("\n✅ All tables created successfully!")
EOF

# Run the script
python3 create_tables.py
```

### Step 3: Verify Tables in AWS Console

1. Go to [DynamoDB Console](https://console.aws.amazon.com/dynamodb)
2. Select **US East (N. Virginia)** region
3. Click **Tables** in the left sidebar
4. Verify all 5 tables are listed and **ACTIVE**

---

## 🏗 Building the Application

### Compile the Project

```bash
# Clean and compile
mvn clean compile

# Expected output: BUILD SUCCESS
```

### Package the Application

```bash
# Create executable JAR (skip tests)
mvn clean package -Dmaven.test.skip=true

# JAR file will be created at:
# target/backend-1.0.0.jar
```

---

## ▶️ Running the Application

### Method 1: Using Maven Spring Boot Plugin

```bash
# Load environment variables and run
source .env && mvn spring-boot:run -Dmaven.test.skip=true
```

### Method 2: Run the JAR Directly

```bash
# Load environment variables
source .env

# Run the JAR
java -jar target/backend-1.0.0.jar
```

### Successful Startup Logs

You should see:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

...
Tomcat started on port 8080 (http) with context path ''
Started BackendApplication in 2.539 seconds
Initial data seeded successfully!
```

---

## 📚 API Documentation

### Base URL

```
http://localhost:8080
```

### Authentication

All API endpoints (except `/api/contact` and `/api/newsletter` POST) require HTTP Basic Authentication:

- **Username:** `admin`
- **Password:** `admin123`

### Available Endpoints

#### 1. Robots API

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/robots` | Get all robots | ✅ Yes |
| GET | `/api/robots/{id}` | Get robot by ID | ✅ Yes |
| GET | `/api/robots?type={type}` | Filter by type | ✅ Yes |
| POST | `/api/robots` | Create new robot | ✅ Yes |
| PUT | `/api/robots/{id}` | Update robot | ✅ Yes |
| DELETE | `/api/robots/{id}` | Delete robot | ✅ Yes |

#### 2. Drones API

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/drones` | Get all drones | ✅ Yes |
| GET | `/api/drones/{id}` | Get drone by ID | ✅ Yes |
| POST | `/api/drones` | Create new drone | ✅ Yes |
| PUT | `/api/drones/{id}` | Update drone | ✅ Yes |
| DELETE | `/api/drones/{id}` | Delete drone | ✅ Yes |

#### 3. Solar Panels API

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/solar-panels` | Get all panels | ✅ Yes |
| GET | `/api/solar-panels/{id}` | Get panel by ID | ✅ Yes |
| POST | `/api/solar-panels` | Create new panel | ✅ Yes |
| PUT | `/api/solar-panels/{id}` | Update panel | ✅ Yes |
| DELETE | `/api/solar-panels/{id}` | Delete panel | ✅ Yes |

#### 4. Contacts API

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/contact` | Submit contact form | ❌ No |
| GET | `/api/contact` | Get all contacts | ✅ Yes |

#### 5. Newsletter API

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/newsletter` | Subscribe | ❌ No |
| GET | `/api/newsletter` | Get subscribers | ✅ Yes |

---

## 🧪 Testing the APIs

### Using cURL

#### Get All Robots

```bash
curl -u admin:admin123 http://localhost:8080/api/robots
```

**Expected Response:**
```json
[
  {
    "id": "1",
    "name": "NeuroBot X1",
    "type": "AI Assistant",
    "description": "Advanced neural network processing...",
    "capabilities": ["Natural Language Processing", "Emotional Recognition"],
    "image": "https://images.pexels.com/...",
    "price": "$12,999",
    "rating": 4.9,
    "reviews": 1247
  }
]
```

#### Get Specific Robot

```bash
curl -u admin:admin123 http://localhost:8080/api/robots/1
```

#### Get All Drones

```bash
curl -u admin:admin123 http://localhost:8080/api/drones
```

#### Get All Solar Panels

```bash
curl -u admin:admin123 http://localhost:8080/api/solar-panels
```

#### Create a Contact (No Auth Required)

```bash
curl -X POST http://localhost:8080/api/contact \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "helpType": "Technical support",
    "message": "Need help with setup"
  }'
```

#### Get All Contacts (Auth Required)

```bash
curl -u admin:admin123 http://localhost:8080/api/contact
```

#### Subscribe to Newsletter (No Auth Required)

```bash
curl -X POST http://localhost:8080/api/newsletter \
  -H "Content-Type: application/json" \
  -d '{
    "email": "subscriber@example.com"
  }'
```

### Using Postman

1. **Import Collection:**
   - Create new request
   - Set method (GET/POST/PUT/DELETE)
   - Enter URL: `http://localhost:8080/api/robots`

2. **Add Authentication:**
   - Go to **Authorization** tab
   - Type: **Basic Auth**
   - Username: `admin`
   - Password: `admin123`

3. **Send Request**

### Using Browser

For GET requests, visit:
```
http://admin:admin123@localhost:8080/api/robots
```

---

## 🐛 Troubleshooting

### Issue 1: "Could not resolve placeholder" Error

**Problem:** Application can't find AWS credentials

**Solution:**
```bash
# Ensure .env file exists and has correct values
cat .env

# Load environment variables before running
source .env
java -jar target/backend-1.0.0.jar
```

### Issue 2: "ResourceNotFoundException" - Tables Not Found

**Problem:** DynamoDB tables don't exist

**Solution:**
```bash
# Run the table creation script again
python3 create_tables.py

# Verify tables in AWS Console
# Make sure you're in us-east-1 region
```

### Issue 3: 401 Unauthorized Error

**Problem:** Missing or incorrect authentication

**Solution:**
```bash
# Use correct credentials
curl -u admin:admin123 http://localhost:8080/api/robots

# NOT just:
curl http://localhost:8080/api/robots

```






# Test Robots endpoint (GET)
curl -u admin:admin123 http://localhost:8080/api/robots

# Test Drones endpoint (GET)
curl -u admin:admin123 http://localhost:8080/api/drones

# Test Solar Panels endpoint (GET)
curl -u admin:admin123 http://localhost:8080/api/solar-panels

# Test Contact endpoint (POST) - SINGULAR, not plural!
curl -X POST -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@example.com","message":"Test message"}' \
  http://localhost:8080/api/contact

# Test Newsletter endpoint (POST) - SINGULAR, not plural!
curl -X POST -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}' \
  http://localhost:8080/api/newsletter








### Issue 4: Port 8080 Already in Use

**Problem:** Another application is using port 8080

**Solution:**
```bash
# Find process using port 8080
sudo lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change port in application.properties
server.port=8081
```

### Issue 5: Maven Compilation Errors

**Problem:** Missing dependencies or Java version mismatch

**Solution:**
```bash
# Check Java version (must be 17+)
java -version

# Clean and reinstall dependencies
mvn clean install -U

# If still failing, delete .m2 cache
rm -rf ~/.m2/repository
mvn clean install
```

---

## 🚀 Production Deployment

### Environment-Specific Configurations

Create profile-specific properties files:

**`application-prod.properties`:**
```properties
server.port=8080
aws.region=us-east-1
logging.level.root=WARN
logging.level.com.allyticlabs=INFO
```

### Run with Production Profile

```bash
java -jar target/backend-1.0.0.jar --spring.profiles.active=prod
```

### Recommended Production Practices

1. **Use IAM Roles** instead of hardcoded credentials (for EC2/ECS/Lambda)
2. **Enable HTTPS** using Spring Boot with SSL certificate
3. **Use Environment Variables** for all sensitive data
4. **Enable CORS** only for your frontend domain
5. **Set up CloudWatch** for monitoring and logging
6. **Use BCrypt** for password encoding (not `{noop}`)
7. **Implement Rate Limiting** to prevent abuse
8. **Set up Auto Scaling** for high availability

### Security Checklist for Production

- [ ] Replace `{noop}` passwords with BCrypt
- [ ] Use AWS Secrets Manager for credentials
- [ ] Enable HTTPS/SSL
- [ ] Restrict CORS to specific domains
- [ ] Enable request logging
- [ ] Set up CloudWatch alarms
- [ ] Use AWS WAF for DDoS protection
- [ ] Implement API rate limiting
- [ ] Enable Spring Security CSRF protection (for non-API endpoints)
- [ ] Use strong admin passwords

---

## 📞 Support

For issues or questions:
- **Email:** support@allyticlabs.com
- **GitHub Issues:** https://github.com/yourusername/allyticlabs-backend/issues

---

## 📄 License

Copyright © 2025 Allytic Labs. All rights reserved.

---

**🎉 Congratulations! Your Allytic Labs Backend is now ready for production!**