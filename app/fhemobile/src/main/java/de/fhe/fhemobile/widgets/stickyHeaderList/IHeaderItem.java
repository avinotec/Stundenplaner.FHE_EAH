package de.fhe.fhemobile.widgets.stickyHeaderList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Cech on 13.05.15.
 */
public abstract class IHeaderItem {

    public enum EItemType {
        LARGE_HEADER, DEFAULT_HEADER, MENSA_IMAGE, WEATHER
    }

    // ---------------------------------------------------------------------------------------------
    //  Constructor
    // ---------------------------------------------------------------------------------------------

    public IHeaderItem(boolean _hasSectionHeader) {
        mHasSectionHeader = _hasSectionHeader;
        mItems            = new ArrayList<>();
    }

    public IHeaderItem(List<IRowItem> _items, boolean _hasSectionHeader) {
        mItems            = _items;
        mHasSectionHeader = _hasSectionHeader;
    }

    // ---------------------------------------------------------------------------------------------
    //  Public Methods
    // ---------------------------------------------------------------------------------------------

    public List<IRowItem> getItems() {
        return mItems;
    }

    public void setItems(List<IRowItem> _items) {
        mItems = _items;
    }

    public void addItem(IRowItem _item) {
        mItems.add(_item);
    }

    public boolean hasSectionHeader() {
        return mHasSectionHeader;
    }

    // ---------------------------------------------------------------------------------------------
    //  Abstract methods
    // ---------------------------------------------------------------------------------------------

    public abstract int getViewType();

    public abstract View getView(LayoutInflater _inflater, View _convertView, ViewGroup _parent);

    // ---------------------------------------------------------------------------------------------
    //  Member
    // ---------------------------------------------------------------------------------------------

    protected List<IRowItem> mItems;
    protected boolean        mHasSectionHeader;
}
