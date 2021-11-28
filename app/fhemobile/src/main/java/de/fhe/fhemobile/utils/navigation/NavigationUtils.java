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
import de.fhe.fhemobile.utils.Define;

public class NavigationUtils {

    /**
     * Complex for RouteCalculator
     * merges buildings 3, 2 and 1 to COMPLEX_321
     */
    public enum Complex {

        //order important, sorted from west to east
        COMPLEX_4,
        COMPLEX_321,
        COMPLEX_5;


        public static Complex getEnum(String building){
            switch(building){
                case "01":
                case "02":
                case "03":
                    return COMPLEX_321;
                case "04":
                    return COMPLEX_4;
                case "05":
                    return COMPLEX_5;
                default:
                    return null;
            }
        }


    }

    /**
     * Get path to floorplan png
     * @param complex
     * @param floor
     * @return filepath to png (without ".png" file extension)
     */
    public static String getPathToFloorPlanPNG(final Complex complex, final String floor) {
        return "drawable/" + getFloorPlanFileName(complex, floor);
    }

    /**
     * Get path to json file of floorplan grid (incl. ".json")
     * @param complex
     * @param floor
     * @return filepath to json
     */
    public static String getPathFloorPlanGrid(final Complex complex, final String floor) {
        String ending = ".json";

        return getFloorPlanFileName(complex, floor) + ending;
    }

    /**
     * Get the string representation of the floor
     * @param index
     * @return floor name as string
     */
    public static String floorIntToString(final int index) {

        String currentFloor = "";

        switch (index) {
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
                currentFloor = "04";
                break;
            default:
                break;
        }
        return currentFloor;
    }

    /**
     * All files concerning the floorplan of a certain complex and floor have the same name
     * (just different file extension e.g. json or png and different locations)
     * @param complex
     * @param floor
     * @return name of the floorplan file
     */
    private static String getFloorPlanFileName(final Complex complex, final String floor){
        String filename = "";

        if(complex.equals(Complex.COMPLEX_321)){
            switch (floor) {
                case "-1":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_UG;
                    break;
                case "00":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_00;
                    break;
                case "01":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_01;
                    break;
                case "02":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_02;
                    break;
                case "03":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_03;
                    break;
                case "04":
                    filename = Define.Maps.BUILDING_03_02_01_FLOOR_04;
                    break;
            }
        }
        else if(complex.equals(Complex.COMPLEX_4)){
            switch(floor){
                case "-1":
                    filename = Define.Maps.BUILDING_04_FLOOR_UG;
                    break;
                case "00":
                    filename = Define.Maps.BUILDING_04_FLOOR_00;
                    break;
                case "01":
                    filename = Define.Maps.BUILDING_04_FLOOR_01;
                    break;
                case "02":
                    filename = Define.Maps.BUILDING_04_FLOOR_02;
                    break;
                case "03":
                    filename = Define.Maps.BUILDING_04_FLOOR_03;
                    break;
            }
        } else if(complex.equals(Complex.COMPLEX_5)){
            switch (floor){
                case "-2":
                    filename = Define.Maps.BUILDING_05_FLOOR_UG2;
                    break;
                case "-1":
                    filename = Define.Maps.BUILDING_05_FLOOR_UG1;
                    break;
                case "00":
                    filename = Define.Maps.BUILDING_05_FLOOR_00;
                    break;
                case "01":
                    filename = Define.Maps.BUILDING_05_FLOOR_01;
                    break;
                case "02":
                    filename = Define.Maps.BUILDING_05_FLOOR_02;
                    break;
                case "03":
                    filename = Define.Maps.BUILDING_05_FLOOR_03_1;
                    filename = Define.Maps.BUILDING_05_FLOOR_03_2;
                    break;
                //Todo:Lösung finden für 03 Ebene 1 und Ebene2
            }
        }

        return filename;
    }

    /**
     * Checks whether cell1 and cell2 are both at floor -1 at complex 321,
     * but cell1 in building 1 and cell2 in building 3, or vice versa
     * (reason: building 2 has no floor -1 -> building 3 and 1 are not connected via basement of building 2)
     * @param cell1 start cell
     * @param cell2 cell to reach from the start cell
     */
    public static boolean checkExceptCaseBuild321FloorUG(Cell cell1, Cell cell2){
        if((cell1.getFloorInt() == -1 && cell2.getFloorInt() == -1) &&
                ((cell1.getBuilding().equals("03") && cell2.getBuilding().equals("01"))
                    || (cell1.getBuilding().equals("01") && cell2.getBuilding().equals("03")))){
            return true;
        }else{
            return false;
        }
    }
}
