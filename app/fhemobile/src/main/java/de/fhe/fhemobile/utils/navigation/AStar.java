/*
 *  Copyright (c) 2020-2021 Ernst-Abbe-Hochschule Jena
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

package de.fhe.fhemobile.utils.navigation;

import static de.fhe.fhemobile.utils.Define.Navigation.cellgrid_height;
import static de.fhe.fhemobile.utils.Define.Navigation.cellgrid_width;
import static de.fhe.fhemobile.utils.navigation.NavigationUtils.checkExceptCaseBuild321FloorUG;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.PriorityQueue;

import de.fhe.fhemobile.vos.navigation.BuildingExitVo;
import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.vos.navigation.Complex;
import de.fhe.fhemobile.vos.navigation.FloorConnectionVo;
import de.fhe.fhemobile.models.navigation.FloorConnectionCell;

/**
 * Class for route calculation at a single floor using the A* algorithm;
 *
 * Details of the A* algorithm -> see pseudocode at the english wikipedia page
 */
public class AStar {

    private static final String TAG = "AStar"; //$NON-NLS

    //Variables
    /**
     * The set of nodes discovered but not closed by the AStar Algorithm,
     * sorted by costsPathToCell because the algorithm should always consider the cheapest path first
     */
    private PriorityQueue<Cell> openCells;

    /**
     * The set of nodes that have been fully looked at/processed by the AStar algorithm
     * (that is when a cell had been added to openCells, checked a second time and then removed from the openCells Queue).
     * This information is needed to avoid running in circles (I guess).
     * Closed Cells are only stored as string/key (building+floor+x+y).
     */
    private ArrayList<String> closedCells;

    private final Cell startCell;
    private final Cell destCell;

    /**
     * Coordinate system of all walkable and non-walkable cells for each building and floor.
     *
     * For each building/complex ({@link Complex} used as key),
     * the Map contains a grid of cells (Cell Array) for each floor (floor int used as key) in that complex.
     * It is generated from the corresponding json files of each building and floor (building_xx_floor_xx_.json in eah\assets).
     */
    private final HashMap<Complex, HashMap<Integer, Cell[][]>> floorGrids;
    /**
     * List of all {@link FloorConnectionVo}'s, each storing the particular set of connected cells
     */
    private final ArrayList<FloorConnectionVo> floorConnections;


    /**
     * Constructs an AStar Object
     * @param startCell The cell the algorithm should start with
     * @param endCell The cell the algorithm should find a way to
     * @param floorCellGrids The required floor plans as grids, sorted by complex and floor
     * @param floorConnections The list of all floor connections in all buildings and floors
     */
    public AStar(final Cell startCell, final Cell endCell,
                 final HashMap<Complex, HashMap<Integer, Cell[][]>> floorCellGrids,
                 final ArrayList<FloorConnectionVo> floorConnections) {
        this.startCell = startCell;
        this.destCell = endCell;
        this.floorGrids = floorCellGrids;
        this.floorConnections = floorConnections;
    }


