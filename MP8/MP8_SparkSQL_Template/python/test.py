import os

with open("test_data") as f:
    data = []
    for line in f:
        data.append(line.split("\t"))


distinct_dict = {}
for line in data:
    if line[0] not in distinct_dict:
        distinct_dict[line[0]] = {}

    if line[1] in distinct_dict[line[0]]:
        print("Distinct Test Failed")
        break
    else:
        distinct_dict[line[0]][line[1]] = True

print("Distinct Test Ends")

check_set = {}
for line in data:
    if line[1] not in check_set:
        check_set[line[1]] = 0
    check_set[line[1]] += 1

count = 0
for count1 in check_set:
    count += check_set[count1] * check_set[count1]

print(count)