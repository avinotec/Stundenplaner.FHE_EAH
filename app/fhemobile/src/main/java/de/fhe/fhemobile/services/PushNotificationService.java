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

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.security.SecureRandom;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.utils.Define;

public class PushNotificationService extends FirebaseMessagingService {
	private static final String TAG = PushNotificationService.class.getSimpleName();
	private static String firebaseToken;

	public PushNotificationService() {
	}


	@Override
	public void onMessageReceived(final RemoteMessage remoteMessage) {
		// ...
		showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
		// TODO(developer): Handle FCM messages here.
		// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
		Log.d(TAG, "From: " + remoteMessage.getFrom());


	}

	@Override
	public void onNewToken(@NonNull final String token) {
		Log.d(TAG, "Refreshed token: " + token);

		// If you want to send messages to this application instance or
		// manage this apps subscriptions on the server side, send the
		// Instance ID token to your app server.
		//sendRegistrationToServer(token);

		setFirebaseToken(token);
	}

	public static String getFirebaseToken() {
		return firebaseToken;
	}

	public static void setFirebaseToken(final String firebaseToken ) {
		PushNotificationService.firebaseToken = firebaseToken;
	}


	private void showNotification(final String title, final String body) {
		final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		//See Android Developer: Starting in Android 8.0 (API level 26), all notifications must be
		// assigned to a channel. For each channel, you can set the visual and auditory behavior
		// that is applied to all notifications in that channel. Then, users can change these
		// settings and decide which notification channels from your app should be intrusive or
		// visible at all.
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			final NotificationChannel notificationChannel = new NotificationChannel(
					Define.PushNotifications.CHANNEL_ID,
					getResources().getString(R.string.push_notification_channel_name),
					NotificationManager.IMPORTANCE_HIGH);
			notificationChannel.setDescription(getResources().getString(R.string.push_notification_channel_description));
			notificationChannel.enableLights(true);
			notificationChannel.setLightColor(Color.RED);
			notificationChannel.setVibrationPattern(Define.PushNotifications.VIBRATION_PATTERN);

			notificationManager.createNotificationChannel(notificationChannel);
		}

		final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Define.PushNotifications.CHANNEL_ID);
		notificationBuilder.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL)
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title)
				.setContentText(body);
		//todo: Set the intent that will fire when the user taps the notification
		//notificationBuilder.setContentIntent(pendingIntent);
		notificationManager.notify(new SecureRandom().nextInt(), notificationBuilder.build());

	}
}
