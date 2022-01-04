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
package de.fhe.fhemobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.List;

import de.fhe.fhemobile.utils.headerlistview.SectionAdapter;
import de.fhe.fhemobile.widgets.stickyHeaderList.IHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IRowItem;

/**
 * Created by Paul Cech on 13.05.15.
 */
public class StickyHeaderAdapter extends SectionAdapter {

    public StickyHeaderAdapter(final Context _context, final List<IHeaderItem> _items) {
        mItems          = _items;
        mLayoutInflater = LayoutInflater.from(_context);
    }

    public void setOnItemClickListener(final AdapterView.OnItemClickListener _onItemClickListener) {
        mOnItemClickListener = _onItemClickListener;
    }

    public void setItems(final List<IHeaderItem> _items) {
        mItems = _items;
    }

    @Override
    public int numberOfSections() {
        return mItems.size();
    }

    @Override
    public int numberOfRows(final int _section) {
        return _section < 0 ? 0 : mItems.get(_section).getItems().size();
    }

    @Override
    public int getRowViewTypeCount() {
        return IRowItem.EItemType.values().length;
    }

    @Override
    public int getSectionHeaderViewTypeCount() {
        return IHeaderItem.EItemType.values().length;
    }

    @Override
    public int getSectionHeaderItemViewType(final int _section) {
        return mItems.get(_section).getViewType();
    }

    @Override
    public int getRowItemViewType(final int _section, final int _row) {
        return mItems.get(_section).getItems().get(_row).getViewType();
    }

    @Override
    public View getRowView(final int _section, final int _row, final View _convertView, final ViewGroup _parent) {
        return mItems.get(_section).getItems().get(_row).getView(mLayoutInflater, _convertView, _parent);
    }

    @Override
    public View getSectionHeaderView(final int _section, final View _convertView, final ViewGroup _parent) {
        return mItems.get(_section).getView(mLayoutInflater, _convertView, _parent);
    }

    @Override
    public Object getRowItem(final int _section, final int _row) {
        return mItems.get(_section).getItems().get(_row);
    }

    @Override
    public Object getSectionHeaderItem(final int _section) {
        return mItems.get(_section);
    }

    @Override
    public boolean hasSectionHeaderView(final int _section) {
        return _section < 0 || _section > mItems.size() - 1 || mItems.get(_section).hasSectionHeader();
    }

    @Override
    public void onRowItemClick(final AdapterView<?> parent, final View view, final int section, final int row, final long id) {
        super.onRowItemClick(parent, view, section, row, id);

        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(parent, view, (section * mItems.size()) + row, id);
        }
    }

    // ---------------------------------------------------------------------------------------------
    //  Member
    // ---------------------------------------------------------------------------------------------

    private static final String LOG_TAG = StickyHeaderAdapter.class.getSimpleName();

    private final LayoutInflater          mLayoutInflater;

    private List<IHeaderItem>       mItems;

    private AdapterView.OnItemClickListener mOnItemClickListener;
}
