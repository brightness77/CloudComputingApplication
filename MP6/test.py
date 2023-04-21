import requests
import json

url = 'https://ikm2evu584.execute-api.us-east-1.amazonaws.com/test/mp11-autograder'

payload = {
			"submitterEmail": "yixiao6@illinois.edu", # <insert your coursera account email>,
			"secret": "NMmOrXhaFnpErlF9", # <insert your secret token from coursera>,
			# "partId" : "G6U3L"
			"dbApi": "https://p5sqtzai25.execute-api.us-east-1.amazonaws.com/Stage1"
		}
print(json.dumps(payload))
r = requests.post(url, data=json.dumps(payload))

print(r.status_code, r.reason)
print(r.text)