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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.activities.SettingsActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.network.NetworkHandler;
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
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//replacement of deprecated setHasOptionsMenu(), onCreateOptionsMenu() and onOptionsItemSelected()
		final MenuHost menuHost = requireActivity();
		final FragmentActivity activity = getActivity();
		final Fragment fragment = this;
		menuHost.addMenuProvider(new MenuProvider() {
			@Override
			public void onCreateMenu(@NonNull final Menu menu, @NonNull final MenuInflater menuInflater) {

				menu.clear();
				// Add menu items here
				menuInflater.inflate(R.menu.menu_myschedule_calendar, menu);
				if(!Define.ENABLE_MYSCHEDULE_UPDATING_AND_PUSHNOTIFICATIONS){
					MenuItem updateButton = menu.findItem(R.id.action_update);
					updateButton.setEnabled(false);
					updateButton.setVisible(false);
				}
			}

			@Override
			public boolean onMenuItemSelected(@NonNull final MenuItem menuItem) {
				// Handle the menu selection
				if (menuItem.getItemId() == R.id.action_jump_to_today) {
					mView.jumpToToday();
					return true;
				}
				if (menuItem.getItemId() == R.id.action_edit_my_courses) {
					final Intent intent = new Intent(activity, SettingsActivity.class);
					intent.putExtra(SettingsActivity.EXTRA_SETTINGS_ID, Features.FeatureId.MYSCHEDULE);
					activity.startActivity(intent);
					return true;
				}
				if (menuItem.getItemId() == R.id.action_update){
					if(Define.ENABLE_MYSCHEDULE_UPDATING_AND_PUSHNOTIFICATIONS){
						NetworkHandler.getInstance().fetchMySchedule();
						mView.setLastUpdatedTextView();
					}

				}

				return false;
			}
		});
	}


	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
	                         final Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mView = (MyScheduleCalendarView) inflater.inflate(R.layout.fragment_myschedule_calendar, container, false);

		askForDeletingScheduleAfterTurnOfSemester();

		//Set text view to show if list is empty
		mView.setEmptyCalendarView();
		mView.setLastUpdatedTextView();

		return mView;
	}

	/**
	 * Now adapters and list are initiated and layouts inflated, so the view can jump to today's courses
	 */
	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mView.jumpToToday();
	}

	@Override
	public void onResume() {
		super.onResume();

		if(Define.ENABLE_MYSCHEDULE_UPDATING_AND_PUSHNOTIFICATIONS){
			NetworkHandler.getInstance().fetchMySchedule();
			mView.setLastUpdatedTextView();
		}
	}

	/**
	 * When the app is opened for the first time after 1st of March and 1st of September,
	 * the user is asked, if the timetable (subscribed courses) should be cleared due to semester change.
	 */
	private void askForDeletingScheduleAfterTurnOfSemester() {

		final SharedPreferences sharedPreferences = getContext().getSharedPreferences(SP_MYSCHEDULE, Context.MODE_PRIVATE);
		final long lastOpened = sharedPreferences.getLong(Define.MySchedule.PREFS_APP_LAST_OPENED, 0);

		//if app has been opened last before
		if(lastOpened != 0){


			final Calendar calLastOpened = Calendar.getInstance(Locale.GERMANY);
			calLastOpened.setTimeInMillis(lastOpened);

			// Semester change in app on 1st of March
			final Calendar calSemester1HolidayStart = Calendar.getInstance(Locale.GERMANY);
			calSemester1HolidayStart.set(Calendar.MONTH, Calendar.MARCH);
			calSemester1HolidayStart.set(Calendar.DAY_OF_MONTH, 1);
			// Semester change in app on 1st of September
			final Calendar calSemester2HolidayStart = Calendar.getInstance(Locale.GERMANY);
			calSemester2HolidayStart.set(Calendar.MONTH, Calendar.SEPTEMBER);
			calSemester2HolidayStart.set(Calendar.DAY_OF_MONTH, 1);
			// today
			final Calendar now = Calendar.getInstance();

			//if lastOpened is before one of the semester change dates and today is after
			if((calLastOpened.before(calSemester1HolidayStart) && now.after(calSemester1HolidayStart))
					|| (calLastOpened.before(calSemester2HolidayStart) && now.after(calSemester2HolidayStart))){

				// show dialog to ask if old timetable should be cleared
				new AlertDialog.Builder(this.getContext())
						.setTitle(R.string.deleteTimetableTitle)
						.setMessage(R.string.deleteTimetableMessage)
						.setPositiveButton(R.string.deleteTimeTableConfirm, new DialogInterface.OnClickListener() {

							public void onClick(final DialogInterface dialog, final int which) {
								MainActivity.clearSubscribedEventSeriesAndUpdateAdapters();
								mView.setLastUpdatedTextView();
							}
						})
						.setNegativeButton(R.string.deleteTimeTableCancel, null)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.show();
			}
		}

		//save date of last opening to shared preferences
		final SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(Define.MySchedule.PREFS_APP_LAST_OPENED, new Date().getTime());
		editor.apply();
	}

	MyScheduleCalendarView mView;
}

