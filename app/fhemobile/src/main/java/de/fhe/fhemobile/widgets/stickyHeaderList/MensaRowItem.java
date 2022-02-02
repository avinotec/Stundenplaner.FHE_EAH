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
package de.fhe.fhemobile.widgets.stickyHeaderList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.vos.mensa.MensaFoodItemVo;

/**
 * Created by Paul Cech on 13.05.15.
 */
public class MensaRowItem extends IRowItem {

    public MensaRowItem(final MensaFoodItemVo _item) {
        mItem           = _item;
    }

    @Override
    public int getViewType() {
        return EItemType.MENSA.ordinal();
    }

    @Override
    public View getView(final LayoutInflater _inflater, View _convertView, final ViewGroup _parent) {

        final ViewHolder viewHolder;
        if(_convertView == null) {
            viewHolder  = new ViewHolder();
            _convertView = _inflater.inflate(R.layout.item_mensa_food, _parent, false);

            viewHolder.mTitle       = (TextView) _convertView.findViewById(R.id.mensaTitle);
            viewHolder.mDescription = (TextView) _convertView.findViewById(R.id.mensaDescription);
            viewHolder.mPrice       = (TextView) _convertView.findViewById(R.id.mensaPrice);

            _convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) _convertView.getTag();
        }

        viewHolder.mTitle.setText(mItem.getTitle());
        viewHolder.mDescription.setText(mItem.getDescription());
        viewHolder.mPrice.setText(mItem.getPrice());

        return _convertView;
    }


    // ---------------------------------------------------------------------------------------------
    //  ViewHolder
    // ---------------------------------------------------------------------------------------------

    static class ViewHolder {
        TextView mTitle;
        TextView mDescription;
        TextView mPrice;
    }

    // ---------------------------------------------------------------------------------------------
    //  Member
    // ---------------------------------------------------------------------------------------------

    private static final String LOG_TAG = MensaRowItem.class.getSimpleName();

    private final MensaFoodItemVo mItem;
}
