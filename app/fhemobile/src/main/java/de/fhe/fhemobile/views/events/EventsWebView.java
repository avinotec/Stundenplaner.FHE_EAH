package de.fhe.fhemobile.views.events;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.network.Endpoints;
import de.fhe.fhemobile.views.SSLTolerantWebViewClient;

/**
 * Created by Nadja on 07.09.21
 * Anzeige des Veranstaltungskalenders der Hochschul-Homepage
 */
public class EventsWebView extends FrameLayout {

    private final Context mContext;

    public EventsWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        final WebView mWebView = (WebView) findViewById(R.id.eventsWebView);
        //wird der WebViewClient nicht geaendert, kann die Seite nicht geladen werden, da die Webview
        mWebView.setWebViewClient(new SSLTolerantWebViewClient(mContext));
        //Ohne JavascriptEnabled laesst sich das WebView nicht scrollen.
        mWebView.getSettings().setJavaScriptEnabled(true);

        //MS experimental
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.loadUrl(Endpoints.EVENTS_ENDPOINT);


    }
}
