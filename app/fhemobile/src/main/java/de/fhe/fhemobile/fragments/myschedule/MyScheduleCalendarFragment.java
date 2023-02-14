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

package de.fhe.fhemobile.fragments.myschedule;

import static de.fhe.fhemobile.utils.Define.MySchedule.SP_MYSCHEDULE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.FragmentActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.activities.SettingsActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.services.FetchMyScheduleBackgroundTask;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.utils.feature.Features;
import de.fhe.fhemobile.views.myschedule.MyScheduleCalendarView;

public class MyScheduleCalendarFragment extends FeatureFragment {

	public static final String TAG = MyScheduleCalendarFragment.class.getSimpleName();

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment MyScheduleCalendarFragment.
	 */
	public static MyScheduleCalendarFragment newInstance() {
		final MyScheduleCalendarFragment fragment = new MyScheduleCalendarFragment();
		final Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public MyScheduleCalendarFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
	                         final Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mView = (MyScheduleCalendarView) inflater.inflate(R.layout.fragment_myschedule_calendar, container, false);

		askForClearingScheduleAfterTurnOfSemester();

		//Set text view to show if list is empty
		mView.setEmptyCalendarView();
		MyScheduleCalendarView.setLastUpdatedTextView();

		return mView;
	}

	/**
	 * Now adapters and list are initiated and layouts inflated, so the view can jump to today's courses
	 */
	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//outdated: My Schedule Calendar list was changed to not displaying past days
//		mView.jumpToToday();

		//replacement of deprecated setHasOptionsMenu(), onCreateOptionsMenu() and onOptionsItemSelected()
		// see https://developer.android.com/jetpack/androidx/releases/activity#1.4.0-alpha01
		final MenuHost menuHost = requireActivity();
		final FragmentActivity activity = getActivity();
		menuHost.addMenuProvider(new MenuProvider() {
			@Override
			public void onCreateMenu(@NonNull final Menu menu, @NonNull final MenuInflater menuInflater) {

				menu.clear();
				// Add menu items here
				menuInflater.inflate(R.menu.menu_myschedule_calendar, menu);
				if(!Define.ENABLE_MYSCHEDULE_UPDATING){
					//disable button for updating
					final MenuItem updateButton = menu.findItem(R.id.action_update_myschedule);
					updateButton.setEnabled(false);
					updateButton.setVisible(false);
				}
			}

			@Override
			public boolean onMenuItemSelected(@NonNull final MenuItem menuItem) {
				// Handle the menu selection

				//outdated: My Schedule Calendar list was changed to not displaying past days
//				if (menuItem.getItemId() == R.id.action_jump_to_today) {
//					mView.jumpToToday();
//					return true;
//				}
				if (menuItem.getItemId() == R.id.action_edit_my_courses) {
					final Intent intent = new Intent(activity, SettingsActivity.class);
					intent.putExtra(SettingsActivity.EXTRA_SETTINGS_ID, Features.FeatureId.MYSCHEDULE);
					activity.startActivity(intent);
					return true;
				}
				if (menuItem.getItemId() == R.id.action_update_myschedule){
					if(Define.ENABLE_MYSCHEDULE_UPDATING){
						FetchMyScheduleBackgroundTask.fetch();
					}
				}

				return false;
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		if(Define.ENABLE_MYSCHEDULE_UPDATING) {
			//Fetch/update my schedule
			FetchMyScheduleBackgroundTask.startPeriodicFetching();
		}
	}

	@Override
	public void onStop() {
		super.onStop();

		FetchMyScheduleBackgroundTask.stopPeriodicFetching();
	}

	/**
	 * When the app is opened for the first time after 1st of March and 1st of September,
	 * the user is asked, if the timetable (subscribed courses) should be cleared due to semester change.
	 */
	private void askForClearingScheduleAfterTurnOfSemester() {

		final SharedPreferences sharedPreferences = Main.getAppContext().getSharedPreferences(SP_MYSCHEDULE, Context.MODE_PRIVATE);
		final long lastOpened = sharedPreferences.getLong(Define.MySchedule.PREFS_APP_LAST_OPENED, 0);

		//if app has been opened last before
		if(lastOpened != 0){

			final Calendar calLastOpened = Calendar.getInstance(Locale.GERMANY);
			calLastOpened.setTimeInMillis(lastOpened);

			// Calendar instances for optional semester change in app on 1st of March
			final Calendar calWsHolidayStart = Calendar.getInstance(Locale.GERMANY);
			calWsHolidayStart.set(Calendar.MONTH, Calendar.MARCH);
			calWsHolidayStart.set(Calendar.DAY_OF_MONTH, 1);
			// Calendar instances for forced semester change in app on 1st of April
			final Calendar calSsStart = Calendar.getInstance(Locale.GERMANY);
			calSsStart.set(Calendar.MONTH, Calendar.APRIL);
			calSsStart.set(Calendar.DAY_OF_MONTH, 1);
			// Calendar instances for optional semester change in app on 1st of September
			final Calendar calSsHolidayStart = Calendar.getInstance(Locale.GERMANY);
			calSsHolidayStart.set(Calendar.MONTH, Calendar.SEPTEMBER);
			calSsHolidayStart.set(Calendar.DAY_OF_MONTH, 1);
			// Calendar instances for forced semester change in app on 1st of October
			final Calendar calWsStart = Calendar.getInstance(Locale.GERMANY);
			calWsStart.set(Calendar.MONTH, Calendar.OCTOBER);
			calWsStart.set(Calendar.DAY_OF_MONTH, 1);
			// today
			final Calendar now = Calendar.getInstance();


			//if lastOpened is before one of the semester change dates and today is after
			if((calLastOpened.before(calWsHolidayStart) && now.after(calWsHolidayStart))
					|| (calLastOpened.before(calSsHolidayStart) && now.after(calSsHolidayStart))){

				boolean userCanRefuse = true;
				// forced semester change
				if((calLastOpened.before(calWsStart) && now.after(calWsStart))
						|| (calLastOpened.before(calSsStart) && now.after(calSsStart))){
					userCanRefuse = false;
				}

				// show dialog to ask if old timetable should be cleared
				final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getContext())
						.setTitle(R.string.deleteTimetableTitle)
						.setPositiveButton(R.string.deleteTimetableConfirm, new DialogInterface.OnClickListener() {

							public void onClick(final DialogInterface dialog, final int which) {
								MainActivity.clearSubscribedEventSeriesAndUpdateAdapters();
							}
						})
						.setIcon(android.R.drawable.ic_dialog_alert);
				if(userCanRefuse) {
					dialogBuilder.setMessage(R.string.deleteTimetableMessageOptional);
					dialogBuilder.setNegativeButton(R.string.deleteTimetableCancel, null);
				} else {
					dialogBuilder.setMessage(R.string.deleteTimetableMessageForced);
				}
				dialogBuilder.show();
			}
		}

		//save date of last opening to shared preferences
		final SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(Define.MySchedule.PREFS_APP_LAST_OPENED, new Date().getTime());
		editor.apply();
	}

	MyScheduleCalendarView mView;
}

