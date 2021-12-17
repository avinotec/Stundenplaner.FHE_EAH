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

import androidx.annotation.NonNull;

import java.util.Arrays;

import de.fhe.fhemobile.models.navigation.Cell;

public class NavigationUtils {

    /**
     * Key for the HashMap containing the route (all cells to walk)
     */
    public static class BuildingFloorKey implements Comparable{

        private Complex mComplex;
        private int mFloor;

        public BuildingFloorKey(Cell cell){
            mComplex = cell.getComplex();
            mFloor = cell.getFloorInt();
        }

        public BuildingFloorKey(Complex complex, int floor){
            mComplex = complex;
            mFloor = floor;
        }

        public Complex getComplex() {
            return mComplex;
        }

        public int getFloorInt() {
            return mFloor;
        }

        public String getFloorString() {
            return floorIntToString(mComplex, mFloor);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof BuildingFloorKey)) return false;
            BuildingFloorKey that = (BuildingFloorKey) other;
            return mFloor == that.mFloor && mComplex.equals(that.mComplex);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[]{mComplex, mFloor});
        }

        /**
         * Returns > 0 if this is bigger than other object o,
         * Returns = 0 if this is equal to other object o,
         * Returns < 0 if this i smaller than other object o
         * @param o other object
         * @return
         */
        @Override
        public int compareTo(Object o){
            BuildingFloorKey otherKey = (BuildingFloorKey) o;
            //gleiches Gebäude, gleiche Etage
            if(this.equals(otherKey)) return 0;

            //gleiches Gebäude -> Etage vergleichen
            else if (this.getComplex().equals(otherKey.getComplex())){
                return otherKey.getFloorInt() > this.getFloorInt() ? 1 : -1;
            }

            //verschiedene Gebäude: COMPLEX_4 < COMPLEX_321 < COMPLEX_5 (the order of declaration of enum values in Complex class is used)
            else return this.getComplex().compareTo(otherKey.getComplex());
        }

        /**
         * Returns n if otherKey corresponds to n floors above this key's floor in the same complex,
         * Returns 0 if keys are equal,
         * Return -n if otherKey corresponds to n floors under this key's floor in the same complex
         * Returns x < 0 if otherKey corresponds to another complex, x is an arbitrary score
         * @param otherKey
         * @return int floor differences
         */
        public int getDifference(BuildingFloorKey otherKey){
            //gleiches Gebäude, gleiche Etage
            if(this.equals(otherKey)) return 0;

            //gleiches Gebäude -> Etage vergleichen
            else if (this.getComplex().equals(otherKey.getComplex())){

                //floor difference when entrance is at floor -1
                if (this.getComplex().equals(Complex.COMPLEX_4)){
                    return otherKey.getFloorInt() - this.getFloorInt() + 1;
                }
                //floor difference when entrance is at floor 00
                return otherKey.getFloorInt() - this.getFloorInt();
            }

            //verschiedene Gebäude -> Anzahl Etagen hoch/runter in dest complex (otherKey)
            // + 100 (to distinguish between case "same complex" and "different complex",
            // otherwise 03.03 to 03.01 gets same score as 03.01 to 04.01);

            //+2 to distinguish between destination floor -1 and 01 in different building,
            // needed because building 04 is entered at -1 and thus a route can contain both floorplans -1 and 01

            //note: number does not correspond to floor difference between otherKey and this anymore
            else return -(Math.abs(otherKey.mFloor + 2) + 100);
        }
    }

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

        @NonNull
        @Override
        public String toString() {
            switch(this){
                case COMPLEX_321:
                    return "03_02_01";
                case COMPLEX_4:
                    return "04";
                case COMPLEX_5:
                    return "05";
                default:
                    return "";
            }
        }

    }

    /**
     * All files concerning the floorplan of a certain complex and floor have the same name
     * (just different file extension e.g. json or png and different locations)
     * e.g. "building_05_floor_ug1" or "building_03_02_01_floor_02"
     * @param complex
     * @param floor (e.g. "ug1", "00", "02", "3Z")
     * @return name of the floorplan file
     */
    private static String getFileNameOfFloorPlan(final Complex complex, final String floor){

        String floorCorrected;
        if(floor.equals("-1")) floorCorrected = "ug1";
        else if(floor.equals("-2")) floorCorrected = "ug2";
        else floorCorrected = floor;

        String filename = "building_" + complex.toString() + "_floor_" + floorCorrected;

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
                if(complex.equals(Complex.COMPLEX_5)){
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
    public static int floorStringToInt(String floor) {

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
