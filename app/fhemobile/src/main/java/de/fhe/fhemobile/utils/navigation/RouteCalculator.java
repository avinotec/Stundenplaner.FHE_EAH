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
import static de.fhe.fhemobile.utils.Define.Navigation.FLOORCONNECTION_TYPE_WAY;
import static de.fhe.fhemobile.utils.Define.Navigation.cellgrid_height;
import static de.fhe.fhemobile.utils.Define.Navigation.cellgrid_width;
import static de.fhe.fhemobile.utils.navigation.NavigationUtils.getfloorName;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.FloorConnection;
import de.fhe.fhemobile.models.navigation.Room;
import de.fhe.fhemobile.utils.navigation.NavigationUtils.Complex;

/**
 *  Klasse für Routenberechnung über mehrere Stockwerke und Gebäude
 *  source: Bachelor Thesis from Tim Münziger from SS2020
 *  edit by Nadja in October 2021
 */
public class RouteCalculator {

    //Constants
    private static final String TAG = "RouteCalculator"; //$NON-NLS

    //Variables
    private Context context;
    private ArrayList<FloorConnection> floorConnections;
    private ArrayList<Room> rooms;
    private Cell startLocation;
    private Cell destinationLocation;
    private ArrayList<Cell> cellsToWalk = new ArrayList<>(); //gesamte Route
    private ArrayList<Cell[][]> floorGrids = new ArrayList<>(); //Koordinatensysteme aller Stockwerke



    /**
     * Constructor
     * @param context
     * @param startLocation
     * @param destinationLocation
     * @param floorConnections
     * @param rooms
     */
    public RouteCalculator(Context context, final Room startLocation, final Room destinationLocation,
                           final ArrayList<FloorConnection> floorConnections, final ArrayList<Room> rooms) {
        this.context = context;
        this.startLocation = startLocation;
        this.destinationLocation = destinationLocation;
        this.floorConnections = floorConnections;
        this.rooms = rooms;
    }

