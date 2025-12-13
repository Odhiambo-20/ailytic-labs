# ailytic-labs
# Complete AWS Elastic Beanstalk Deployment Guide (From Scratch)

## ⚠️ CRITICAL SECURITY WARNING

**Your .env file contains REAL credentials that are now exposed!** You must:
1. **IMMEDIATELY rotate/delete these credentials**
2. **NEVER commit .env files to version control**
3. **Use environment variables in AWS, not .env files**

### What to do RIGHT NOW:
1. Go to AWS IAM Console → Delete those access keys
2. Go to Stripe Dashboard → Rotate those API keys
3. Go to Safaricom Daraja → Rotate M-Pesa credentials
4. Generate a new JWT secret

---

## PART 1: CREATE AWS ACCOUNT (15 minutes)

### Step 1: Sign Up for AWS

1. **Go to**: https://aws.amazon.com
2. **Click**: "Create an AWS Account" (top right corner)
3. **Enter**:
   - Email address
   - Password
   - AWS account name (e.g., "AllyticLabs")
4. **Click**: "Continue"

### Step 2: Contact Information

1. **Select**: "Professional" or "Personal" account
2. **Fill in**:
   - Full Name
   - Phone Number
   - Country
   - Address
3. **Click**: "Create Account and Continue"

### Step 3: Payment Information

1. **Enter** credit/debit card details
   - AWS requires card for verification
   - **Free tier available**: Won't be charged if you stay within limits
2. **Complete** card verification
3. **Click**: "Verify and Continue"

### Step 4: Identity Verification

1. **Enter** phone number
2. **Receive** verification code via SMS or call
3. **Enter** the code
4. **Click**: "Continue"

### Step 5: Select Support Plan

1. **Choose**: "Basic support - Free"
2. **Click**: "Complete sign up"

### Step 6: Wait for Account Activation

- Takes 5-15 minutes
- You'll receive email when ready
- **Check spam folder** if not received

---

## PART 2: SECURE YOUR AWS ACCOUNT (10 minutes)

### Step 1: Enable MFA (Multi-Factor Authentication)

1. **Go to**: AWS Console → Search "IAM"
2. **Click**: "IAM" service
3. **Click**: "Add MFA" in the security recommendations
4. **Choose**: "Virtual MFA device"
5. **Use**: Google Authenticator or Authy app on your phone
6. **Scan** QR code with your app
7. **Enter** two consecutive MFA codes
8. **Click**: "Assign MFA"

---

## PART 3: CREATE IAM USER FOR DEPLOYMENT (15 minutes)

**Why**: Never use root account for deployments

### Step 1: Create IAM User

1. **In IAM Dashboard**, click "Users" (left sidebar)
2. **Click**: "Create user" (top right)
3. **Enter username**: `eb-deployment-user`
4. **Check**: "Provide user access to the AWS Management Console" (optional)
5. **Click**: "Next"

### Step 2: Set Permissions

1. **Select**: "Attach policies directly"
2. **Search and check** these policies:
   - `AWSElasticBeanstalkFullAccess`
   - `AmazonDynamoDBFullAccess`
   - `IAMFullAccess` (needed for EB to create roles)
   - `AmazonS3FullAccess` (EB uses S3 for deployment files)
3. **Click**: "Next"
4. **Click**: "Create user"

### Step 3: Create Access Keys

1. **Click** on the user you just created (`eb-deployment-user`)
2. **Click**: "Security credentials" tab
3. **Scroll down** to "Access keys"
4. **Click**: "Create access key"
5. **Select**: "Command Line Interface (CLI)"
6. **Check**: "I understand..." checkbox
7. **Click**: "Next"
8. **Add description** (optional): "EB Deployment"
9. **Click**: "Create access key"

### Step 4: SAVE YOUR CREDENTIALS

⚠️ **CRITICAL**: You'll see these only once!

