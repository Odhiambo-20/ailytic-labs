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







# Allytic Labs Backend - AWS Elastic Beanstalk

Spring Boot backend application deployed on AWS Elastic Beanstalk with DynamoDB, M-Pesa, Stripe, and Google OAuth integration.

---

## 🚀 Live Deployment

**Backend URL**: `http://allytic-labs-prod.eba-pukad2pd.us-east-1.elasticbeanstalk.com`

**Status**: Production Ready ✅  
**Platform**: AWS Elastic Beanstalk (Java 17/Corretto)  
**Region**: us-east-1 (N. Virginia)  
**Database**: DynamoDB  
**Instance**: t3.small  

---

## 📋 Prerequisites

### Required Tools

1. **Java 17** (OpenJDK)
   ```bash
   java -version
   # Should show: openjdk version "17.x.x"
   ```

2. **Maven**
   ```bash
   mvn -version
   ```

3. **AWS CLI**
   ```bash
   aws --version
   ```

4. **Elastic Beanstalk CLI**
   ```bash
   eb --version
   ```

### AWS Credentials

You need valid AWS access credentials with permissions for:
- Elastic Beanstalk
- DynamoDB
- IAM
- S3

---

## 🛠️ Setup Instructions

### 1. Clone Repository

```bash
git clone https://github.com/Odhiambo-20/ailytic-labs.git
cd ailytic-labs/com.allyticlabs.backend
```

### 2. Configure AWS Credentials

```bash
aws configure
```

Enter your credentials:
```
AWS Access Key ID: YOUR_ACCESS_KEY
AWS Secret Access Key: YOUR_SECRET_KEY
Default region name: us-east-1
Default output format: json
```

### 3. Verify Configuration

```bash
# Test AWS connection
aws sts get-caller-identity

# Should display your account info
```

---

## 🏃 Running the Backend

### Option 1: Access Already Deployed Backend (Recommended)

The backend is **already running** on AWS. Simply check its status:

```bash
# Navigate to project directory
cd ~/Documents/Desktop/Allytic-Labs/com.allyticlabs.backend

# Check environment status
eb status
```

**Expected Output**:
```
Environment details for: allytic-labs-prod
  Application name: allytic-labs-backend
  Region: us-east-1
  Deployed Version: app-XXXXXXXXX
  Platform: Corretto 17 running on 64bit Amazon Linux 2023
  Status: Ready
  Health: Green
  CNAME: allytic-labs-prod.eba-pukad2pd.us-east-1.elasticbeanstalk.com
```

✅ **If Status = Ready and Health = Green**: Backend is running!

### Option 2: Run Locally (Development)

```bash
# Build the project
mvn clean install

# Run locally
mvn spring-boot:run
```

Application will start on: `http://localhost:8080`

---

## 🔄 Managing the Deployed Backend

### Check Status

```bash
eb status
```

### View Logs

```bash
# View recent logs
eb logs

# Stream logs in real-time
eb logs --stream
```

### Restart Application

```bash
eb restart
```

Wait 2-3 minutes, then verify:
```bash
eb status
```

### SSH into Server

```bash
eb ssh
```

Inside the server:
```bash
# Check if Java is running
sudo netstat -tlnp | grep java

# View application logs
sudo tail -f /var/log/web.stdout.log

# Exit SSH
exit
```

### Check Environment Variables

```bash
eb printenv
```

### Update Environment Variables

```bash
eb setenv KEY=value
```

Example:
```bash
eb setenv JWT_SECRET=new_secret_key
```

---

## 🔧 Making Changes & Redeploying

### 1. Make Your Code Changes

Edit your Java files as needed.

### 2. Build New JAR

```bash
# Clean and build
mvn clean package -DskipTests
```

### 3. Deploy to AWS

```bash
# Deploy (takes 2-5 minutes)
eb deploy

# Monitor deployment
eb status
```

### 4. Verify Deployment

```bash
# Check health
eb health

# Test API endpoint
curl http://allytic-labs-prod.eba-pukad2pd.us-east-1.elasticbeanstalk.com/api/v1/auth/login
```

---

## 📡 API Endpoints Testing

