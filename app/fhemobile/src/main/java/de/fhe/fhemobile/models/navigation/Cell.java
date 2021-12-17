/*
 *  Copyright (c) 2019-2021 Ernst-Abbe-Hochschule Jena
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package de.fhe.fhemobile.models.navigation;

import static de.fhe.fhemobile.utils.Define.Navigation.COSTS_CELL;
import static de.fhe.fhemobile.utils.navigation.NavigationUtils.floorStringToInt;

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
    //boolean ob die Zelle begehbar ist
    private boolean walkable;
    //Eindeutigen Key für Zelle
    private String key;

    //Constructors
    public Cell() {
    }

    public Cell(final int xCoordinate, final int yCoordinate, final String building, final String floor, final boolean walkable) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.building = new Building(building);
        this.floor = floor;
        this.walkable = walkable;
        this.costPassingCell = COSTS_CELL;
        this.key = building + getFloorString() + xCoordinate + yCoordinate;
    }

    public Cell(final int xCoordinate, final int yCoordinate, final Complex complex, final String floor, final boolean walkable) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.building = new Building(complex);
        this.floor = floor;
        this.walkable = walkable;
        this.costPassingCell = COSTS_CELL;
        this.key = building + getFloorString() + xCoordinate + yCoordinate;
    }

    /**
     * Overloaded constructor for cell type {@link Room} and {@link Exit}
     * @param xCoordinate
     * @param yCoordinate
     * @param building
     * @param floor
     * @param walkable
     */
    public Cell(final int xCoordinate, final int yCoordinate, final String building, final String floor,
                final boolean walkable, final int costPassingCell) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.building = new Building(building);
        this.floor = floor;
        this.walkable = walkable;
        this.costPassingCell = costPassingCell;
        this.key = this.building.getBuilding() + getFloorString() +"-" + xCoordinate +"-"+ yCoordinate;
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

    public int getFloorInt() {

        return floorStringToInt(floor);
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
    public void setCostPassingCell(final int costPassingCell) {
        this.costPassingCell = costPassingCell;
    }

    public void setCostsPathToCell(final int costsPathToCell) {
        this.costsPathToCell =+costsPathToCell;
    }

    public void setBuilding(final String building) {
        this.building = new Building(building);
    }

    public void setFloor(final String floor) {
        this.floor = floor;
    }

    public void setXCoordinate(final int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public void setYCoordinate(final int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public void setParentCell(final Cell parentCell) {
        this.parentCell = parentCell;
    }

    /**
     * Compose a key for the cell for use in a hash map
     * @return unique key
     */
    public String getKey(){
        return this.key;
    }

    public void setWalkability(final boolean walkable) {
        this.walkable = walkable;
    }
}
