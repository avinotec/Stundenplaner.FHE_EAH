package de.fhe.fhemobile.services;

/*
 *  Copyright (c) 2022-2022 Ernst-Abbe-Hochschule Jena
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

import com.google.gson.Gson;

import org.junit.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.utils.myschedule.MyScheduleUtils;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;

public class ServerRegistrationBackgroundTask implements Runnable {

    private static final String TAG = ServerRegistrationBackgroundTask.class.getSimpleName();

    final String fcmToken;
    final List<String> subscriptions = new ArrayList<>();

    public ServerRegistrationBackgroundTask(String token, List<MyScheduleEventSeriesVo> eventSeriesVos) {
        fcmToken = token;
        subscriptions.addAll(MyScheduleUtils.collectEventSetIds(eventSeriesVos));
    }

    @Override
    public void run() {
        URL url = null;
        try {
            url = new URL(URL_REGISTER_PUSH_NOTIFICATIONS_EAH); // Debug: ( + "?debug=1")
        } catch (MalformedURLException e) {
            Log.e(TAG, "URL ist nicht URL-konform: " + URL_REGISTER_PUSH_NOTIFICATIONS_EAH, e);
        }
        HttpURLConnection client = null;
        try {
            if (BuildConfig.DEBUG) Assert.assertNotNull(url);
            client = (HttpURLConnection) url.openConnection();

            String data = URLEncoder.encode("fcm_token", "UTF-8")
                    + "=" + URLEncoder.encode(fcmToken, "UTF-8");

            data += "&" + URLEncoder.encode("eventset_ids", "UTF-8") + "="
                    + URLEncoder.encode((new Gson()).toJson(subscriptions), "UTF-8");

            //Anmerkung Stepping: In Jena wird keine basic Authentifizierung benötigt
				/* final String userPassword = Define.sAuthSoapUserName + ':' + Define.sAuthSoapPassword;
				final String encoding = Base64.encodeToString(userPassword.getBytes(), Base64.DEFAULT);
				client.setRequestProperty("Authorization", "Basic " + encoding); */

            client.setRequestMethod("POST");
            client.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(client.getOutputStream());
            wr.write(data);
            wr.flush();
            wr.close();

            if (client.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line).append("\n");
                }

                final String text = sb.toString();

                Log.d(TAG, "SERVER RESPONSE: " + text);
            } else {
                Log.d(TAG, "Der ResponseCode war: " + client.getResponseCode());

                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getErrorStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line).append("\n");
                }

                Log.d(TAG, "SERVER ERROR RESPONSE: " + sb.toString());
            }


        } catch (final MalformedURLException error) {
            Log.d(TAG, "MalformedURLException error: " + error.toString());
            //Handles an incorrectly entered URL
        } catch (final SocketTimeoutException error) {
            Log.d(TAG, "SocketTimeoutException: " + error.toString());
            //Handles URL access timeout.
        } catch (final IOException error) {
            Log.d(TAG, "IOException: " + error.toString());
            //Handles input and output errors
        } catch (final NullPointerException error) {
            Log.d(TAG, "NullPointerException: " + error.toString());
        }
    }
}