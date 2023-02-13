
def mainFunction(event):
    # db initialize

    graphStr = event['graph']

    # parse string & build edge list
    split1 = graphStr.split(',')
    edgeList = []
    for str1 in split1:
        toAdd = str1.split('->')
        edgeList.append(toAdd)

    # build adj list
    adjList = {}
    for edge in edgeList:
        if edge[0] not in adjList:
            adjList[edge[0]] = []

        adjList[edge[0]].append(edge[1])

    # do bfs starting with every node
    for departure in adjList:
        taskQ = []
        taskQ.append(departure)

        visited = {departure: True, }

        curDistance = 0

        while len(taskQ) > 0:
            curSize = len(taskQ)
            for i in range(curSize):
                destination = taskQ.pop(0)

                # distance
                if curDistance != 0:
                    print("Adding " + departure + " to " + destination + " with distance of " + str(curDistance))

                # children
                if destination not in adjList:
                    continue

                for next in adjList[destination]:
                    if next in visited:
                        continue

                    visited[next] = True
                    taskQ.append(next)

            curDistance += 1


event = {
    "graph": "Chicago->Urbana,Urbana->Springfield,Chicago->Lafayette",
}

mainFunction(event)