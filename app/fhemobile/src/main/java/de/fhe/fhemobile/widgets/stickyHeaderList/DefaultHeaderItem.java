package de.fhe.fhemobile.widgets.stickyHeaderList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.fhe.fhemobile.R;

/**
 * Created by Paul Cech on 13.05.15.
 */
public class DefaultHeaderItem extends IHeaderItem {

    public DefaultHeaderItem(String _headerText, boolean _hasSectionHeader) {
        super(_hasSectionHeader);
        mHeaderText       = _headerText;

    }

    public DefaultHeaderItem(String _headerText, boolean _hasSectionHeader, List<IRowItem> _items) {
        super(_items, _hasSectionHeader);
        mHeaderText       = _headerText;
    }

    @Override
    public int getViewType() {
        return EItemType.DEFAULT_HEADER.ordinal();
    }

    @Override
    public View getView(LayoutInflater _inflater, View _convertView, ViewGroup _parent) {

        ViewHolder viewHolder;
        if (_convertView == null) {
            viewHolder = new ViewHolder();

            // TODO: sticky header library apparently has a bug with using the parent ViewGroup
            //       as the sticky header always looses its height. So I just set the parant to null
            //       and not as intented to "_parent, false".
            _convertView = _inflater.inflate(R.layout.item_header_text_default, null);

            viewHolder.mHeadline = (TextView) _convertView.findViewById(R.id.itemHeader);

            _convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) _convertView.getTag();
        }

        viewHolder.mHeadline.setText(mHeaderText);

        return _convertView;
    }


    // ---------------------------------------------------------------------------------------------
    //  ViewHolder
    // ---------------------------------------------------------------------------------------------

    static class ViewHolder {
        TextView mHeadline;
    }


    // ---------------------------------------------------------------------------------------------
    //  Member
    // ---------------------------------------------------------------------------------------------

    private static final String LOG_TAG = DefaultHeaderItem.class.getSimpleName();

    private String         mHeaderText;
}
