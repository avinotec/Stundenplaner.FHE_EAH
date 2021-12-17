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

package de.fhe.fhemobile.models.navigation;

import de.fhe.fhemobile.utils.navigation.Complex;


/**
 * Class for distinguishing between names of buildings (03, 02, 01, 04, 05) and
 * buildings considered as one unit (3-2-1, 4, 5)
 * needed for route calculation
 */
public class Building {

    private Complex mComplex;
    private String mBuilding;

    public Building(String building){
        this.mBuilding = building;
        this.mComplex = Complex.getEnum(building);
    }

    /**
     * Constructor for non-walkable cells without a building
     * @param complex
     */
    Building(Complex complex){
        this.mBuilding = "_";
        this.mComplex = complex;
    }

    /**
     * Get the complex ({@link Complex}) the building belongs to (BUILDING_4, BUILDING_5, BUILDING_321)
     * @return complex
     */
    public Complex getComplex() {
        return mComplex;
    }

    /**
     * Returns the String of the building ("01", "02", ...)
     * @return building as String
     */
    public String getBuilding() {
        return mBuilding;
    }


    /**
     * Returns ID of the building which equals the string (e.g. "-1", "01", "04, ...)
     * @return id of the building
     */
    public String getId(){ return mBuilding;}
}
