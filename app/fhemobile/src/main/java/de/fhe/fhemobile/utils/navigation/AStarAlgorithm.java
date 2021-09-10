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

    //Constructor
    AStarAlgorithm(Cell startCell, Cell endCell, ArrayList<ArrayList<Cell>> grid) {
        this.startCell = startCell;
        this.endCell = endCell;
        this.grid = grid;
    }

    //Calculation of cells to walk on one grid
    ArrayList<Cell> getNavigationCellsOnGrid() {

        ArrayList<Cell> navigationCells = new ArrayList<>();

        try {
            //Set priority queue with comparator
            open = new PriorityQueue<>(16, new Comparator<Cell>() {
                @Override
                public int compare(Cell cellOne, Cell cellTwo) {
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
            e.printStackTrace();
        }
        return navigationCells;
    }

    //A* algorithm
    private void aStar() {
        while (!open.isEmpty()) {

            Cell currentCell = open.poll();

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

    //Check and update cost of a cell
    private void checkAndUpdateCost(Cell current, Cell test, int cost) {

        int testFinalCost = test.getHeuristicCost() + cost;
        boolean inOpen = open.contains(test);

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

    //Set cost of the cell to check
    private void setCostPerCell(Cell current, Cell test) {

        Class<? extends Cell> aClass = test.getClass();

        Cell compareCellClass = new Cell();
        Room compareRoomClass = new Room();
        Transition compareTransitionClass = new Transition();

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
