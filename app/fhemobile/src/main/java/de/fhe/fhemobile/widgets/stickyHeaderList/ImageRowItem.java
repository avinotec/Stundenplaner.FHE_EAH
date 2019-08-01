package de.fhe.fhemobile.widgets.stickyHeaderList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.fhe.fhemobile.R;

/**
 * Created by Paul Cech on 13.05.15.
 */
public class ImageRowItem extends IRowItem {

    public ImageRowItem(int _imageRes) {
        mImageRes = _imageRes;
    }

    @Override
    public int getViewType() {
        return EItemType.DEFAULT_IMAGE.ordinal();
    }

    @Override
    public View getView(LayoutInflater _inflater, View _convertView, ViewGroup _parent) {
        ViewHolder holder;
        if (_convertView == null) {
            holder = new ViewHolder();
            _convertView = _inflater.inflate(R.layout.item_header_image, _parent, false);

            holder.mHeaderImage = (ImageView) _convertView.findViewById(R.id.itemHeaderImage);

            _convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) _convertView.getTag();
        }

        holder.mHeaderImage.setImageResource(mImageRes);

        return _convertView;
    }

    static class ViewHolder {
        ImageView mHeaderImage;
    }

    private static final String LOG_TAG = ImageRowItem.class.getSimpleName();

    private int mImageRes;
}
