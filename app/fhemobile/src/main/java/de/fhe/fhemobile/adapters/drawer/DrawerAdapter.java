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

    public DrawerAdapter(Context _Context, List<DrawerItem> _Items) {
        mContext = _Context;
        mItems   = _Items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public DrawerItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
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

    private Context          mContext;
    private List<DrawerItem> mItems;
}
