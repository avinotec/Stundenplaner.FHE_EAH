/*
 *  Copyright (c) 2019-2022 Ernst-Abbe-Hochschule Jena
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

import static de.fhe.fhemobile.utils.Define.Navigation.COSTS_EXIT;

import java.util.ArrayList;

import de.fhe.fhemobile.models.navigation.Cell;

/*
    class created by Nadja, 23.11.2021
 */

/**
 * Class that models an exit of a building
 */
public class BuildingExitVo extends Cell {

    // this exit should be use when changing to the complex "exitTo"
    private final ArrayList<Complex> exitTo = new ArrayList<>();
    // this entry should be use when changing from complex "entryFrom"
    private final ArrayList<Complex> entryFrom = new ArrayList<>();

    /**
     *
     * @param xCoordinate the x coordinate of the {@link BuildingExitVo}
     * @param yCoordinate the y coordinate of the {@link BuildingExitVo}
     * @param building the building as string the {@link BuildingExitVo} belongs to
     * @param floor the floor as string the {@link BuildingExitVo} belongs to
     * @param exitTo list containing the buildings this {@link BuildingExitVo} should be used when changing to
     * @param entryFrom the building of the {@link BuildingExitVo} should be entered via this exit when coming from the buildings of the entryFrom list
     */
    public BuildingExitVo(final int xCoordinate, final int yCoordinate, final String building, final String floor,
                          final ArrayList<String> exitTo, final ArrayList<String> entryFrom) {
        super(xCoordinate, yCoordinate, building, floor, true, COSTS_EXIT);

        for(final String destBuilding: exitTo){
            this.exitTo.add(new BuildingVo(destBuilding).getComplex());
        }

        for(final String startBuilding: entryFrom){
            this.entryFrom.add(new BuildingVo(startBuilding).getComplex());
        }
    }

    public final ArrayList<Complex> getExitTo(){ return exitTo;  }

    public final ArrayList<Complex> getEntryFrom(){ return entryFrom; }
}