### Base URL
```
http://allytic-labs-prod.eba-pukad2pd.us-east-1.elasticbeanstalk.com/api/v1
```

### 1. Register a New User

```bash
curl -X POST http://allytic-labs-prod.eba-pukad2pd.us-east-1.elasticbeanstalk.com/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@test.com",
    "password": "Test123!",
    "confirmPassword": "Test123!",
    "firstName": "Test",
    "lastName": "User",
    "name": "Test User"
  }'
```

**Expected Response** (Success):
```json
{
  "status": "success",
  "message": "User registered successfully",
  "data": {
    "userId": "uuid-here",
    "username": "testuser",
    "email": "test@test.com"
  }
}
```

**Expected Response** (Validation Error):
```json
{
  "errors": {
    "lastName": "Last name is required",
    "firstName": "First name is required"
  },
  "message": "Validation failed",
  "status": "error"
}
```

---

### 2. Login

```bash
curl -X POST http://allytic-labs-prod.eba-pukad2pd.us-east-1.elasticbeanstalk.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test@test.com",
    "password": "Test123!"
  }'
```

**Expected Response** (Success):
```json
{
  "status": "success",
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000
}
```

**Expected Response** (Invalid Credentials):
```json
{
  "message": "Invalid email or password",
  "status": "error"
}
```

---

### 3. Test Protected Endpoint (Drones)

First, save the access token from login:
```bash
# Save token to variable
TOKEN="your_access_token_here"

# Access protected endpoint
curl -X GET http://allytic-labs-prod.eba-pukad2pd.us-east-1.elasticbeanstalk.com/api/v1/drones \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response** (Success):
```json
{
  "status": "success",
  "data": [
    {
      "id": "uuid",
      "name": "Agricultural Drone",
      "type": "Agriculture",
      "price": "$2,499.99"
    }
  ]
}
```

**Expected Response** (Unauthorized):
```json
{
  "message": "Unauthorized",
  "status": 401
}
```

---

### 4. M-Pesa Payment (STK Push)

```bash
curl -X POST http://allytic-labs-prod.eba-pukad2pd.us-east-1.elasticbeanstalk.com/api/v1/payments/mpesa/stkpush \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "userId": "user-uuid",
    "amount": 100.00,
    "currency": "KES",
    "paymentMethod": "MPESA",
    "phoneNumber": "254712345678",
    "description": "Test payment",
    "merchantId": "MERCHANT-001",
    "merchantName": "Allytic Labs",
    "orderId": "ORDER-123456",
    "customerEmail": "test@test.com",
    "customerPhone": "254712345678",
    "idempotencyKey": "unique-key-123",
    "timestamp": 1702450800000,
    "metadata": {
      "productName": "Test Product"
    }
  }'
```

**Expected Response**:
```json
{
  "status": "success",
  "message": "STK push initiated",
  "paymentId": "payment-uuid",
  "checkoutRequestId": "ws_CO_123456789"
}
```

---

### 5. Stripe Payment Intent

```bash
curl -X POST http://allytic-labs-prod.eba-pukad2pd.us-east-1.elasticbeanstalk.com/api/v1/payments/stripe/create-intent \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "userId": "user-uuid",
    "amount": 50.00,
    "currency": "USD",
    "paymentMethod": "STRIPE",
    "description": "Test payment",
    "merchantId": "MERCHANT-001",
    "merchantName": "Allytic Labs",
    "orderId": "ORDER-123456",
    "customerEmail": "test@test.com",
    "customerPhone": "+1234567890",
    "idempotencyKey": "unique-key-456",
    "timestamp": 1702450800000,
    "metadata": {
      "productName": "Test Product"
    }
  }'
```

**Expected Response**:
```json
{
  "status": "success",
  "clientSecret": "pi_xxxxxxxxxxxxx_secret_xxxxxxxxxxxxx",
  "paymentId": "payment-uuid"
}
```

---

### 6. Newsletter Subscription

```bash
curl -X POST http://allytic-labs-prod.eba-pukad2pd.us-east-1.elasticbeanstalk.com/api/v1/newsletter \
  -H "Content-Type: application/json" \
  -d '{
    "email": "subscriber@test.com"
  }'
