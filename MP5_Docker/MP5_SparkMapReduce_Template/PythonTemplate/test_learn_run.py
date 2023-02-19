
import sys
import os
from pyspark import SparkConf, SparkContext

stopWordsPath = os.path.join(sys.path[0], 'stopwords.txt')
delimitersPath = os.path.join(sys.path[0], 'delimiters.txt')

stopWords = []
delimiters = []

with open(stopWordsPath) as f:
    # TODO
    stopWords = f.read().split('\n')
    # print(stopWords)
    f.close()

with open(delimitersPath) as f:
    # TODO
    delimiters = f.read()
    # print(delimiters)
    f.close()



def tokenizer(s: str, delimiter: str):
    result = []
    temp = ""
    for c in s:
        if c in delimiter:
            if temp:
                result.append(temp)
            temp = ""
        else:
            temp += c

    if temp:
        result.append(temp)

    return result


test_string = "AB;cdwd oij_123g"
r = tokenizer(test_string, delimiters)



conf = SparkConf().setMaster("local").setAppName("Learn_App")
conf.set("spark.driver.bindAddress", "127.0.0.1")
sc = SparkContext(conf=conf)

lines = sc.parallelize(
    ["scala",
       "java",
       "hadoop",
       "spark",
       "akka",
       "spark vs hadoop",
       "pyspark",
       "pyspark and spark"]
)


# count map
def count_map(value):
    # tokenize
    tokenized = tokenizer(value, delimiters)
    result = []
    for token in tokenized:
        result.append((token, 1))

    return result

words_map = lines.flatMap(count_map)
wm_c = words_map.collect()
print(" ========== Map Result!  ========== ")
print(wm_c)

words_reduce = words_map.reduceByKey(lambda a, b: a + b)
top_data = words_reduce.collect()
print(" ========== Reduce Result!  ========== ")
print(top_data)


top_data.sort(key=lambda tup:tup[1], reverse = True)

if len(top_data) > 4:
    top_data = top_data[:4]

print(top_data)





