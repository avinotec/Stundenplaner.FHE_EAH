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
package de.fhe.fhemobile.adapters.canteen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.vos.canteen.CanteenFoodItemVo;

/**
 * Created by Nadja - 03/2022
 */
public class CanteenDayAdapter extends BaseAdapter {

    private static final String TAG = "CanteenDayAdapter";

    private final Context context;
    private List<CanteenFoodItemVo> mItems;

    public CanteenDayAdapter(final Context context) {
        this.context = context;
    }

    public void setItems(List<CanteenFoodItemVo> items){
        mItems = items;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mItems != null ? mItems.size() : 0;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's data set.
     * @return The data at the specified position.
     */
    @Override
    public CanteenFoodItemVo getItem(int position) {
        return mItems.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return
     */
    @Override
    public long getItemId(int position) {
        //note: returning position is intended - no mistake
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.item_canteen_food, parent, false);
        }

        final CanteenFoodItemVo currentItem = mItems.get(position);

        final TextView foodTitle = (TextView) convertView.findViewById(R.id.tv_canteen_fooditem_title);
        foodTitle.setText(currentItem.getTitle());

        final TextView foodDescription = (TextView) convertView.findViewById(R.id.tv_canteen_fooditem_description);
        foodDescription.setText(currentItem.getDescription());

        final TextView foodPrice = (TextView) convertView.findViewById(R.id.tv_canteen_fooditem_price);
        foodPrice.setText(currentItem.getPrice());

        return convertView;

    }
}
