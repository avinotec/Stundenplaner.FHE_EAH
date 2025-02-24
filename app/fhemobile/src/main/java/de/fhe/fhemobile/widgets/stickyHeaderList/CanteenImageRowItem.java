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
import android.widget.ImageView;

import de.fhe.fhemobile.R;

/**
 * Created by Paul Cech on 13.05.15.
 */
public class CanteenImageRowItem extends IRowItem {

    public CanteenImageRowItem(final int _imageRes) {
        mImageRes = _imageRes;
    }

    @Override
    public final int getViewType() {
        return EItemType.CANTEEN_IMAGE.ordinal();
    }

    @Override
    public final View getView(final LayoutInflater _inflater, View _convertView, final ViewGroup _parent) {
        final ViewHolder holder;
        if (_convertView == null) {
            holder = new ViewHolder();
            _convertView = _inflater.inflate(R.layout.item_header_image_canteen, _parent, false);

            holder.mHeaderImage = (ImageView)  _convertView.findViewById(R.id.itemHeaderImage);

            _convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) _convertView.getTag();
        }

        holder.mHeaderImage.setImageResource(mImageRes);

        return _convertView;
    }

    static class ViewHolder {
        ImageView  mHeaderImage;
    }

    private static final String LOG_TAG = CanteenImageRowItem.class.getSimpleName();

    private final int         mImageRes;
}
