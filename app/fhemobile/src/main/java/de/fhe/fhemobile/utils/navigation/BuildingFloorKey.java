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

import java.util.Arrays;

import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.Complex;

/**
 * Key for the HashMap containing the route (all cells to walk)
 */
public class BuildingFloorKey implements Comparable {

    private final Complex mComplex;
    private final int mFloor;

    /**
     *
     * @param cell
     */
    public BuildingFloorKey(final Cell cell) {
        mComplex = cell.getComplex();
        mFloor = cell.getFloorInt();
    }

    /**
     *
     * @return
     */
    public Complex getComplex() { return mComplex; }

    /**
     *
     * @return
     */
    public int getFloorInt() { return mFloor; }

    public String getFloorString() { return NavigationUtils.floorIntToString(mComplex, mFloor);  }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof BuildingFloorKey))
            return false;
        final BuildingFloorKey that = (BuildingFloorKey) other;
        return mFloor == that.mFloor && mComplex == that.mComplex;
    }

    @Override
    public int hashCode() { return Arrays.hashCode(new Object[]{mComplex, mFloor}); }

    /**
     * Returns > 0 if this is bigger than other object o,
     * Returns = 0 if this is equal to other object o,
     * Returns < 0 if this i smaller than other object o
     *
     * @param o other object
     * @return
     */
    @Override
    public int compareTo(final Object o) {
        final BuildingFloorKey otherKey = (BuildingFloorKey) o;
        //gleiches Gebäude, gleiche Etage
        if (this.equals(otherKey))
            return 0;
        //gleiches Gebäude -> Etage vergleichen
        else if (this.getComplex() == otherKey.getComplex()) {
            return otherKey.getFloorInt() > this.getFloorInt() ? 1 : -1;
        }
        //verschiedene Gebäude: COMPLEX_4 < COMPLEX_321 < COMPLEX_5 (the order of declaration of enum values in Complex class is used)
        else return this.getComplex().compareTo(otherKey.getComplex());
    }

// --Commented out by Inspection START (04.01.2022 18:46):
//    /**
//     * Returns n if otherKey corresponds to n floors above this key's floor in the same complex,
//     * Returns 0 if keys are equal,
//     * Return -n if otherKey corresponds to n floors under this key's floor in the same complex
//     * Returns x < 0 if otherKey corresponds to another complex, x is an arbitrary score
//     *
//     * @param otherKey
//     * @return int floor differences
//     */
//    public int getDifference(final BuildingFloorKey otherKey) {
//        //gleiches Gebäude, gleiche Etage
//        if (this.equals(otherKey)) return 0;
//
//            //gleiches Gebäude -> Etage vergleichen
//        else if (this.getComplex() == otherKey.getComplex()) {
//
//            //floor difference when entrance is at floor -1
//            if (this.getComplex() == Complex.COMPLEX_4) {
//                return otherKey.getFloorInt() - this.getFloorInt() + 1;
//            }
//            //floor difference when entrance is at floor 00
//            return otherKey.getFloorInt() - this.getFloorInt();
//        }
//
//        //verschiedene Gebäude -> Anzahl Etagen hoch/runter in dest complex (otherKey)
//        // + 100 (to distinguish between case "same complex" and "different complex",
//        // otherwise 03.03 to 03.01 gets same score as 03.01 to 04.01);
//
//        //+2 to distinguish between destination floor -1 and 01 in different building,
//        // needed because building 04 is entered at -1 and thus a route can contain both floorplans -1 and 01
//
//        //note: number does not correspond to floor difference between otherKey and this anymore
//        else return -(Math.abs(otherKey.mFloor + 2) + 100);
//    }
// --Commented out by Inspection STOP (04.01.2022 18:46)
}
