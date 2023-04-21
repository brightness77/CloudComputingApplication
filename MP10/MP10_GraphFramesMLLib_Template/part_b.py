from pyspark import SparkContext
from pyspark.sql import SQLContext
from pyspark.ml.clustering import KMeans
from pyspark.ml.linalg import Vectors
import pyspark.sql.functions as F

############################################
#### PLEASE USE THE GIVEN PARAMETERS     ###
#### FOR TRAINING YOUR KMEANS CLUSTERING ###
#### MODEL                               ###
############################################

NUM_CLUSTERS = 4
SEED = 0
MAX_ITERATIONS = 100
INITIALIZATION_MODE = "random"

sc = SparkContext()
sqlContext = SQLContext(sc)


def get_clusters(df, num_clusters, max_iterations, initialization_mode,
                 seed):
    # TODO:
    # Use the given data and the cluster pparameters to train a K-Means model
    # Find the cluster id corresponding to data point (a car)
    # Return a list of lists of the titles which belong to the same cluster
    # For example, if the output is [["Mercedes", "Audi"], ["Honda", "Hyundai"]]
    # Then "Mercedes" and "Audi" should have the same cluster id, and "Honda" and
    # "Hyundai" should have the same cluster id

    kMeans = KMeans()
    kMeans.setK(num_clusters)
    kMeans.setMaxIter(max_iterations)
    kMeans.setInitMode(initialization_mode)
    kMeans.setSeed(seed)

    model = kMeans.fit(df)

    predictions = model.transform(df)

    pred_list = predictions.collect()

    result_dict = {}
    for row in pred_list:
        name = row['name']
        predict_id = row['prediction']

        if predict_id not in result_dict:
            result_dict[predict_id] = []

        result_dict[predict_id].append(name)

    return list(result_dict.values())


def parse_line(line):
    # TODO: Parse data from line into an RDD
    # Hint: Look at the data format and columns required by the KMeans fit and
    # transform functions
    line_data = line.strip().split(',')
    to_return = [Vectors.dense(line_data[1:]), line_data[0]]
    return to_return


if __name__ == "__main__":
    f = sc.textFile("dataset/cars.data")

    line_data = f.map(parse_line)

    # TODO: Convert RDD into a dataframe
    df = line_data.toDF(['features', 'name', ])

    clusters = get_clusters(df, NUM_CLUSTERS, MAX_ITERATIONS,
                            INITIALIZATION_MODE, SEED)
    for cluster in clusters:
        print(','.join(cluster))
