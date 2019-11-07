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
package de.fhe.fhemobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

import de.fhe.fhemobile.Main;

/**
 * Created by paul on 16.03.15.
 */
public class TimeTableSettings {

    private static final String SP_DATABASE = "spTimeTable";

    private static final String PREF_CHOSEN_TIMETABLE_ID = "prefChosenTimeTableId";

    public static void saveTimeTableSelection(String _TimeTableId) {
        SharedPreferences sp = Main.getAppContext().getSharedPreferences(SP_DATABASE, Context.MODE_PRIVATE);
        sp.edit().putString(PREF_CHOSEN_TIMETABLE_ID, _TimeTableId).apply();
    }

    public static String fetchTimeTableSelection() {
        String result;

        SharedPreferences sp = Main.getAppContext().getSharedPreferences(SP_DATABASE, Context.MODE_PRIVATE);
        result = sp.getString(PREF_CHOSEN_TIMETABLE_ID, null);

        return result;
    }
}
