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
import static de.fhe.fhemobile.utils.Define.Navigation.*;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.FloorConnection;
import de.fhe.fhemobile.models.navigation.Room;

/**
 * Class for route calculation at a single floor using the A* algorithm
 */
class AStar {

    private static final String TAG = "AStar"; //$NON-NLS

    //Variables
    private PriorityQueue<Cell> openCells; //Zellen im Zustand "wartend", sortiert nach costsPathToCell (s. doc\BA_Kapitel_AStarAlgorithmus.txt)
    private boolean[][] closedCells; //Zellen im Zustand "fertig"
    private Cell startCell; //Startzelle für den kürzesten Weg auf diesem Stockwerk; kann auch eine Treppe, Aufzug etc sein
    private Cell endCell; //Endzelle für den kürzesten Weg auf diesem Stockwerk; kann auch eine Treppe, Aufzug etc sein
    private ArrayList<ArrayList<Cell>> floorCellGrid; //Koordinatensystem aus allen begehbaren und nicht-begehbaren Zellen des Stockwerks

    /**
     * Constructs an AStar Object
     * @param startCell
     * @param endCell
     * @param floorCellGrid
     */
    AStar(final Cell startCell, final Cell endCell, final ArrayList<ArrayList<Cell>> floorCellGrid) {
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
            //Set priority queue with comparator
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

            openCells.add(startCell); //add startCell to priority queue (cells with status "wartend")
            closedCells = new boolean[floorCellGrid.size()][floorCellGrid.get(0).size()]; //set size of closed array (cells with status "fertig")
            performAStarAlgorithm(); //run A* algorithm

            //backtracing path
            if (closedCells[endCell.getXCoordinate()][endCell.getYCoordinate()]) {

                Cell current = floorCellGrid.get(endCell.getXCoordinate()).get(endCell.getYCoordinate());

                while (current.getParentCell() != null) {
                    navigationCells.add(current);
                    current = current.getParentCell();
                }
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

            final Cell currentCell = openCells.poll();

            if (currentCell != null && currentCell.getWalkability()) {
                closedCells[currentCell.getXCoordinate()][currentCell.getYCoordinate()] = true;

                if (!currentCell.equals(endCell)) {

                    Cell testCell;

                    //Überprüft die Kosten der 4 direkt angrenzenden Zellen der aktuellen Zelle (Diagonale wird nicht betrachtet)
                    //Check left
                    if (currentCell.getXCoordinate() - 1 >= 0) {
                        testCell = floorCellGrid.get(currentCell.getXCoordinate() - 1).get(currentCell.getYCoordinate());
                        setCostOfCell(currentCell );
                        updateParentAndCosts(currentCell, testCell, currentCell.getCostsPathToCell() + currentCell.getCostPassingCell());
                    }

                    //Check right
                    if (currentCell.getXCoordinate() + 1 < floorCellGrid.size()) {
                        testCell = floorCellGrid.get(currentCell.getXCoordinate() + 1).get(currentCell.getYCoordinate());
                        setCostOfCell(currentCell);
                        updateParentAndCosts(currentCell, testCell, currentCell.getCostsPathToCell() + currentCell.getCostPassingCell());
                    }

                    //Check below
                    if (currentCell.getYCoordinate() - 1 >= 0) {
                        testCell = floorCellGrid.get(currentCell.getXCoordinate()).get(currentCell.getYCoordinate() - 1);
                        setCostOfCell(currentCell);
                        updateParentAndCosts(currentCell, testCell, currentCell.getCostsPathToCell() + currentCell.getCostPassingCell());
                    }

                    //Check above
                    if (currentCell.getYCoordinate() + 1 < floorCellGrid.get(0).size()) {
                        testCell = floorCellGrid.get(currentCell.getXCoordinate()).get(currentCell.getYCoordinate() + 1);
                        setCostOfCell(currentCell);
                        updateParentAndCosts(currentCell, testCell, currentCell.getCostsPathToCell() + currentCell.getCostPassingCell());
                    }
                }
            }
        }
    }

    /**
     * Update costs for path to cell and parent cell for the particular cell
     * @param cell
     * @param parentCell
     * @param costsPathToParentCell costsPathToCell of the parent cell
     */
    private void updateParentAndCosts(final Cell cell, final Cell parentCell, final int costsPathToParentCell) {

        final int costsPathToCell = cell.getCostPassingCell() + costsPathToParentCell;
        final boolean isInOpenQueue = openCells.contains(cell); // cell hat bereits Zustand "wartend"

        //check if cell is walkable and does not have status "closed"
        if (cell.getWalkability() && !closedCells[cell.getXCoordinate()][cell.getYCoordinate()]) {

            // cell has unknown status or this path to the cell is cheaper than previous path to it
            if (!isInOpenQueue || costsPathToCell < cell.getCostsPathToCell()) {

                //update costs and parent cell
                cell.setCostsPathToCell(costsPathToParentCell);
                cell.setParentCell(parentCell);

                if (!isInOpenQueue) {
                    openCells.add(cell); //update status of cell from unknown to "wartend"/"open"
                }
            }
        }
    }

    //Todo: warum werden im Original die Kosten wie das Passieren einer Zelle in die Elternzelle geschrieben, also warum hängt costPassingCell von der nächsten zelle im Weg ab?
//    /**
//     * Set cost of the cell to check
//     * @param current
//     * @param test
//     */
//    private void setCostPerCell(final Cell current, final Cell test) {
//
//        final Class<? extends Cell> aClass = test.getClass();
//
//        final Cell compareCellClass = new Cell();
//        final Room compareRoomClass = new Room();
//        final FloorConnection compareFloorConnectionClass = new FloorConnection();
//
//        if (aClass.equals(compareCellClass.getClass())) {
//            current.setCostPassingCell(COSTS_CELL);
//        }
//
//        if (aClass.equals(compareRoomClass.getClass())) {
//            current.setCostPassingCell(COSTS_ROOM);
//        }
//
//        if (aClass.equals(compareFloorConnectionClass.getClass())) {
//            current.setCostPassingCell(COSTS_FLOORCONNECTION);
//        }
//    }

    /**
     //     * Set cost of the cell to check
     //     * @param current
     //     * @param test
     //     */
    private void setCostOfCell(final Cell cell) {

        final Class<? extends Cell> aClass = cell.getClass();


        if (aClass.equals((new Cell()).getClass())) {
            cell.setCostPassingCell(COSTS_CELL);
        }
        else if (aClass.equals((new Room()).getClass())) {
            cell.setCostPassingCell(COSTS_ROOM);
        }
        else if (aClass.equals((new FloorConnection()).getClass())) {
            cell.setCostPassingCell(COSTS_FLOORCONNECTION);
        }
        else {
            Log.e(TAG, "Cell is not of class Cell, Room or Floorconnection");
        }
    }
}
