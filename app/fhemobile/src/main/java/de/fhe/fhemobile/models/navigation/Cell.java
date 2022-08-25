/*
 *  Copyright (c) 2019-2022 Ernst-Abbe-Hochschule Jena
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

import de.fhe.fhemobile.vos.navigation.BuildingExitVo;
import de.fhe.fhemobile.vos.navigation.BuildingVo;
import de.fhe.fhemobile.vos.navigation.Complex;
import de.fhe.fhemobile.vos.navigation.RoomVo;

/**
 * Model for a cell of the navigation route
 */
public class Cell {

    //Constants
    private static final String TAG = Cell.class.getSimpleName();

    //Variables
    private BuildingVo building;
    private String floor;
    private int xCoordinate;
    private int yCoordinate;
    /**
     * Costs for using/passing a cell
     */
    private int costPassingCell;
    /**
     * Costs of the path to this cell plus costs of this cell (costPassingCell)
     */
    private int costsPathToCell = 0;
    /**
     * The parent cell is the calculated {@link Cell} that is used to get to this {@link Cell}.
     * It is needed for backtracing in {@link de.fhe.fhemobile.utils.navigation.AStar}.
     */
    private Cell parentCell;
    /**
     * True, if the cell should be considered by the navigation algorithm.
     */
    private boolean walkable;
    /**
     * Unique key of this {@link Cell}
     */
    private String key;

    //Constructors
    public Cell() {
    }

    /**
     * Construct a {@link Cell}
     * @param xCoordinate The x coordinate of the {@link Cell}
     * @param yCoordinate The y coordinate of the {@link Cell}
     * @param complex The {@link Complex} the {@link Cell} belongs to
     * @param floor The floor the {@link Cell} belongs to
     * @param walkable False, if the cell should not be considered by the navigation algorithm.
     */
    public Cell(final int xCoordinate, final int yCoordinate, final Complex complex, final String floor, final boolean walkable) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.building = new BuildingVo(complex);

        this.floor = floor;
        this.walkable = walkable;
        this.costPassingCell = COSTS_CELL;
        // create key for Cell
        this.key = this.building.getBuilding() + getFloorString() +"-" + xCoordinate +"-"+ yCoordinate;
    }

    /**
     * Overloaded constructor for cell type {@link RoomVo} and {@link BuildingExitVo}
     * @param xCoordinate The x coordinate of the {@link Cell}
     * @param yCoordinate The y coordinate of the {@link Cell}
     * @param building The building the {@link Cell} belongs to
     * @param floor The floor the {@link Cell} belongs to
     * @param walkable True, if the cell can be passed. False, if the cell should not be used by the navigation algorithm.
     */
    public Cell(final int xCoordinate, final int yCoordinate, final String building, final String floor,
                final boolean walkable, final int costPassingCell) {

        this( xCoordinate, yCoordinate, null, floor, walkable );
        this.building = new BuildingVo(building);
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
     * Get the building of this {@link Cell} ("01", "02", "03", "04", "05").
     * Note that building 3, 2 and 1 are considered as different buildings but the same {@link Complex}
     * @return The building name of this cell
     */
    public String getBuilding() {
        return building.getBuilding();
    }

    public String getFloorString() {
        return floor;
    }

    /**
     * Get the {@link Complex} of this {@link Cell}
     * Note that building 3, 2 and 1 are considered as the same {@link Complex}(Building_321).
     * @return The {@link Complex} of this {@link Cell}
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
        this.costsPathToCell = costsPathToCell;
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
     * Get the key for this {@link Cell} for usage in a hash map
     * @return The unique key
     */
    public String getKey(){
        return this.key;
    }

    public void setWalkability(final boolean walkable) {
        this.walkable = walkable;
    }
}