    /**
     * Call the A* algorithm to calculate a route from start to destination using the shortest path,
     * then get the list of all cells to walk by backtracing.
     * @return Map of cells to walk, sorted for displaying
     */
    public final LinkedHashMap<BuildingFloorKey, ArrayList<Cell>> getCellsToWalk() {

        final LinkedHashMap<BuildingFloorKey, ArrayList<Cell>> cellsToWalk = new LinkedHashMap<>();

        try {
            //Set priority queue with comparator (prioritize cells based on their costsPathToCell)
            openCells = new PriorityQueue<>(16, new Comparator<Cell>() {

                @Override
                public int compare(final Cell cellOne, final Cell cellTwo) {
                    return Integer.compare(cellOne.getCostsPathToCell(), cellTwo.getCostsPathToCell());
                }
            });

            //initialize start cell
            startCell.setCostsPathToCell(-1);
            //add startCell to priority queue (cells the A* algorithm has discovered)
            openCells.add(startCell);
            //initialize set of closed cells (cells the A* algorithm has processed and removed from openCells)
            closedCells = new ArrayList<>();
            //set costs of end cell to 0 to force algorithm to "enter" the destination room when reaching a neighboring cell of the room cell
            Objects.requireNonNull(floorGrids.get(destCell.getComplex()).get(destCell.getFloorInt()))
                    [destCell.getXCoordinate()][destCell.getYCoordinate()]
                    .setCostPassingCell(0);
            //set start cell costs to 0
            Objects.requireNonNull(floorGrids.get(startCell.getComplex()).get(startCell.getFloorInt()))
                    [startCell.getXCoordinate()][(startCell.getYCoordinate())].setCostPassingCell(0);
            //run A* algorithm
            performAStarAlgorithm();

            //backtracing to reconstruct navigation path
            //get end cell as start cell for backtracing
            Cell currentCell = Objects.requireNonNull(floorGrids.get(destCell.getComplex()).get(destCell.getFloorInt()))
                    [destCell.getXCoordinate()][destCell.getYCoordinate()];

            final BuildingFloorKey buildingFloorKeyCurrentCell = new BuildingFloorKey(currentCell);
            // if destination is exit then add it as cellToWalk
            if(currentCell instanceof BuildingExitVo){
                //TODO analyzer sagt, die Bedingung wäre IMMER false ???
                //Antwort Nadja: cellsToWalk wird im Aufruf performAStarAlgorithm weiter oben gefüllt, das erkennt der vermutlich nicht
                if(cellsToWalk.containsKey(buildingFloorKeyCurrentCell)){
                    cellsToWalk.get(buildingFloorKeyCurrentCell).add(currentCell);
                }
                else{
                    final ArrayList<Cell> list = new ArrayList<>();
                    list.add(currentCell);
                    cellsToWalk.put(buildingFloorKeyCurrentCell, list);
                }
            }
            //do not add destination (and start cell) to walkable cells (bc those get displayed with path icon)
            currentCell = currentCell.getParentCell();

            //reconstruct path
            while (currentCell.getParentCell() != null) {

                final BuildingFloorKey buildingFloorKeyNewCurrentCell = new BuildingFloorKey(currentCell);

                if(cellsToWalk.containsKey( buildingFloorKeyNewCurrentCell )){
                    cellsToWalk.get(buildingFloorKeyNewCurrentCell).add(currentCell);
                }
                else{
                    final ArrayList<Cell> list = new ArrayList<>();
                    list.add(currentCell);
                    cellsToWalk.put(buildingFloorKeyNewCurrentCell, list);
                }

                currentCell = currentCell.getParentCell();
            }
            // if start is exit then add it as cellToWalk
            if(currentCell.getParentCell() == null && currentCell instanceof BuildingExitVo){

                final BuildingFloorKey buildingFloorKey = new BuildingFloorKey(currentCell);

                if(cellsToWalk.containsKey( buildingFloorKey )){
                    cellsToWalk.get( buildingFloorKey ).add(currentCell);
                }
                else{
                    final ArrayList<Cell> list = new ArrayList<>();
                    list.add(currentCell);
                    cellsToWalk.put(buildingFloorKey, list);
                }
            }

        } catch (final Exception e) {
            Log.e(TAG, "error calculating route ", e);
        }
        return cellsToWalk;
    }

