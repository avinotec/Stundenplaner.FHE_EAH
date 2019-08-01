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
        sp.edit().putString(PREF_CHOSEN_TIMETABLE_ID, _TimeTableId).commit();
    }

    public static String fetchTimeTableSelection() {
        String result;

        SharedPreferences sp = Main.getAppContext().getSharedPreferences(SP_DATABASE, Context.MODE_PRIVATE);
        result = sp.getString(PREF_CHOSEN_TIMETABLE_ID, null);

        return result;
    }
}
