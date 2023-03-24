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
 * View for showing and navigating through maps/floorplans of a certain building
 *
 * Created by paul on 23.02.14.
 * Edit by Nadja: rename from MapsSingleView to MapsView
 */
public class MapsView extends FrameLayout {
    private static final String TAG = MapsView.class.getSimpleName();


    public MapsView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public void initializeView(final MapVo _Map) {
        final String htmlData = "<!DOCTYPE html>" +
                "<html>" +
                "<head></head>" +
                "<body>" +
                "<img src=\"" + _Map.getImageUrl() + "\">" +
                "</body></html>";
        mMapView.getSettings().setBuiltInZoomControls(true);
        mMapView.getSettings().setLoadWithOverviewMode(true);
        mMapView.getSettings().setUseWideViewPort(true);
//        mMapView.getSettings().setDisplayZoomControls(false);
        mMapView.loadDataWithBaseURL("file:///android_asset/floorplan_images/", htmlData, "text/html", "UTF-8", "");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mMapView = (WebView) findViewById(R.id.mapsWebView);

    }

    private WebView mMapView;
}
