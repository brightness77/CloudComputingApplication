import json
import boto3
from botocore.config import Config


def getDynamoResource():
    my_config = Config(
        region_name='us-east-1',
    )

    client = boto3.resource('dynamodb', config=my_config)
    return client


def addDataToTable(table, source, destination, distance):
    data = table.put_item(
        TableName='MP3_GraphResult',
        Item={
            'source_destination': (source + "-" + destination),
            'source': source,
            'destination': destination,
            'distance': distance,
        }
    )


def lambda_handler(event, context):
    # TODO implement

    # db initialize
    dynamodb = getDynamoResource()
    table = dynamodb.Table('MP3_GraphResult')

    graphStr = event['graph']

    # parse string & build edge list
    split1 = graphStr.split(',')
    edgeList = []
    for str1 in split1:
        toAdd = str1.split('->')
        edgeList.append(toAdd)

    # build adj list
    adjList = {}
    for edge in edgeList:
        if edge[0] not in adjList:
            adjList[edge[0]] = []

        adjList[edge[0]].append(edge[1])

    # do bfs starting with every node
    for departure in adjList:
        taskQ = []
        taskQ.append(departure)

        visited = {departure: True, }

        curDistance = 0

        while len(taskQ) > 0:
            curSize = len(taskQ)
            for i in range(curSize):
                destination = taskQ.pop(0)

                # distance
                if curDistance != 0:
                    addDataToTable(table, departure, destination, curDistance)

                # children
                if destination not in adjList:
                    continue

                for next in adjList[destination]:
                    if next in visited:
                        continue

                    visited[next] = True
                    taskQ.append(next)

            curDistance += 1

    response = {
        'statusCode': 200,
        'body': json.dumps('Successfully Read Graph')
    }

    return response
