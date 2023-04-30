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

/**
 * Created by paul on 23.02.14.
 */
public class MapVo {

// --Commented out by Inspection START (23.04.2023 15:10):
//    public MapVo() {
//    }
// --Commented out by Inspection STOP (23.04.2023 15:10)

    /**
     * Map Value Object
     * @param _stringID id of the string resource for the name of the map e.g. "House 4: 1st floor"
     * @param _imageUrl the image's url
     */
    public MapVo(final int _stringID, final String _imageUrl) {
        mNameID = _stringID;        //edited by Nadja 17.11.2021
        mImageUrl = _imageUrl;
    }

    /**
     * returns resource ID for the name's string
     * @return string resource ID
     */
    public int getNameID() {
        return mNameID;
    }

// --Commented out by Inspection START (23.04.2023 15:10):
//    public void setNameID(final int _nameID) {
//        mNameID = _nameID;
//    }
// --Commented out by Inspection STOP (23.04.2023 15:10)

    public String getImageUrl() {
        return mImageUrl;
    }

// --Commented out by Inspection START (02.11.2021 17:09):
//    public void setImageUrl(String _imageUrl) {
//        mImageUrl = _imageUrl;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:09)

    private final int mNameID;
    private final String mImageUrl;
}
