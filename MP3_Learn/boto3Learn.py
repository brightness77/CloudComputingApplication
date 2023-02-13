import boto3
from botocore.config import Config


my_config = Config(
    region_name = 'us-east-1',
)

# resource
dynamodb = boto3.resource('dynamodb', config = my_config)

# table = dynamodb.create_table(
#     TableName = 'user',
# )

table = dynamodb.Table('dynamodb')

source = 'Chicago'
destination = 'Los Angeles'
distance = 2

table.put_item(
    Item = {
        'source':source,
        'destination':destination,
        'distance':distance,
    }
)