#!/usr/bin/env python

#Execution Command: spark-submit PopularityLeagueSpark.py dataset/links/ dataset/league.txt
import sys
from pyspark import SparkConf, SparkContext

conf = SparkConf().setMaster("local").setAppName("PopularityLeague")
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


# League
leagueIds = sc.textFile(sys.argv[2], 1)
league_id_list = leagueIds.collect()

#TODO

# Filter out links not in map
league_link = link_reduce.filter(lambda a: str(a[0]) in league_id_list)

# Get the data
league_list = league_link.collect()

# O(n ^ 2) to assign rank
ranks = []
for league1 in league_list:
    cur_rank = 0
    for league2 in league_list:
        cur_rank += (1 if league1[1] > league2[1] else 0)

    ranks.append((league1[0], cur_rank))

# Sort alphabetically
ranks.sort(key=str)

output = open(sys.argv[3], "w")

#TODO
#write results to output file. Foramt for each line: (key + \t + value +"\n")

for data in ranks:
    output.write("%s\t%s\n" % (data[0], data[1]))

output.close()

sc.stop()

