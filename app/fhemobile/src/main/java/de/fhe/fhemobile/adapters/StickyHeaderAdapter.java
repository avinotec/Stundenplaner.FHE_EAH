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

import com.applidium.headerlistview.SectionAdapter;

import java.util.List;

import de.fhe.fhemobile.widgets.stickyHeaderList.DefaultHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IRowItem;

/**
 * Created by Paul Cech on 13.05.15.
 */
public class StickyHeaderAdapter extends SectionAdapter {

    public StickyHeaderAdapter(Context _context, List<IHeaderItem> _items) {
        mContext        = _context;
        mItems          = _items;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener _onItemClickListener) {
        mOnItemClickListener = _onItemClickListener;
    }

    public void setItems(List<IHeaderItem> _items) {
        mItems = _items;
    }

    @Override
    public int numberOfSections() {
        return mItems.size();
    }

    @Override
    public int numberOfRows(int _section) {
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
    public int getSectionHeaderItemViewType(int _section) {
        return mItems.get(_section).getViewType();
    }

    @Override
    public int getRowItemViewType(int _section, int _row) {
        return mItems.get(_section).getItems().get(_row).getViewType();
    }

    @Override
    public View getRowView(int _section, int _row, View _convertView, ViewGroup _parent) {
        return mItems.get(_section).getItems().get(_row).getView(mLayoutInflater, _convertView, _parent);
    }

    @Override
    public View getSectionHeaderView(int _section, View _convertView, ViewGroup _parent) {
        return mItems.get(_section).getView(mLayoutInflater, _convertView, _parent);
    }

    @Override
    public Object getRowItem(int _section, int _row) {
        return mItems.get(_section).getItems().get(_row);
    }

    @Override
    public Object getSectionHeaderItem(int _section) {
        return mItems.get(_section);
    }

    @Override
    public boolean hasSectionHeaderView(int _section) {
        return _section < 0 || _section > mItems.size() - 1 || mItems.get(_section).hasSectionHeader();
    }

    @Override
    public void onRowItemClick(AdapterView<?> parent, View view, int section, int row, long id) {
        super.onRowItemClick(parent, view, section, row, id);

        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(parent, view, (section * mItems.size()) + row, id);
        }
    }

    // ---------------------------------------------------------------------------------------------
    //  Member
    // ---------------------------------------------------------------------------------------------

    private static final String LOG_TAG = StickyHeaderAdapter.class.getSimpleName();

    private final Context                 mContext;
    private final LayoutInflater          mLayoutInflater;

    private List<IHeaderItem>       mItems;

    private AdapterView.OnItemClickListener mOnItemClickListener;
}