```
Access Key ID: AKIAXXXXXXXXXXXXXXXX
Secret Access Key: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

**Write these down** or download the CSV file. You'll need them in Step 5!

---

## PART 4: INSTALL REQUIRED SOFTWARE (20 minutes)

### Step 1: Update System Packages

```bash
sudo apt update
sudo apt upgrade -y
```

### Step 2: Install Java 17 (if not already installed)

```bash
# Check if Java is installed
java -version

# If not installed or wrong version:
sudo apt install openjdk-17-jdk -y

# Verify
java -version
# Should show: openjdk version "17.x.x"
```

### Step 3: Install Maven (if not already installed)

```bash
# Check if Maven is installed
mvn -version

# If not installed:
sudo apt install maven -y

# Verify
mvn -version
```

### Step 4: Install AWS CLI

```bash
# Download installer
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"

# Install unzip if needed
sudo apt install unzip -y

# Unzip
unzip awscliv2.zip

# Install
sudo ./aws/install

# Verify
aws --version
# Should show: aws-cli/2.x.x
```

### Step 5: Install Python pip (needed for EB CLI)

```bash
# Install pip
sudo apt install python3-pip -y

# Verify
pip3 --version
```

### Step 6: Install EB CLI

```bash
# Install EB CLI
pip3 install awsebcli --upgrade --user

# Add to PATH (add this to your ~/.bashrc)
echo 'export PATH=$PATH:~/.local/bin' >> ~/.bashrc

# Reload bashrc
source ~/.bashrc

# Verify
eb --version
# Should show: EB CLI 3.x.x
```

---

## PART 5: CONFIGURE AWS CREDENTIALS (5 minutes)

### Step 1: Configure AWS CLI

```bash
aws configure
```

You'll be prompted for:

```
AWS Access Key ID [None]: AKIAXXXXXXXXXXXXXXXX
AWS Secret Access Key [None]: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
Default region name [None]: us-east-1
Default output format [None]: json
```

**Enter**:
- Access Key ID: From Part 3, Step 4
- Secret Access Key: From Part 3, Step 4
- Region: `us-east-1`
- Output format: `json`

### Step 2: Test Configuration

```bash
# Test if credentials work
aws sts get-caller-identity
```

Should show:
```json
{
    "UserId": "AIDAXXXXXXXXXXXXXXXXX",
    "Account": "123456789012",
    "Arn": "arn:aws:iam::123456789012:user/eb-deployment-user"
}
```

---

## PART 6: PREPARE YOUR APPLICATION (10 minutes)

### Step 1: Navigate to Your Project

```bash
cd ~/Documents/Desktop/Allytic-Labs/com.allyticlabs.backend
```

### Step 2: Update application.properties for Production

**Important**: Change the server port to 5000 (EB expects this)

Edit `src/main/resources/application.properties`:

```properties
# Change this line:
server.port=5000
# From: server.port=8080

# Keep everything else the same
```

### Step 3: Build Your Application

```bash
# Clean previous builds
mvn clean

# Build the JAR (skip tests for faster build)
mvn package -DskipTests

# If you see errors, run:
mvn clean install -DskipTests
```

**Wait**: This takes 2-5 minutes

**Success looks like**:
```
[INFO] BUILD SUCCESS
[INFO] Total time: 02:34 min
```

**Your JAR file is at**: `target/backend-1.0.0.jar`

### Step 4: Verify JAR was created

```bash
ls -lh target/backend-1.0.0.jar
```

Should show:
```
-rw-rw-r-- 1 victor victor 45M Dec 13 10:30 target/backend-1.0.0.jar
```

---

## PART 7: INITIALIZE ELASTIC BEANSTALK (10 minutes)

### Step 1: Initialize EB in Your Project

```bash
# Make sure you're in the project directory
cd ~/Documents/Desktop/Allytic-Labs/com.allyticlabs.backend

