package de.fhe.fhemobile.services;

/*
 *  Copyright (c) 2022-2023 Ernst-Abbe-Hochschule Jena
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

import static de.fhe.fhemobile.network.Endpoints.URL_REGISTER_PUSH_NOTIFICATIONS_EAH;

import android.util.Log;

import org.junit.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;

/**
 * {@link Runnable} to register subscribed event series at server
 * using another thread
 *
 * created by Nadja - 09/22
 */
public class ServerRegistrationBackgroundTask implements Runnable {

	private static final String TAG = ServerRegistrationBackgroundTask.class.getSimpleName();

	// example FCM Token: eyrLLTIcSh6tGpGV89Ti8x:APA91bH5oRRLl3xAgK12yOWXnVr8rZtNd9pfjrIPU2D9z6rld0Bap3J3f-cg7qqVSkmcICQFG6v6Y5Y6wBwSMgMo2oX7za8AwueWQfeTCB8sUqSwLifnxYeI6WDbW9LDlYd1VmxjLAH4
	// 164 Zeichen
	final String fcmToken;
	final List<MyScheduleEventSeriesVo> subscribedEventSeriesVos = new ArrayList<>();

	public ServerRegistrationBackgroundTask(final String token, final Collection<MyScheduleEventSeriesVo> eventSeriesVos) {
		if (BuildConfig.DEBUG) assert( token != null );
		if (BuildConfig.DEBUG) assert( !token.isEmpty() );

		fcmToken = token;
		subscribedEventSeriesVos.addAll(eventSeriesVos);
	}

	@Override
	public final void run() {
		URL url = null;
		try {
			url = new URL(URL_REGISTER_PUSH_NOTIFICATIONS_EAH); // Debug: ( + "&debug=true")
		} catch (final MalformedURLException e) {
			Log.e(TAG, "URL ist nicht URL-konform: " + URL_REGISTER_PUSH_NOTIFICATIONS_EAH, e);     //NON-NLS
			return;
		}

		// https://wi-srv7.wi.eah-jena.de/api/fcm_register_user.php?&os=android&language=DE&fcm_token=token0815&eventseries_names=[]&debug=1&nonsense=nonsense
		//add params
		String data = null;
		try {
			data = encodeAsParam("os", Define.PushNotifications.VALUE_ANDROID);                          //NON-NLS
			data += encodeAsParam("fcm_token", fcmToken);                                             //NON-NLS
			if("de".equals(Locale.getDefault().getLanguage())){                                     //NON-NLS
				data += encodeAsParam("language", Define.PushNotifications.VALUE_LANG_DE);  //NON-NLS
			} else {
				data += encodeAsParam("language", Define.PushNotifications.VALUE_LANG_EN);  //NON-NLS
			}
			for(final MyScheduleEventSeriesVo eventSeriesVo : subscribedEventSeriesVos){
				data += encodeAsParam("eventseries_names[]", eventSeriesVo.getTitle()); //NON-NLS
			}
		} catch (final UnsupportedEncodingException error) {
			Log.e(TAG, "UnsupportedEncodingException error", error); //NON-NLS

			// Abbruch
			return;
		}



		final HttpsURLConnection client;
//      final HttpURLConnection client; //debug
		try {
			if (BuildConfig.DEBUG) Assert.assertNotNull(url);
			client = (HttpsURLConnection) url.openConnection();
//            client = (HttpURLConnection) url.openConnection(); //debug
		} catch (final NullPointerException error) {
			Log.e(TAG, "NullPointerException", error);  //NON-NLS
			return; // Abbruch
		} catch (final IOException error) {
			Log.e(TAG, "IOException", error);   //NON-NLS
			//Handles input and output errors
			return; // Abbruch
		}


		try {
			//Anmerkung Stepping: In Jena wird keine basic Authentifizierung benötigt
				/* final String userPassword = Define.sAuthSoapUserName + ':' + Define.sAuthSoapPassword;
				final String encoding = Base64.encodeToString(userPassword.getBytes(), Base64.DEFAULT);
				client.setRequestProperty("Authorization", "Basic " + encoding); */
			client.setRequestMethod("POST");
			client.setDoOutput(true);

			final OutputStreamWriter wr = new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8);
			wr.write(data);
			wr.flush();
			wr.close();

			Log.d(TAG, "Der ResponseCode war: " + client.getResponseCode()); //NON-NLS // 200 oder Error
			final InputStream inputStream;
			if (client.getResponseCode() == 200) {
				inputStream = client.getInputStream();      // OK
			} else {
				inputStream = client.getErrorStream();      // Error
			}

			final BufferedReader reader = new BufferedReader(new InputStreamReader( inputStream, StandardCharsets.UTF_8));
			final StringBuilder sb = new StringBuilder();
			String line;

			// Read Server Response
			while ((line = reader.readLine()) != null) {
				// Append server response in string
				sb.append(line).append("\n");                           //NON-NLS
			}

			Log.d(TAG, "SERVER RESPONSE: " + sb);                   //NON-NLS

		} catch (final MalformedURLException error) {
			Log.e(TAG, "MalformedURLException error", error);       //NON-NLS
			//Handles an incorrectly entered URL
		} catch (final SocketTimeoutException error) {
			Log.e(TAG, "SocketTimeoutException", error);
			//Handles URL access timeout.
		} catch (final UnsupportedEncodingException e) {
			Log.e(TAG, "Unsupporte  dEncodingException", e);
		} catch (final ProtocolException e) {
			Log.e(TAG, "ProtocolException", e);
		} catch (final IOException error) {
			Log.e(TAG, "IOException", error);
			//Handles input and output errors
		} catch (final NullPointerException error) {
			Log.e(TAG, "NullPointerException", error);
		} catch (final RuntimeException e){
			Log.e(TAG, "Exception", e);                 //NON-NLS
		}
	}

	private static String encodeAsParam(final String name, final String value) throws UnsupportedEncodingException {
		return "&" + URLEncoder.encode(name, "UTF-8")       //NON-NLS
				+ "=" + URLEncoder.encode(value, "UTF-8");  //NON-NLS
	}
}
