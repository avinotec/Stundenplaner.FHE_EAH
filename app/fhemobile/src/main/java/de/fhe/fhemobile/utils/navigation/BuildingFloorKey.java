/*
 *  Copyright (c) 2020-2022 Ernst-Abbe-Hochschule Jena
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
import de.fhe.fhemobile.vos.navigation.Complex;

/**
 * Key for the HashMap containing the route (all cells to walk)
 */
public class BuildingFloorKey implements Comparable {

    private final Cell cell;

    /**
     * Constructor
     * @param cell
     */
    public BuildingFloorKey(final Cell cell) {
        this.cell = cell;
    }

    /**
     *
     * @return
     */
    public Complex getComplex() { return cell.getComplex(); }

    /**
     *
     * @return
     */
    public int getFloorInt() { return cell.getFloorInt(); }

    /**
     *
     * @return
     */
    public String getFloorString() { return NavigationUtils.floorIntToString(cell.getComplex(), cell.getFloorInt());  }

    /**
     *
     * @param other object to compare with
     * @return boolean true, if the content of the object is equal
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof BuildingFloorKey))
            return false;
        final BuildingFloorKey that = (BuildingFloorKey) other;
        final boolean f = (cell.getFloorInt() == that.cell.getFloorInt());
        final boolean c = (cell.getComplex() == that.cell.getComplex());
        return (f && c);
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() { return Arrays.hashCode(new Object[]{cell.getComplex(), cell.getFloorInt()}); }

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
            return ( (otherKey.getFloorInt() > this.getFloorInt()) ? 1 : -1);
        }
        //verschiedene Gebäude: COMPLEX_4 < COMPLEX_321 < COMPLEX_5 (the order of declaration of enum values in Complex class is used)
        else
            return (this.getComplex().compareTo(otherKey.getComplex()));
    }

}
