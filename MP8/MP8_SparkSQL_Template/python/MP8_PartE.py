from pyspark import SparkContext, SQLContext
from pyspark.sql.types import StructType
from pyspark.sql.types import StructField
from pyspark.sql.types import StringType, IntegerType

sc = SparkContext()
sqlContext = SQLContext(sc)

####
# 1. Setup (10 points): Download the gbook file and write a function to load it in an RDD & DataFrame
####

# RDD API
# Columns:
# 0: place (string), 1: count1 (int), 2: count2 (int), 3: count3 (int)

# schema = StructType([
#     StructField("word", StringType()),
#     StructField("count1", IntegerType()),
#     StructField("count2", StringType()),
#     StructField("count3", StringType()),
# ])
#
# df = sqlContext.read.format("csv").option("delimiter", "\t").schema(schema).load("gbooks")


lines = sc.textFile('gbooks').map(lambda line: line.split("\t"))
df = sqlContext.createDataFrame(lines, ['word', 'count1', 'count2', 'count3'])
df = df.withColumn('count1', df['count1'].cast('int'))



# Spark SQL - DataFrame API


####
# 5. Joining (10 points): The following program construct a new dataframe out of 'df' with a much smaller size.
####

# df = df.limit(1000)

df2 = df.select("word", "count1").distinct().limit(100)
df2.createOrReplaceTempView('gbooks2')

# Now we are going to perform a JOIN operation on 'df2'. Do a self-join on 'df2' in lines with the same #'count1' values and see how many lines this JOIN could produce. Answer this question via DataFrame API and #Spark SQL API
# Spark SQL API

query = sqlContext.sql("SELECT * " +
                "FROM gbooks2 g1 " +
                "INNER JOIN gbooks2 g2 " +
                "ON g1.count1 = g2.count1 ")

# query = df2.alias('df2_1').join(df2.alias("df2_2"), 'count1')


print(query.count())

# output: 210

