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
package de.fhe.fhemobile.utils.timetable;

import android.content.Context;
import android.content.SharedPreferences;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.utils.Define;

/**
 *
 */
public final class TimetableSettings {

    public static final String TAG = TimetableSettings.class.getSimpleName();

    /* Utility classes have all fields and methods declared as static.
    Creating private constructors in utility classes prevents them from being accidentally instantiated. */
	private TimetableSettings() {
	}

	/**
     * Saves timetable favourite to Shared Preferences
     * @param _TimetableId
     */
    public static void saveTimetableSelection(final String _TimetableId) {
        final SharedPreferences sp = Main.getAppContext().getSharedPreferences(Define.Timetable.SP_TIMETABLE_SETTINGS, Context.MODE_PRIVATE);
        sp.edit().putString(Define.Timetable.PREF_CHOSEN_TIMETABLE_ID, _TimetableId).apply();
    }

    /**
     * Loads timetable id that was set as favourite (is selected) from Shared Preferences
     * @return the timetable id
     */
    public static String getTimetableSelection() {

        final SharedPreferences sp = Main.getAppContext().getSharedPreferences(Define.Timetable.SP_TIMETABLE_SETTINGS, Context.MODE_PRIVATE);
        String result = sp.getString(Define.Timetable.PREF_CHOSEN_TIMETABLE_ID, null);
        //avoid errors due to usage of old SplusId
        if(result != null && result.startsWith("SPLUS")) {
            saveTimetableSelection(null);
            result = null;
        }

        return result;
    }
}
