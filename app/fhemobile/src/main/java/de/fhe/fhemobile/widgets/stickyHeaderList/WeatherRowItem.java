package de.fhe.fhemobile.widgets.stickyHeaderList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.widgets.WeatherHeader;

/**
 * Created by Paul Cech on 13.05.15.
 */
public class WeatherRowItem extends IRowItem {

    public WeatherRowItem() {
    }

    @Override
    public int getViewType() {
        return EItemType.WEATHER.ordinal();
    }

    @Override
    public View getView(LayoutInflater _inflater, View _convertView, ViewGroup _parent) {

        ViewHolder viewHolder;
        if(_convertView == null) {
            viewHolder = new ViewHolder();
            _convertView = _inflater.inflate(R.layout.item_header_weather, _parent, false);

            viewHolder.mWeatherHeader = (WeatherHeader) _convertView.findViewById(R.id.weatherHeader);

            _convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) _convertView.getTag();
        }

        return _convertView;
    }

    static class ViewHolder {
        WeatherHeader mWeatherHeader;
    }

    private static final String LOG_TAG = WeatherRowItem.class.getSimpleName();
}
