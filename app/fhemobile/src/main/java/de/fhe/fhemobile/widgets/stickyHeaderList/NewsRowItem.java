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
import de.fhe.fhemobile.vos.news.NewsItemVo;

/**
 * Created by Paul Cech on 13.05.15.
 */
public class NewsRowItem extends IRowItem {

    public NewsRowItem(final NewsItemVo _item) {
        mItem = _item;
    }

    @Override
    public int getViewType() {
        return EItemType.NEWS.ordinal();
    }

    @Override
    public View getView(final LayoutInflater _inflater, View _convertView, final ViewGroup _parent) {

        final ViewHolder viewHolder;
        if(_convertView == null) {
            viewHolder = new ViewHolder();
            _convertView = _inflater.inflate(R.layout.item_news_list, _parent, false);

            viewHolder.mTitle = (TextView) _convertView.findViewById(R.id.newsTitle);
            viewHolder.mDescription = (TextView) _convertView.findViewById(R.id.newsDescription);
            viewHolder.mPubDate = (TextView) _convertView.findViewById(R.id.newsPubDate);

            _convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) _convertView.getTag();
        }

        viewHolder.mTitle.setText(mItem.getTitle());

        if( mItem.getDescription() == null )
        {
            viewHolder.mDescription.setVisibility( View.GONE );
        }
        else
        {
            viewHolder.mDescription.setVisibility( View.VISIBLE );
            viewHolder.mDescription.setText( mItem.getDescription() );
        }

        viewHolder.mPubDate.setText(mItem.getPubDate());

        return _convertView;
    }

    static class ViewHolder {
        TextView mTitle;
        TextView mDescription;
        TextView mPubDate;
    }

    private static final String LOG_TAG = NewsRowItem.class.getSimpleName();

    private final NewsItemVo mItem;
}
