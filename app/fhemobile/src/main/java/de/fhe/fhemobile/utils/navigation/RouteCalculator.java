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
import static de.fhe.fhemobile.utils.navigation.NavigationUtils.getNameOfFloorPlanGrid;

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
    private final Context context;
    private final ArrayList<FloorConnection> floorConnections;
    private final ArrayList<Room> rooms;
    private final Cell startLocation;
    private final Cell destLocation;
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
    public RouteCalculator(final Context context, final Room startLocation, final Room destLocation,
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

        //Get grids of floors to use - fills in variable floorGrids
        getNeededFloorGrids();

        try {

            //route just within one complex, user does not have to change between complexes
            if(startLocation.getComplex().equals(destLocation.getComplex())){
                //construct an instance of the Navigation Algorithm (AStar)
                AStar aStar = new AStar(startLocation, destLocation, floorGrids, floorConnections); //to floor 4 no new cells added, when from building with no 4th floor
                //compute route and save it to cellsToWalk
                cellsToWalk.addAll(aStar.getCellsToWalk());
            }
            //start and destination complex differ -> user has to change between complexes
            else{
                //todo: Routenzusammensetzung mit Gebäudewechsel
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
     * Builds grids of all needed floors in all used buildings and saves it to variable "floorGrids"
     */
    private void getNeededFloorGrids() {

        Complex startComplex = startLocation.getComplex();
        Complex destinationComplex = destLocation.getComplex();


        //add floorgrids of the start and destination building,
        //für einen Komplex/Gebäude werden immer alle floorgrids hinzugefügt,
        //es werden nur Gebäude ausgelassen die weder Start noch Ziel enthalten
        try {
            if(startComplex.equals(Complex.COMPLEX_321)
                    || destinationComplex.equals(Complex.COMPLEX_321)){
                floorGrids.put(Complex.COMPLEX_321, new HashMap<>());

                for(int i = -1; i <= 4; i++){
                    floorGrids.get(Complex.COMPLEX_321).put(i, buildFloorGrid(Complex.COMPLEX_321, i));
                }
            }
            if(startComplex.equals(Complex.COMPLEX_4)
                    || destinationComplex.equals(Complex.COMPLEX_4)){
                floorGrids.put(Complex.COMPLEX_4, new HashMap<>());

                for(int i = -1; i <= 5; i++){
                    floorGrids.get(Complex.COMPLEX_4).put(i, buildFloorGrid(Complex.COMPLEX_4, i));

                }
            }
            if(startComplex.equals(Complex.COMPLEX_5)
                    || destinationComplex.equals(Complex.COMPLEX_5)){
                floorGrids.put(Complex.COMPLEX_5, new HashMap<>());

                for(int i = -2; i <= 3; i++){
                    floorGrids.get(Complex.COMPLEX_5).put(i, buildFloorGrid(Complex.COMPLEX_5, i));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error building floor grids", e);
        }

    }


    /**
     * Builds a grid of all cells of a floorplan
     * @param complex {@link Complex}
     * @param floorInt floor as integer (ug = -1)
     * @return floorgrid as 2D Arraylist with all cells of the floor
     */
    private Cell[][] buildFloorGrid(final Complex complex, final int floorInt) {

        final Cell[][] floorGrid = new Cell[(int)cellgrid_width][(int)cellgrid_height];
        final String floor = floorIntToString(floorInt);

        try {
            final JSONHandler jsonHandler = new JSONHandler();
            String json;

            //Get floor plan JSON from assets
            json = JSONHandler.readFloorGridFromAssets(context, getNameOfFloorPlanGrid(complex, floor) + ".json");
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
            Log.e(TAG, "error building the floorGrid (" + complex.toString() + "," + floorInt+")", e);
        }
        return floorGrid;
    }


    /**
     * Get all usable floor connections on floor, sorted by distance
     * @param startCell
     * @param floor
     * @return list of available stairs and elevators as {@link FloorConnection} objects
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
//                if (!availableFloorConnectionsHelper.get(j).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_EXIT)) {
//                    adjustedAvailableFloorConnections.add(availableFloorConnectionsHelper.get(j));
//                }
//            }
//
//            for (int j = 0; j < availableFloorConnectionsHelper.size(); j++) {
//                if (availableFloorConnectionsHelper.get(j).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_EXIT)) {
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
