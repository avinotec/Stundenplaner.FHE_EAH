package de.fhe.fhemobile.models.navigation;

/**
 * Model for a cell of the navigation route
 */
public class Cell {

    //Constants
    private static final String TAG = "Cell"; //$NON-NLS

    //Variables
    private int costPassingCell = 0; //Kosten für Nutzung/Durquerung einer Zelle
    private int costsPathToCell = 0; // Kosten für den Weg bis zu dieser Zelle + Kosten für aktuelle Zelle
    private String building;
    private String floor;
    private int xCoordinate;
    private int yCoordinate;
    private Cell parentCell; // auf dem Weg davor liegende Zelle (für Rückverfolgung benötigt)
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
    public int getCostPassingCell() {
        return costPassingCell;
    }

    public int getCostsPathToCell() {
        return costsPathToCell;
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

    public Cell getParentCell() {
        return parentCell;
    }

    public boolean getWalkability() { return walkable; }

    //Setter
    public void setCostPassingCell(int costPassingCell) {
        this.costPassingCell = costPassingCell;
    }

    public void setCostsPathToCell(int costsPathToCell) {
        this.costsPathToCell =+costsPathToCell;
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

    public void setParentCell(Cell parentCell) {
        this.parentCell = parentCell;
    }

    public void setWalkability(boolean walkable) {
        this.walkable = walkable;
    }
}
