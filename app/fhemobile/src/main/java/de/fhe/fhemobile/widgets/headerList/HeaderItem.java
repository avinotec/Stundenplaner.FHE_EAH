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
package de.fhe.fhemobile.widgets.headerList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.fhe.fhemobile.R;

/**
 * Created by paul on 03.03.15.
 */
public class HeaderItem implements IBaseItem {

    public HeaderItem(final String _headerText) {
        mHeaderText = _headerText;
    }

    @Override
    public int getViewType() {
        return EItemType.HEADER.ordinal();
    }

    @Override
    public View getView(final LayoutInflater _inflater, View _convertView, final ViewGroup _parent) {
        final ViewHolder holder;
        if (_convertView == null) {
            holder = new ViewHolder();
            _convertView = _inflater.inflate(R.layout.item_header_text_default, _parent, false);

            holder.mHeaderText = (TextView) _convertView.findViewById(R.id.tv_item_header_default_day);
            
            _convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) _convertView.getTag();
        }

        holder.mHeaderText.setText(mHeaderText);
        
        return _convertView;
    }

    static class ViewHolder {
        TextView mHeaderText;
    }

    private final String mHeaderText;
}
