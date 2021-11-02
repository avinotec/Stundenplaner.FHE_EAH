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
package de.fhe.fhemobile.adapters.drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.fhe.fhemobile.R;

/**
 * Created by paul on 22.01.14.
 */
public class DrawerAdapter extends BaseAdapter {

    public DrawerAdapter(final Context _Context, final List<DrawerItem> _Items) {
        mContext = _Context;
        mItems   = _Items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public DrawerItem getItem(final int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_drawer, parent, false);

            viewHolder.mCheckedIndicator = (LinearLayout) convertView.findViewById(R.id.drawerSelectedItem);
            viewHolder.mLabel = (TextView) convertView.findViewById(R.id.drawerLabel);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mLabel.setText(mItems.get(position).getText());

        return convertView;
    }

    /**
     *
     */
    public static class ViewHolder {
        public LinearLayout mCheckedIndicator;
        public TextView     mLabel;
    }

    private final Context          mContext;
    private final List<DrawerItem> mItems;
}
