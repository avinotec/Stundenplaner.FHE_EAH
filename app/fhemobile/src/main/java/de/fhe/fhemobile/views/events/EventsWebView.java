package de.fhe.fhemobile.views.events;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.FrameLayout;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.network.Endpoints;
import de.fhe.fhemobile.views.SSLTolerentWebViewClient;

/**
 * Created by Nadja on 07.09.21
 * Anzeige des Veranstaltungskalenders der Hochschul-Homepage
 */
public class EventsWebView extends FrameLayout {

    private final Context mContext;

    private WebView mWebView;

    public EventsWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mWebView = (WebView) findViewById(R.id.eventsWebView);
        //wird der WebViewClient nicht geaendert, kann die Seite nicht geladen werden, da die Webview
        mWebView.setWebViewClient(new SSLTolerentWebViewClient(mContext));
        //Ohne JavascriptEnabled laesst sich das Impressum nicht scrollen.
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(Endpoints.EVENTS_ENDPOINT);

    }
}
