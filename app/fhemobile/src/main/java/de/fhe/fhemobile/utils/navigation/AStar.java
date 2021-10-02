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

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import de.fhe.fhemobile.models.navigation.Cell;

/**
 * Class for route calculation at a single floor using the A* algorithm
 */
class AStar {

    private static final String TAG = "AStar"; //$NON-NLS

    //Variables
    private PriorityQueue<Cell> openCells; //The set of discovered nodes that may need to be (re-)expanded. Sorted by costsPathToCell
    private boolean[][] closedCells; //Zellen im Zustand "fertig"
    private Cell startCell; //Startzelle für den kürzesten Weg auf diesem Stockwerk; kann auch eine Treppe, Aufzug etc sein
    private Cell endCell; //Endzelle für den kürzesten Weg auf diesem Stockwerk; kann auch eine Treppe, Aufzug etc sein
    private Cell[][] floorCellGrid; //Koordinatensystem aus allen begehbaren und nicht-begehbaren Zellen des Stockwerks

    /**
     * Constructs an AStar Object
     * @param startCell
     * @param endCell
     * @param floorCellGrid
     */
    AStar(final Cell startCell, final Cell endCell, final Cell[][] floorCellGrid) {
        this.startCell = startCell;
        this.endCell = endCell;
        this.floorCellGrid = floorCellGrid;
    }

    /**
     * Calculates the cells to walk using the shortest way
     * @return List of cells to walk at the floor
     */
    final ArrayList<Cell> getCellsToWalk() {

        final ArrayList<Cell> navigationCells = new ArrayList<>();

        try {
            //Set priority queue with comparator (prioritize cells based on their costsPathToCell)
            openCells = new PriorityQueue<>(16, new Comparator<Cell>() {

                /**
                 *
                 * @param cellOne
                 * @param cellTwo
                 * @return
                 */
                @Override
                public int compare(final Cell cellOne, final Cell cellTwo) {
                    return Integer.compare(cellOne.getCostsPathToCell(), cellTwo.getCostsPathToCell());
                }
            });

            startCell.setCostsPathToCell(-1); //initialize startcell
            openCells.add(startCell); //add startCell to priority queue (cells with status "open")
            closedCells = new boolean[(int) cellgrid_width][(int) cellgrid_height]; //set size of closed array (cells with status "closed")
            floorCellGrid[endCell.getXCoordinate()][endCell.getYCoordinate()].setCostPassingCell(0); //set costs of endcell to 0
            floorCellGrid[startCell.getXCoordinate()][(startCell.getYCoordinate())].setCostPassingCell(0);
            performAStarAlgorithm(); //run A* algorithm

            //backtracing to reconstruct navigation path
            //get end cell as start cell for backtracing
            Cell currentCell = floorCellGrid[endCell.getXCoordinate()][endCell.getYCoordinate()];

            //reconstruct path
            while (currentCell.getParentCell() != null) {
                navigationCells.add(currentCell);
                currentCell = currentCell.getParentCell();
            }

        } catch (Exception e) {
            Log.e(TAG, "error calculating route ", e);
        }
        return navigationCells;
    }

    /**
     * Performs A* algorithm
     */
    private void performAStarAlgorithm() {
        while (!openCells.isEmpty()) {

            final Cell currentCell = openCells.poll(); //get the cell that's path to it is the cheapest and remove it from the priority queue

            if (currentCell != null && currentCell.getWalkability()) {
                closedCells[currentCell.getXCoordinate()][currentCell.getYCoordinate()] = true;

                if (!currentCell.equals(endCell)) { //current cell is not destination

                    //Überprüft die Kosten der 4 direkt angrenzenden Zellen der aktuellen Zelle (Diagonale wird nicht betrachtet)
                    //Check left
                    if (currentCell.getXCoordinate() > 0) { //check that cell is not at edge of grid
                        Cell neighbouringCell = floorCellGrid[currentCell.getXCoordinate() - 1][currentCell.getYCoordinate()];
                        updateParentAndPathToCellCosts(neighbouringCell, currentCell);
                    }

                    //Check right
                    if (currentCell.getXCoordinate() < cellgrid_width) { //check that cell is not at edge of grid
                        Cell neighbouringCell = floorCellGrid[currentCell.getXCoordinate() + 1][currentCell.getYCoordinate()];
                        updateParentAndPathToCellCosts(neighbouringCell, currentCell);
                    }

                    //Check below
                    if (currentCell.getYCoordinate() > 0) { //check that cell is not at edge of grid
                        Cell neighbouringCell = floorCellGrid[currentCell.getXCoordinate()][currentCell.getYCoordinate() - 1];
                        updateParentAndPathToCellCosts(neighbouringCell, currentCell);
                    }

                    //Check above
                    if (currentCell.getYCoordinate() < cellgrid_height) { //check that cell is not at edge of grid
                        Cell neighbouringCell = floorCellGrid[currentCell.getXCoordinate()][currentCell.getYCoordinate() + 1];
                        updateParentAndPathToCellCosts(neighbouringCell, currentCell);
                    }
                } else return;
            }
        }
    }

    /**
     * Update parent cell for the particular cell and the costs for the path to the cell
     * @param cell
     * @param parentCell
     */
    private void updateParentAndPathToCellCosts(final Cell cell, final Cell parentCell) {

        final int parentCellPathCosts = parentCell.getCostsPathToCell(); //is initialized with -1
        final int costsPathToCell = parentCellPathCosts >= 0 ?
                cell.getCostPassingCell() + parentCellPathCosts : cell.getCostPassingCell();
        final boolean isInOpenQueue = openCells.contains(cell); // cell has already status "open"

        //check if cell is walkable and does not have status "closed"
        if (cell.getWalkability() && !closedCells[cell.getXCoordinate()][cell.getYCoordinate()]) {

            // cell has status "unknown" or this path to the cell is cheaper than the path currently known
            if (!isInOpenQueue || costsPathToCell < cell.getCostsPathToCell()) {

                //update costs and parent cell
                cell.setCostsPathToCell(costsPathToCell);
                cell.setParentCell(parentCell);

                if (!isInOpenQueue) {
                    openCells.add(cell); //update status of cell from unknown to "open"
                }
            }
        }
    }

}
