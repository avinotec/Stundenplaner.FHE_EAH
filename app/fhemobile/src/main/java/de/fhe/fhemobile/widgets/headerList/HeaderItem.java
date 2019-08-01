package de.fhe.fhemobile.widgets.headerList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.fhe.fhemobile.R;

/**
 * Created by paul on 03.03.15.
 */
public class HeaderItem implements IBaseItem {

    public HeaderItem(String _headerText) {
        mHeaderText = _headerText;
    }

    @Override
    public int getViewType() {
        return EItemType.HEADER.ordinal();
    }

    @Override
    public View getView(LayoutInflater _inflater, View _convertView, ViewGroup _parent) {
        ViewHolder holder;
        if (_convertView == null) {
            holder = new ViewHolder();
            _convertView = _inflater.inflate(R.layout.item_header_text_default, _parent, false);

            holder.mHeaderText = (TextView) _convertView.findViewById(R.id.itemHeader);
            
            _convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) _convertView.getTag();
        }

        holder.mHeaderText.setText(mHeaderText);
        
        return _convertView;
    }

    static class ViewHolder {
        TextView mHeaderText;
    }

    private String mHeaderText;
}
