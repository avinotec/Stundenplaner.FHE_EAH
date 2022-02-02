/*
 *  Copyright (c) 2019-2021 Ernst-Abbe-Hochschule Jena
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

import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.Complex;

public class NavigationUtils {

    /**
     * All files concerning the floorplan of a certain complex and floor have the same name
     * (just different file extension e.g. json or png and different locations)
     * e.g. "building_05_floor_ug1" or "building_03_02_01_floor_02"
     * @param complex
     * @param floor (e.g. "ug1", "00", "02", "3Z")
     * @return name of the floorplan file
     */
    private static String getFileNameOfFloorPlan(final Complex complex, final String floor){

        final String floorCorrected;
        if("-1".equals(floor)) floorCorrected = "ug1";
        else if("-2".equals(floor)) floorCorrected = "ug2";
        else floorCorrected = floor;

        final String filename = "building_" + complex.toString() + "_floor_" + floorCorrected;

        return filename;
    }

    /**
     * Get path to floorplan png
     * @param complex
     * @param floor
     * @return filepath to png (without ".png" file extension)
     */
    public static String getPathToFloorPlanPNG(final Complex complex, final String floor) {
        return "floorplan_images/" + getFileNameOfFloorPlan(complex, floor) +".png";
    }

    /**
     * Get path to json file of floorplan grid (incl. ".json")
     * @param complex
     * @param floor
     * @return filepath to json
     */
    public static String getPathToFloorPlanGrid(final Complex complex, final String floor) {
        return "floorgrids/" + getFileNameOfFloorPlan(complex, floor) + ".json";
    }


    /**
     * Get the string representation of the floor
     * @param complex complex of the floor (because in building 05: "3Z" = 4)
     * @param floorInt floor as integer
     * @return floor name as string
     */
    public static String floorIntToString(final Complex complex, final int floorInt) {

        String currentFloor = "";

        switch (floorInt) {
            case -2:
                currentFloor = "-2";
                break;
            case -1:
                currentFloor = "-1";
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
                if(complex == Complex.COMPLEX_5){
                    currentFloor = "3Z";
                } else {
                    currentFloor = "04";
                }
                break;
            default:
                break;
        }
        return currentFloor;
    }


    /**
     * Returns floor as integer (special case in building 5: "3Z" = 4)
     * @param floor floor as string
     * @return floor as integer
     */
    public static int floorStringToInt(final String floor) {

        int floorAsInteger = 0;

        switch (floor) {
            case "-2":
            case "ug2":
                floorAsInteger = -2;
                break;
            case "-1":
            case "ug1":
                floorAsInteger = -1;
                break;
            case "00":
                floorAsInteger = 0;
                break;
            case "01":
                floorAsInteger = 1;
                break;
            case "02":
                floorAsInteger = 2;
                break;
            case "03":
                floorAsInteger = 3;
                break;
            case "3Z":
            case "04":
                floorAsInteger = 4;
                break;
            default:
                break;
        }
        return floorAsInteger;
    }


    /**
     * Checks whether cell1 and cell2 are both at floor -1 at complex 321,
     * but cell1 in building 1 and cell2 in building 3, or vice versa
     * (reason: building 2 has no floor -1 -> building 3 and 1 are not connected via basement of building 2)
     * @param cell1 start cell
     * @param cell2 cell to reach from the start cell
     */
    public static boolean checkExceptCaseBuild321FloorUG(final Cell cell1, final Cell cell2){
	    return (cell1.getFloorInt() == -1 && cell2.getFloorInt() == -1) &&
			    (("03".equals(cell1.getBuilding()) && "01".equals(cell2.getBuilding()))
					    || ("01".equals(cell1.getBuilding()) && "03".equals(cell2.getBuilding())));
    }
}
