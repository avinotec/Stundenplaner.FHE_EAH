package de.fhe.fhemobile.widgets.stickyHeaderList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Paul Cech on 13.05.15.
 */
public abstract class IRowItem {

    public abstract int getViewType();

    public abstract View getView(LayoutInflater _inflater, View _convertView, ViewGroup _parent);

    public enum EItemType {
        MENSA, MENSA_IMAGE, DEFAULT_IMAGE, DOUBLE_ROW, NEWS, WEATHER
    }
}
