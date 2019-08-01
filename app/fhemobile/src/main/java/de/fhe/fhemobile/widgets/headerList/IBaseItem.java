package de.fhe.fhemobile.widgets.headerList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by paul on 03.03.15.
 */
public interface IBaseItem {
    
    int getViewType();
    
    View getView(LayoutInflater _inflater, View _convertView, ViewGroup _parent);

    enum EItemType {
        HEADER, DATE, EVENT, HEADER_IMAGE
    }
}
