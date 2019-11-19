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
package de.fhe.fhemobile.views.maps;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.FrameLayout;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.vos.maps.MapVo;

/**
 * Created by paul on 23.02.14.
 */
public class MapsSingleView extends FrameLayout {
    public MapsSingleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void initializeView(MapVo _Map) {
        String htmlData = "<!DOCTYPE html>" +
                "<html>" +
                "<head></head>" +
                "<body>" +
                "<img src=\"" + _Map.getImageUrl() + "\">" +
                "</body></html>";
        mMapView.getSettings().setBuiltInZoomControls(true);
        mMapView.getSettings().setLoadWithOverviewMode(true);
        mMapView.getSettings().setUseWideViewPort(true);
//        mMapView.getSettings().setDisplayZoomControls(false);
        mMapView.loadDataWithBaseURL("file:///android_asset/maps/", htmlData, "text/html", "UTF-8", "");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mMapView = (WebView) findViewById(R.id.mapsWebView);

    }

    private static final String LOG_TAG = MapsSingleView.class.getSimpleName();

    private final Context mContext;

    private WebView mMapView;
}
