package de.fhe.fhemobile.models.navigation;

import java.util.ArrayList;

public class Room extends Cell{

    //Constants
    private static final String TAG = "Room"; //$NON-NLS

    //Variables
    private String roomNumber;
    private ArrayList<String> persons;
    private String qrCode;

    //Constructors
    public Room() {
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

    //Setter
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setPersons(ArrayList<String> persons) {
        this.persons = persons;
    }

    public void setQRCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
