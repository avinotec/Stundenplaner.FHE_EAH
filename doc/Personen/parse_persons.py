import csv, json, io

persons = []

with open('Personen-2020-05-18.CSV', 'r') as file:
    persons_csv = csv.reader(file, delimiter=';')

    for person in persons_csv:
        # person consists of room number, room name, person name, belegt bis
        new_person = {'name': person[2], 'room': person[0]}
        persons.append(new_person)

with io.open('persons.json', 'w', encoding='utf8') as f:
    json.dump(persons, f, ensure_ascii = False)
