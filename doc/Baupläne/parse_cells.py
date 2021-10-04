import os, csv, json, re


fp_csv_folder = r"csv/"


def generateJson(csv_reader, building, floor):
    walkable_cells = []

    y = 0
    for row in csv_reader:
        for x in range(len(row)):

            #walkable cell    
            if row[x] == 'w':
                cell = { "xCoordinate": x, "yCoordinate": y, "walkable": True }
                walkable_cells.append(cell)

            #stairs
            elif row[x] == 's':
                connected_cell = { "building": building, "floor": floor,
                                        "xCoordinate": x, "yCoordinate": y,
                                        "walkable": True
                                }
                if (x,y) in stairs:
                    stairs[(x,y)].append(connected_cell)
                else:
                    stairs[(x,y)] = [connected_cell]

            #elevator
            elif row[x] == 'e':
                connected_cell = { "building": building, "floor": floor,
                                    "xCoordinate": x, "yCoordinate": y,
                                    "walkable": True
                                    }
                if (x,y) in elevators:
                    elevators[(x,y)].append(connected_cell)
                else:
                    elevators[(x,y)] = [connected_cell]

            #room
            elif re.match('\d+', row[x]):
                roomnumber = row[x]
                room = { "roomNumber": roomnumber, "building": building, "floor": floor,
            	            "persons": [],
                            "qrCode": building + floor + roomnumber,
                            "xCoordinate": x, "yCoordinate": y,
                            "walkable": True 
                        }
                rooms.append(room)
                
            #error
            elif row[x] != '':
                print("Fehler bei", building, 'floor', floor, 'Zelle', x,y)

        y = y + 1    
    
    #save walkable cells to json for this building and floor
    if (building == '04') | (building == '05'):
        with open('json/building_'+ building +'_floor_'+ floor + '.json', 'w') as f:
            json.dump(walkable_cells, f)
    else:
        with open('json/tmp/building_'+ building +'_floor_'+ floor + '.json', 'w') as f:
            json.dump(walkable_cells, f)


#initialize
elevators = {}
stairs = {}
rooms = []

#read in csv files
for file_name in os.listdir(fp_csv_folder):
    if re.match(r'cellplan_0[12345]-((0[01234])|(ug))\.CSV',file_name): #check correct file name pattern
        #get building and floor
        building = file_name[9:11]
        floor = file_name[12:14]

        with open(fp_csv_folder + str(file_name)) as file:
            floorplan_csv = csv.reader(file, delimiter=';')
            generateJson(floorplan_csv, building, floor);

#save rooms.json
with open('json/rooms.json', 'w') as f:
    json.dump(rooms, f)

#save all floorconnections
floorconnections = [{"type": "staircase", "connectedCells": value} for key, value in stairs.items()] + [{"type": "elevator", "connectedCells": value} for key, value in elevators.items()]
with open('json/floorconnections.json', 'w') as f:
    json.dump(floorconnections, f)


#merge building 3, 2 and 1
for i in ['ug', '00', '01', '02', '03', '04']:
    with open('json/tmp/building_03_floor_' + i + '.json', 'r') as f:
        json_building03 = json.load(f)
    with open('json/tmp/building_02_floor_' + i + '.json', 'r') as f:
        json_building02 = json.load(f)
    with open('json/tmp/building_01_floor_' + i + '.json', 'r') as f:
        json_building01 = json.load(f)
    
    json_building321 = json_building03 + json_building02 + json_building03

    with open('json/building_03_02_01_floor_'+i+'.json', 'w') as f:
        json.dump(json_building321, f)

        