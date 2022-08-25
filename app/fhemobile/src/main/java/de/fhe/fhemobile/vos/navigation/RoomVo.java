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

import static de.fhe.fhemobile.utils.Define.Navigation.COSTS_ROOM;

import de.fhe.fhemobile.models.navigation.Cell;

public class RoomVo extends Cell {

    //Constants
    private static final String TAG = RoomVo.class.getSimpleName();

    //Variables
    private String roomNumber;
    private String qrCode;

    //Constructors
    public RoomVo(){
        super();
    }

    public RoomVo(final String roomNumber, final String building, final String floor, final String qrCode,
                  final int x, final int y, final boolean walkable) {
        super(x, y, building, floor, walkable, COSTS_ROOM);
        this.roomNumber = roomNumber;
        this.qrCode = qrCode;
    }

    //Getter
    public String getRoomNumber() {
        return roomNumber;
    }

    public String getQRCode() {
        return qrCode;
    }

    public String getRoomName() {
        final String roomName = getBuilding() + "." + getFloorString() + "." + roomNumber;

        return roomName;
    }

    /**
     * Returns the ID of the room which equals the roomname
     * @return id of the room
     */
    public String getId(){
        return getRoomName();
    }

}
