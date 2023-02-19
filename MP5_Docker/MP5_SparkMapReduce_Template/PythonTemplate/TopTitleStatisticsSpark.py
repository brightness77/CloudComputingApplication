#!/usr/bin/env python
import sys
from pyspark import SparkConf, SparkContext

conf = SparkConf().setMaster("local").setAppName("TopTitleStatistics")
conf.set("spark.driver.bindAddress", "127.0.0.1")
sc = SparkContext(conf=conf)

lines = sc.textFile(sys.argv[1], 1)

# TODO

data_list = lines.collect()
num_list = [int(data.split('\t')[1]) for data in data_list]

# Initialization
r_sum = 0
r_count = len(num_list)
r_min = num_list[0]
r_max = num_list[0]

for num in num_list:
    r_max = max(r_max, num)
    r_min = min(r_min, num)

    r_sum += num

r_mean = r_sum // r_count
r_variance = 0
for num in num_list:
    r_variance += (r_mean - num) ** 2

r_variance //= r_count

outputFile = open(sys.argv[2], "w")

outputFile.write('Mean\t%s\n' % r_mean)
outputFile.write('Sum\t%s\n' % r_sum)
outputFile.write('Min\t%s\n' % r_min)
outputFile.write('Max\t%s\n' % r_max)
outputFile.write('Var\t%s\n' % r_variance)

outputFile.close()

'''
TODO write your output here
write results to output file. Format
outputFile.write('Mean\t%s\n' % ans1)
outputFile.write('Sum\t%s\n' % ans2)
outputFile.write('Min\t%s\n' % ans3)
outputFile.write('Max\t%s\n' % ans4)
outputFile.write('Var\t%s\n' % ans5)
'''

sc.stop()

