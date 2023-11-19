/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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
package de.fhe.fhemobile.views.joboffers;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.FrameLayout;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.network.Endpoints;
import de.fhe.fhemobile.views.SSLTolerantWebViewClient;

public class JobOffersWebView extends FrameLayout {

    private final Context mContext;

    public  JobOffersWebView(final Context context, final AttributeSet attrs){
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        final WebView mWebView = (WebView) findViewById(R.id.webview_joboffers);
        //if the WebViewClient is not changed, the website is not able to load
        mWebView.setWebViewClient(new SSLTolerantWebViewClient(mContext));
        //JavaScript needs to be enabled to work properly
        mWebView.getSettings().setJavaScriptEnabled(true);

        //TODO MS experimental
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        //deprecated mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.loadUrl(Endpoints.JOBOFFERS_ENDPOINT);
    }
}
