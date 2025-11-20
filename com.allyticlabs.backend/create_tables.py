import boto3
import os
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# Get AWS credentials from environment variables
aws_access_key = os.getenv('AWS_ACCESS_KEY_ID')
aws_secret_key = os.getenv('AWS_SECRET_ACCESS_KEY')
aws_region = os.getenv('AWS_REGION', 'us-east-1')

# Validate that credentials are loaded
if not aws_access_key or not aws_secret_key:
    raise ValueError("AWS credentials not found! Please set AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY in your .env file")

# Initialize DynamoDB client
dynamodb = boto3.client('dynamodb', 
                        region_name=aws_region,
                        aws_access_key_id=aws_access_key,
                        aws_secret_access_key=aws_secret_key)

tables = ['Robots', 'Drones', 'SolarPanels', 'Contacts', 'Newsletters']

for table_name in tables:
    try:
        response = dynamodb.create_table(
            TableName=table_name,
            KeySchema=[
                {'AttributeName': 'id', 'KeyType': 'HASH'}
            ],
            AttributeDefinitions=[
                {'AttributeName': 'id', 'AttributeType': 'S'}
            ],
            BillingMode='PAY_PER_REQUEST'
        )
        print(f"✓ Created table: {table_name}")
    except Exception as e:
        if 'ResourceInUseException' in str(e):
            print(f"✓ Table {table_name} already exists")
        else:
            print(f"✗ Error creating {table_name}: {e}")

print("\nAll done! Now run your application:")
print("source .env && java -jar target/backend-1.0.0.jar")