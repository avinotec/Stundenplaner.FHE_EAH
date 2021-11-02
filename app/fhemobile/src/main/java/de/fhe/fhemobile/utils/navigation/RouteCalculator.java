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
import static de.fhe.fhemobile.utils.navigation.NavigationUtils.floorIntToString;
import static de.fhe.fhemobile.utils.navigation.NavigationUtils.getFloorPlanName;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.FloorConnection;
import de.fhe.fhemobile.models.navigation.FloorConnectionCell;
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
    private Cell destLocation;
    //gesamte Route
    private ArrayList<Cell> cellsToWalk = new ArrayList<>();
    //Koordinatensysteme aller Stockwerke
    //for each building (key = complex e.g. BUILDING_321) and for each floor (key = floor as int) a grid of cells is stored
    private HashMap<Complex, HashMap<Integer, Cell[][]>> floorGrids = new HashMap<>();


    /**
     * Constructor
     * @param context
     * @param startLocation
     * @param destLocation
     * @param floorConnections
     * @param rooms
     */
    public RouteCalculator(Context context, final Room startLocation, final Room destLocation,
                           final ArrayList<FloorConnection> floorConnections, final ArrayList<Room> rooms) {
        this.context = context;
        this.startLocation = startLocation;
        this.destLocation = destLocation;
        this.floorConnections = floorConnections;
        this.rooms = rooms;
    }

    /**
     * Calculates the route and returns a list of all cells to walk
     * @return ArrayList of all cells at the route
     */
    public ArrayList<Cell> getWholeRoute() {

        Cell startCell = startLocation;
        Cell destCell = destLocation;

        //Get grids of floors to use - fills in variable floorGrids
        getAllFloorGrids();

        try {
//            //Get paths through all grids
//            for (int k = 0; k < floorGrids.size(); k++) {
//
//                //Get all floorconnections at floor k and sort by distance
//                ArrayList<FloorConnection> availableFloorConnections
//                        = getFloorConnections(startCell, k);
//
//                //Set destCell
//                //Set destCell with destLocation on same floor plan
//                //destination floor == current floor and current building == destination building
//                if (startCell.getComplex().equals(destLocation.getComplex())
//                        && startCell.getFloorString().equals(destLocation.getFloorString())) {
//
//                    destCell = destLocation;
//
//                }

//                if (k + 1 < floorGrids.size()) {
//
//                    //Set destCell with destLocation on different floor plans
//                    //FloorConnection as destCell, same building
//                    if (floorGrids.get(k)[0][0].getBuilding().equals(
//                            floorGrids.get(k + 1)[0][0].getBuilding())) {
//                        destCell = availableFloorConnections.get(0).getConnectedCell(startCell.getBuilding(), startCell.getFloorString());
//                    }
//                    else if (isBuilding321(floorGrids.get(k)[0][0].getBuilding())
//                            && isBuilding321(floorGrids.get(k + 1)[0][0].getBuilding())) {
//
//                        destCell = availableFloorConnections.get(0).getConnectedCell(startCell.getBuilding(), startCell.getFloorString());
//                    }
//
//                    //FloorConnection as destCell, different buildings
//                    //if building 4 -> floor -1 to floor 0 in building 3/2/1
//                    if (floorGrids.get(k)[0][0].getBuilding().equals(BUILDING_04)
//                            && (floorGrids.get(k + 1)[0][0].getBuilding().equals(BUILDING_03)
//                            || floorGrids.get(k + 1)[0][0].getBuilding().equals(BUILDING_02)
//                            || floorGrids.get(k + 1)[0][0].getBuilding().equals(BUILDING_01))) {
//
//                        for (int i = 0; i < availableFloorConnections.size(); i++) {
//
//                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {
//
//                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {
//                                    if (availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_04)) {
//                                        destCell = availableFloorConnections.get(i).getConnectedCells().get(j);
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    //if building 5 -> floor 1 to floor 1 in building 3/2/1
//                    if (floorGrids.get(k)[0][0].getBuilding().equals(BUILDING_05)
//                            && isBuilding321(floorGrids.get(k + 1)[0][0].getBuilding())) {
//
//                        for (int i = 0; i < availableFloorConnections.size(); i++) {
//
//                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {
//
//                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {
//
//                                    if (availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_05)) {
//                                        destCell = availableFloorConnections.get(i).getConnectedCells().get(j);
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    //if building 3 -> floor 0 to floor -1 in building 4
//                    if (isBuilding321(floorGrids.get(k)[0][0].getBuilding())
//                            && floorGrids.get(k + 1)[0][0].getBuilding().equals(BUILDING_04)) {
//
//                        for (int i = 0; i < availableFloorConnections.size(); i++) {
//
//                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {
//
//                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {
//
//                                    if (isBuilding321(availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding())) {
//                                        destCell = availableFloorConnections.get(i).getConnectedCells().get(j);
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    //if building 3 -> floor 1 to floor 1 in building 5
//                    if (isBuilding321(floorGrids.get(k)[0][0].getBuilding())
//                            && floorGrids.get(k + 1)[0][0].getBuilding().equals(BUILDING_05)) {
//
//                        for (int i = 0; i < availableFloorConnections.size(); i++) {
//
//                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {
//
//                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {
//
//                                    if (isBuilding321(availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding())) {
//                                        destCell = availableFloorConnections.get(i).getConnectedCells().get(j);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }

                //Get path through floor
                AStar aStar = new AStar(startCell, destCell, floorGrids, floorConnections); //to floor 4 no new cells added, when from building with no 4th floor
                cellsToWalk.addAll(aStar.getCellsToWalk());

                //Log.i("_____CELLS_TO_WALK_part_____", String.valueOf(aStar.getCellsToWalk()));

//                //Set next startCell
//                if (k + 1 < floorGrids.size()) {
//
//                    //Same building
//                    if (floorGrids.get(k)[0][0].getBuilding().equals(
//                            floorGrids.get(k + 1)[0][0].getBuilding())) {
//
//                        startCell = availableFloorConnections.get(0).getConnectedCell(
//                                floorGrids.get(k + 1)[0][0].getBuilding(),
//                                floorGrids.get(k + 1)[0][0].getFloorString());
//                    }
//                    else if (isBuilding321(floorGrids.get(k)[0][0].getBuilding())
//                            && isBuilding321(floorGrids.get(k + 1)[0][0].getBuilding())) {
//
//                        startCell = availableFloorConnections.get(0).getConnectedCell(floorGrids.get(k + 1)[0][0].getBuilding(),
//                                floorGrids.get(k + 1)[0][0].getFloorString());
//                    }
//
//                    //Different building
//                    //if building 4 -> floor -1 to floor 0 in building 3/2/1
//                    if (floorGrids.get(k)[0][0].getBuilding().equals(BUILDING_04)
//                            && floorGrids.get(k + 1)[0][0].getBuilding().equals(BUILDING_03)) {
//
//                        for (int i = 0; i < availableFloorConnections.size(); i++) {
//
//                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {
//
//                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {
//
//                                    if (isBuilding321(availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding())) {
//                                        startCell = availableFloorConnections.get(i).getConnectedCells().get(j);
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    //if building 5 -> floor 1 to floor  1 in building 3/2/1
//                    if (floorGrids.get(k)[0][0].getBuilding().equals(BUILDING_05)
//                            && floorGrids.get(k + 1)[0][0].getBuilding().equals(BUILDING_03)) {
//
//                        for (int i = 0; i < availableFloorConnections.size(); i++) {
//
//                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {
//
//                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {
//
//                                    if (isBuilding321(availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding())) {
//                                        startCell = availableFloorConnections.get(i).getConnectedCells().get(j);
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    //if building 3/2/1 -> floor 0 to floor -1 in building 4
//                    if (isBuilding321(floorGrids.get(k)[0][0].getBuilding())
//                            && floorGrids.get(k + 1)[0][0].getBuilding().equals(BUILDING_04)) {
//
//                        for (int i = 0; i < availableFloorConnections.size(); i++) {
//
//                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {
//
//                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {
//                                    if (availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_04)) {
//                                        startCell = availableFloorConnections.get(i).getConnectedCells().get(j);
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    //if building 3/2/1 -> floor 1 to floor 1 in building 5
//                    if (isBuilding321(floorGrids.get(k)[0][0].getBuilding())
//                            && floorGrids.get(k + 1)[0][0].getBuilding().equals(BUILDING_05)) {
//
//                        for (int i = 0; i < availableFloorConnections.size(); i++) {
//
//                            if (availableFloorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {
//
//                                for (int j = 0; j < availableFloorConnections.get(i).getConnectedCells().size(); j++) {
//                                    if (availableFloorConnections.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_05)) {
//                                        startCell = availableFloorConnections.get(i).getConnectedCells().get(j);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
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
     * @return
     */
    private void getAllFloorGrids() {

        final int startFloorInt = startLocation.getFloorInt();
        final int destinationFloorInt = destLocation.getFloorInt();
        Complex startComplex = startLocation.getComplex();
        Complex destinationComplex = destLocation.getComplex();

        try {
            floorGrids.put(Complex.COMPLEX_321, new HashMap<>());
            for(int i = -1; i <= 4; i++){
                floorGrids.get(Complex.COMPLEX_321).put(i, buildFloorGrid(Complex.COMPLEX_321,i));
            }
            //todo: uncomment if json files for building 4and 5 were added
//            floorGrids.put(Complex.COMPLEX_4, new HashMap<>());
//            for(int i = -1; i <= 5; i++){
//                floorGrids.get(Complex.COMPLEX_4).put(i, buildFloorGrid(Complex.COMPLEX_4,i));
//            }
//            floorGrids.put(Complex.COMPLEX_5, new HashMap<>());
//            for(int i = -1; i <= 5; i++){
//                floorGrids.get(Complex.COMPLEX_5).put(i, buildFloorGrid(Complex.COMPLEX_5,i));
//            }

//            //Start and destination location are in same building
//            //note: building 3,2 and 1 are considered as same building
//            if (startComplex.equals(destinationComplex)) {
//
//                //im Kellergeschoss gibt es keinen Durchgang von Haus 3 nach Haus 1
//                if (startFloorInt == -1 && destinationFloorInt == -1
//                        && startLocation.getComplex().equals(Complex.COMPLEX_321)
//                        && destLocation.getComplex().equals(Complex.COMPLEX_321)
//                        && !(startLocation.getBuilding().equals(destLocation.getBuilding()))) {
//
//                    //todo house 3.ug nach 1.ug
//                    String startBuilding = startLocation.getBuilding();
//                    floorGrids.get(startBuilding).put(destinationFloorInt,
//                            buildFloorGrid(startComplex, destinationFloorInt));
//                    floorGrids.get(BUILDING_03).put(0, buildFloorGrid(BUILDING_03, 0));
//                    floorGrids.get(BUILDING_02).put(0, buildFloorGrid(BUILDING_02, 0));
//                    floorGrids.get(BUILDING_01).put(0, buildFloorGrid(BUILDING_01, 0));
//                }
//                // add grids for all floors from start floor till destination floor
//                else {
//                    int i = startFloorInt;
//                    while(i != destinationFloorInt) {
//
//                        Cell[][] floorGrid = buildFloorGrid(startLocation.getBuilding(), i);
//                        floorGrids.get(startLocation.getBuilding()).put(startFloorInt, floorGrid);
//
//                        if (startFloorInt < destinationFloorInt) i++;
//                        else if (startFloorInt > destinationFloorInt) i--;
//                        else if (startFloorInt == destinationFloorInt) break;
//                    }
//                    //add floor grid for destination floor
//                    floorGrids.get(destLocation.getBuilding()).put(destinationFloorInt,
//                            buildFloorGrid(destLocation.getBuilding(), destinationFloorInt));
//                }
//            }
//
//            //start and destination location are in different buildings/complexes
//            else {
//
//                //add all grids for the way from start floor to ground floor (to get out of building)
//                for (int i = startFloorInt; i > -1; i--) {
//                    floorGrids.get(startLocation.getBuilding())
//                            .put(i, buildFloorGrid(startLocation.getBuilding(), i));
//                }
//
//                //add grids for way from ground floor to destination floor in destination building
//                int i = 0;
//                while(true){
//                    floorGrids.get(destLocation.getBuilding())
//                            .put(i, buildFloorGrid(destLocation.getBuilding(), i));
//                    //end while loop if destination floor reached
//                    if (i == destinationFloorInt) break;
//                    //counting i up or down depending on destination floor equals -1, ug or upper floors
//                    if (destinationFloorInt > 0) i++;
//                    else if (destinationFloorInt < 0) i--;
//                }
//
//                //walk from building 4 to 5 (or vice versa) passing building 3-2-1
//                if((startComplex.equals(Complex.COMPLEX_4) && destinationComplex.equals(Complex.COMPLEX_5))
//                        || (startComplex.equals(Complex.COMPLEX_5) && destinationComplex.equals(Complex.COMPLEX_4))){
//
//                    floorGrids.get(BUILDING_03).put(0, buildFloorGrid(BUILDING_03, 0));
//                }
//
//                //todo take bridge between 321 and 5 into account
//
//            }
        } catch (Exception e) {
            Log.e(TAG, "error building floor grids", e);
        }
    }



    /**
     * Build a grid of all cells as a floor plan
     * @paramcomplex
     * @param floorInt floor as integer (ug = -1)
     * @return 2D Arraylist with all cells of the floor
     */
    private Cell[][] buildFloorGrid(final Complex complex, final int floorInt) {

        final Cell[][] floorGrid = new Cell[(int)cellgrid_width][(int)cellgrid_height];
        final String floor = floorIntToString(floorInt);

        try {
            final JSONHandler jsonHandler = new JSONHandler();
            String json;

            //Get floor plan JSON from assets
            json = JSONHandler.readFloorPlanFromAssets(context, getFloorPlanName(complex, floor) + ".json");
            final HashMap<String, Cell> walkableCells = jsonHandler.parseJsonWalkableCells(json);

            //fill in rooms
            for(final Room r : rooms){
                if(r.getComplex().equals(complex) && r.getFloorString().equals(floor)){
                    floorGrid[r.getXCoordinate()][r.getYCoordinate()] = r;
                }
            }
            //fill in floorconnections
            for(final FloorConnection fc : floorConnections){
                for(final FloorConnectionCell cell : fc.getConnectedCells())
                    if(cell.getComplex().equals(complex) && cell.getFloorString().equals(floor)){
                        floorGrid[cell.getXCoordinate()][cell.getYCoordinate()] = cell;
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
                            floorGrid[x][y] = new Cell(x, y, complex, floor, true);
                        } else{
                            //then fill with non-walkable cell
                            floorGrid[x][y] = new Cell(x, y, complex, floor, false);
                        }
                    }
                }
            }
        } catch (final Exception e) {
            Log.e(TAG, "error building the floor floorGrid", e);
        }
        return floorGrid;
    }


    /**
     * Get all usable floor connections on floor, sorted by distance
     * ways outside buildings and bridge are not allowed on floor = 0 //todo ???
     * @param startCell
     * @param floor
     * @return list of available stairs, elevator etc as {@link FloorConnection} objects
     */
    private ArrayList<FloorConnection> getFloorConnections(final Cell startCell, final int floor) {

        //final ArrayList<FloorConnection> availableFloorConnectionsHelper = new ArrayList<>();
        final ArrayList<FloorConnection> adjustedAvailableFloorConnections = new ArrayList<>();

        final Complex complexStartCell = startCell.getComplex();

        try {
            //check if floor grid for building and floor exists
            final Cell[][] floorgrid = floorGrids.get(complexStartCell).get(floor);

//            // get all reachable floor connections at this floor
//            for (int i = 0; i < floorConnections.size(); i++) {
//
//                for (int j = 0; j < floorConnections.get(i).getConnectedCells().size(); j++) {
//
//                    String buildingFloorconnection = floorConnections.get(i).getConnectedCells().get(j).getBuilding();
//
//                    String floorFloorconnection = floorConnections.get(i).getConnectedCells().get(j).getFloorString();
//                    String floorStartcell = startCell.getFloorString();
//
//                    //floor connection reachable from current floor (building 4 and 5)
//                    if (buildingFloorconnection.equals(buildingStartCell)
//                            && floorFloorconnection.equals(floorStartcell)) {
//
//                        //Next floor in floorGrids reachable from floorconnection
//                        availableFloorConnectionsHelper.addAll(getAvailableFloorConnectionsHelper(i, j, floor, startCell));
//                    }
//                    //floor connection reachable from current floor (building 3/2/1)
//                    else if (isBuilding321(buildingFloorconnection)
//                            && isBuilding321(startCell.getBuilding())
//                            && floorFloorconnection.equals(startCell.getFloorString())) {
//
//                        //Next floor in floorGrids reachable from floorconnection
//                        availableFloorConnectionsHelper.addAll(getAvailableFloorConnectionsHelper(i, j, floor, startCell));
//                    }
//                }
//            }

//            //Sort by distance
//            Collections.sort(availableFloorConnectionsHelper, new Comparator<FloorConnectionCell>() {
//                public int compare(FloorConnectionCell a, FloorConnectionCell b) {
//                    return Integer.compare(a.getCostsPathToCell(), b.getCostsPathToCell());
//                }
//            });
//
//            //Put crossings to the end, crossings not allowed on floor = 0
//            for (int j = 0; j < availableFloorConnectionsHelper.size(); j++) {
//                if (!availableFloorConnectionsHelper.get(j).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {
//                    adjustedAvailableFloorConnections.add(availableFloorConnectionsHelper.get(j));
//                }
//            }
//
//            for (int j = 0; j < availableFloorConnectionsHelper.size(); j++) {
//                if (availableFloorConnectionsHelper.get(j).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_WAY)) {
//                    adjustedAvailableFloorConnections.add(availableFloorConnectionsHelper.get(j));
//                }
//            }
        } catch (final Exception e) {
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
//    private ArrayList<FloorConnection> getAvailableFloorConnectionsHelper(final int i, final int j, final int index, final Cell startCell) {
//
//        final ArrayList<FloorConnection> availableFloorConnectionsHelper = new ArrayList<>();
//
        //        for (int k = 0; k < floorConnections.get(i).getConnectedCells().size(); k++) {
        //            String buildingFloorconnection = floorConnections.get(i).getConnectedCells().get(k).getBuilding();
        //            String buildingFloorgrid = floorGrids.get(index + 1)[0][0].getBuilding();
        //            String floorFloorconnection = floorConnections.get(i).getConnectedCells().get(k).getFloorString();
        //            String floorFloorgrid = floorGrids.get(index + 1)[0][0].getFloorString();
        //
        //            if ((buildingFloorconnection.equals(buildingFloorgrid)
        //                    || (isBuilding321(buildingFloorconnection) && isBuilding321(buildingFloorgrid)))
        //                    && floorFloorconnection.equals(floorFloorgrid)) {
        //
        //                final AStar aStar = new AStar(startCell, floorConnections.get(i).getConnectedCells().get(j), floorGrids.get(index));
        //                final ArrayList<Cell> navigationCells = aStar.getCellsToWalk();
        //
        //                final FloorConnection floorConnectionHelper = floorConnections.get(i);
        //                floorConnectionHelper.setCostsPathToCell(navigationCells.size());
        //                availableFloorConnectionsHelper.add(floorConnectionHelper);
        //            }
        //
        //        }
//        return availableFloorConnectionsHelper;
//    }

//    private static boolean isBuilding321(final String building){
//        if(building.equals(BUILDING_01) || building.equals(BUILDING_02) || building.equals(BUILDING_03)){
//            return true;
//        }
//        return false;
//    }
}
