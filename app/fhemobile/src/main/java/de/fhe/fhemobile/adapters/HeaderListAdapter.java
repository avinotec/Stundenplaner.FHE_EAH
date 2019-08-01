package de.fhe.fhemobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import de.fhe.fhemobile.widgets.headerList.IBaseItem;

/**
 * Created by paul on 03.03.15.
 */
public class HeaderListAdapter extends BaseAdapter {

    public HeaderListAdapter(Context _context, List<IBaseItem> _items) {
        mItems          = _items;
        mLayoutInflater = LayoutInflater.from(_context);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mItems.get(position).getView(mLayoutInflater, convertView, parent);
    }

    @Override
    public int getViewTypeCount() {
        return IBaseItem.EItemType.values().length;
    }

    //getItemViewType-------------------------------------------------------------------------------
    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getViewType();
    }

    private List<IBaseItem> mItems;
    private LayoutInflater  mLayoutInflater;
}
