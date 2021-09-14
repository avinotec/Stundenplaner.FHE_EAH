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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Comparator;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.models.navigation.*;

public class RouteCalculator {

    //Constants
    private static final String TAG = "RouteCalculator"; //$NON-NLS

    private static final String BUILDING_03_02_01_FLOOR_UG = "building_03_02_01_floor_ug";
    private static final String BUILDING_03_02_01_FLOOR_00 = "building_03_02_01_floor_00";
    private static final String BUILDING_03_02_01_FLOOR_01 = "building_03_02_01_floor_01";
    private static final String BUILDING_03_02_01_FLOOR_02 = "building_03_02_01_floor_02";
    private static final String BUILDING_03_02_01_FLOOR_03 = "building_03_02_01_floor_03";
    private static final String BUILDING_03_02_01_FLOOR_04 = "building_03_02_01_floor_04";
    private static final String BUILDING_04_FLOOR_UG = "building_04_floor_ug";
    private static final String BUILDING_04_FLOOR_00 = "building_04_floor_00";
    private static final String BUILDING_04_FLOOR_01 = "building_04_floor_01";
    private static final String BUILDING_04_FLOOR_02 = "building_04_floor_02";
    private static final String BUILDING_04_FLOOR_03 = "building_04_floor_03";
    private static final String BUILDING_05_FLOOR_UG = "building_05_floor_ug";
    private static final String BUILDING_05_FLOOR_00 = "building_05_floor_00";
    private static final String BUILDING_05_FLOOR_01 = "building_05_floor_01";
    private static final String BUILDING_05_FLOOR_02 = "building_05_floor_02";
    private static final String BUILDING_05_FLOOR_03 = "building_05_floor_03";

    private static final String BUILDING_01 = "01";
    private static final String BUILDING_02 = "02";
    private static final String BUILDING_03 = "03";
    private static final String BUILDING_04 = "04";
    private static final String BUILDING_05 = "05";

    private static final String TRANSITION_TYPE_CROSSING = "crossing";

    private static final int GRID_X = 40;
    private static final int GRID_Y = 30;

    private static final String JSON = ".json";

    //Variables
    private Context context;
    private ArrayList<Transition> transitions;
    private Cell startLocation;
    private Cell destinationLocation;
    private ArrayList<Cell> cellsToWalk = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<Cell>>> grids = new ArrayList<>();

    //Constructor
    public RouteCalculator(Context context, Room startLocation, Room destinationLocation, ArrayList<Transition> transitions) {
        this.context = context;
        this.startLocation = startLocation;
        this.destinationLocation = destinationLocation;
        this.transitions = transitions;
    }

    //Get all cells to walk from start to destination location
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("LongLogTag")
    public ArrayList<Cell> getNavigationCells() {

        Cell startCell = startLocation;
        Cell endCell = new Cell();

        //Get grids of floors to use
        grids = navigationBuildings();

        for (int i = 0; i < grids.size(); i++) {
            Log.i("_____TEST_GRIDS_b.f____", grids.get(i).get(0).get(0).getBuilding() + "." + grids.get(i).get(0).get(0).getFloor());
        }

        try {
            //Get paths through all grids
            for (int index = 0; index < grids.size(); index++) {

                Log.i("_____TEST_startCell_b.f x.y w_____", startCell.getBuilding() + "."
                        + startCell.getFloor() + " " + startCell.getXCoordinate() + "." + startCell.getYCoordinate()
                        + " " + startCell.getWalkability());

                //Get reachable transitions and sort by distance
                ArrayList<Transition> usableTransitions = getUsableTransitions(startCell, grids, index);

                //Set endCell
                //Set endCell with destinationLocation on same floor plan
                if (startCell.getBuilding().equals(BUILDING_05) && destinationLocation.getBuilding().equals(BUILDING_05)
                        && startCell.getFloor().equals(destinationLocation.getFloor())) {

                    endCell = destinationLocation;
                }

                if (startCell.getBuilding().equals(BUILDING_04) && destinationLocation.getBuilding().equals(BUILDING_04)
                        && startCell.getFloor().equals(destinationLocation.getFloor())) {

                    endCell = destinationLocation;
                }

                if ((startCell.getBuilding().equals(BUILDING_03)
                        || startCell.getBuilding().equals(BUILDING_02)
                        || startCell.getBuilding().equals(BUILDING_01))
                        && (destinationLocation.getBuilding().equals(BUILDING_03)
                        || destinationLocation.getBuilding().equals(BUILDING_02)
                        || destinationLocation.getBuilding().equals(BUILDING_01))
                        & startCell.getFloor().equals(destinationLocation.getFloor())) {

                    endCell = destinationLocation;
                }

                if (index + 1 < grids.size()) {

                    //Set endCell with destinationLocation on different floor plans
                    //Transition as endCell, same building
                    if (grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_05)
                            && grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_05)) {

                        endCell = usableTransitions.get(0).getSingleCell(startCell.getBuilding(), startCell.getFloor());
                    }

                    if (grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_04)
                            && grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_04)) {

                        endCell = usableTransitions.get(0).getSingleCell(startCell.getBuilding(), startCell.getFloor());
                    }

