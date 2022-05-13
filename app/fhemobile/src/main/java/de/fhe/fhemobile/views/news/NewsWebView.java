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
package de.fhe.fhemobile.views.news;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.FrameLayout;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.network.Endpoints;
import de.fhe.fhemobile.views.SSLTolerentWebViewClient;

/**
 * Created by Nadja on 06.09.21
 * for displaying News-Browser-Webpage using WebView
 */
public class NewsWebView extends FrameLayout {

    private final Context mContext;

    private WebView mWebView;

    public NewsWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mWebView = (WebView) findViewById(R.id.webview_news);
        //wird der WebViewClient nicht geaendert, kann die Seite nicht geladen werden, da die Webview
        mWebView.setWebViewClient(new SSLTolerentWebViewClient(mContext));
        //JavaScripted needs to be enabled
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(Endpoints.NEWS_ENDPOINT);

    }
}
