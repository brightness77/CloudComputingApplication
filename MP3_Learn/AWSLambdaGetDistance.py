import json
import boto3
from botocore.config import Config


def getDynamoResource():
    my_config = Config(
        region_name='us-east-1',
    )

    client = boto3.resource('dynamodb', config=my_config)
    return client


def close(fulfillment_state, message):
    response = {
        'dialogAction': {
            'type': 'Close',
            'fulfillmentState': fulfillment_state,
            'message': message
        }
    }

    return response


def handleEvent(intent_request):
    intent_name = intent_request['currentIntent']['name']
    source = intent_request['currentIntent']['slots']['Source']
    destination = intent_request['currentIntent']['slots']['Destination']

    dynamodb = getDynamoResource()
    table = dynamodb.Table('MP3_GraphResult')

    response = table.get_item(
        Key={
            'source_destination': (source + '-' + destination),
        }
    )

    return close(
        'Fulfilled',
        {
            'contentType': 'PlainText',
            'content': response['Item']['distance']
        },
    )


def lambda_handler(event, context):
    # TODO implement

    # logger.debug('event.bot.name={}'.format(event['bot']['name']))

    return handleEvent(event)