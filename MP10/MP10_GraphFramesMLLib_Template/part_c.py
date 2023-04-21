from pyspark import *
from pyspark.sql import SparkSession
from graphframes import *

sc = SparkContext()
spark = SparkSession.builder.appName('fun').getOrCreate()


def get_shortest_distances(graphframe, dst_id):
    # TODO
    # Find shortest distances in the given graphframe to the vertex which has id `dst_id`
    # The result is a dictionary where key is a vertex id and the corresponding value is
    # the distance of this node to vertex `dst_id`.

    sssp = graphframe.shortestPaths(landmarks=[dst_id])

    sssp_collected = sssp.collect()

    cur_return = {}

    for row in sssp_collected:
        cur_id = row['id']
        cur_dis = row['distances']

        if len(cur_dis) == 0:
            cur_return[cur_id] = -1
        else:
            cur_return[cur_id] = cur_dis[dst_id]

    return cur_return


if __name__ == "__main__":
    vertex_list = []
    edge_list = []
    with open('dataset/graph.data') as f:
        for line in f:
            line_parsed = line.strip().split(' ')
            # TODO: Parse line to get vertex id
            src = line_parsed[0]
            # TODO: Parse line to get ids of vertices that src is connected to
            dst_list = line_parsed[1:]
            vertex_list.append((src,))
            edge_list += [(src, dst) for dst in dst_list]

    vertices = spark.createDataFrame(vertex_list, ['id', ])  # TODO: Create dataframe for vertices
    edges = spark.createDataFrame(edge_list, ['src', 'dst', ])  # TODO: Create dataframe for edges

    g = GraphFrame(vertices, edges)
    sc.setCheckpointDir("/tmp/shortest-paths")

    # We want the shortest distance from every vertex to vertex 1
    for k, v in get_shortest_distances(g, '1').items():
        print(k, v)
