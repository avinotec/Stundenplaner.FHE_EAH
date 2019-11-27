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
package de.fhe.fhemobile.views.impressum;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.network.Endpoints;

/**
 * Created by paul on 22.01.14.
 */
public class ImpressumView extends FrameLayout {

    private final Context mContext;

    private WebView mWebView;

    public ImpressumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mWebView = (WebView) findViewById(R.id.impressumWebView);
        //wird der WebViewClient nicht geaendert, kann die Seite nicht geladen werden, da die Webview
        mWebView.setWebViewClient(new SSLTolerentWebViewClient(mContext));
        //Ohne JavascriptEnabled laesst sich das Impressum nicht scrollen.
	    mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(Endpoints.IMPRESSUM_ENDPOINT);
        TextView dataProtection = findViewById(R.id.data_protection_link);
        dataProtection.setMovementMethod(LinkMovementMethod.getInstance());
        TextView versionText = findViewById(R.id.version_number);
        String sText = "Vers.: " + BuildConfig.FLAVOR + " " +BuildConfig.VERSION_NAME +" ("+ BuildConfig.VERSION_CODE + ')'; //$NON-NLS
        if ( BuildConfig.DEBUG ) {
            sText += " DEBUG"; //NON-NLS
        }  //$NON-NLS
        versionText.setText(sText);
    }
}
