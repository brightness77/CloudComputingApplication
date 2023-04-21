from kubernetes import client, config
from flask import Flask, request
from os import path
import yaml, random, string, json
import sys


# Configs can be set in Configuration class directly or using helper utility
config.load_kube_config()
v1 = client.CoreV1Api()
app = Flask(__name__)
# app.run(debug = True)

@app.route('/config', methods=['GET'])
def get_config():

    # your code here
    pods = v1.list_pod_for_all_namespaces()

    output = {"pods":
                  [{"name": pod.metadata.name,
                    "ip": pod.status.pod_ip,
                    "namespace": pod.metadata.namespace,
                    "node": pod.spec.node_name,
                    "status": pod.status.phase
                    } for pod in pods.items]
              }
    output = json.dumps(output)

    return output

@app.route('/img-classification/free',methods=['POST'])
def post_free():
    # your code here

    # body_json = request.get_json()
    # data_set = body_json['dataset']

    # body = client.V1Job(metadata=client.V1ObjectMeta(name="mp12-job-free"), )
    pretty = 'pretty_example'
    dry_run = 'dry_run_example'
    field_manager = 'field_manager_example'
    field_validation = 'field_validation_example'

    # resp = v1.create_namespaced_job("free-service", body)
    # resp = client.BatchV1Api().create_namespaced_job("free-service", body)
    # resp = client.BatchV1Api().create_namespaced_job("free-service", body, pretty=pretty, dry_run=dry_run,
    #                                                  field_manager=field_manager, field_validation=field_validation)

    with open('createJobFree.yaml', 'r') as file:
        job = yaml.safe_load(file)

    # print(job)

    resp = client.BatchV1Api().create_namespaced_job("free-service", job)
    print(resp)

    return "success"


@app.route('/img-classification/premium', methods=['POST'])
def post_premium():
    # your code here

    # body_json = request.get_json()
    # data_set = body_json['dataset']

    # body = client.V1Job(metadata=client.V1ObjectMeta(name="mp12-job-premium"), )
    # resp = v1.create_namespaced_job("default", body)

    with open('createJobPremium.yaml', 'r') as file:
        job = yaml.safe_load(file)

    resp = client.BatchV1Api().create_namespaced_job("default", job)
    print(resp)

    return "success"

    
if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000)
