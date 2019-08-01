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

    private Context mContext;

    private WebView mMapView;
}
