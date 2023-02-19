#!/usr/bin/env python

'''Exectuion Command: spark-submit TitleCountSpark.py stopwords.txt delimiters.txt dataset/titles/ dataset/output'''

import sys, codecs
from pyspark import SparkConf, SparkContext

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

stopWordsPath = sys.argv[1]
delimitersPath = sys.argv[2]

stop_words = []
delimiters = []

with open(stopWordsPath) as f:
    # TODO
    stop_words = f.read().split('\n')
    f.close()

with open(delimitersPath) as f:
    # TODO
    delimiters = f.read()
    f.close()

conf = SparkConf().setMaster("local").setAppName("TitleCount")
conf.set("spark.driver.bindAddress", "127.0.0.1")
sc = SparkContext(conf=conf)

lines = sc.textFile(sys.argv[3], 1)



# TODO

# Count map
def count_map(value):

    # tokenize
    tokenized = tokenizer(value, delimiters)
    result = []
    for token in tokenized:
        lowered_string = token.lower()
        if lowered_string not in stop_words:
            result.append(tuple((lowered_string, 1)))

    return result

words_map = lines.flatMap(count_map)


# Count reduce
words_reduce = words_map.reduceByKey(lambda a, b: a + b)


# CAUTION!!!
# top_data = words_reduce.collect()
# Better not directly collect here since this is very very slow and will probably TLE!


# Sort by count descending
top_map = words_reduce.sortBy(lambda a: a[1], ascending=False)

result_collect = top_map.take(10)
result_collect.sort()

outputFile = open(sys.argv[4], "w")

# TODO
for data in result_collect:
    outputFile.write("%s\t%s\n" % (data[0], str(data[1])))

outputFile.close()

# write results to output file. Foramt for each line: (line +"\n")

sc.stop()