                    if ((grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_03)
                            || grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_02)
                            || grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_01))
                            && (grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_03)
                            || grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_02)
                            || grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_01))) {

                        endCell = usableTransitions.get(0).getSingleCell(startCell.getBuilding(), startCell.getFloor());
                    }

                    //Transition as endCell, different buildings
                    //if building 4 -> floor -1 to floor 0 in building 3/2/1
                    if (grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_04)
                            && (grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_03)
                            || grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_02)
                            || grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_01))) {

                        for (int i = 0; i < usableTransitions.size(); i++) {

                            if (usableTransitions.get(i).getTypeOfTransition().equals(TRANSITION_TYPE_CROSSING)) {

                                for (int j = 0; j < usableTransitions.get(i).getConnectedCells().size(); j++) {

                                    if (usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_04)) {

                                        endCell = usableTransitions.get(i).getConnectedCells().get(j);
                                    }
                                }
                            }
                        }
                    }

                    //if building 5 -> floor 1 to floor 1 in building 3/2/1
                    if (grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_05)
                            && (grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_03)
                            || grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_02)
                            || grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_01))) {

                        for (int i = 0; i < usableTransitions.size(); i++) {

                            if (usableTransitions.get(i).getTypeOfTransition().equals(TRANSITION_TYPE_CROSSING)) {

                                for (int j = 0; j < usableTransitions.get(i).getConnectedCells().size(); j++) {

                                    if (usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_05)) {

                                        endCell = usableTransitions.get(i).getConnectedCells().get(j);
                                    }
                                }
                            }
                        }
                    }

                    //if building 3 -> floor 0 to floor -1 in building 4
                    if ((grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_03)
                            || grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_02)
                            || grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_01))
                            && grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_04)) {

                        for (int i = 0; i < usableTransitions.size(); i++) {

                            if (usableTransitions.get(i).getTypeOfTransition().equals(TRANSITION_TYPE_CROSSING)) {

                                for (int j = 0; j < usableTransitions.get(i).getConnectedCells().size(); j++) {

                                    if ((usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_03)
                                            || usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_02)
                                            || usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_01))) {

                                        endCell = usableTransitions.get(i).getConnectedCells().get(j);
                                    }
                                }
                            }
                        }
                    }

                    //if building 3 -> floor 1 to floor 1 in building 5
                    if ((grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_03)
                            || grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_02)
                            || grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_01))
                            && grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_05)) {

                        for (int i = 0; i < usableTransitions.size(); i++) {

                            if (usableTransitions.get(i).getTypeOfTransition().equals(TRANSITION_TYPE_CROSSING)) {

                                for (int j = 0; j < usableTransitions.get(i).getConnectedCells().size(); j++) {

                                    if ((usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_03)
                                            || usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_02)
                                            || usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_01))) {

                                        endCell = usableTransitions.get(i).getConnectedCells().get(j);
                                    }
                                }
                            }
                        }
                    }
                }

                Log.i("_____TEST_endCell_b.f x.y w____", endCell.getBuilding() + "." + endCell.getFloor() + " "
                        + endCell.getXCoordinate() + "." + endCell.getYCoordinate() + " " + endCell.getWalkability());

                //Get path through floor
                AStarAlgorithm aStarAlgorithm = new AStarAlgorithm(startCell, endCell, grids.get(index)); //to floor 4 no new cells added, when from building with no 4th floor
                cellsToWalk.addAll(aStarAlgorithm.getNavigationCellsOnGrid());

                Log.i("_____CELLS_TO_WALK_part_____", String.valueOf(aStarAlgorithm.getNavigationCellsOnGrid()));

                //Set next startCell
                if (index + 1 < grids.size()) {

                    //Same building
                    if (grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_05)
                            && grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_05)) {

                        startCell = usableTransitions.get(0).getSingleCell(grids.get(index + 1).get(0).get(0).getBuilding(),
                                grids.get(index + 1).get(0).get(0).getFloor());
                    }

                    if (grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_04)
                            && grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_04)) {

                        startCell = usableTransitions.get(0).getSingleCell(grids.get(index + 1).get(0).get(0).getBuilding(),
                                grids.get(index + 1).get(0).get(0).getFloor());
                    }

                    if ((grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_03)
                            || grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_02)
                            || grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_01))
                            && (grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_03)
                            || grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_02)
                            || grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_01))) {

                        startCell = usableTransitions.get(0).getSingleCell(grids.get(index + 1).get(0).get(0).getBuilding(),
                                grids.get(index + 1).get(0).get(0).getFloor());
                    }

                    //Different building
                    //if building 4 -> floor -1 to floor 0 in building 3/2/1
                    if (grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_04)
                            && grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_03)) {

                        for (int i = 0; i < usableTransitions.size(); i++) {

                            if (usableTransitions.get(i).getTypeOfTransition().equals(TRANSITION_TYPE_CROSSING)) {

                                for (int j = 0; j < usableTransitions.get(i).getConnectedCells().size(); j++) {

                                    if ((usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_03)
                                            || usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_02)
                                            || usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_01))) {

                                        startCell = usableTransitions.get(i).getConnectedCells().get(j);
                                    }
                                }
                            }
                        }
                    }

                    //if building 5 -> floor 1 to floor  1 in building 3/2/1
                    if (grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_05)
                            && grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_03)) {

                        for (int i = 0; i < usableTransitions.size(); i++) {

                            if (usableTransitions.get(i).getTypeOfTransition().equals(TRANSITION_TYPE_CROSSING)) {

                                for (int j = 0; j < usableTransitions.get(i).getConnectedCells().size(); j++) {

                                    if ((usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_03)
                                            || usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_02)
                                            || usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_01))) {

                                        startCell = usableTransitions.get(i).getConnectedCells().get(j);
                                    }
                                }
                            }
                        }
                    }

                    //if building 3/2/1 -> floor 0 to floor -1 in building 4
                    if ((grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_03)
                            || grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_02)
                            || grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_01))
                            && grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_04)) {

                        for (int i = 0; i < usableTransitions.size(); i++) {

                            if (usableTransitions.get(i).getTypeOfTransition().equals(TRANSITION_TYPE_CROSSING)) {

                                for (int j = 0; j < usableTransitions.get(i).getConnectedCells().size(); j++) {

                                    if (usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_04)) {

                                        startCell = usableTransitions.get(i).getConnectedCells().get(j);
                                    }
                                }
                            }
                        }
                    }

                    //if building 3/2/1 -> floor 1 to floor 1 in building 5
                    if ((grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_03)
                            || grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_02)
                            || grids.get(index).get(0).get(0).getBuilding().equals(BUILDING_01))
                            && grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_05)) {

                        for (int i = 0; i < usableTransitions.size(); i++) {

                            if (usableTransitions.get(i).getTypeOfTransition().equals(TRANSITION_TYPE_CROSSING)) {

                                for (int j = 0; j < usableTransitions.get(i).getConnectedCells().size(); j++) {

                                    if (usableTransitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_05)) {

                                        startCell = usableTransitions.get(i).getConnectedCells().get(j);
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

        Log.i("_____TEST_CELLS_TO_WALK_size_____", String.valueOf(cellsToWalk.size()));

        if (BuildConfig.DEBUG) {
            for (Cell cellToWalk : cellsToWalk) {
                Log.i("_____TEST_CELLS_TO_WALK_b.f x.y_____", cellToWalk.getBuilding() + "." + cellToWalk.getFloor() + " "
                        + cellToWalk.getXCoordinate() + "." + cellToWalk.getYCoordinate());
            }
        }
        return cellsToWalk;
    }

    /**
     * Get grids of floors in buildings to use (high level navigation)
     */
    private ArrayList<ArrayList<ArrayList<Cell>>> navigationBuildings() {

        ArrayList<ArrayList<ArrayList<Cell>>> gridsToAdd = new ArrayList<>();

        final int startFloorInteger = startLocation.getFloorAsInteger();
        final int destinationFloorInteger = destinationLocation.getFloorAsInteger();
        int startBuildingInteger = startLocation.getBuildingAsInteger();
        int destinationBuildingInteger = destinationLocation.getBuildingAsInteger();

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
            //Start and end location in same building
            if (startBuildingInteger == destinationBuildingInteger) {

                //Start floor > destination floor
                if (startFloorInteger > destinationFloorInteger) {

                    for (int index = startFloorInteger; index >= destinationFloorInteger; index--) {

                        gridsToAdd.add(buildGrid(startLocation.getBuilding(), getCurrentFloor(index)));
                    }
                }

                //Start floor < destination floor
                if (startFloorInteger < destinationFloorInteger) {

                    for (int index = startFloorInteger; index <= destinationFloorInteger; index++) {

                        gridsToAdd.add(buildGrid(startLocation.getBuilding(), getCurrentFloor(index)));
                    }
                }

                //Start floor = destination floor
                if (startFloorInteger == destinationFloorInteger) {

                    gridsToAdd.add(buildGrid(startLocation.getBuilding(), startLocation.getFloor()));
                }
            }

            //Start and end location in different building
            if (startBuildingInteger != destinationBuildingInteger) {

                //From building 4 to 3 or 4 to 5 or 3 to 5
                if (startBuildingInteger < destinationBuildingInteger) {

                    //From building 4 to 3
                    if (startBuildingInteger == 1 && destinationBuildingInteger == 2) {

                        //Start floor to -1
                        for (int index = startFloorInteger; index >= -1; index--) {

                            gridsToAdd.add(buildGrid(BUILDING_04, getCurrentFloor(index)));
                        }

                        //Destination floor > 0
                        if (destinationFloorInteger > 0) {

                            for (int index = 0; index <= destinationFloorInteger; index++) {

                                gridsToAdd.add(buildGrid(BUILDING_03, getCurrentFloor(index)));
                            }
                        }

                        //Destination floor < 0
                        if (destinationFloorInteger < 0) {

                            for (int index = 0; index >= destinationFloorInteger; index--) {

                                gridsToAdd.add(buildGrid(BUILDING_03, getCurrentFloor(index)));
                            }
                        }

                        //destination floor = 0
                        if (destinationFloorInteger == 0) {

                            gridsToAdd.add(buildGrid(BUILDING_03, getCurrentFloor(0)));
                        }
                    }

                    //From building 3 to 5
                    if (startBuildingInteger == 2 && destinationBuildingInteger == 3) {

                        //Start floor >= 1 to 1
                        if (startFloorInteger >= 1) {

                            for (int index = startFloorInteger; index >= 1; index--) {

                                gridsToAdd.add(buildGrid(BUILDING_03, getCurrentFloor(index)));
                            }
                        }

                        //Start floor < 1 to 1
                        if (startFloorInteger < 1) {

                            for (int index = startFloorInteger; index <= 1; index++) {

                                gridsToAdd.add(buildGrid(BUILDING_03, getCurrentFloor(index)));
                            }
                        }

                        //Destination floor > 1
                        if (destinationFloorInteger > 1) {

                            for (int index = 1; index <= destinationFloorInteger; index++) {

                                gridsToAdd.add(buildGrid(BUILDING_05, getCurrentFloor(index)));
                            }
                        }

                        //Destination floor < 1
                        if (destinationFloorInteger < 1) {

                            for (int index = 1; index >= destinationFloorInteger; index--) {

                                gridsToAdd.add(buildGrid(BUILDING_05, getCurrentFloor(index)));
                            }
                        }

                        //Destination floor = 1
                        if (destinationFloorInteger == 1) {

                            gridsToAdd.add(buildGrid(BUILDING_05, getCurrentFloor(1)));
                        }
                    }

                    //From building 4 to 5
                    if (startBuildingInteger == 1 && destinationBuildingInteger == 3) {

                        //Start floor to -1
                        for (int index = startFloorInteger; index >= -1; index--) {
                            gridsToAdd.add(buildGrid(BUILDING_04, getCurrentFloor(index)));
                        }

                        //Building 3 floor 0 to 1
                        gridsToAdd.add(buildGrid(BUILDING_03, getCurrentFloor(0)));
                        gridsToAdd.add(buildGrid(BUILDING_03, getCurrentFloor(1)));

                        //Destination floor > 1
                        if (destinationFloorInteger > 1) {

                            for (int index = 1; index <= destinationFloorInteger; index++) {

                                gridsToAdd.add(buildGrid(BUILDING_05, getCurrentFloor(index)));
                            }
                        }

                        //Destination floor < 1
                        if (destinationFloorInteger < 1) {

                            for (int index = 1; index >= destinationFloorInteger; index--) {

                                gridsToAdd.add(buildGrid(BUILDING_05, getCurrentFloor(index)));
                            }
                        }

                        //Destination floor = 1
                        if (destinationFloorInteger == 1) {

                            gridsToAdd.add(buildGrid(BUILDING_05, getCurrentFloor(1)));
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

                                gridsToAdd.add(buildGrid(BUILDING_05, getCurrentFloor(index)));
                            }
                        }

                        //Start floor < 1
                        if (startFloorInteger < 1) {

                            for (int index = startFloorInteger; index <= 1; index++) {

                                gridsToAdd.add(buildGrid(BUILDING_05, getCurrentFloor(index)));
                            }
                        }

                        //Destination floor > 1
                        if (destinationFloorInteger > 1) {

                            for (int index = 1; index <= destinationFloorInteger; index++) {

                                gridsToAdd.add(buildGrid(BUILDING_03, getCurrentFloor(index)));
                            }
                        }

                        //Destination floor < 1
                        if (destinationFloorInteger < 1) {

                            for (int index = 1; index >= destinationFloorInteger; index--) {

                                gridsToAdd.add(buildGrid(BUILDING_03, getCurrentFloor(index)));
                            }
                        }

                        //Destination floor = 1
                        if (destinationFloorInteger == 1) {

                            gridsToAdd.add(buildGrid(BUILDING_03, getCurrentFloor(1)));
                        }
                    }

                    //From building 3 to 4
                    if (startBuildingInteger == 2 && destinationBuildingInteger == 1) {

                        //Start floor >= 0
                        if (startFloorInteger >= 0) {

                            for (int index = startFloorInteger; index >= 0; index--) {

                                gridsToAdd.add(buildGrid(BUILDING_03, getCurrentFloor(index)));
                            }
                        }

                        //Start floor < 0
                        if (startFloorInteger < 0) {

                            for (int index = startFloorInteger; index <= 0; index++) {

                                gridsToAdd.add(buildGrid(BUILDING_03, getCurrentFloor(index)));
                            }
                        }

                        //Destination floor > -1
                        if (destinationFloorInteger > -1) {

                            for (int index = -1; index <= destinationFloorInteger; index++) {

                                gridsToAdd.add(buildGrid(BUILDING_04, getCurrentFloor(index)));
                            }
                        }

                        //Destination floor = -1
                        if (destinationFloorInteger == -1) {

                            gridsToAdd.add(buildGrid(BUILDING_04, getCurrentFloor(-1)));
                        }
                    }

                    //From building 5 to 4
                    if (startBuildingInteger == 3 && destinationBuildingInteger == 1) {

                        //Start floor >= 1
                        if (startFloorInteger >= 1) {

                            for (int index = startFloorInteger; index >= 1; index--) {

                                gridsToAdd.add(buildGrid(BUILDING_05, getCurrentFloor(index)));
                            }
                        }

                        //Start floor < 1
                        if (startFloorInteger < 1) {

                            for (int index = startFloorInteger; index <= 1; index++) {

                                gridsToAdd.add(buildGrid(BUILDING_05, getCurrentFloor(index)));
                            }
                        }

                        //Building 3 floor 1 to 0
                        gridsToAdd.add(buildGrid(BUILDING_03, getCurrentFloor(1)));
                        gridsToAdd.add(buildGrid(BUILDING_03, getCurrentFloor(0)));

                        //Destination floor > -1
                        if (destinationFloorInteger > -1) {

                            for (int index = -1; index <= destinationFloorInteger; index++) {

                                gridsToAdd.add(buildGrid(BUILDING_04, getCurrentFloor(index)));
                            }
                        }

                        //Destination floor = -1
                        if (destinationFloorInteger == -1) {
                            gridsToAdd.add(buildGrid(BUILDING_04, getCurrentFloor(-1)));
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
     * Integer to String for floors
     */
    private String getCurrentFloor( final int index) {

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
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private ArrayList<ArrayList<Cell>> buildGrid(final String building, final String floor) {

        ArrayList<ArrayList<Cell>> grid = new ArrayList<>();

        try {
            JSONHandler jsonHandler = new JSONHandler();
            String json;

            //Get floor plan JSON from assets
            json = jsonHandler.readJsonFromAssets(context, getFloorPlan(building, floor) + JSON);
            final ArrayList<Cell> walkableCells = jsonHandler.parseJsonWalkableCells(json);

            for (int x = 0; x < GRID_X; x++) {

                grid.add(new ArrayList<Cell>());

                for (int y = 0; y < GRID_Y; y++) {

                    boolean walkable = false;

                    for (int i = 0; i < walkableCells.size(); i++) {

                        if (walkableCells.get(i).getXCoordinate() == x && walkableCells.get(i).getYCoordinate() == y) {

                            grid.get(x).add(new Cell(x, y, building, floor, true));
                            walkable = true;
                        }
                    }

                    if (!walkable) {

                        grid.get(x).add(new Cell(x, y, building, floor, false));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error building the floor grid", e);
        }
        return grid;
    }

    /**
     * Get floor plan String without ending (.json / .jpeg)
     * @param building
     * @param floor
     * @return
     */
    private String getFloorPlan(final String building, final String floor) {

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
     * Get all usable transitions on current floor plan, sorted by distance, crossings are not allowed on index = 0
     * @param startCell
     * @param grids
     * @param index
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<Transition> getUsableTransitions(final Cell startCell, final ArrayList<ArrayList<ArrayList<Cell>>> grids, final int index) {

        ArrayList<Transition> usableTransitionsHelper = new ArrayList<>();
        ArrayList<Transition> adjustedUsableTransitions = new ArrayList<>();

        try {
            if (index + 1 < grids.size()) {

                //Get all reachable transitions
                for (int i = 0; i < transitions.size(); i++) {

                    for (int j = 0; j < transitions.get(i).getConnectedCells().size(); j++) {

                        //Transition reachable from current floor (building 5)
                        if (transitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_05)
                                && startCell.getBuilding().equals(BUILDING_05)
                                && transitions.get(i).getConnectedCells().get(j).getFloor().equals(startCell.getFloor())) {

                            //Next floor in grids reachable from transition
                            usableTransitionsHelper.addAll(getUsableTransitionsHelper(i, j, index, startCell));
                        }

                        //Transition reachable from current floor (building 4)
                        if (transitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_04)
                                && startCell.getBuilding().equals(BUILDING_04)
                                && transitions.get(i).getConnectedCells().get(j).getFloor().equals(startCell.getFloor())) {

                            //Next floor in grids reachable from transition
                            usableTransitionsHelper.addAll(getUsableTransitionsHelper(i, j, index, startCell));
                        }

                        //Transition reachable from current floor (building 3/2/1)
                        if ((transitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_03)
                                || transitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_02)
                                || transitions.get(i).getConnectedCells().get(j).getBuilding().equals(BUILDING_01))
                                && (startCell.getBuilding().equals(BUILDING_03)
                                || startCell.getBuilding().equals(BUILDING_02)
                                || startCell.getBuilding().equals(BUILDING_01))
                                && transitions.get(i).getConnectedCells().get(j).getFloor().equals(startCell.getFloor())) {

                            //Next floor in grids reachable from transition
                            usableTransitionsHelper.addAll(getUsableTransitionsHelper(i, j, index, startCell));
                        }
                    }
                }

                //Sort by distance
                usableTransitionsHelper.sort(new Comparator<Transition>() {
                    public int compare(Transition TransitionOne, Transition TransitionTwo) {
                        return Integer.compare(TransitionOne.getFinalCost(), TransitionTwo.getFinalCost());
                    }
                });

                //Put crossings to the end, crossings not allowed on index = 0
                for (int j = 0; j < usableTransitionsHelper.size(); j++) {

                    if (!usableTransitionsHelper.get(j).getTypeOfTransition().equals(TRANSITION_TYPE_CROSSING)) {

                        adjustedUsableTransitions.add(usableTransitionsHelper.get(j));
                    }
                }

                for (int j = 0; j < usableTransitionsHelper.size(); j++) {

                    if (usableTransitionsHelper.get(j).getTypeOfTransition().equals(TRANSITION_TYPE_CROSSING)) {

                        adjustedUsableTransitions.add(usableTransitionsHelper.get(j));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error getting reachable transitions", e);
        }
        return adjustedUsableTransitions;
    }

    /**
     * Get usable transitions helper method
     * @param i
     * @param j
     * @param index
     * @param startCell
     * @return
     */
    private ArrayList<Transition> getUsableTransitionsHelper(final int i, final int j, final int index, final Cell startCell) {

        final ArrayList<Transition> usableTransitionsHelper = new ArrayList<>();

        for (int k = 0; k < transitions.get(i).getConnectedCells().size(); k++) {

            if (transitions.get(i).getConnectedCells().get(k).getBuilding().equals(BUILDING_05)
                    && grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_05)
                    && transitions.get(i).getConnectedCells().get(k).getFloor().equals(grids.get(index + 1).get(0).get(0).getFloor())) {

                final AStarAlgorithm aStarAlgorithm = new AStarAlgorithm(startCell, transitions.get(i).getConnectedCells().get(j), grids.get(index));
                final ArrayList<Cell> navigationCells = aStarAlgorithm.getNavigationCellsOnGrid();

                final Transition transitionHelper = transitions.get(i);
                transitionHelper.setFinalCost(navigationCells.size());
                usableTransitionsHelper.add(transitionHelper);
            }

            if (transitions.get(i).getConnectedCells().get(k).getBuilding().equals(BUILDING_04)
                    && grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_04)
                    && transitions.get(i).getConnectedCells().get(k).getFloor().equals(grids.get(index + 1).get(0).get(0).getFloor())) {

                final AStarAlgorithm aStarAlgorithm = new AStarAlgorithm(startCell, transitions.get(i).getConnectedCells().get(j), grids.get(index));
                final ArrayList<Cell> navigationCells = aStarAlgorithm.getNavigationCellsOnGrid();

                final Transition transitionHelper = transitions.get(i);
                transitionHelper.setFinalCost(navigationCells.size());
                usableTransitionsHelper.add(transitionHelper);
            }

            if ((transitions.get(i).getConnectedCells().get(k).getBuilding().equals(BUILDING_03)
                    || transitions.get(i).getConnectedCells().get(k).getBuilding().equals(BUILDING_02)
                    || transitions.get(i).getConnectedCells().get(k).getBuilding().equals(BUILDING_01))
                    && (grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_03)
                    || grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_02)
                    || grids.get(index + 1).get(0).get(0).getBuilding().equals(BUILDING_01))
                    && transitions.get(i).getConnectedCells().get(k).getFloor().equals(grids.get(index + 1).get(0).get(0).getFloor())) {

                final AStarAlgorithm aStarAlgorithm = new AStarAlgorithm(startCell, transitions.get(i).getConnectedCells().get(j), grids.get(index));
                final ArrayList<Cell> navigationCells = aStarAlgorithm.getNavigationCellsOnGrid();

                final Transition transitionHelper = transitions.get(i);
                transitionHelper.setFinalCost(navigationCells.size());
                usableTransitionsHelper.add(transitionHelper);
            }
        }
        return usableTransitionsHelper;
    }
}