    /**
     * Perform A* algorithm.
     *
     * Only costs of path' and parent cells are computed, no backtracing of cells to walk.
     */
    private void performAStarAlgorithm() {

        // while there are cells to walk through
        while (!openCells.isEmpty()) {

            //get the cell that's path to it is the cheapest and remove it from the priority queue
            final Cell currentCell = openCells.poll();

            if (currentCell != null && currentCell.getWalkability()) {
                //add currentCell to closedCells
                closedCells.add(currentCell.getKey());

                //current cell is not destination
                if (!currentCell.equals(destCell)) {

                    //Überprüft die Kosten der direkt angrenzenden Nachbarzellen der aktuellen Zelle
                    // (diagonale Nachbarn werden nicht betrachtet)
                    // und setzt ggf. die CostsPathToCell dieser Zellen


                    //if currentCell is floorconnection,
                    // then also consider all cells connected by the floorconnection as neighbors
                    if(currentCell instanceof FloorConnectionCell)

                        //if start and destination are located at the same floor,
                        // floorconnections are not needed and thus should not be considered to save runtime
                        //attention: in the exceptional case of a route between building 3 and 1 at floor -1,
                        // floorconnections are needed due to lack of a connection of building 3 and 1 at floor -1
                        if (startCell.getComplex() != destCell.getComplex()
                                || startCell.getFloorInt() != destCell.getFloorInt()
                                || checkExceptCaseBuild321FloorUG(startCell, destCell) ) {

                            final ArrayList<FloorConnectionCell> connectedCells = findConnectedCells((FloorConnectionCell) currentCell);
                            for (final FloorConnectionCell neighborCell : connectedCells) {
                                //get corresponding cell in floorGrid
                                final Cell neighborInGrid = floorGrids.get(neighborCell.getComplex()).get(neighborCell.getFloorInt())
                                        [neighborCell.getXCoordinate()][neighborCell.getYCoordinate()];
                                updateParentAndPathToCellCosts(neighborInGrid, currentCell);
                            }
                        }

                    final Cell[][] currentFloorGrid = Objects.requireNonNull(
                            Objects.requireNonNull(floorGrids.get(currentCell.getComplex())).get(currentCell.getFloorInt()));

                    //if-conditions ensure that current cell is not at edge of grid (so the left/right/... neighbor cell is valid)
                    //left neighbor
                    if (currentCell.getXCoordinate() > 0) {
                        final Cell neighbouringCell =
                                currentFloorGrid[currentCell.getXCoordinate() - 1][currentCell.getYCoordinate()];
                        updateParentAndPathToCellCosts(neighbouringCell, currentCell);
                    }

                    //right neighbor
                    if (currentCell.getXCoordinate() < cellgrid_width) {
                        //check that cell is not at edge of grid
                        final Cell neighbouringCell = currentFloorGrid[currentCell.getXCoordinate() + 1][currentCell.getYCoordinate()];
                        updateParentAndPathToCellCosts(neighbouringCell, currentCell);
                    }

                    //neighbor above
                    if (currentCell.getYCoordinate() > 0) {
                        //check that cell is not at edge of grid
                        final Cell neighbouringCell = currentFloorGrid[currentCell.getXCoordinate()][currentCell.getYCoordinate() - 1];
                        updateParentAndPathToCellCosts(neighbouringCell, currentCell);
                    }

                    //neighbor below
                    if (currentCell.getYCoordinate() < cellgrid_height) {
                        //check that cell is not at edge of grid
                        final Cell neighbouringCell = currentFloorGrid[currentCell.getXCoordinate()][currentCell.getYCoordinate() + 1];
                        updateParentAndPathToCellCosts(neighbouringCell, currentCell);
                    }
                }
                else return;
            }
        }
    }

    /**
     * Update parent cell for the particular cell and the costs for the path to it
     * @param cell the cell to update
     * @param parentCell the cell's parent
     */
    private void updateParentAndPathToCellCosts(final Cell cell, final Cell parentCell) {

        final int parentCellPathCosts = parentCell.getCostsPathToCell();
        //note: CostsPathToCell is initialized with -1

        //if CostsPathToCell of parent has not been set yet (just initialized with -1),
        // then set it to the costs for passing the "cell",
        // otherwise add those costs to the previous costsPathToCell of the parent cell
        final int costsPathToCell = parentCellPathCosts >= 0 ?
                cell.getCostPassingCell() + parentCellPathCosts : cell.getCostPassingCell();

        // cell has already status "open"
        final boolean isInOpenQueue = openCells.contains(cell);

        //check if cell is walkable and does not have status "closed" (not contained in closedCells)
        if (cell.getWalkability() && !closedCells.contains(cell.getKey())) {

            // cell has status "unknown" or this path to the cell is cheaper than the path currently known
            if (!isInOpenQueue || costsPathToCell < cell.getCostsPathToCell()) {

                //update costs and parent cell
                cell.setCostsPathToCell(costsPathToCell);
                cell.setParentCell(parentCell);

                if (!isInOpenQueue) {
                    //update status of cell from unknown to "open"
                    openCells.add(cell);
                }
            }
        }
    }

    /**
     * Find the {@link FloorConnectionVo} object the {@link FloorConnectionCell} belongs to
     * @param cell The {@link FloorConnectionCell} of a {@link FloorConnectionVo} object
     * @return List of all {@link FloorConnectionCell}s connected with the specified {@link FloorConnectionCell}
     */
    private ArrayList<FloorConnectionCell> findConnectedCells(final FloorConnectionCell cell){
        for (final FloorConnectionVo fc : floorConnections){
            for(final FloorConnectionCell _cell :  fc.getConnectedCells()){
                if (_cell.getKey().equals(cell.getKey())){
                    return fc.getConnectedCells();
                }
            }
        }
        return null;
    }
}
