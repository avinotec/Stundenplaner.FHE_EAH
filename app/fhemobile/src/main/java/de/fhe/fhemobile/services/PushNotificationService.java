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

import static de.fhe.fhemobile.utils.Define.PushNotifications.PARAM_EXAM_ADDED;
import static de.fhe.fhemobile.utils.Define.PushNotifications.PARAM_TIMETABLE_CHANGED;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;

public class PushNotificationService extends FirebaseMessagingService {

	private static final String TAG = PushNotificationService.class.getSimpleName();

	private static String fcmToken;

	@Override
	public void onMessageReceived(final RemoteMessage remoteMessage) {

		if(PARAM_TIMETABLE_CHANGED.equals(remoteMessage.getNotification().getTitle())){

			final String message = remoteMessage.getNotification().getBody() + " "
					+ Main.getAppContext().getString(R.string.fcm_timetablechange_message_part1);
			final String messageLong = message + " " + Main.getAppContext().getString(R.string.fcm_message_part2);

			//todo: click action
			showNotification(
					Main.getAppContext().getString(R.string.fcm_change_title),
					message,
					messageLong);

		} else if(PARAM_EXAM_ADDED.equals(remoteMessage.getNotification().getTitle())){
			final String message = Main.getAppContext().getString(R.string.fcm_examadded_message_part1)
					+ " " + remoteMessage.getNotification().getBody() + ".";
			final String messageLong = message + " " + Main.getAppContext().getString(R.string.fcm_message_part2);

			showNotification(
					Main.getAppContext().getString(R.string.fcm_change_title),
					message,
					messageLong);
		}

		// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
		Log.d(TAG, "From: " + remoteMessage.getFrom());
		Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
	}

	@Override
	public void onNewToken(@NonNull final String token) {
		Log.d(TAG, "Refreshed token: " + token);

		if(Define.ENABLE_PUSHNOTIFICATIONS){
			//unregister old token
			sendRegistrationToServer(fcmToken, new ArrayList<>());
			//set and register new token
			setFcmToken(token);
			sendRegistrationToServer(fcmToken, Main.getSubscribedEventSeries());
		}
	}

	public static void setFcmToken(final String fcmToken) {
		PushNotificationService.fcmToken = fcmToken;
	}

	/**
	 * Register the given event series' for the given firebase token
	 * @param eventSeriesVos List of {@link MyScheduleEventSeriesVo}s to register for
	 */
	public static void sendRegistrationToServer(final String token, final List<MyScheduleEventSeriesVo> eventSeriesVos){
		if(BuildConfig.DEBUG && Define.ENABLE_PUSHNOTIFICATIONS) Assert.assertNotNull(fcmToken);
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

	/**
	 * Build and show push notification
	 * @param title The title of the notification
	 * @param messageShort The message to show if notification is not expanded
	 * @param messageLong The message to show if notification gets expanded
	 */
	private void showNotification(final String title,
								  final String messageShort,
								  final String messageLong) {
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

		// Create an explicit intent for an Activity in your app
		final PendingIntent pendingIntent = PendingIntent.getActivity(Main.getAppContext(), 0,
				new Intent(Main.getAppContext(), MainActivity.class), 0);


		final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Define.PushNotifications.CHANNEL_ID);
		notificationBuilder.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL)
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title)
				.setContentText(messageShort)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(messageLong))
				//automatically removes the notification when the user taps it
				.setAutoCancel(true)
				.setContentIntent(pendingIntent);

		notificationManager.notify(new SecureRandom().nextInt(), notificationBuilder.build());
	}
}