```

**Expected Response**:
```json
{
  "status": "success",
  "message": "Successfully subscribed to newsletter"
}
```

---

### 7. Contact Form

```bash
curl -X POST http://allytic-labs-prod.eba-pukad2pd.us-east-1.elasticbeanstalk.com/api/v1/contact \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@test.com",
    "message": "I need help with my order"
  }'
```

**Expected Response**:
```json
{
  "status": "success",
  "message": "Contact form submitted successfully"
}
```

---

## 🐛 Troubleshooting

### Backend Returns 502 Bad Gateway

```bash
# Check logs
eb logs | grep -i error

# Restart application
eb restart
```

### Backend Returns 401 Unauthorized

- Token expired or invalid
- Login again to get new token
- Check if JWT_SECRET environment variable is set

### Environment Variables Not Working

```bash
# Check current variables
eb printenv

# Set missing variables
eb setenv KEY=value

# Restart to apply changes
eb restart
```

### Health Status is Yellow/Red

```bash
# Check detailed health
eb health

# View logs for errors
eb logs --all

# Common fixes:
# 1. Check environment variables are set
# 2. Verify DynamoDB IAM permissions
# 3. Check application logs for errors
```

### Can't Connect to DynamoDB

1. Go to AWS Console → IAM → Roles
2. Find: `aws-elasticbeanstalk-ec2-role`
3. Attach policy: `AmazonDynamoDBFullAccess`
4. Restart: `eb restart`

---

## 📊 Monitoring

### View Application Logs

```bash
# Recent logs
eb logs

# Real-time logs
eb logs --stream

# All logs
eb logs --all
```

### Check Health

```bash
# Health status
eb health

# Detailed status
eb status

# Open CloudWatch (in browser)
# AWS Console → CloudWatch → Logs → /aws/elasticbeanstalk/allytic-labs-prod
```

---

## 🔐 Security

### Environment Variables (Already Configured)

- `JWT_SECRET`: JWT signing key
- `STRIPE_SECRET_KEY`: Stripe API key
- `MPESA_CONSUMER_KEY`: M-Pesa API key
- `MPESA_CONSUMER_SECRET`: M-Pesa secret
- `GOOGLE_CLIENT_ID`: OAuth client ID
- `GOOGLE_CLIENT_SECRET`: OAuth secret
- `AWS_REGION`: us-east-1

### CORS Configuration

Backend allows requests from:
- `https://allyticlabs-frontend.vercel.app`
- `http://localhost:3000` (development)

---

## 💰 Cost Breakdown

- **t3.small instance**: ~$15/month
- **DynamoDB**: Pay-per-request (minimal for low traffic)
- **Data transfer**: Included in free tier
- **Total**: ~$15-20/month

### Stop Environment to Save Costs

```bash
# Terminate environment (careful!)
eb terminate allytic-labs-prod

# Recreate when needed
eb create allytic-labs-prod --single
```

---

## 🚀 Useful Commands Reference

```bash
# Status & Health
eb status                 # Check environment status
eb health                 # Detailed health info
eb open                   # Open app in browser

# Logs & Debugging
eb logs                   # View recent logs
eb logs --stream          # Real-time logs
eb ssh                    # SSH into instance

# Deployment
eb deploy                 # Deploy new version
eb restart                # Restart application

# Configuration
eb printenv               # View environment variables
eb setenv KEY=value       # Set environment variable
eb config                 # Edit configuration

# Information
eb list                   # List all environments
eb console                # Open EB console in browser
```

---

## 📞 Support

- **GitHub**: [https://github.com/Odhiambo-20/ailytic-labs](https://github.com/Odhiambo-20/ailytic-labs)
- **AWS Documentation**: [https://docs.aws.amazon.com/elasticbeanstalk](https://docs.aws.amazon.com/elasticbeanstalk)
- **Spring Boot Docs**: [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)

---

## 📝 License

This project is proprietary and confidential.

---

**Last Updated**: December 13, 2024  
**Version**: 1.0.0  
**Author**: Allytic Labs Team
