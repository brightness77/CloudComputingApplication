import requests
import json

payload = {
        "USE_CACHE": "True",
        "REQUEST": "read",
        "SQLS": [1,2,3]
    }
api = "https://p5sqtzai25.execute-api.us-east-1.amazonaws.com/Stage1"
r = requests.post(api, data=json.dumps(payload))

res = ""
res = r.json()['body']

print(r.status_code, r.reason)
print(r.text)
print(res)