import csv
import random
import json

# This script requires the address-points-4326.csv file.
# This can be downloaded here: https://ckan0.cf.opendata.inter.prod-toronto.ca/ne/dataset/address-points-municipal-toronto-one-address-repository

o = open('locations.txt', 'w')
f = open('address-points-4326.csv', 'r')

reader = csv.reader(f)
csv_list = list(reader)

for i in range(0, 50):
    chosen_row = random.choice(csv_list)

    o.write(str(i))
    o.write(',')

    coord_json = json.loads(chosen_row[37].replace("'", '"'))
    coords = coord_json['coordinates'][0]

    o.write(str(coords[1]))
    o.write(',')
    o.write(str(coords[0]))

    o.write('\n')

o.close()
f.close()
