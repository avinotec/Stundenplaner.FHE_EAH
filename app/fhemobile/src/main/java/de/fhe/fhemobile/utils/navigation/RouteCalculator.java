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

import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_01;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_02;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_03;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_03_02_01_FLOOR_00;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_03_02_01_FLOOR_01;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_03_02_01_FLOOR_02;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_03_02_01_FLOOR_03;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_03_02_01_FLOOR_04;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_03_02_01_FLOOR_UG;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_04;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_04_FLOOR_00;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_04_FLOOR_01;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_04_FLOOR_02;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_04_FLOOR_03;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_04_FLOOR_UG;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_05;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_05_FLOOR_00;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_05_FLOOR_01;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_05_FLOOR_02;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_05_FLOOR_03;
import static de.fhe.fhemobile.utils.Define.Navigation.BUILDING_05_FLOOR_UG;
import static de.fhe.fhemobile.utils.Define.Navigation.FLOORCONNECTION_TYPE_WAY;
import static de.fhe.fhemobile.utils.Define.Navigation.cellgrid_height;
import static de.fhe.fhemobile.utils.Define.Navigation.cellgrid_width;

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

/**
 *  Klasse für Routenberechnung über mehrere Stockwerke und Gebäude
 *  source: Bachelor Thesis from Tim Münziger from SS2020
 *
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
     * Constuctor
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
                if (startCell.getBuilding().equals(destinationLocation.getBuilding())
                        && startCell.getFloor().equals(destinationLocation.getFloor())) {

                    endCell = destinationLocation;

                } else  if (isBuilding321(startCell.getBuilding())
                        && isBuilding321(destinationLocation.getBuilding())
                        && startCell.getFloor().equals(destinationLocation.getFloor())) {

                    endCell = destinationLocation;

                }

                if (index + 1 < floorGrids.size()) {

                    //Set endCell with destinationLocation on different floor plans
                    //FloorConnection as endCell, same building
                    if (floorGrids.get(index)[0][0].getBuilding().equals(
                            floorGrids.get(index + 1)[0][0].getBuilding())) {
                        endCell = availableFloorConnections.get(0).getConnectedCell(startCell.getBuilding(), startCell.getFloor());
                    }
                    else if (isBuilding321(floorGrids.get(index)[0][0].getBuilding())
                            && isBuilding321(floorGrids.get(index + 1)[0][0].getBuilding())) {

                        endCell = availableFloorConnections.get(0).getConnectedCell(startCell.getBuilding(), startCell.getFloor());
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
                                floorGrids.get(index + 1)[0][0].getFloor());
                    }
                    else if (isBuilding321(floorGrids.get(index)[0][0].getBuilding())
                            && isBuilding321(floorGrids.get(index + 1)[0][0].getBuilding())) {

                        startCell = availableFloorConnections.get(0).getConnectedCell(floorGrids.get(index + 1)[0][0].getBuilding(),
                                floorGrids.get(index + 1)[0][0].getFloor());
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

        final int startFloorInteger = startLocation.getFloorAsInteger();
        final int destinationFloorInteger = destinationLocation.getFloorAsInteger();
        int startBuildingInteger = startLocation.getBuildingAsInteger();
        int destinationBuildingInteger = destinationLocation.getBuildingAsInteger();

        //TODO sind das magische Zahlen? Was ist die Bedeutung der Zahlen?

        switch (startBuildingInteger) {
            case 4:
                startBuildingInteger = 1;
                break;
            case 1:
            case 2:
            case 3:
                startBuildingInteger = 2;
                break;
            case 5:
                startBuildingInteger = 3;
                break;
            default:
                break;
        }

        switch (destinationBuildingInteger) {
            case 4:
                destinationBuildingInteger = 1;
                break;
            case 1:
            case 2:
            case 3:
                destinationBuildingInteger = 2;
                break;
            case 5:
                destinationBuildingInteger = 3;
                break;
            default:
                break;
        }

        try {
            //Start and destination location are in same building
            if (startBuildingInteger == destinationBuildingInteger) {

                for (int index = startFloorInteger; index >= destinationFloorInteger;) {
                    if ((startFloorInteger < destinationFloorInteger)) {
                        gridsToAdd.add(buildFloorGrid(startLocation.getBuilding(), floorIndexToString(index)));
                        index++;
                    } else if (startFloorInteger > destinationFloorInteger){
                        gridsToAdd.add(buildFloorGrid(startLocation.getBuilding(), floorIndexToString(index)));
                        index--;
                    } else if (startFloorInteger == destinationFloorInteger){
                        gridsToAdd.add(buildFloorGrid(startLocation.getBuilding(), startLocation.getFloor()));
                        break;
                    }
                }

            }

            //Start and destination location are in different building
            else {

                //From building 4 to 3 or 4 to 5 or 3 to 5
                if (startBuildingInteger < destinationBuildingInteger) {

                    //From building 4 to 3
                    if (startBuildingInteger == 1 && destinationBuildingInteger == 2) {

                        //Start floor to -1
                        for (int index = startFloorInteger; index >= -1; index--) {
                            gridsToAdd.add(buildFloorGrid(BUILDING_04, floorIndexToString(index)));
                        }

                        //Destination floor > 0
                        if (destinationFloorInteger > 0) {
                            for (int index = 0; index <= destinationFloorInteger; index++) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_03, floorIndexToString(index)));
                            }
                        }
                        //Destination floor < 0
                        else if (destinationFloorInteger < 0) {
                            for (int index = 0; index >= destinationFloorInteger; index--) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_03, floorIndexToString(index)));
                            }
                        }
                        //destination floor = 0
                        else /* redundant if (destinationFloorInteger == 0) */ {
                            gridsToAdd.add(buildFloorGrid(BUILDING_03, floorIndexToString(0)));
                        }
                    }

                    //From building 3 to 5
                    if (startBuildingInteger == 2 && destinationBuildingInteger == 3) {

                        //Start floor >= 1 to 1
                        if (startFloorInteger >= 1) {
                            for (int index = startFloorInteger; index >= 1; index--) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_03, floorIndexToString(index)));
                            }
                        }
                        //Start floor < 1 to 1
                        else /* redundant if (startFloorInteger < 1) */ {
                            for (int index = startFloorInteger; index <= 1; index++) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_03, floorIndexToString(index)));
                            }
                        }

                        //Destination floor > 1
                        if (destinationFloorInteger > 1) {
                            for (int index = 1; index <= destinationFloorInteger; index++) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_05, floorIndexToString(index)));
                            }
                        }
                        //Destination floor < 1
                        else if (destinationFloorInteger < 1) {
                            for (int index = 1; index >= destinationFloorInteger; index--) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_05, floorIndexToString(index)));
                            }
                        }
                        //Destination floor = 1
                        else /* redundant if (destinationFloorInteger == 1) */ {
                            gridsToAdd.add(buildFloorGrid(BUILDING_05, floorIndexToString(1)));
                        }
                    }

                    //From building 4 to 5
                    if (startBuildingInteger == 1 && destinationBuildingInteger == 3) {

                        //Start floor to -1
                        for (int index = startFloorInteger; index >= -1; index--) {
                            gridsToAdd.add(buildFloorGrid(BUILDING_04, floorIndexToString(index)));
                        }

                        //Building 3 floor 0 to 1
                        gridsToAdd.add(buildFloorGrid(BUILDING_03, floorIndexToString(0)));
                        gridsToAdd.add(buildFloorGrid(BUILDING_03, floorIndexToString(1)));

                        //Destination floor > 1
                        if (destinationFloorInteger > 1) {
                            for (int index = 1; index <= destinationFloorInteger; index++) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_05, floorIndexToString(index)));
                            }
                        }
                        //Destination floor < 1
                        else if (destinationFloorInteger < 1) {
                            for (int index = 1; index >= destinationFloorInteger; index--) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_05, floorIndexToString(index)));
                            }
                        }
                        //Destination floor = 1
                        else /* redundant if (destinationFloorInteger == 1) */ {
                            gridsToAdd.add(buildFloorGrid(BUILDING_05, floorIndexToString(1)));
                        }
                    }
                }

                //From building 5 to 3 or 3 to 4 or 5 to 4
                if (destinationBuildingInteger < startBuildingInteger) {

                    //From building 5 to 3
                    if (startBuildingInteger == 3 && destinationBuildingInteger == 2) {

                        //Start floor >= 1
                        if (startFloorInteger >= 1) {
                            for (int index = startFloorInteger; index >= 1; index--) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_05, floorIndexToString(index)));
                            }
                        }
                        //Start floor < 1
                        else if (startFloorInteger < 1) {
                            for (int index = startFloorInteger; index <= 1; index++) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_05, floorIndexToString(index)));
                            }
                        }

                        //Destination floor > 1
                        if (destinationFloorInteger > 1) {

                            for (int index = 1; index <= destinationFloorInteger; index++) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_03, floorIndexToString(index)));
                            }
                        }
                        //Destination floor < 1
                        else if (destinationFloorInteger < 1) {

                            for (int index = 1; index >= destinationFloorInteger; index--) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_03, floorIndexToString(index)));
                            }
                        }
                        //Destination floor = 1
                        else if (destinationFloorInteger == 1) {
                            gridsToAdd.add(buildFloorGrid(BUILDING_03, floorIndexToString(1)));
                        }
                    }

                    //From building 3 to 4
                    if (startBuildingInteger == 2 && destinationBuildingInteger == 1) {

                        //Start floor >= 0
                        if (startFloorInteger >= 0) {

                            for (int index = startFloorInteger; index >= 0; index--) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_03, floorIndexToString(index)));
                            }
                        }
                        //Start floor < 0
                        else if (startFloorInteger < 0) {

                            for (int index = startFloorInteger; index <= 0; index++) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_03, floorIndexToString(index)));
                            }
                        }

                        //Destination floor > -1
                        if (destinationFloorInteger > -1) {
                            for (int index = -1; index <= destinationFloorInteger; index++) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_04, floorIndexToString(index)));
                            }
                        }
                        //Destination floor = -1
                        else if (destinationFloorInteger == -1) {
                            gridsToAdd.add(buildFloorGrid(BUILDING_04, floorIndexToString(-1)));
                        }
                    }

                    //From building 5 to 4
                    if (startBuildingInteger == 3 && destinationBuildingInteger == 1) {

                        //Start floor >= 1
                        if (startFloorInteger >= 1) {
                            for (int index = startFloorInteger; index >= 1; index--) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_05, floorIndexToString(index)));
                            }
                        }
                        //Start floor < 1
                        else if (startFloorInteger < 1) {
                            for (int index = startFloorInteger; index <= 1; index++) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_05, floorIndexToString(index)));
                            }
                        }

                        //Building 3 floor 1 to 0
                        gridsToAdd.add(buildFloorGrid(BUILDING_03, floorIndexToString(1)));
                        gridsToAdd.add(buildFloorGrid(BUILDING_03, floorIndexToString(0)));

                        //Destination floor > -1
                        if (destinationFloorInteger > -1) {
                            for (int index = -1; index <= destinationFloorInteger; index++) {
                                gridsToAdd.add(buildFloorGrid(BUILDING_04, floorIndexToString(index)));
                            }
                        }
                        //Destination floor = -1
                        else if (destinationFloorInteger == -1) {
                            gridsToAdd.add(buildFloorGrid(BUILDING_04, floorIndexToString(-1)));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error navigating through buildings", e);
        }
        return gridsToAdd;
    }

    /**
     * Get the string representation of the floor
     * @param index
     * @return floor name as string
     */
    private String floorIndexToString(final int index) {

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
     * Build grid of a floor plan
     * @param building
     * @param floor
     * @return 2D Arraylist with all cells of the floor
     */
    private Cell[][] buildFloorGrid(final String building, final String floor) {

        Cell[][] floorGrid = new Cell[(int)cellgrid_width][(int)cellgrid_height];

        try {
            JSONHandler jsonHandler = new JSONHandler();
            String json;

            //Get floor plan JSON from assets
            //TODO schon wieder die Rooms einlesen
            json = JSONHandler.readJsonFromAssets(context, floorplanName(building, floor) + ".json");
            final HashMap<String, Cell> walkableCells = jsonHandler.parseJsonWalkableCells(json);

            //fill in rooms
            for(Room r : rooms){
                if(r.getBuilding().equals(building) && r.getFloor().equals(floor)){
                    floorGrid[r.getXCoordinate()][r.getYCoordinate()] = r;
                }
            }
            //fill in floorconnections
            for(FloorConnection fc : floorConnections){
                for(Cell cell : fc.getConnectedCells())
                    if(cell.getBuilding().equals(building) && cell.getFloor().equals(floor)){
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
                        if(walkableCells.get(Integer.toString(x) + '_' + Integer.toString(y)) != null){ //x_y is used as key to store the cell
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
     * Get name of file for the floor plan - corresponds to file name without ending (.json / .jpeg /.png)
     * @param building
     * @param floor
     * @return Name as String
     */
    private String floorplanName(final String building, final String floor) {

        String floorPlan;

        switch (building + "." + floor) {
            case "01.ug":
            case "02.ug":
            case "03.ug":
                floorPlan = BUILDING_03_02_01_FLOOR_UG;
                break;
            case "01.00":
            case "02.00":
            case "03.00":
                floorPlan = BUILDING_03_02_01_FLOOR_00;
                break;
            case "01.01":
            case "02.01":
            case "03.01":
                floorPlan = BUILDING_03_02_01_FLOOR_01;
                break;
            case "01.02":
            case "02.02":
            case "03.02":
                floorPlan = BUILDING_03_02_01_FLOOR_02;
                break;
            case "01.03":
            case "02.03":
            case "03.03":
                floorPlan = BUILDING_03_02_01_FLOOR_03;
                break;
            case "01.04":
            case "02.04":
            case "03.04":
                floorPlan = BUILDING_03_02_01_FLOOR_04;
                break;
            case "04.ug":
                floorPlan = BUILDING_04_FLOOR_UG;
                break;
            case "04.00":
                floorPlan = BUILDING_04_FLOOR_00;
                break;
            case "04.01":
                floorPlan = BUILDING_04_FLOOR_01;
                break;
            case "04.02":
                floorPlan = BUILDING_04_FLOOR_02;
                break;
            case "04.03":
                floorPlan = BUILDING_04_FLOOR_03;
                break;
            case "05.ug":
                floorPlan = BUILDING_05_FLOOR_UG;
                break;
            case "05.00":
                floorPlan = BUILDING_05_FLOOR_00;
                break;
            case "05.01":
                floorPlan = BUILDING_05_FLOOR_01;
                break;
            case "05.02":
                floorPlan = BUILDING_05_FLOOR_02;
                break;
            case "05.03":
                floorPlan = BUILDING_05_FLOOR_03;
                break;
            default:
                floorPlan = null;
        }
        return floorPlan;
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
                        String floorFloorconnection = floorConnections.get(i).getConnectedCells().get(j).getFloor();
                        String floorStartcell = startCell.getFloor();

                        //floor connection reachable from current floor (building 4 and 5)
                        if (buildingFloorconnection.equals(buildingStartcell)
                                && floorFloorconnection.equals(floorStartcell)) {

                            //Next floor in floorGrids reachable from floorconnection
                            availableFloorConnectionsHelper.addAll(getAvailableFloorConnectionsHelper(i, j, index, startCell));
                        }
                        //floor connection reachable from current floor (building 3/2/1)
                        else if (isBuilding321(buildingFloorconnection)
                                && isBuilding321(startCell.getBuilding())
                                && floorFloorconnection.equals(startCell.getFloor())) {

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
            Log.e(TAG, "error getting reachable transitions", e);
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
            String floorFloorconnection = floorConnections.get(i).getConnectedCells().get(k).getFloor();
            String floorFloorgrid = floorGrids.get(index + 1)[0][0].getFloor();

            if ((buildingFloorconnection == buildingFloorgrid
                    || (isBuilding321(buildingFloorconnection) && isBuilding321(buildingFloorgrid)))
                    && floorFloorconnection == floorFloorgrid) {

                final AStar aStar = new AStar(startCell, floorConnections.get(i).getConnectedCells().get(j), floorGrids.get(index));
                final ArrayList<Cell> navigationCells = aStar.getCellsToWalk();

                final FloorConnection floorConnectionHelper = floorConnections.get(i);
                floorConnectionHelper.setCostsPathToCell(navigationCells.size());
                availableFloorConnectionsHelper.add(floorConnectionHelper);
            }

        }
        return availableFloorConnectionsHelper;
    }

    private boolean isBuilding321(String building){
        if(building.equals(BUILDING_01) || building.equals(BUILDING_02) || building.equals(BUILDING_03)){
            return true;
        } else {
            return false;
        }
    }
}
