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

package de.fhe.fhemobile.widgets.picker;

import android.content.Context;
import android.util.AttributeSet;

import java.util.List;

import de.fhe.fhemobile.widgets.picker.base.IdPicker;

/**
 * Created by Nadja - 08.12.2021
 */
public class FloorPicker extends IdPicker {

    public FloorPicker(Context context, AttributeSet attrs) { super(context, attrs); }

    public void setItems(List<Integer> _items){
        mItems = _items;
        if(mItems == null){
            throw new AssertionError("Floor items cannot be null!");
        }
    }

    @Override
    protected String getId(int _Position) {
        return mItems.get(_Position).toString();
    }

    @Override
    protected String getName(int _Position) {
        return String.format("%02d", Integer.parseInt(mItems.get(_Position).toString()));
    }

    @Override
    protected int getCount() {
        return mItems.size();
    }

    private List<Integer> mItems;
}
