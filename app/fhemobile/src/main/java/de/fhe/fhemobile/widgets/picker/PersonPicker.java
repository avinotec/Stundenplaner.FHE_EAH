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

package de.fhe.fhemobile.widgets.picker;

import android.content.Context;
import android.util.AttributeSet;

import java.util.List;

import de.fhe.fhemobile.models.navigation.Person;
import de.fhe.fhemobile.widgets.picker.base.IdPicker;


/**
 * Created by Nadja - 03.01.22
 */
public class PersonPicker extends IdPicker {

    public PersonPicker(final Context context, final AttributeSet attrs) { super(context, attrs); }

    public void setItems(final List<Person> _items){
        mItems = _items;
        if(mItems == null){
            throw new AssertionError("Person items cannot be null!");
        }
    }

    @Override
    protected String getId(final int _Position) {
        return mItems.get(_Position).getName();
    }

    @Override
    protected String getName(final int _Position) {
        return mItems.get(_Position).getName();
    }

    @Override
    protected int getCount() {
        return mItems.size();
    }

    private List<Person> mItems;
}
