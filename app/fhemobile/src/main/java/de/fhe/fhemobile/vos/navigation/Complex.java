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

package de.fhe.fhemobile.vos.navigation;

import androidx.annotation.NonNull;

/**
 * Complex for RouteCalculator
 * merges buildings 3, 2 and 1 to COMPLEX_321
 */
public enum Complex {

    //order important, sorted from west to east
    COMPLEX_4,
    COMPLEX_321,
    COMPLEX_5;

    public static Complex getEnum(final String building) {
        switch (building) {
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
        switch (this) {
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
