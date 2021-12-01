import os, csv, json, re


fp_csv_folder = r"csv/"


def generateJson(csv_reader, building, floor):
    walkable_cells = []

    y = 0
    for row in csv_reader:
        for x in range(len(row)):

            floornumber = floor
            if floornumber == "ug1":
                floornumber = "-1"
            elif floornumber == "ug2":
                floornumber = "-2"
            elif floornumber == "3z":
                floornumber = "03"

            #walkable cell    
            if row[x] == 'w':
                cell = { "xCoordinate": x, "yCoordinate": y, "walkable": True }
                walkable_cells.append(cell)

            #stairs
            elif row[x] == 's':
                connected_cell = { "building": building, "floor": floornumber,
                                        "xCoordinate": x, "yCoordinate": y,
                                        "walkable": True
                                }
                if (x,y) in stairs:
                    stairs[(x,y)].append(connected_cell)
                else:
                    stairs[(x,y)] = [connected_cell]

            #elevator
            elif row[x] == 'e':
                connected_cell = { "building": building, "floor": floornumber,
                                    "xCoordinate": x, "yCoordinate": y,
                                    "walkable": True
                                    }
                if (x,y) in elevators:
                    elevators[(x,y)].append(connected_cell)
                else:
                    elevators[(x,y)] = [connected_cell]

            #bridge - ignore to simplify navigation
            #elif row[x] == "bridge":

                # bridge_cell = { "building": building, "floor": floornumber,
                #                     "xCoordinate": x, "yCoordinate": y,
                #                     "walkable": True
                #                     }
                # bridgeCells.append(bridge_cell)
                
                
            #room
            elif re.match('\d+', row[x]):
                roomnumber = row[x]
                room = { "roomNumber": roomnumber, "building": building, "floor": floornumber,
            	            "persons": [],
                            "qrCode": building + floor + roomnumber,
                            "xCoordinate": x, "yCoordinate": y,
                            "walkable": True 
                        }
                rooms.append(room)

            #exit/entry
            elif row[x] == "exit":
                exitTo = []
                #use exit when changing buildings
                if(building == "01" and x == 40 and y == 19): exitTo = ["05"]
                elif(building == "03" and x == 13 and y == 2): exitTo = ["04"]
                elif(building == "04" and x == 23 and y == 19): exitTo = ["03", "05"]
                elif(building == "05" and x == 24 and y == 5): exitTo == ["03", "04"]

                entryFrom = []
                #use this entry when changing buildings #only one specific entry for each building due to Corona
                if(building == "04" and x == 9 and y == 20): entryFrom = ["03", "05"] 
                elif(building == "02" and x == 33 and y == 10): entryFrom = ["04", "05"]
                elif(building == "05" and x == 24 and y == 5): entryFrom = ["03", "04"]

                exit = {"building": building, "floor": floornumber,
                        "xCoordinate": x, "yCoordinate": y,
                        "exitTo": exitTo,
                        "entryFrom": entryFrom}
                exits.append(exit)

            #error
            elif row[x] != '':
                print("Fehler bei", building, 'floor', floor, 'Zelle', x,y)

        y = y + 1    
    
    #save walkable cells to json for this building and floor
    if (building == '04') or (building == '05'):
        with open('json/building_'+ building +'_floor_'+ floor + '.json', 'w') as f:
            json.dump(walkable_cells, f)
    #write json in tmp folder to merge building 3, 2 and 1
    else:
        with open('json/tmp/building_'+ building +'_floor_'+ floor + '.json', 'w') as f:
            json.dump(walkable_cells, f)


#initialize
elevators = {}
stairs = {}
rooms = []
bridgeCells = []
exits = []


#read in csv files
for file_name in os.listdir(fp_csv_folder):
    if re.match(r'cellplan_0[12345]-((0[01234])|ug1|ug2|3Z)\.CSV', file_name): #check correct file name pattern
        #todo check if ug_level 1 and ug_level 2 are read in
        print(file_name)
        #get building and floor
        building = file_name[9:11] #todo: Ermittlung des Floors für 05.03 Ebene 1 und EBene 2 überprüfen
        floor = file_name[12:-4]
        

        with open(fp_csv_folder + str(file_name)) as file:
            floorplan_csv = csv.reader(file, delimiter=';')
            floorname = floor
            generateJson(floorplan_csv, building, floor);

#save rooms.json
with open('json/rooms.json', 'w') as f:
    json.dump(rooms, f)

#save all floorconnections
floorconnections = [{"type": "staircase", "connectedCells": value} for key, value in stairs.items()] + \
    [{"type": "elevator", "connectedCells": value} for key, value in elevators.items()] #+ \
    #[{"type": "bridge", "connectedCells": bridgeCells}]
with open('json/floorconnections.json', 'w') as f:
    json.dump(floorconnections, f)

#save exits
with open('json/exits.json', 'w') as f:
    json.dump(exits, f)

#merge building 3, 2 and 1
for i in ['ug1', '00', '01', '02', '03', '04']:
    with open('json/tmp/building_03_floor_' + i + '.json', 'r') as f:
        json_building03 = json.load(f)
    with open('json/tmp/building_02_floor_' + i + '.json', 'r') as f:
        json_building02 = json.load(f)
    with open('json/tmp/building_01_floor_' + i + '.json', 'r') as f:
        json_building01 = json.load(f)
    
    json_building321 = json_building03 + json_building02 + json_building01

    with open('json/building_03_02_01_floor_'+ i +'.json', 'w') as f:
        json.dump(json_building321, f)

        