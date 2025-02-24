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
package de.fhe.fhemobile.widgets.picker.base;

/**
 * Created by paul on 12.03.15.
 */
class IdItem {
    IdItem(final String _Name, final String _Id) {
        mName = _Name;
        mId   = _Id;
    }

    public String getName() {
        return mName;
    }

    public void setName(final String _name) {
        mName = _name;
    }

    public String getId() {
        return mId;
    }

    public void setId(final String _id) {
        mId = _id;
    }

    private String mName;
    private String mId;
}
