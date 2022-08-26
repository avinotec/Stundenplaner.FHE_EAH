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
package de.fhe.fhemobile.widgets.stickyHeaderList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Cech on 13.05.15.
 */
public abstract class IHeaderItem {

    public enum EItemType {
        LARGE_HEADER, DEFAULT_HEADER, CANTEEN_IMAGE, WEATHER
    }

    // ---------------------------------------------------------------------------------------------
    //  Constructor
    // ---------------------------------------------------------------------------------------------

    IHeaderItem(final boolean _hasSectionHeader) {
        mHasSectionHeader = _hasSectionHeader;
        mItems            = new ArrayList<>();
    }

    IHeaderItem(final List<IRowItem> _items, final boolean _hasSectionHeader) {
        mItems            = _items;
        mHasSectionHeader = _hasSectionHeader;
    }

    // ---------------------------------------------------------------------------------------------
    //  Public Methods
    // ---------------------------------------------------------------------------------------------

    public List<IRowItem> getItems() {
        return mItems;
    }

    public void setItems(final List<IRowItem> _items) {
        mItems = _items;
    }

    public void addItem(final IRowItem _item) {
        mItems.add(_item);
    }

    public boolean hasSectionHeader() {
        return mHasSectionHeader;
    }

    // ---------------------------------------------------------------------------------------------
    //  Abstract methods
    // ---------------------------------------------------------------------------------------------

    public abstract int getViewType();

    public abstract View getView(LayoutInflater _inflater, View _convertView, ViewGroup _parent);

    // ---------------------------------------------------------------------------------------------
    //  Member
    // ---------------------------------------------------------------------------------------------

    private List<IRowItem> mItems;
    private final boolean        mHasSectionHeader;
}
