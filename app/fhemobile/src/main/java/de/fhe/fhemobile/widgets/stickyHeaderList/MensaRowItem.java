package de.fhe.fhemobile.widgets.stickyHeaderList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.vos.mensa.MensaFoodItemVo;

/**
 * Created by Paul Cech on 13.05.15.
 */
public class MensaRowItem extends IRowItem {

    public MensaRowItem(MensaFoodItemVo _item) {
        mItem           = _item;
    }

    @Override
    public int getViewType() {
        return EItemType.MENSA.ordinal();
    }

    @Override
    public View getView(LayoutInflater _inflater, View _convertView, ViewGroup _parent) {

        ViewHolder viewHolder;
        if(_convertView == null) {
            viewHolder  = new ViewHolder();
            _convertView = _inflater.inflate(R.layout.item_mensa_food, _parent, false);

            viewHolder.mTitle       = (TextView) _convertView.findViewById(R.id.mensaTitle);
            viewHolder.mDescription = (TextView) _convertView.findViewById(R.id.mensaDescription);
            viewHolder.mPrice       = (TextView) _convertView.findViewById(R.id.mensaPrice);

            _convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) _convertView.getTag();
        }

        viewHolder.mTitle.setText(mItem.getTitle());
        viewHolder.mDescription.setText(mItem.getDescription());
        viewHolder.mPrice.setText(mItem.getPrice());

        return _convertView;
    }


    // ---------------------------------------------------------------------------------------------
    //  ViewHolder
    // ---------------------------------------------------------------------------------------------

    static class ViewHolder {
        TextView mTitle;
        TextView mDescription;
        TextView mPrice;
    }

    // ---------------------------------------------------------------------------------------------
    //  Member
    // ---------------------------------------------------------------------------------------------

    private static final String LOG_TAG = MensaRowItem.class.getSimpleName();

    private MensaFoodItemVo mItem;
}
