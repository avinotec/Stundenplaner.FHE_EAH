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
package de.fhe.fhemobile.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import de.fhe.fhemobile.R;

public class PushNotificationService extends FirebaseMessagingService {
	private static final String TAG = "PushNotificationService";
	public PushNotificationService() {
	}

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		// ...
		showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
		// TODO(developer): Handle FCM messages here.
		// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
		Log.d(TAG, "From: " + remoteMessage.getFrom());


	}

	private void showNotification(String title, String body) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		String NOTIFICATION_CHANNEL_ID = "de.fhe.fhemobile.push";
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification",NotificationManager.IMPORTANCE_DEFAULT);
			notificationChannel.setDescription("Stundenplanänderung");
			notificationChannel.enableLights(true);
			notificationChannel.setLightColor(Color.RED);
			notificationChannel.setVibrationPattern(new long[]{1000,500,1000,0});
			notificationManager.createNotificationChannel(notificationChannel);
		}
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
		notificationBuilder.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL)
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title)
				.setContentText(body)
				.setContentInfo("Info");
		notificationManager.notify(new Random().nextInt(),notificationBuilder.build());


	}

	@Override
	public void onNewToken(String token) {
		Log.d(TAG, "Refreshed token: " + token);

		// If you want to send messages to this application instance or
		// manage this apps subscriptions on the server side, send the
		// Instance ID token to your app server.
		//sendRegistrationToServer(token);
	}

}
