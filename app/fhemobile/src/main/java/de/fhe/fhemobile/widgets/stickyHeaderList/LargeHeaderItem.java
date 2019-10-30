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
package de.fhe.fhemobile.widgets.stickyHeaderList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.fhe.fhemobile.R;

/**
 * Created by Paul Cech on 13.05.15.
 */
public class LargeHeaderItem extends IHeaderItem {

    public LargeHeaderItem(String _headerText, boolean _hasSectionHeader) {
        super(_hasSectionHeader);
        mHeaderText       = _headerText;

    }

    public LargeHeaderItem(String _headerText, boolean _hasSectionHeader, List<IRowItem> _items) {
        super(_items, _hasSectionHeader);
        mHeaderText       = _headerText;
    }

    @Override
    public int getViewType() {
        return EItemType.LARGE_HEADER.ordinal();
    }

    @Override
    public View getView(LayoutInflater _inflater, View _convertView, ViewGroup _parent) {

        ViewHolder viewHolder;
        if (_convertView == null) {
            viewHolder = new ViewHolder();

            // TODO: sticky header library apparently has a bug with using the parent ViewGroup
            //       as the sticky header always looses its height. So I just set the parant to null
            //       and not as intented to "_parent, false".
            _convertView = _inflater.inflate(R.layout.item_header_text_large, null);

            viewHolder.mHeadline = (TextView) _convertView.findViewById(R.id.headerLargeText);

            _convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) _convertView.getTag();
        }

        viewHolder.mHeadline.setText(mHeaderText);

        return _convertView;
    }


    // ---------------------------------------------------------------------------------------------
    //  ViewHolder
    // ---------------------------------------------------------------------------------------------

    static class ViewHolder {
        TextView mHeadline;
    }


    // ---------------------------------------------------------------------------------------------
    //  Member
    // ---------------------------------------------------------------------------------------------

    private static final String LOG_TAG = LargeHeaderItem.class.getSimpleName();

    private String         mHeaderText;
}
