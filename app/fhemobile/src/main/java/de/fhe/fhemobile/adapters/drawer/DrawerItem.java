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
package de.fhe.fhemobile.adapters.drawer;

/**
 * Created by paul on 18.03.15.
 */
public class DrawerItem {
    public DrawerItem(final int _id, final String _text) {
        mId = _id;
        mText = _text;
    }

    public int getId() {
        return mId;
    }

    public void setId(final int _id) {
        mId = _id;
    }

    public String getText() {
        return mText;
    }

    public void setText(final String _text) {
        mText = _text;
    }

    private int    mId;
    private String mText;
}
