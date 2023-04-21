import requests
import json

url = "https://6rsqbadgd5jm4jj52ezggecgqu0dbnol.lambda-url.us-east-1.on.aws/"

payload = {
            "submitterEmail": 'yixiao6@illinois.edu', # Your Email Id as it appears in the coursera instruction page.
            "secret": 'jrRrpjAA6tiwlSil', # Your token as it appears in the coursera instruction page. This token will only be valid for 30 mins.
            "ipaddress": '44.212.10.83:5000' # Public IPv4 address which you can find on the EC2 instance home page. Add port number on which your server is running.
    }


print("Running the autograder. This might take several seconds...")

r = requests.post(url, data=json.dumps(payload), headers = {"Content-Type": "application/json"})

print(r.status_code, r.reason)
print(r.text)
