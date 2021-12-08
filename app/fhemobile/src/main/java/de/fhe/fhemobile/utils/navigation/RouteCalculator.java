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
import static de.fhe.fhemobile.utils.navigation.NavigationUtils.getPathToFloorPlanGrid;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.Exit;
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
    private final ArrayList<Exit> exits;
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
     */
    public RouteCalculator(final Context context, final Room startLocation, final Room destLocation,
                           final ArrayList<FloorConnection> floorConnections, final ArrayList<Exit> exits) {
        this.context = context;
        this.startLocation = startLocation;
        this.destLocation = destLocation;
        this.floorConnections = floorConnections;
        this.exits = exits;
    }

    /**
     * Calculates the route and returns a list of all cells to walk
     * @return ArrayList of all cells at the route
     */
    public ArrayList<Cell> getWholeRoute() {

        //Get grids of floors to use - fills in variable floorGrids
        getNeededFloorGrids();

        try {

            Complex startComplex = startLocation.getComplex();
            Complex destComplex = destLocation.getComplex();

            //ROUTE OHNE GEBÄUDEWECHSEL
            //route just within one complex, user does not have to change between complexes
            if(startComplex.equals(destLocation.getComplex())){
                //construct an instance of the Navigation Algorithm (AStar)
                AStar aStar = new AStar(startLocation, destLocation, floorGrids, floorConnections);
                //compute route and save it to cellsToWalk
                cellsToWalk.addAll(aStar.getCellsToWalk());
            }

            //ROUTE MIT GEBÄUDEWECHSEL
            //start and destination complex differ -> user has to change between complexes
            else{

                //determine Exit to use for leaving complex to the start complex
                // and determine Exit to use for entering the destination complex
                Exit exit = null;
                Exit entry = null;
                for(Iterator<Exit> it = exits.iterator(); it.hasNext(); ){
                    Exit exitIt = it.next();

                    //use exitIt as exit if exitIt belongs to start complex
                    // and is an exit to the destination complex
                    if(exitIt.getComplex().equals(startComplex)){
                        if(exitIt.getExitTo().contains(destComplex)){
                            if(exit == null) {
                                exit = exitIt;
                            }
                            else Log.i(TAG,"More than one possible exit for complex "+startComplex.toString()+" to "+destComplex.toString());

                            //exit and entry found
                            if (entry != null) break;
                        }
                    }

                    //use exitIt as entry if exitIt belongs to the destination complex
                    // and is an entry when coming from the start complex
                    if (exitIt.getComplex().equals(destComplex)) {
                        if (exitIt.getEntryFrom().contains(startComplex)) {
                            if (entry == null) {
                                entry = exitIt;
                            } else
                                Log.i(TAG, "More than one possible entry to complex " + destComplex.toString() + " from complex " + startComplex.toString());

                            //exit and entry found
                            if(exit != null) break;
                        }
                    }
                }

                //route to get out of start complex
                AStar aStar1 = new AStar(startLocation, exit, floorGrids, floorConnections);
                cellsToWalk.addAll(aStar1.getCellsToWalk());

                //route to get to destination within the destination complex
                AStar aStar2 = new AStar(entry, destLocation, floorGrids, floorConnections);
                cellsToWalk.addAll(aStar2.getCellsToWalk());

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
        final String floor = floorIntToString(complex, floorInt);

        try {
            final JSONHandler jsonHandler = new JSONHandler();
            String json;

            //Get floor plan JSON from assets
            String filename = getPathToFloorPlanGrid(complex, floor);
            json = JSONHandler.readFloorGridFromAssets(context, filename);
            final HashMap<String, Cell> walkableCells = jsonHandler.parseJsonWalkableCells(json);

            //fill in rooms
            if(MainActivity.rooms.isEmpty()) JSONHandler.loadRooms(context);
            for(final Room r : MainActivity.rooms){
                if(r.getComplex().equals(complex) && r.getFloorString().equals(floor)){
                    floorGrid[r.getXCoordinate()][r.getYCoordinate()] = r;
                }
            }
            //fill in exits
            for(final Exit ex : exits){
                if(ex.getComplex().equals(complex) && ex.getFloorString().equals(floor)){
                    floorGrid[ex.getXCoordinate()][ex.getYCoordinate()] = ex;
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

}
