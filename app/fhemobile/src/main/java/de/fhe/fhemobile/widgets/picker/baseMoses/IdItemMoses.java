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
package de.fhe.fhemobile.widgets.picker.baseMoses;

/**
 * Created by Tony Spegel on 23.05.24
 */
class IdItemMoses {
    IdItemMoses(
            final String _Name,
            final String _Id,
            final Integer _MosesObjectId
    ) {
        mName = _Name;
        mId = _Id;
        mMosesObjectId = _MosesObjectId;
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

    public Integer getmMosesObjectId() {
        return mMosesObjectId;
    }

    public void setmMosesObjectId(Integer mMosesObjectId) {
        this.mMosesObjectId = mMosesObjectId;
    }

    private String mName;
    private String mId;
    private Integer mMosesObjectId;
}
