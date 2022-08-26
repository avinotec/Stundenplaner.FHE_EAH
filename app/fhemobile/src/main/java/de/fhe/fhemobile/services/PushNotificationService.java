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

import org.junit.Assert;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;

public class PushNotificationService extends FirebaseMessagingService {

	private static final String TAG = PushNotificationService.class.getSimpleName();

	private static String fcmToken;

	@Override
	public void onMessageReceived(final RemoteMessage remoteMessage) {

		showNotification(
				remoteMessage.getNotification().getTitle(),
				remoteMessage.getNotification().getBody());

		// todo: fetch My Schedule here? or wait for user to open app
		//  and maybe also wait for pressing the update button?
		//NetworkHandler.getInstance().fetchMySchedule();

		// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
		Log.d(TAG, "From: " + remoteMessage.getFrom());
		Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
	}

	@Override
	public void onNewToken(@NonNull final String token) {
		Log.d(TAG, "Refreshed token: " + token);

		//unregister old token
		sendRegistrationToServer(fcmToken, new ArrayList<>());
		//set and register new token
		setFcmToken(token);
		sendRegistrationToServer(fcmToken, Main.getSubscribedEventSeries());
	}

	public static void setFcmToken(final String fcmToken) {
		PushNotificationService.fcmToken = fcmToken;
	}

	/**
	 * Register the given event series' for the given firebase token
	 * @param eventSeriesVos List of {@link MyScheduleEventSeriesVo}s to register for
	 */
	public static void sendRegistrationToServer(String token, List<MyScheduleEventSeriesVo> eventSeriesVos){
		if(BuildConfig.DEBUG) Assert.assertNotNull(fcmToken);
		if(fcmToken != null) {
			Main.executorService.execute(new ServerRegistrationBackgroundTask(token, eventSeriesVos));
		}
	}

	/**
	 * Register the {@link Main#subscribedEventSeries} for the current {@link PushNotificationService#fcmToken}
	 */
	public static void registerSubscribedEventSeries(){
		if(BuildConfig.DEBUG) Assert.assertNotNull(fcmToken);

		if(fcmToken != null) {
			Main.executorService.execute(new ServerRegistrationBackgroundTask(
					fcmToken, Main.getSubscribedEventSeries()));
		}
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
