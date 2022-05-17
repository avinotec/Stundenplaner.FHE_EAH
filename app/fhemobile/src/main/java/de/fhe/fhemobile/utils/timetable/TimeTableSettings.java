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
public final class TimeTableSettings {

    public static final String TAG = TimeTableSettings.class.getSimpleName();

    /**
     * Saves timetable favourite to Shared Preferences
     * @param _TimeTableId
     */
    public static void saveTimeTableSelection(final String _TimeTableId) {
        final SharedPreferences sp = Main.getAppContext().getSharedPreferences(Define.TimeTable.SP_TIMETABLE_SETTINGS, Context.MODE_PRIVATE);
        sp.edit().putString(Define.TimeTable.PREF_CHOSEN_TIMETABLE_ID, _TimeTableId).apply();
    }

    /**
     * Loads timetable id that was set as favourite (is selected) from Shared Preferences
     * @return the timetable id
     */
    public static String getTimeTableSelection() {

        final SharedPreferences sp = Main.getAppContext().getSharedPreferences(Define.TimeTable.SP_TIMETABLE_SETTINGS, Context.MODE_PRIVATE);
        String result = sp.getString(Define.TimeTable.PREF_CHOSEN_TIMETABLE_ID, null);
        //avoid errors due to usage of old SplusId
        if(result != null && result.startsWith("SPLUS")) {
            saveTimeTableSelection(null);
            result = null;
        }

        return result;
    }
}
