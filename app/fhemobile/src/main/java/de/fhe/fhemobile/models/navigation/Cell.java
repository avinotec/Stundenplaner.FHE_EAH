package de.fhe.fhemobile.models.navigation;

import static de.fhe.fhemobile.utils.Define.Navigation.COSTS_CELL;

import de.fhe.fhemobile.utils.navigation.NavigationUtils.Complex;

/**
 * Model for a cell of the navigation route
 */
public class Cell {

    //Constants
    private static final String TAG = "Cell"; //$NON-NLS

    //Variables
    private Building building;
    private String floor;
    private int xCoordinate;
    private int yCoordinate;
    //Kosten für Nutzung/Durchquerung einer Zelle
    private int costPassingCell;
    //Kosten für den Weg bis zu dieser Zelle + Kosten für aktuelle Zelle
    private int costsPathToCell = 0;
    // parent cell auf dem Weg davor liegende Zelle (für Rückverfolgung benötigt)
    private Cell parentCell;
    //Ist die Zelle begehbar?
    private boolean walkable;

    //Constructors
    public Cell() {
    }

    public Cell(int xCoordinate, int yCoordinate, String building, String floor, boolean walkable) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.building = new Building(building);
        this.floor = floor;
        this.walkable = walkable;
        this.costPassingCell = COSTS_CELL;
    }

    /**
     * Overloaded constructor for inheriting cell type {@link FloorConnection}
     * @param costPassingCell
     */
    public Cell(int costPassingCell) {
        this.costPassingCell = costPassingCell;
    }

    /**
     * Overloaded constructor for cell type {@link Room}
     * @param xCoordinate
     * @param yCoordinate
     * @param building
     * @param floor
     * @param walkable
     */
    public Cell(int xCoordinate, int yCoordinate, String building, String floor,
                boolean walkable, int costPassingCell) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.building = new Building(building);
        this.floor = floor;
        this.walkable = walkable;
        this.costPassingCell = costPassingCell;
    }

    //Getter
    public int getCostPassingCell() {
        return costPassingCell;
    }

    public int getCostsPathToCell() {
        return costsPathToCell;
    }

    /**
     * Returns the building of the Cell ("01", "02", "03", "04", "05")
     * Complex 3, 2 and 1 are not considered as the same building
     * @return
     */
    public String getBuilding() {
        return building.getBuilding();
    }

    public String getFloorString() {
        return floor;
    }

    /**
     * Returns an instance of the {@link Complex} enum class
     * Complex 3, 2 and 1 are considered as the same (Building_321)
     * @return the building complex of the cell
     */
    public Complex getComplex() {
        return building.getComplex();
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
        this.building = new Building(building);
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