# Initialize EB
eb init
```

### Step 2: Answer the Prompts

**Prompt 1**: Select a default region
```
Select a default region
1) us-east-1 : US East (N. Virginia)
2) us-west-1 : US West (N. California)
3) us-west-2 : US West (Oregon)
...
(default is 3): 
```
**Type**: `1` (for us-east-1) and press Enter

---

**Prompt 2**: Enter Application Name
```
Enter Application Name
(default is "com.allyticlabs.backend"):
```
**Type**: `allytic-labs-backend` and press Enter

---

**Prompt 3**: Select a platform
```
Select a platform.
1) .NET Core on Linux
2) .NET on Windows Server
3) Docker
4) GlassFish
5) Go
6) Java
...
(make a selection):
```
**Type**: `6` (for Java) and press Enter

---

**Prompt 4**: Select a platform branch
```
Select a platform branch.
1) Corretto 17 running on 64bit Amazon Linux 2023
2) Corretto 11 running on 64bit Amazon Linux 2023
3) Corretto 8 running on 64bit Amazon Linux 2023
...
(default is 1):
```
**Press**: Enter (use default - Corretto 17)

---

**Prompt 5**: CodeCommit
```
Do you wish to continue with CodeCommit? (Y/n):
```
**Type**: `n` and press Enter

---

**Prompt 6**: SSH
```
Do you want to set up SSH for your instances?
(Y/n):
```
**Type**: `Y` and press Enter

---

**Prompt 7**: Select keypair
```
Select a keypair.
1) [ Create new KeyPair ]
(default is 1):
```
**Press**: Enter (create new keypair)

---

**Prompt 8**: Enter keypair name
```
Type a keypair name.
(Default is aws-eb):
```
**Press**: Enter (use default name)

### Step 3: Verify Initialization

```bash
# Check if .elasticbeanstalk folder was created
ls -la .elasticbeanstalk/

# Should show:
# config.yml
```

---

## PART 8: CREATE AND DEPLOY ENVIRONMENT (15 minutes)

### Step 1: Create Environment with Single Instance

```bash
# Create environment (this takes 5-10 minutes)
eb create allytic-labs-prod --single --instance-type t3.small
```

**What this does**:
- `allytic-labs-prod`: Your environment name
- `--single`: Uses 1 instance (cheaper, good for starting)
- `--instance-type t3.small`: Small instance ($0.0208/hour ≈ $15/month)

**You'll see**:
```
Creating application version archive "app-241213_103045".
Uploading allytic-labs-backend/app-241213_103045.zip to S3...
Environment details for: allytic-labs-prod
  Application name: allytic-labs-backend
  Region: us-east-1
  Deployed Version: app-241213_103045
  Environment ID: e-xxxxxxxxxx
  Platform: arn:aws:elasticbeanstalk:us-east-1::platform/...
  Tier: WebServer-Standard-1.0
  CNAME: allytic-labs-prod.us-east-1.elasticbeanstalk.com
  Updated: 2024-12-13 10:30:45
Printing Status:
2024-12-13 10:30:50    INFO    createEnvironment is starting.
2024-12-13 10:30:52    INFO    Using elasticbeanstalk-us-east-1-123456789 as Amazon S3 storage bucket for environment data.
...
```

**Wait**: This takes 5-10 minutes. Get coffee! ☕

### Step 2: Monitor Progress

In another terminal, you can watch:
```bash
eb status
```

**Success looks like**:
```
Environment details for: allytic-labs-prod
  Application name: allytic-labs-backend
  Region: us-east-1
  Platform: Java 17 running on Amazon Linux 2023
  Status: Ready
  Health: Green
