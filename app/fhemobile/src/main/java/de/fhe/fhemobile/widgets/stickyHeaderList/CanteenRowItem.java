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
import de.fhe.fhemobile.vos.canteen.CanteenDishVo;

/**
 * Created by Paul Cech on 13.05.15.
 */
public class CanteenRowItem extends IRowItem {

    public CanteenRowItem(final CanteenDishVo _item) {
        mItem           = _item;
    }

    @Override
    public int getViewType() {
        return EItemType.CANTEEN.ordinal();
    }

    @Override
    public View getView(final LayoutInflater _inflater, View _convertView, final ViewGroup _parent) {

        final ViewHolder viewHolder;
        if(_convertView == null) {
            viewHolder  = new ViewHolder();
            _convertView = _inflater.inflate(R.layout.item_canteen_food, _parent, false);

            viewHolder.mTitle       = (TextView) _convertView.findViewById(R.id.tv_canteen_fooditem_title);
            viewHolder.mDescription = (TextView) _convertView.findViewById(R.id.tv_canteen_fooditem_description);
            viewHolder.mPrice       = (TextView) _convertView.findViewById(R.id.tv_canteen_fooditem_price);

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

    private static final String LOG_TAG = CanteenRowItem.class.getSimpleName();

    private final CanteenDishVo mItem;
}
