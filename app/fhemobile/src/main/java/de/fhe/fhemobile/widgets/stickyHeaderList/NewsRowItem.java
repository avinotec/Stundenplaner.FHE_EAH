package de.fhe.fhemobile.widgets.stickyHeaderList;

import android.content.Context;
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

    public NewsRowItem(NewsItemVo _item) {
        mItem = _item;
    }

    @Override
    public int getViewType() {
        return EItemType.NEWS.ordinal();
    }

    @Override
    public View getView(LayoutInflater _inflater, View _convertView, ViewGroup _parent) {

        ViewHolder viewHolder;
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

    private NewsItemVo mItem;
}
