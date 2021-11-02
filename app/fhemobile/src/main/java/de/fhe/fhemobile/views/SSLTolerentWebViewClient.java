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

package de.fhe.fhemobile.views;

import android.content.Context;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;

import de.fhe.fhemobile.R;

public class SSLTolerentWebViewClient extends WebViewClient {
	private static final String TAG = "SSLTolerentWebViewClien";
	private Context context;
	public SSLTolerentWebViewClient(final Context context){

	}
	@Override
	public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {

		try {
			final AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(R.string.notification_error_ssl_cert_invalid);
			builder.setPositiveButton(context.getString(R.string.ssl_error_continue), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					handler.proceed();
				}
			});
			builder.setNegativeButton(context.getString(R.string.ssl_error_cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					handler.cancel();
				}
			});
			final AlertDialog dialog = builder.create();
			dialog.show();
		}catch (final Exception e){
			Log.d(TAG, "onReceivedSslError: ",e);
			handler.proceed();
		}
	}

}
