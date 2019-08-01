package de.fhe.fhemobile.widgets.headerList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.fhe.fhemobile.R;

/**
 * Created by paul on 03.03.15.
 */
public class DoubleLineItem implements IBaseItem {

    public DoubleLineItem(String _headline, String _subHeadline) {
        mHeadline    = _headline;
        mSubHeadline = _subHeadline;
    }

    @Override
    public int getViewType() {
        return EItemType.DATE.ordinal();
    }

    @Override
    public View getView(LayoutInflater _inflater, View _convertView, ViewGroup _parent) {
        
        ViewHolder holder;
        if (_convertView == null) {
            holder = new ViewHolder();
            _convertView = _inflater.inflate(R.layout.item_double_line, _parent, false);
            
            holder.mHeadline    = (TextView) _convertView.findViewById(R.id.itemHeadline);
            holder.mSubHeadline = (TextView) _convertView.findViewById(R.id.itemSubHeadline);
            
            _convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) _convertView.getTag();
        }
        
        holder.mHeadline.setText(mHeadline);
        holder.mSubHeadline.setText(mSubHeadline);
        
        return _convertView;
    }

    static class ViewHolder {
        TextView mHeadline;
        TextView mSubHeadline;
    }
    
    private String mHeadline;
    private String mSubHeadline;
}
