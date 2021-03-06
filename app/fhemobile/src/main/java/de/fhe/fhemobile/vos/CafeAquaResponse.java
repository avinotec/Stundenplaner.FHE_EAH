/*
 *  Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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
package de.fhe.fhemobile.vos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 04.03.15.
 */
public class CafeAquaResponse {

    public CafeAquaResponse() {
    }

    public boolean isOpen() {
        return mOpen;
    }

// --Commented out by Inspection START (01.11.2019 00:17):
//    public void setOpen(boolean _open) {
//        mOpen = _open;
//    }
// --Commented out by Inspection STOP (01.11.2019 00:17)

    @SerializedName("open")
    private boolean mOpen;
}
