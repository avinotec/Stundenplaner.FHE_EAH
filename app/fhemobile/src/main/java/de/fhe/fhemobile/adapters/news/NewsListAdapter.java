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
package de.fhe.fhemobile.adapters.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.vos.news.NewsItemVo;

/**
 * Created by paul on 04.02.14.
 */
public class NewsListAdapter extends BaseAdapter {

    private Context mContext;
    private NewsItemVo[] mItems;

    static class ViewHolder {
        TextView mTitle;
        TextView mDescription;
        TextView mPubDate;
    }

    public NewsListAdapter(Context _Context, NewsItemVo[] _Items) {
        mContext = _Context;
        mItems = _Items;
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_news_list, parent, false);

            viewHolder.mTitle = (TextView) convertView.findViewById(R.id.newsTitle);
            viewHolder.mDescription = (TextView) convertView.findViewById(R.id.newsDescription);
            viewHolder.mPubDate = (TextView) convertView.findViewById(R.id.newsPubDate);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        NewsItemVo item = mItems[position];

        viewHolder.mTitle.setText(item.getTitle());

        if( item.getDescription() == null )
        {
            viewHolder.mDescription.setVisibility( View.GONE );
        }
        else
        {
            viewHolder.mDescription.setVisibility( View.VISIBLE );
            viewHolder.mDescription.setText( item.getDescription() );
        }

        viewHolder.mPubDate.setText(item.getPubDate());

        return convertView;
    }
}