```

---

## PART 9: SET ENVIRONMENT VARIABLES (10 minutes)

**IMPORTANT**: Generate NEW credentials before this step!

### Step 1: Generate New JWT Secret

```bash
# Generate new JWT secret
openssl rand -base64 64
```

Copy the output - you'll use it below

### Step 2: Set All Environment Variables

```bash
eb setenv \
  AWS_REGION=us-east-1 \
  PAYMENT_ENCRYPTION_KEY=your_new_encryption_key \
  STRIPE_SECRET_KEY=sk_test_YOUR_NEW_STRIPE_KEY \
  STRIPE_PUBLIC_KEY=pk_test_YOUR_NEW_STRIPE_KEY \
  STRIPE_WEBHOOK_SECRET=whsec_YOUR_NEW_WEBHOOK_SECRET \
  MPESA_CONSUMER_KEY=YOUR_NEW_MPESA_KEY \
  MPESA_CONSUMER_SECRET=YOUR_NEW_MPESA_SECRET \
  MPESA_PASSKEY=YOUR_MPESA_PASSKEY \
  MPESA_SHORT_CODE=174379 \
  MPESA_INITIATOR_NAME=testapi \
  MPESA_INITIATOR_PASSWORD=YOUR_INITIATOR_PASSWORD \
  MPESA_ENVIRONMENT=sandbox \
  JWT_SECRET=YOUR_NEW_JWT_SECRET_FROM_STEP1 \
  GOOGLE_CLIENT_ID=YOUR_GOOGLE_CLIENT_ID \
  GOOGLE_CLIENT_SECRET=YOUR_NEW_GOOGLE_SECRET \
  OAUTH2_REDIRECT_URI=https://allyticlabs-frontend.vercel.app/oauth2/redirect
```

**Replace ALL the `YOUR_` values with your actual new credentials!**

**This takes**: 2-3 minutes to update

### Step 3: Update M-Pesa Callback URLs

After deployment, get your EB URL:
```bash
eb status | grep CNAME
```

You'll see something like:
```
CNAME: allytic-labs-prod.us-east-1.elasticbeanstalk.com
```

Now update callback URLs:
```bash
eb setenv \
  MPESA_CALLBACK_URL=https://allytic-labs-prod.us-east-1.elasticbeanstalk.com/api/v1/payments/mpesa/callback \
  MPESA_TIMEOUT_URL=https://allytic-labs-prod.us-east-1.elasticbeanstalk.com/api/v1/payments/mpesa/timeout \
  MPESA_RESULT_URL=https://allytic-labs-prod.us-east-1.elasticbeanstalk.com/api/v1/payments/mpesa/result \
  MPESA_VALIDATION_URL=https://allytic-labs-prod.us-east-1.elasticbeanstalk.com/api/v1/payments/mpesa/validate \
  MPESA_CONFIRMATION_URL=https://allytic-labs-prod.us-east-1.elasticbeanstalk.com/api/v1/payments/mpesa/confirm
```

---

## PART 10: CONFIGURE IAM ROLE FOR DYNAMODB (5 minutes)

Your application needs permission to access DynamoDB.

### Step 1: Go to AWS Console

1. **Open**: https://console.aws.amazon.com
2. **Search**: "IAM" in the top search bar
3. **Click**: IAM service

### Step 2: Find EB Instance Role

1. **Click**: "Roles" (left sidebar)
2. **Search**: "aws-elasticbeanstalk-ec2-role"
3. **Click** on the role

### Step 3: Attach DynamoDB Policy

1. **Click**: "Add permissions" → "Attach policies"
2. **Search**: "DynamoDB"
3. **Check**: `AmazonDynamoDBFullAccess`
4. **Click**: "Attach policies"

### Step 4: Restart Environment

```bash
eb restart
```

Wait 2-3 minutes for restart.

---

## PART 11: TEST YOUR DEPLOYMENT (10 minutes)

### Step 1: Get Your Application URL

```bash
eb status
```

Look for the CNAME:
```
CNAME: allytic-labs-prod.us-east-1.elasticbeanstalk.com
```

### Step 2: Open in Browser

```bash
eb open
```

This opens your app in the browser.

### Step 3: Test Health Endpoint

```bash
curl https://allytic-labs-prod.us-east-1.elasticbeanstalk.com/actuator/health
```

Should return:
```json
{"status":"UP"}
```

### Step 4: Check Logs if Issues

```bash
# View all logs
eb logs

# View logs in real-time
eb logs --stream
```

### Step 5: SSH Into Instance (if needed)

```bash
eb ssh
```

Once inside:
```bash
# View application logs
sudo tail -f /var/log/web.stdout.log

