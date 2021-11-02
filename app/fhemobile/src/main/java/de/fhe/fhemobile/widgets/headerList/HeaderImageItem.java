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
import android.widget.ImageView;

import de.fhe.fhemobile.R;

/**
 * Created by Paul Cech on 13.05.15.
 */
public class HeaderImageItem implements IBaseItem {

    private HeaderImageItem(final int _imageRes) {
        mImageRes = _imageRes;
    }

    @Override
    public int getViewType() {
        return EItemType.HEADER_IMAGE.ordinal();
    }

    @Override
    public View getView(final LayoutInflater _inflater, View _convertView, ViewGroup _parent) {
        ViewHolder holder;
        if (_convertView == null) {
            holder = new ViewHolder();
            _convertView = _inflater.inflate(R.layout.item_header_image, _parent, false);

            holder.mHeaderImage = (ImageView) _convertView.findViewById(R.id.itemHeaderImage);

            _convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) _convertView.getTag();
        }

        holder.mHeaderImage.setImageResource(mImageRes);

        return _convertView;
    }

    static class ViewHolder {
        ImageView mHeaderImage;
    }

    private static final String LOG_TAG = HeaderImageItem.class.getSimpleName();

    private final int mImageRes;
}
