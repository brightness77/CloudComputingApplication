
maps = {}

with open("test-titles") as f:
    print(f)

    for line in f:
        if line not in maps:
            maps[line] = 0
        maps[line] += 1
        # print(len(line))

print("About to print maps")

print(maps)

