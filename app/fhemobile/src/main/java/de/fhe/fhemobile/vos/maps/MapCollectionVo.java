/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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
package de.fhe.fhemobile.vos.maps;

import java.util.List;

/**
 * Created by paul on 04.03.15.
 */
public class MapCollectionVo {

    /**
     *
     * @param _stringID string resource ID of the name's string e.g. "House 4"
     * @param _maps
     */
    public MapCollectionVo(final int _stringID, final List<MapVo> _maps) {
        mNameID = _stringID;        //edited by Nadja 17.11.2021
        mMaps = _maps;
    }

    public int getNameID() {
        return mNameID;
    }

// --Commented out by Inspection START (23.04.2023 15:08):
//    public void setNameID(final int _stringID) {
//        mNameID = _stringID;
//    }
// --Commented out by Inspection STOP (23.04.2023 15:08)

    public List<MapVo> getMaps() {
        return mMaps;
    }

// --Commented out by Inspection START (23.04.2023 15:08):
//    public void setMaps(final List<MapVo> _maps) {
//        mMaps = _maps;
//    }
// --Commented out by Inspection STOP (23.04.2023 15:08)

    private int mNameID;
    private List<MapVo> mMaps;
}