    /**
     * Calculates the route and returns a list of all cells to walk
     * @return ArrayList of all cells at the route
     */
    @SuppressLint("LongLogTag")
    public ArrayList<Cell> getWholeRoute() {

        Cell startCell = startLocation;
        Cell endCell = null;

        //Get grids of floors to use
        floorGrids = getAllFloorGrids();

        try {
            //Get paths through all grids
            for (int index = 0; index < floorGrids.size(); index++) {

                //Get reachable floorconnections and sort by distance
                ArrayList<FloorConnection> availableFloorConnections = getAvailableFloorConnections(startCell, floorGrids, index);

                //Set endCell
                //Set endCell with destinationLocation on same floor plan
                //destination floor == current floor and current building == destination building
                if (startCell.getBuilding().equals(destinationLocation.getBuilding())
                        && startCell.getFloorString().equals(destinationLocation.getFloorString())) {

                    endCell = destinationLocation;

                }
                //buildings 3, 2 and 1 are treated as same building
                else  if (isBuilding321(startCell.getBuilding())
                        && isBuilding321(destinationLocation.getBuilding())
                        && startCell.getFloorString().equals(destinationLocation.getFloorString())) {

                    endCell = destinationLocation;
                }

                if (index + 1 < floorGrids.size()) {

                    //Set endCell with destinationLocation on different floor plans
                    //FloorConnection as endCell, same building
                    if (floorGrids.get(index)[0][0].getBuilding().equals(
                            floorGrids.get(index + 1)[0][0].getBuilding())) {
                        endCell = availableFloorConnections.get(0).getConnectedCell(startCell.getBuilding(), startCell.getFloorString());
                    }
                    else if (isBuilding321(floorGrids.get(index)[0][0].getBuilding())
                            && isBuilding321(floorGrids.get(index + 1)[0][0].getBuilding())) {

                        endCell = availableFloorConnections.get(0).getConnectedCell(startCell.getBuilding(), startCell.getFloorString());
                    }

                    //FloorConnection as endCell, different buildings
                    //if building 4 -> floor -1 to floor 0 in building 3/2/1
                    if (floorGrids.get(index)[0][0].getBuilding().equals(BUILDING_04)
                            && (floorGrids.get(index + 1)[0][0].getBuilding().equals(BUILDING_03)
                            || floorGrids.get(index + 1)[0][0].getBuilding().equals(BUILDING_02)
                            || floorGrids.get(index + 1)[0][0].getBuilding().equals(BUILDING_01))) {

                        for (int i = 0; i < availableFloorConnections.size(); i++) {

                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {

                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {
                                    if (availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_04)) {
                                        endCell = availableFloorConnections.get(i).getConnectedCells().get(j);
                                    }
                                }
                            }
                        }
                    }

                    //if building 5 -> floor 1 to floor 1 in building 3/2/1
                    if (floorGrids.get(index)[0][0].getBuilding().equals(BUILDING_05)
                            && isBuilding321(floorGrids.get(index + 1)[0][0].getBuilding())) {

                        for (int i = 0; i < availableFloorConnections.size(); i++) {

                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {

                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {

                                    if (availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_05)) {
                                        endCell = availableFloorConnections.get(i).getConnectedCells().get(j);
                                    }
                                }
                            }
                        }
                    }

                    //if building 3 -> floor 0 to floor -1 in building 4
                    if (isBuilding321(floorGrids.get(index)[0][0].getBuilding())
                            && floorGrids.get(index + 1)[0][0].getBuilding().equals(BUILDING_04)) {

                        for (int i = 0; i < availableFloorConnections.size(); i++) {

                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {

                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {

                                    if (isBuilding321(availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding())) {
                                        endCell = availableFloorConnections.get(i).getConnectedCells().get(j);
                                    }
                                }
                            }
                        }
                    }

                    //if building 3 -> floor 1 to floor 1 in building 5
                    if (isBuilding321(floorGrids.get(index)[0][0].getBuilding())
                            && floorGrids.get(index + 1)[0][0].getBuilding().equals(BUILDING_05)) {

                        for (int i = 0; i < availableFloorConnections.size(); i++) {

                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {

                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {

                                    if (isBuilding321(availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding())) {
                                        endCell = availableFloorConnections.get(i).getConnectedCells().get(j);
                                    }
                                }
                            }
                        }
                    }
                }

                //Get path through floor
                AStar aStar = new AStar(startCell, endCell, floorGrids.get(index)); //to floor 4 no new cells added, when from building with no 4th floor
                cellsToWalk.addAll(aStar.getCellsToWalk());

                Log.i("_____CELLS_TO_WALK_part_____", String.valueOf(aStar.getCellsToWalk()));

                //Set next startCell
                if (index + 1 < floorGrids.size()) {

                    //Same building
                    if (floorGrids.get(index)[0][0].getBuilding().equals(
                            floorGrids.get(index + 1)[0][0].getBuilding())) {

                        startCell = availableFloorConnections.get(0).getConnectedCell(
                                floorGrids.get(index + 1)[0][0].getBuilding(),
                                floorGrids.get(index + 1)[0][0].getFloorString());
                    }
                    else if (isBuilding321(floorGrids.get(index)[0][0].getBuilding())
                            && isBuilding321(floorGrids.get(index + 1)[0][0].getBuilding())) {

                        startCell = availableFloorConnections.get(0).getConnectedCell(floorGrids.get(index + 1)[0][0].getBuilding(),
                                floorGrids.get(index + 1)[0][0].getFloorString());
                    }

                    //Different building
                    //if building 4 -> floor -1 to floor 0 in building 3/2/1
                    if (floorGrids.get(index)[0][0].getBuilding().equals(BUILDING_04)
                            && floorGrids.get(index + 1)[0][0].getBuilding().equals(BUILDING_03)) {

                        for (int i = 0; i < availableFloorConnections.size(); i++) {

                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {

                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {

                                    if (isBuilding321(availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding())) {
                                        startCell = availableFloorConnections.get(i).getConnectedCells().get(j);
                                    }
                                }
                            }
                        }
                    }

                    //if building 5 -> floor 1 to floor  1 in building 3/2/1
                    if (floorGrids.get(index)[0][0].getBuilding().equals(BUILDING_05)
                            && floorGrids.get(index + 1)[0][0].getBuilding().equals(BUILDING_03)) {

                        for (int i = 0; i < availableFloorConnections.size(); i++) {

                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {

                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {

                                    if (isBuilding321(availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding())) {
                                        startCell = availableFloorConnections.get(i).getConnectedCells().get(j);
                                    }
                                }
                            }
                        }
                    }

                    //if building 3/2/1 -> floor 0 to floor -1 in building 4
                    if (isBuilding321(floorGrids.get(index)[0][0].getBuilding())
                            && floorGrids.get(index + 1)[0][0].getBuilding().equals(BUILDING_04)) {

                        for (int i = 0; i < availableFloorConnections.size(); i++) {

                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {

                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {
                                    if (availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_04)) {
                                        startCell = availableFloorConnections.get(i).getConnectedCells().get(j);
                                    }
                                }
                            }
                        }
                    }

                    //if building 3/2/1 -> floor 1 to floor 1 in building 5
                    if (isBuilding321(floorGrids.get(index)[0][0].getBuilding())
                            && floorGrids.get(index + 1)[0][0].getBuilding().equals(BUILDING_05)) {

                        for (int i = 0; i < availableFloorConnections.size(); i++) {

                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {

                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {
                                    if (availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_05)) {
                                        startCell = availableFloorConnections.get(i).getConnectedCells().get(j);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error getting navigation cells", e);
        }

//        Log.i("_____TEST_CELLS_TO_WALK_size_____", String.valueOf(cellsToWalk.size()));

//        if (BuildConfig.DEBUG) {
//            for (Cell cellToWalk : cellsToWalk) {
//                Log.i("_____TEST_CELLS_TO_WALK_b.f x.y_____", cellToWalk.getBuilding() + "." + cellToWalk.getFloor() + " "
//                        + cellToWalk.getXCoordinate() + "." + cellToWalk.getYCoordinate());
//            }
//        }
        return cellsToWalk;
    }


    /**
     * Get grids of all needed floors in all buildings
     */
    private ArrayList<Cell[][]> getAllFloorGrids() {

        ArrayList<Cell[][]> gridsToAdd = new ArrayList<>();

        final int startFloorInt = startLocation.getFloorAsInteger();
        final int destinationFloorInt = destinationLocation.getFloorAsInteger();
        Complex startComplex = startLocation.getComplex();
        Complex destinationComplex = destinationLocation.getComplex();

        try {
            //Start and destination location are in same building
            //note: building 3,2 and 1 are considered as same building
            if (startComplex.equals(destinationComplex)) {

                //im Kellergeschoss gibt es keinen Durchgang von Haus 3 nach Haus 1
                if (startFloorInt == -1 && destinationFloorInt == -1
                        && startLocation.getComplex().equals(Complex.COMPLEX_321)
                        && destinationLocation.getComplex().equals(Complex.COMPLEX_321)
                        && !(startLocation.getBuilding().equals(destinationLocation.getBuilding()))) {

                    //todo house 3.ug nach 1.ug
                    gridsToAdd.add(buildFloorGrid(startLocation.getBuilding(), destinationFloorInt));
                    gridsToAdd.add(buildFloorGrid(BUILDING_03, 0));
                    gridsToAdd.add(buildFloorGrid(BUILDING_02, 0));
                    gridsToAdd.add(buildFloorGrid(BUILDING_01, 0));
                }
                // add grids for all floors from start floor till destination floor
                else {
                    int i = startFloorInt;
                    while(i != destinationFloorInt) {

                        Cell[][] floorGrid = buildFloorGrid(startLocation.getBuilding(), i);
                        gridsToAdd.add(floorGrid);

                        if (startFloorInt < destinationFloorInt) i++;
                        else if (startFloorInt > destinationFloorInt) i--;
                        else if (startFloorInt == destinationFloorInt) break;
                    }
                    //add floor grid for destination floor
                    gridsToAdd.add(buildFloorGrid(startLocation.getBuilding(), destinationFloorInt));
                }
            }

            //start and destination location are in different buildings/complexes
            else {

                //add all grids for the way from start floor to ground floor (to get out of building)
                for (int i = startFloorInt; i > -1; i--) {
                    gridsToAdd.add(buildFloorGrid(startLocation.getBuilding(), i));
                }

                //add grids for way from ground floor to destination floor in destination building
                int i = 0;
                while(true){
                    gridsToAdd.add(buildFloorGrid(destinationLocation.getBuilding(), i));
                    //end while loop if destination floor reached
                    if (i == destinationFloorInt) break;
                    //counting i up or down depending on destination floor equals -1, ug or upper floors
                    if (destinationFloorInt > 0) i++;
                    else if (destinationFloorInt < 0) i--;
                }

                //walk from building 4 to 5 (or vice versa) passing building 3-2-1
                if((startComplex.equals(Complex.COMPLEX_4) && destinationComplex.equals(Complex.COMPLEX_5))
                        || (startComplex.equals(Complex.COMPLEX_5) && destinationComplex.equals(Complex.COMPLEX_4))){

                    gridsToAdd.add(buildFloorGrid(BUILDING_03, 0));
                }

                //todo take bridge between 321 and 5 into account

            }
        } catch (Exception e) {
            Log.e(TAG, "error building floor grids", e);
        }
        return gridsToAdd;
    }

    /**
     * Get the string representation of the floor
     * @param index
     * @return floor name as string
     */
    private String floorIntToString(final int index) {

        String currentFloor = "";

        switch (index) {
            case -1:
                currentFloor = "ug";
                break;
            case 0:
                currentFloor = "00";
                break;
            case 1:
                currentFloor = "01";
                break;
            case 2:
                currentFloor = "02";
                break;
            case 3:
                currentFloor = "03";
                break;
            case 4:
                currentFloor = "04";
                break;
            default:
                break;
        }
        return currentFloor;
    }

    /**
     * Build a grid of all cells as a floor plan
     * @param building
     * @param floorInt floor as integer (ug = -1)
     * @return 2D Arraylist with all cells of the floor
     */
    private Cell[][] buildFloorGrid(final String building, final int floorInt) {

        Cell[][] floorGrid = new Cell[(int)cellgrid_width][(int)cellgrid_height];
        String floor = floorIntToString(floorInt);

        try {
            JSONHandler jsonHandler = new JSONHandler();
            String json;

            //Get floor plan JSON from assets
            json = JSONHandler.readFloorPlanFromAssets(context, getfloorName(building, floor) + ".json");
            final HashMap<String, Cell> walkableCells = jsonHandler.parseJsonWalkableCells(json);

            //fill in rooms
            for(Room r : rooms){
                if(r.getBuilding().equals(building) && r.getFloorString().equals(floor)){
                    floorGrid[r.getXCoordinate()][r.getYCoordinate()] = r;
                }
            }
            //fill in floorconnections
            for(FloorConnection fc : floorConnections){
                for(Cell cell : fc.getConnectedCells())
                    if(cell.getBuilding().equals(building) && cell.getFloorString().equals(floor)){
                        floorGrid[cell.getXCoordinate()][cell.getYCoordinate()] = fc;
                        floorGrid[cell.getXCoordinate()][cell.getYCoordinate()].setXCoordinate(cell.getXCoordinate());
                        floorGrid[cell.getXCoordinate()][cell.getYCoordinate()].setYCoordinate(cell.getYCoordinate());
                    }
            }

            //iterate over whole grid size to fill floorGrid with remaining walkable and non-walkable cell
            for (int x = 0; x < cellgrid_width; x++) {
                for (int y = 0; y < cellgrid_height; y++) {

                    //check normal walkable cell
                    if (floorGrid[x][y] == null){
                        //x_y is used as key to store the cell
                        final Cell aCell = walkableCells.get( "" + x + '_' + y );
                        if(aCell != null){
                            //add a walkable cell with coordinates x,y to grid
                            floorGrid[x][y] = new Cell(x, y, building, floor, true);
                        } else{
                            //then fill with non-walkable cell
                            floorGrid[x][y] = new Cell(x, y, building, floor, false);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error building the floor floorGrid", e);
        }
        return floorGrid;
    }


    /**
     * Get all usable floorconnections on current floor plan, sorted by distance
     * ways outside buildings and bridge are not allowed on index = 0
     * @param startCell
     * @param floorGrids
     * @param index
     * @return
     */
    private ArrayList<FloorConnection> getAvailableFloorConnections(final Cell startCell, final ArrayList<Cell[][]> floorGrids, final int index) {

        ArrayList<FloorConnection> availableFloorConnectionsHelper = new ArrayList<>();
        ArrayList<FloorConnection> adjustedAvailableFloorConnections = new ArrayList<>();

        try {
            if (index + 1 < floorGrids.size()) {

                //Get all reachable floor connections
                for (int i = 0; i < floorConnections.size(); i++) {

                    for (int j = 0; j < floorConnections.get(i).getConnectedCells().size(); j++) {

                        String buildingFloorconnection = floorConnections.get(i).getConnectedCells().get(j).getBuilding();
                        String buildingStartcell = startCell.getBuilding();
                        String floorFloorconnection = floorConnections.get(i).getConnectedCells().get(j).getFloorString();
                        String floorStartcell = startCell.getFloorString();

                        //floor connection reachable from current floor (building 4 and 5)
                        if (buildingFloorconnection.equals(buildingStartcell)
                                && floorFloorconnection.equals(floorStartcell)) {

                            //Next floor in floorGrids reachable from floorconnection
                            availableFloorConnectionsHelper.addAll(getAvailableFloorConnectionsHelper(i, j, index, startCell));
                        }
                        //floor connection reachable from current floor (building 3/2/1)
                        else if (isBuilding321(buildingFloorconnection)
                                && isBuilding321(startCell.getBuilding())
                                && floorFloorconnection.equals(startCell.getFloorString())) {

                            //Next floor in floorGrids reachable from floorconnection
                            availableFloorConnectionsHelper.addAll(getAvailableFloorConnectionsHelper(i, j, index, startCell));
                        }
                    }
                }

                //Sort by distance
                Collections.sort(availableFloorConnectionsHelper, new Comparator<FloorConnection>() {
                    public int compare(FloorConnection a, FloorConnection b) {
                        return Integer.compare(a.getCostsPathToCell(), b.getCostsPathToCell());
                    }
                });

                //Put crossings to the end, crossings not allowed on index = 0
                for (int j = 0; j < availableFloorConnectionsHelper.size(); j++) {
                    if (!availableFloorConnectionsHelper.get(j).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {
                        adjustedAvailableFloorConnections.add(availableFloorConnectionsHelper.get(j));
                    }
                }

                for (int j = 0; j < availableFloorConnectionsHelper.size(); j++) {
                    if (availableFloorConnectionsHelper.get(j).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {
                        adjustedAvailableFloorConnections.add(availableFloorConnectionsHelper.get(j));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error getting reachable floorconnections", e);
        }
        return adjustedAvailableFloorConnections;
    }

    /**
     * Get usable floorconnections helper method
     * @param i
     * @param j
     * @param index
     * @param startCell
     * @return
     */
    private ArrayList<FloorConnection> getAvailableFloorConnectionsHelper(final int i, final int j, final int index, final Cell startCell) {

        final ArrayList<FloorConnection> availableFloorConnectionsHelper = new ArrayList<>();

        for (int k = 0; k < floorConnections.get(i).getConnectedCells().size(); k++) {
            String buildingFloorconnection = floorConnections.get(i).getConnectedCells().get(k).getBuilding();
            String buildingFloorgrid = floorGrids.get(index + 1)[0][0].getBuilding();
            String floorFloorconnection = floorConnections.get(i).getConnectedCells().get(k).getFloorString();
            String floorFloorgrid = floorGrids.get(index + 1)[0][0].getFloorString();

            if ((buildingFloorconnection.equals(buildingFloorgrid)
                    || (isBuilding321(buildingFloorconnection) && isBuilding321(buildingFloorgrid)))
                    && floorFloorconnection.equals(floorFloorgrid)) {

                final AStar aStar = new AStar(startCell, floorConnections.get(i).getConnectedCells().get(j), floorGrids.get(index));
                final ArrayList<Cell> navigationCells = aStar.getCellsToWalk();

                final FloorConnection floorConnectionHelper = floorConnections.get(i);
                floorConnectionHelper.setCostsPathToCell(navigationCells.size());
                availableFloorConnectionsHelper.add(floorConnectionHelper);
            }

        }
        return availableFloorConnectionsHelper;
    }

    private static boolean isBuilding321(final String building){
        if(building.equals(BUILDING_01) || building.equals(BUILDING_02) || building.equals(BUILDING_03)){
            return true;
        }
        return false;
    }
}
