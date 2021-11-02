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

import static de.fhe.fhemobile.utils.Define.Navigation.COSTS_FLOORCONNECTION;

public class FloorConnectionCell extends Cell{

    private String type;

    public FloorConnectionCell(final int xCoordinate, final int yCoordinate, final String building, final String floor,
                               final boolean walkable, final String type){
        super(xCoordinate, yCoordinate, building, floor, walkable, COSTS_FLOORCONNECTION);
        this.type = type;
    }

    public String getTypeOfFloorConnection() {
        return type;
    }
}
