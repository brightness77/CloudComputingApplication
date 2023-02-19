#!/usr/bin/env python
import sys
from pyspark import SparkConf, SparkContext

conf = SparkConf().setMaster("local").setAppName("TopPopularLinks")
conf.set("spark.driver.bindAddress", "127.0.0.1")
sc = SparkContext(conf=conf)

lines = sc.textFile(sys.argv[1], 1) 

#TODO

# Link Map
def link_map_func(value):
    #parse each line
    index_of_maohao = value.index(':')

    source = int(value[:index_of_maohao])
    targets = value[index_of_maohao + 2:].split()

    result = []

    for target in targets:
        target_int = int(target)
        result.append((target_int, 1))

    # append self
    result.append((source, 0))

    return result

link_map = lines.flatMap(link_map_func)


# Link Reduce
link_reduce = link_map.reduceByKey(lambda a, b: a + b)


# Sort Descending by popularity
sorted_populars = link_reduce.sortBy(lambda a: a[1], ascending=False)

result_collect = sorted_populars.take(10)

# Sort key by alphabetical order
result_collect.sort(key=str)

output = open(sys.argv[2], "w")

#TODO
#write results to output file. Foramt for each line: (key + \t + value +"\n")

for data in result_collect:
    output.write("%s\t%s\n" % (data[0], data[1]))

output.close()

sc.stop()