# Exit SSH
exit
```

---

## PART 12: UPDATE YOUR FRONTEND CORS (5 minutes)

Your frontend needs to point to the new backend URL.

### Update Frontend Environment Variables

In your Vercel frontend project:

1. **Go to**: Vercel Dashboard
2. **Select**: Your frontend project
3. **Go to**: Settings → Environment Variables
4. **Update** (or add):
```
REACT_APP_API_URL=https://allytic-labs-prod.us-east-1.elasticbeanstalk.com
```
5. **Redeploy** frontend

---

## PART 13: MAKING UPDATES (FUTURE DEPLOYMENTS)

### When You Change Code:

```bash
# 1. Make your code changes

# 2. Build new JAR
cd ~/Documents/Desktop/Allytic-Labs/com.allyticlabs.backend
mvn clean package -DskipTests

# 3. Deploy updates (takes 2-5 minutes)
eb deploy

# 4. Monitor deployment
eb status

# 5. Check logs if needed
eb logs

# 6. Test
eb open
```

**That's it!** Every time you make changes:
1. Build JAR
2. Run `eb deploy`
3. Wait 2-5 minutes

---

## USEFUL COMMANDS REFERENCE

```bash
# Check environment status
eb status

# View logs
eb logs

# Stream logs in real-time
eb logs --stream

# Open app in browser
eb open

# Restart application
eb restart

# SSH into instance
eb ssh

# Check environment variables
eb printenv

# Set new environment variable
eb setenv KEY=value

# Terminate environment (CAREFUL!)
eb terminate allytic-labs-prod

# Scale instances (upgrade from single)
eb scale 2

# Check health
eb health

# List all environments
eb list
```

---

## TROUBLESHOOTING COMMON ISSUES

### Issue 1: Build Fails

```bash
# Clean and rebuild
mvn clean install -DskipTests -U

# If still fails, check Java version
java -version
# Must be Java 17
```

### Issue 2: Environment Health is Red/Yellow

```bash
# Check logs
eb logs --all

# Common causes:
# - Port mismatch (must be 5000)
# - Missing environment variables
# - DynamoDB permissions not set
```

### Issue 3: Can't Access DynamoDB

```bash
# Verify IAM role has DynamoDB access
# Go to AWS Console → IAM → Roles
# Check aws-elasticbeanstalk-ec2-role has DynamoDBFullAccess
```

### Issue 4: CORS Errors

Update `application.properties`:
```properties
app.cors.allowed.origins=https://allyticlabs-frontend.vercel.app,https://allytic-labs-prod.us-east-1.elasticbeanstalk.com
```

Then:
```bash
mvn clean package -DskipTests
eb deploy
```

---

## COST BREAKDOWN

- **t3.small instance**: ~$15/month
- **DynamoDB**: Pay per request (very cheap for small apps)
- **Data transfer**: Included in free tier
- **Load Balancer** (if you upgrade): ~$16/month

**Total estimated cost**: $15-30/month for small traffic

### To Reduce Costs:

```bash
# Stop environment when not in use
eb terminate allytic-labs-prod

# Recreate when needed
eb create allytic-labs-prod --single
```

---

## NEXT STEPS

1. ✅ **Set up custom domain** (Route 53)
2. ✅ **Enable HTTPS** (AWS Certificate Manager - FREE)
3. ✅ **Set up monitoring** (CloudWatch alarms)
4. ✅ **Configure backups** for DynamoDB
5. ✅ **Set up CI/CD** (GitHub Actions)
6. ✅ **Enable auto-scaling** when traffic grows

---

## EMERGENCY CONTACTS

- **AWS Support**: https://console.aws.amazon.com/support
- **EB Documentation**: https://docs.aws.amazon.com/elasticbeanstalk
- **Billing Alerts**: Set up in AWS Budgets

---

**🎉 CONGRATULATIONS!** 

Your backend is now live at:
`https://allytic-labs-prod.us-east-1.elasticbeanstalk.com`

You can update it anytime with:
```bash
mvn clean package -DskipTests && eb deploy




## 🎊 **CONGRATULATIONS!**

Your backend is now **fully deployed and running** on AWS Elastic Beanstalk!

Your API is accessible at:
```
https://allytic-labs-prod.eba-pukad2pd.us-east-1.elasticbeanstalk.com
```
