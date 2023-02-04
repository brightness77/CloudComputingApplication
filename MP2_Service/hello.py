from flask import Flask, request

app = Flask(__name__)

@app.route('/hello')
def hello_world():
    return 'Hello World'

seed = 0

@app.route('/', methods=['POST'], )
def setSeed():
    global seed
    body_json = request.get_json()
    seed = body_json['num']
    return str(seed)

@app.route('/', methods=['GET'], )
def getSeed():
    global seed
    return str(seed)



if __name__ == '__main__':
    app.run()