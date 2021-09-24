import os, csv, json, re


fp_csv_folder = r"csv/"


def generateJson(csv_reader, building, floor):
    walkable_cells = [];

    y = 0
    for row in csv_reader:
        for x in range(len(row)):

            if row[x] == 'w':
                cell = { "xCoordinate": x, "yCoordinate": y, "walkable": True }

                walkable_cells.append(cell)

            elif row[x] == 's':
                print('s')

            elif row[x] == 'e':
                print('elevator')

            elif re.match('\d+', row[x]):
                room = { "roomNumber": "xx", "building": "xx", "floor": "00",
            	            "persons": [],
                            "qrCode": "05ug01",
                            "xCoordinate": x, "yCoordinate": y,
                            "walkable": True 
                        }

                rooms.append(room)
                


            elif row[x] != '':
                print("Fehler")

        y = y + 1    
    
    # save walkable cells to json for this building and floor
    with open('json/building'+building+floor + '.json', 'w') as f:
        json.dump(walkable_cells, f)


#initialize
floorconnections = []
rooms = []

#read in csv files
for file_name in os.listdir(fp_csv_folder):
    with open(fp_csv_folder + str(file_name)) as file:
        floorplan_csv = csv.reader(file, delimiter=';')
        building = '030201'
        floor = 'test'
        generateJson(floorplan_csv, building, floor);

print(rooms)
