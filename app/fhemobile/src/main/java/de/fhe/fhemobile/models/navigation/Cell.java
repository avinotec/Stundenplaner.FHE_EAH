package de.fhe.fhemobile.models.navigation;

/**
 * Model for a cell unit of the floorplan
 */
public class Cell {

    //Constants
    private static final String TAG = "Cell"; //$NON-NLS

    //Variables
    private int heuristicCost = 0;
    private int finalCost = 0;
    private String building;
    private String floor;
    private int xCoordinate;
    private int yCoordinate;
    private Cell parent;
    private boolean walkable;

    //Constructors
    public Cell() {
    }
    public Cell(int xCoordinate, int yCoordinate, String building, String floor, boolean walkable) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this. building = building;
        this.floor = floor;
        this.walkable = walkable;
    }

    //Getter
    public int getHeuristicCost() {
        return heuristicCost;
    }

    public int getFinalCost() {
        return finalCost;
    }

    public String getBuilding() {
        return building;
    }

    public String getFloor() {
        return floor;
    }

    public int getBuildingAsInteger() {

        int buildingAsInteger = 0;

        switch (building) {
            case "01":
                buildingAsInteger = 1;
                break;
            case "02":
                buildingAsInteger = 2;
                break;
            case "03":
                buildingAsInteger = 3;
                break;
            case "04":
                buildingAsInteger = 4;
                break;
            case "05":
                buildingAsInteger = 5;
                break;
            default:
                break;
        }
        return buildingAsInteger;
    }

    public int getFloorAsInteger() {

        int floorAsInteger = 0;

        switch (floor) {
            case "ug":
                floorAsInteger = -1;
                break;
            case "00":
                floorAsInteger = 0;
                break;
            case "01":
                floorAsInteger = 1;
                break;
            case "02":
                floorAsInteger = 2;
                break;
            case "03":
                floorAsInteger = 3;
                break;
            case "04":
                floorAsInteger = 4;
                break;
            default:
                break;
        }
        return floorAsInteger;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public Cell getParent() {
        return parent;
    }

    public boolean getWalkability() { return walkable; }

    //Setter
    public void setHeuristicCost(int heuristicCost) {
        this.heuristicCost = heuristicCost;
    }

    public void setFinalCost(int finalCost) {
        this.finalCost =+ finalCost;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public void setXCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public void setParent(Cell parent) {
        this.parent = parent;
    }

    public void setWalkability(boolean walkable) {
        this.walkable = walkable;
    }
}
