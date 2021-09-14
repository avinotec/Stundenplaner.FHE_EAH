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

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import de.fhe.fhemobile.models.navigation.*;

class AStarAlgorithm {

    //Constants
    private static final String TAG = "AStarAlgorithm"; //$NON-NLS

    private static final int COSTS_CELL = 1;
    private static final int COSTS_ROOM = 1;
    private static final int COSTS_TRANSITION = 2;

    //Variables
    private PriorityQueue<Cell> open;
    private boolean[][] closed;
    private Cell startCell;
    private Cell endCell;
    private ArrayList<ArrayList<Cell>> grid;

    /**
     * Constructor
     * @param startCell
     * @param endCell
     * @param grid
     */
    AStarAlgorithm(final Cell startCell, final Cell endCell, final ArrayList<ArrayList<Cell>> grid) {
        this.startCell = startCell;
        this.endCell = endCell;
        this.grid = grid;
    }

    /**
     * Calculation of cells to walk on one grid
     * @return
     */
    final ArrayList<Cell> getNavigationCellsOnGrid() {

        final ArrayList<Cell> navigationCells = new ArrayList<>();

        try {
            //Set priority queue with comparator
            open = new PriorityQueue<>(16, new Comparator<Cell>() {

                /**
                 *
                 * @param cellOne
                 * @param cellTwo
                 * @return
                 */
                @Override
                public int compare(final Cell cellOne, final Cell cellTwo) {
                    return Integer.compare(cellOne.getFinalCost(), cellTwo.getFinalCost());
                }
            });

            //Set startCell for priority queue, set size of closed array and run A* algorithm
            open.add(startCell);
            closed = new boolean[grid.size()][grid.get(0).size()];
            aStar();

            //Trace back path
            if (closed[endCell.getXCoordinate()][endCell.getYCoordinate()]) {

                Cell current = grid.get(endCell.getXCoordinate()).get(endCell.getYCoordinate());

                while (current.getParent() != null) {
                    navigationCells.add(current);
                    current = current.getParent();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error calculating route ", e);
        }
        return navigationCells;
    }

    /**
     * A* algorithm
     */
    private void aStar() {
        while (!open.isEmpty()) {

            final Cell currentCell = open.poll();

            if (currentCell != null && currentCell.getWalkability()) {
                closed[currentCell.getXCoordinate()][currentCell.getYCoordinate()] = true;

                if (!currentCell.equals(endCell)) {

                    Cell testCell;

                    //Check left
                    if (currentCell.getXCoordinate() - 1 >= 0) {
                        testCell = grid.get(currentCell.getXCoordinate() - 1).get(currentCell.getYCoordinate());
                        setCostPerCell(currentCell, testCell);
                        checkAndUpdateCost(currentCell, testCell, currentCell.getFinalCost() + currentCell.getHeuristicCost());
                    }

                    //Check right
                    if (currentCell.getXCoordinate() + 1 < grid.size()) {
                        testCell = grid.get(currentCell.getXCoordinate() + 1).get(currentCell.getYCoordinate());
                        setCostPerCell(currentCell, testCell);
                        checkAndUpdateCost(currentCell, testCell, currentCell.getFinalCost() + currentCell.getHeuristicCost());
                    }

                    //Check below
                    if (currentCell.getYCoordinate() - 1 >= 0) {
                        testCell = grid.get(currentCell.getXCoordinate()).get(currentCell.getYCoordinate() - 1);
                        setCostPerCell(currentCell, testCell);
                        checkAndUpdateCost(currentCell, testCell, currentCell.getFinalCost() + currentCell.getHeuristicCost());
                    }

                    //Check above
                    if (currentCell.getYCoordinate() + 1 < grid.get(0).size()) {
                        testCell = grid.get(currentCell.getXCoordinate()).get(currentCell.getYCoordinate() + 1);
                        setCostPerCell(currentCell, testCell);
                        checkAndUpdateCost(currentCell, testCell, currentCell.getFinalCost() + currentCell.getHeuristicCost());
                    }
                }
            }
        }
    }

    /**
     * Check and update cost of a cell
     * @param current
     * @param test
     * @param cost
     */
    private void checkAndUpdateCost(final Cell current, final Cell test, final int cost) {

        final int testFinalCost = test.getHeuristicCost() + cost;
        final boolean inOpen = open.contains(test);

        if (test.getWalkability() && !closed[test.getXCoordinate()][test.getYCoordinate()]) {

            if (!inOpen || testFinalCost < test.getFinalCost()) {
                test.setFinalCost(cost);
                test.setParent(current);

                if (!inOpen) {
                    open.add(test);
                }
            }
        }
    }

    /**
     * Set cost of the cell to check
     * @param current
     * @param test
     */
    private void setCostPerCell(final Cell current, final Cell test) {

        final Class<? extends Cell> aClass = test.getClass();

        final Cell compareCellClass = new Cell();
        final Room compareRoomClass = new Room();
        final Transition compareTransitionClass = new Transition();

        if (aClass.equals(compareCellClass.getClass())) {
            current.setHeuristicCost(COSTS_CELL);
        }

        if (aClass.equals(compareRoomClass.getClass())) {
            current.setHeuristicCost(COSTS_ROOM);
        }

        if (aClass.equals(compareTransitionClass.getClass())) {
            current.setHeuristicCost(COSTS_TRANSITION);
        }
    }
}
