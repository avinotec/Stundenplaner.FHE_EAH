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

package de.fhe.fhemobile.fragments.mytimetable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.views.mytimetable.MyTimeTableCalendarView;

public class MyTimeTableCalendarFragment extends FeatureFragment {

	private MyTimeTableCalendarView mView;
	private static final String PREFS_LAST_APP_OPENED = "lastAppOpened";

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment MyTimeTableCalendarFragment.
	 */
	public static MyTimeTableCalendarFragment newInstance() {
		MyTimeTableCalendarFragment fragment = new MyTimeTableCalendarFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public MyTimeTableCalendarFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mView = (MyTimeTableCalendarView) inflater.inflate(R.layout.fragment_my_time_table_calendar, container, false);
		mView.initializeView(getActivity().getSupportFragmentManager());

		askForTimeTableDeletionAfterTurnOfSemester();

		//unterhalb der Liste wird immer "Kein Kurs gewählt" angezeigt. Dieser Text ist aber nicht immer sichtbar.
		// Daher ist das Feld in den Fragment Ressourcen vorhanden
		mView.setEmptyCalenderView();

		return mView;
	}

	/**
	 * Zeigt bei Semesterwechsel Dialog zur Nachfrage, ob der Stundenplan gelöscht werden soll
	 */
	private void askForTimeTableDeletionAfterTurnOfSemester() {
		//Wenn die App das letzte Mal vor Semesterferienbeginn geöffnet wurde und das aktuelle Datum nach dem Beginn, soll nachgefragt werden.
		//lastAppOpened muss dabei ungleich 0 sein, gleich 0 bedeutet, die App wurde vorher noch nicht gestartet.
		// in Sekunden seit 1970, Unixtime
		SharedPreferences sharedPreferences = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
		final long lastAppOpened = sharedPreferences.getLong(PREFS_LAST_APP_OPENED, 0);

		Calendar calLastOpened = Calendar.getInstance(new Locale("de", "DE"));
		calLastOpened.setTimeInMillis(lastAppOpened);

		// Löschen des alten Kalenders mitten in den Semesterferien
		// 1. März
		Calendar calSemester1HolidayStart = Calendar.getInstance(new Locale("de", "DE"));
		calSemester1HolidayStart.set(Calendar.MONTH, Calendar.MARCH);
		calSemester1HolidayStart.set(Calendar.DAY_OF_MONTH, 1);
		// 1. September
		Calendar calSemester2HolidayStart = Calendar.getInstance(new Locale("de", "DE"));
		calSemester2HolidayStart.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calSemester2HolidayStart.set(Calendar.DAY_OF_MONTH, 1);

		// wo sind wir heute?
		Calendar calNow = Calendar.getInstance();

		// hat es seit dem letzten
		if (    // wir sind noch nie geöffnet worden, also kein Dialog
				( lastAppOpened != 0 )
				&& (
						// calLastOpened: wann ist die App das letzte Mal gestartet worden
						(calLastOpened.before(calSemester1HolidayStart) && calNow.after(calSemester1HolidayStart))
						|| (calLastOpened.before(calSemester2HolidayStart) && calNow.after(calSemester2HolidayStart))
				)
		) {
			// Benutzer fragen, ob der nun alte Stundenplan gelöscht werden soll.
			new AlertDialog.Builder(this.getContext())
					.setTitle(R.string.deleteTimetableTitle)
					.setMessage(R.string.deleteTimetableMessage)
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//Stundenplan löschen (die Listen leer machen und aus den Preferences entfernen)
							MainActivity.clearSubscribedCoursesAndUpdateAdapters();
							//todo: wozu brauchen wir die coursesOfChosenSemester hier? - Nadja
							//MyTimeTableDialogFragment.coursesOfChosenSemester.clear();
							sharedPreferences.edit()
									.remove(Define.SHARED_PREFERENCES_SUBSCRIBED_COURSES)
									.apply();
						}
					})
					.setNegativeButton(android.R.string.no, null)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.show();
		}
		//Speichere das letzte Datum, wann die App geöffnet wurde, damit wir nur beim Semesterwechsel gefragt werden.
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(PREFS_LAST_APP_OPENED, new Date().getTime());
		editor.apply();
	}

	/**
	 * onViewCreated : hier sind alle Adapter und alle Listen intialisiert. Jetzt können wir auf die aktuelle Woche vorspringen.
	 */
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// es sind alle Views initialisiert, Fragmente sind alle inflated

		mView.jumpToToday();
	}
	
}

