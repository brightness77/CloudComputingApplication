import csv

LAX = 0
SNA = 0
ALL = 0

with open('query1_data.csv') as csvfile:
    csv_reader = csv.reader(csvfile, delimiter=',')
    for row in csv_reader:
        ALL += 1
        if row[2] == 'LAX':
            LAX += 1
        elif row[2] == 'SNA':
            SNA += 1


print('ALL: ' + str(ALL))
print('LAX: ' + str(LAX))
print('SNA: ' + str(SNA))
