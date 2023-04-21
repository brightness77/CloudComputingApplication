from pyspark import *
from pyspark.sql import SparkSession
from graphframes import *

sc = SparkContext()
spark = SparkSession.builder.appName('fun').getOrCreate()


def get_connected_components(graphframe):
    # TODO:
    # get_connected_components is given a graphframe that represents the given graph
    # It returns a list of lists of ids.
    # For example, [[a_1, a_2, ...], [b_1, b_2, ...], ...]
    # then a_1, a_2, ..., a_n lie in the same component,
    # b_1, b2, ..., b_m lie in the same component, etc

    cc = graphframe.connectedComponents()

    cc_list = cc.collect()

    result_array = []
    cur_comp_id = -1
    for row in cc_list:
        comp_id = row['component']
        vertex_id = row['id']

        if comp_id != cur_comp_id:
            cur_comp_id = comp_id
            result_array.append([])

        result_array[-1].append(vertex_id)

    return result_array

if __name__ == "__main__":
    vertex_list = []
    edge_list = []
    with open('dataset/graph.data') as f:  # Do not modify
        for line in f:
            line_parsed = line.strip().split(' ')
            src = line_parsed[0]  # TODO: Parse src from line
            dst_list = line_parsed[1:]  # TODO: Parse dst_list from line
            vertex_list.append((src,))
            edge_list += [(src, dst) for dst in dst_list]

    vertices = spark.createDataFrame(vertex_list, ['id', ])  # TODO: Create vertices dataframe
    edges = spark.createDataFrame(edge_list, ['src', 'dst', ])  # TODO: Create edges dataframe

    g = GraphFrame(vertices, edges)
    sc.setCheckpointDir("/tmp/connected-components")

    result = get_connected_components(g)
    for line in result:
        print(' '.join(line))
