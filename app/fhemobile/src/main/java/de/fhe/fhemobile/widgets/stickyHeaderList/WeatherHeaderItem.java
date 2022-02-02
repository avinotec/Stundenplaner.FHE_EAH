/*
 *  Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package de.fhe.fhemobile.widgets.stickyHeaderList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.widgets.WeatherHeader;

/**
 * Created by Paul Cech on 13.05.15.
 */
public class WeatherHeaderItem extends IHeaderItem {

    public WeatherHeaderItem(final boolean _hasSectionHeader) {
        super(_hasSectionHeader);
    }

    public WeatherHeaderItem(final List<IRowItem> _items, final boolean _hasSectionHeader) {
        super(_items, _hasSectionHeader);
    }

    @Override
    public int getViewType() {
        return EItemType.WEATHER.ordinal();
    }

    @Override
    public View getView(final LayoutInflater _inflater, View _convertView, final ViewGroup _parent) {

        final ViewHolder viewHolder;
        if(_convertView == null) {
            viewHolder = new ViewHolder();
            _convertView = _inflater.inflate(R.layout.item_header_weather, _parent, false);

            viewHolder.mWeatherHeader = (WeatherHeader) _convertView.findViewById(R.id.weatherHeader);

            _convertView.setTag(viewHolder);
        }
        else {
            //TODO never used
            viewHolder = (ViewHolder) _convertView.getTag();
        }

        return _convertView;
    }

    static class ViewHolder {
        WeatherHeader mWeatherHeader;
    }

    private static final String LOG_TAG = WeatherHeaderItem.class.getSimpleName();
}
