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
