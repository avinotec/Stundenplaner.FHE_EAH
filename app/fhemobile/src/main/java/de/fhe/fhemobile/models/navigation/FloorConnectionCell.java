package de.fhe.fhemobile.models.navigation;

import static de.fhe.fhemobile.utils.Define.Navigation.COSTS_FLOORCONNECTION;

public class FloorConnectionCell extends Cell{

    private String type;

    public FloorConnectionCell(int xCoordinate, int yCoordinate, String building, String floor,
                        boolean walkable, String type){
        super(xCoordinate, yCoordinate, building, floor, walkable, COSTS_FLOORCONNECTION);
        this.type = type;
    }

    public String getTypeOfFloorConnection() {
        return type;
    }
}
