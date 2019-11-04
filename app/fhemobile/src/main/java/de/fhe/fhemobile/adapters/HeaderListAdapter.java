/*
 * Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhe.fhemobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import de.fhe.fhemobile.widgets.headerList.IBaseItem;

/**
 * Created by paul on 03.03.15.
 */
public class HeaderListAdapter extends BaseAdapter {

    public HeaderListAdapter(Context _context, List<IBaseItem> _items) {
        mItems          = _items;
        mLayoutInflater = LayoutInflater.from(_context);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mItems.get(position).getView(mLayoutInflater, convertView, parent);
    }

    @Override
    public int getViewTypeCount() {
        return IBaseItem.EItemType.values().length;
    }

    //getItemViewType-------------------------------------------------------------------------------
    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getViewType();
    }

    private final List<IBaseItem> mItems;
    private final LayoutInflater  mLayoutInflater;
}
