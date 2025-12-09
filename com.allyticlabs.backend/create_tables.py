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

# Define tables with their configurations
tables_config = {
    'Users': {
        'KeySchema': [
            {'AttributeName': 'userId', 'KeyType': 'HASH'}
        ],
        'AttributeDefinitions': [
            {'AttributeName': 'userId', 'AttributeType': 'S'},
            {'AttributeName': 'email', 'AttributeType': 'S'},
            {'AttributeName': 'username', 'AttributeType': 'S'}
        ],
        'GlobalSecondaryIndexes': [
            {
                'IndexName': 'email-index',
                'KeySchema': [
                    {'AttributeName': 'email', 'KeyType': 'HASH'}
                ],
                'Projection': {'ProjectionType': 'ALL'}
            },
            {
                'IndexName': 'username-index',
                'KeySchema': [
                    {'AttributeName': 'username', 'KeyType': 'HASH'}
                ],
                'Projection': {'ProjectionType': 'ALL'}
            }
        ]
    },
    'Robots': {
        'KeySchema': [
            {'AttributeName': 'id', 'KeyType': 'HASH'}
        ],
        'AttributeDefinitions': [
            {'AttributeName': 'id', 'AttributeType': 'S'}
        ]
    },
    'Drones': {
        'KeySchema': [
            {'AttributeName': 'id', 'KeyType': 'HASH'}
        ],
        'AttributeDefinitions': [
            {'AttributeName': 'id', 'AttributeType': 'S'}
        ]
    },
    'SolarPanels': {
        'KeySchema': [
            {'AttributeName': 'id', 'KeyType': 'HASH'}
        ],
        'AttributeDefinitions': [
            {'AttributeName': 'id', 'AttributeType': 'S'}
        ]
    },
    'Contacts': {
        'KeySchema': [
            {'AttributeName': 'id', 'KeyType': 'HASH'}
        ],
        'AttributeDefinitions': [
            {'AttributeName': 'id', 'AttributeType': 'S'}
        ]
    },
    'Newsletters': {
        'KeySchema': [
            {'AttributeName': 'id', 'KeyType': 'HASH'}
        ],
        'AttributeDefinitions': [
            {'AttributeName': 'id', 'AttributeType': 'S'}
        ]
    }
}

for table_name, config in tables_config.items():
    try:
        table_params = {
            'TableName': table_name,
            'KeySchema': config['KeySchema'],
            'AttributeDefinitions': config['AttributeDefinitions'],
            'BillingMode': 'PAY_PER_REQUEST'
        }
        
        # Add Global Secondary Indexes if they exist
        if 'GlobalSecondaryIndexes' in config:
            table_params['GlobalSecondaryIndexes'] = config['GlobalSecondaryIndexes']
        
        response = dynamodb.create_table(**table_params)
        print(f"✓ Created table: {table_name}")
        
        # Print GSI info for Users table
        if table_name == 'Users':
            print(f"  └─ With Global Secondary Indexes: email-index, username-index")
            
    except Exception as e:
        if 'ResourceInUseException' in str(e):
            print(f"✓ Table {table_name} already exists")
        else:
            print(f"✗ Error creating {table_name}: {e}")

print("\nAll done! Now run your application:")
print("export $(grep -v '^#' .env | grep -v '^$' | xargs) && java -jar target/backend-1.0.0.jar")