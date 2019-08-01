package de.fhe.fhemobile.views.impressum;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.FrameLayout;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.network.Endpoints;

/**
 * Created by paul on 22.01.14.
 */
public class ImpressumView extends FrameLayout {

    private Context mContext;

    private WebView mWebView;

    public ImpressumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mWebView = (WebView) findViewById(R.id.impressumWebView);
        mWebView.loadUrl(Endpoints.BASE_URL + Endpoints.APP_NAME + Endpoints.IMPRESSUM);

    }
}
