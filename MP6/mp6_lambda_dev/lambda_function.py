import json
import sys
import logging
import redis
import pymysql

DB_HOST_T = "database-3.cluster-ro-ck7lt9uxmu6g.us-east-1.rds.amazonaws.com"
DB_USER_T = "admin"
DB_PASS_T = "abc12345"
DB_NAME_T = "mpdb"
DB_TABLE_T = "herostb"
REDIS_URL_T = "redis://elc-tutorial.tlzedr.ng.0001.use1.cache.amazonaws.com:6379"

DB_HOST = "database-1.cluster-cyde3qsbzgo7.us-east-1.rds.amazonaws.com"
DB_USER = "admin"
DB_PASS = "password"
DB_NAME = "mp6db"
DB_TABLE = "hero"
REDIS_URL = "redis://mp6-redis.earome.ng.0001.use1.cache.amazonaws.com:6379"

TTL = 10


class DB:
    def __init__(self, **params):
        params.setdefault("charset", "utf8mb4")
        params.setdefault("cursorclass", pymysql.cursors.DictCursor)

        self.mysql = pymysql.connect(**params)

    def query(self, sql):
        with self.mysql.cursor() as cursor:
            cursor.execute(sql)
            return cursor.fetchall()

    def get_idx(self):
        with self.mysql.cursor() as cursor:
            cursor.execute(f"SELECT MAX(id) as id FROM {DB_TABLE}")
            idx = str(cursor.fetchone()['id'] + 1)
            return idx

    def insert(self, idx, data):
        with self.mysql.cursor() as cursor:
            hero = data["hero"]
            power = data["power"]
            name = data["name"]
            xp = data["xp"]
            color = data["color"]

            sql = f"INSERT INTO {DB_TABLE} (`id`, `hero`, `power`, `name`, `xp`, `color`) VALUES ('{idx}', '{hero}', '{power}', '{name}', '{xp}', '{color}')"

            cursor.execute(sql)
            self.mysql.commit()


def read(use_cache, indices, Database, Cache):
    result = []

    print("Cur cache status is " + str(use_cache))

    for index in indices:
        query_result = None
        sql = f"SELECT * From {DB_TABLE} WHERE id = {index}"

        if use_cache:
            # lazy load strategy
            cached = Cache.get(index)
            if cached is None:
                # query from db
                query_result = Database.query(sql)[0]

                stringnified = json.dumps(query_result)
                Cache.setex(index, TTL, stringnified)
            else:
                # query_result = cached.decode('ascii')
                query_result = json.loads(cached.decode('ascii'))

        else:
            # directly query from db
            query_result = Database.query(sql)[0]

            stringnified = json.dumps(query_result)
            Cache.setex(index, TTL, stringnified)

        result.append(query_result)

    return result


def write(use_cache, sqls, Database, Cache):
    for object in sqls:
        index = Database.get_idx()
        Database.insert(index, object)

        if use_cache:
            # write through strategy
            stringnified = json.dumps(object)
            Cache.setex(index, TTL, stringnified)


def lambda_handler(event, context):
    USE_CACHE = (event['USE_CACHE'] == "True")
    REQUEST = event['REQUEST']

    # initialize database and cache
    try:
        Database = DB(host=DB_HOST, user=DB_USER, password=DB_PASS, db=DB_NAME)
    except pymysql.MySQLError as e:
        print("ERROR: Unexpected error: Could not connect to MySQL instance.")
        print(e)
        sys.exit()

    Cache = redis.Redis.from_url(REDIS_URL)

    print(" ========== LOG ==========")
    print(REQUEST)
    print(event["SQLS"])

    result = []
    if REQUEST == "read":
        # event["SQLS"] should be a list of integers
        result = read(USE_CACHE, event["SQLS"], Database, Cache)
    elif REQUEST == "write":
        # event["SQLS"] should be a list of jsons
        write(USE_CACHE, event["SQLS"], Database, Cache)
        result = "write success"

    print(" ========== END OF LOG ==========")

    return {
        'statusCode': 200,
        'body': result
    }