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


import java.util.ArrayList;

import de.fhe.fhemobile.models.navigation.FloorConnectionCell;


/**
 * Modellklasse für Stockwerk- und Gebäudeverbindungen wie Treppenhäuser, Aufzüge (und die Brücke)
 * created: Bachelor Thesis from Tim Münziger from SS2020
 * edited by Nadja 15.09.21
 */
public class FloorConnectionVo {

    //Constants
    private static final String TAG = FloorConnectionVo.class.getSimpleName();

    //Variables
    private final String typeOfFloorConnection; //stairs, elevator, bridge
    private final ArrayList<FloorConnectionCell> connectedCells;


    public FloorConnectionVo(final String type, final ArrayList<FloorConnectionCell> connectedCells){
        this.connectedCells = connectedCells;
        this.typeOfFloorConnection = type;
    }

    //Getter
    public String getTypeOfFloorConnection() {
        return typeOfFloorConnection;
    }

    public ArrayList<FloorConnectionCell> getConnectedCells() {
        return connectedCells;
    }


}
