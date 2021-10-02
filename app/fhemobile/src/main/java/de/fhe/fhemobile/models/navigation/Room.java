package de.fhe.fhemobile.models.navigation;

import static de.fhe.fhemobile.utils.Define.Navigation.COSTS_ROOM;

import java.util.ArrayList;

public class Room extends Cell{

    //Constants
    private static final String TAG = "Room"; //$NON-NLS

    //Variables
    private String roomNumber;
    private ArrayList<String> persons;
    private String qrCode;

    //Constructors
    public Room(){
        super();
    }

    public Room(String roomNumber, String building, String floor, String qrCode,
                ArrayList<String> persons, int x, int y, boolean walkable) {
        super(x, y, building, floor, walkable, COSTS_ROOM);
        this.roomNumber = roomNumber;
        this.persons = persons;
        this.qrCode = qrCode;
    }

    //Getter
    public String getRoomNumber() {
        return roomNumber;
    }

    public ArrayList<String> getPersons() {
        return persons;
    }

    public String getQRCode() {
        return qrCode;
    }

    public String getRoomName() {
        String roomName;
        roomName = getBuilding() + "." + getFloor() + "." + roomNumber;

        return roomName;
    }

}
