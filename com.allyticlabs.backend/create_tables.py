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
    },
    'qr_payments': {
        'KeySchema': [
            {'AttributeName': 'id', 'KeyType': 'HASH'}
        ],
        'AttributeDefinitions': [
            {'AttributeName': 'id', 'AttributeType': 'S'}
        ]
    },
    'payments': {
        'KeySchema': [
            {'AttributeName': 'id', 'KeyType': 'HASH'}
        ],
        'AttributeDefinitions': [
            {'AttributeName': 'id', 'AttributeType': 'S'}
        ]
    },
    'mpesa_payments': {
        'KeySchema': [
            {'AttributeName': 'id', 'KeyType': 'HASH'}
        ],
        'AttributeDefinitions': [
            {'AttributeName': 'id', 'AttributeType': 'S'}
        ]
    },
    'payment_transactions': {
        'KeySchema': [
            {'AttributeName': 'id', 'KeyType': 'HASH'}
        ],
        'AttributeDefinitions': [
            {'AttributeName': 'id', 'AttributeType': 'S'}
        ]
    },
    'stripe_payments': {
        'KeySchema': [
            {'AttributeName': 'id', 'KeyType': 'HASH'}
        ],
        'AttributeDefinitions': [
            {'AttributeName': 'id', 'AttributeType': 'S'}
        ]
    },
    'WebhookLogs': {
        'KeySchema': [
            {'AttributeName': 'webhookId', 'KeyType': 'HASH'},
            {'AttributeName': 'timestamp', 'KeyType': 'RANGE'}
        ],
        'AttributeDefinitions': [
            {'AttributeName': 'webhookId', 'AttributeType': 'S'},
            {'AttributeName': 'timestamp', 'AttributeType': 'N'},
            {'AttributeName': 'paymentId', 'AttributeType': 'S'}
        ],
        'GlobalSecondaryIndexes': [
            {
                'IndexName': 'PaymentIdIndex',
                'KeySchema': [
                    {'AttributeName': 'paymentId', 'KeyType': 'HASH'}
                ],
                'Projection': {'ProjectionType': 'ALL'}
            }
        ]
    }
}

print("Starting DynamoDB table creation...\n")

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
        
        # Print GSI info if applicable
        if 'GlobalSecondaryIndexes' in config:
            gsi_names = [gsi['IndexName'] for gsi in config['GlobalSecondaryIndexes']]
            print(f"  └─ With Global Secondary Indexes: {', '.join(gsi_names)}")
            
    except Exception as e:
        if 'ResourceInUseException' in str(e):
            print(f"✓ Table {table_name} already exists")
        else:
            print(f"✗ Error creating {table_name}: {e}")

print("\n" + "="*60)
print("Summary of created tables:")
print("="*60)
print("Payment-related tables:")
print("  • qr_payments")
print("  • payments")
print("  • mpesa_payments")
print("  • payment_transactions")
print("  • stripe_payments")
print("  • WebhookLogs (with PaymentIdIndex GSI)")
print("\nOther tables:")
print("  • Users (with email-index, username-index GSIs)")
print("  • Robots")
print("  • Drones")
print("  • SolarPanels")
print("  • Contacts")
print("  • Newsletters")
print("="*60)
print("\nAll done! Now run your application:")
print("export $(grep -v '^#' .env | grep -v '^$' | xargs) && java -jar target/backend-1.0.0.jar")
