
import requests
import json
import uuid

url = "https://seorwrpmwh.execute-api.us-east-1.amazonaws.com/prod/mp3-autograder-2022-spring"

graph_api = 
chatbot_name = "MPThree_Bot"
chatbot_alias = 
region = "us-east-1"
email = 
accountid = 
identity_pool_id = 

payload = {
	"graphApi": graph_api, #<post api for storing the graph>,
	"botName": chatbot_name, # <name of your Amazon Lex Bot>,
	"botAlias": chatbot_alias, # <alias name given when publishing the bot>,
	"identityPoolId": identity_pool_id, #<cognito identity pool id for lex>,
	"accountId": accountid, #<your aws account id used for accessing lex>,
	"submitterEmail": email, # <insert your coursera account email>,
	"secret": "cG0dNUJ2vtJitmgB", # <insert your secret token from coursera>,
	"region": region, #<Region where your lex is deployed (Ex: us-east-1)>
    }

r = requests.post(url, data=json.dumps(payload))

print(r.status_code, r.reason)
print(r.text)
