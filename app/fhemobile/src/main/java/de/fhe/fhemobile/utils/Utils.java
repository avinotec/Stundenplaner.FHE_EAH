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
package de.fhe.fhemobile.utils;

import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.BuildConfig;

import org.junit.Assert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.fhe.fhemobile.Main;

/**
 * Created by paul on 04.03.15.
 */
public final class Utils {

    public static final String TAG = Utils.class.getSimpleName();

	/* Utility classes have all fields and methods declared as static.
	Creating private constructors in utility classes prevents them from being accidentally instantiated. */
	private Utils() {
	}

	//correctUmlauts--------------------------------------------------------------------------------

    /**
     * Replaces incorrect german umlauts in the given string
     * @param str string
     * @return corrected string
     */
    public static String correctUmlauts(String str){
        //method added by Nadja - 05.01.2021

        if ( BuildConfig.DEBUG ) Assert.assertNotNull(str);

        // falls der übergebene String tatsächlich null sein sollte :-(
        if ( str != null ) {

            str = str.replaceAll("Ã„", "Ä");
            str = str.replaceAll("Ã\u009C", "Ü");
            str = str.replaceAll("Ã–", "Ö");
            str = str.replaceAll("Ã\u009F", "ß");
            str = str.replaceAll("Ã¼", "ü");
            str = str.replaceAll("Ã¶", "ö");
            str = str.replaceAll("Ã¤", "ä");

        }
        return str;
    }


    //getStringResource-----------------------------------------------------------------------------

    /**
     * Retrieves a resource string.
     *
     * @param _variableName Resource name of the wanted string
     * @return String of the Resource value
     */
    public static String getStringResource(final String _variableName) {
        if (_variableName == null || _variableName.isEmpty()) {
            return _variableName;
        }

        try {
            return Main.getAppContext().getString(getResourceId(_variableName, "string"));
        } catch (final RuntimeException e) {
            Log.e( TAG, "Error in fetching ressource", e);

            //fallback if not found
            return _variableName;
        }
    }

    //getResourceId---------------------------------------------------------------------------------

    /**
     * Returns the resource ID based on resource name.
     *
     * @param _variableName Resource name identifier
     * @param _resourcename Resource type
     * @return Resource ID
     */
    public static int getResourceId(final String _variableName, final String _resourcename) {
        try {
            return Main.getAppContext().getResources().getIdentifier(_variableName, _resourcename, Main.getAppContext().getPackageName());
        } catch (final RuntimeException e) {
            Log.e(TAG, "Fehler bei der Ressourcensuche",e);
            return -1;
        }
    }

    /**
     * hides the current soft keyboard, if it is open.
     */
    public static void hideKeyboard(final AppCompatActivity _activity) {
        final InputMethodManager inputManager = (InputMethodManager) _activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (_activity.getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(_activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            inputManager.hideSoftInputFromWindow(null, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     *
     * @param _ResId
     */
    public static void showToast(@StringRes final int _ResId) {
        showToast(Main.getAppContext(), _ResId);
    }

    /**
     *
     * @param _Context
     * @param _ResId
     */
    public static void showToast(final Context _Context, @StringRes final int _ResId) {
        Toast.makeText(_Context, _ResId,
                Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * @param _Text
     */
    public static void showToast(final String _Text) {
        showToast(Main.getAppContext(), _Text);
    }

    /**
     *
     * @param _Context
     * @param _Text
     */
    public static void showToast(final Context _Context, final String _Text) {
        Toast.makeText(_Context, _Text, Toast.LENGTH_SHORT).show();
    }


	// Der Stundenplan liefert Zeitangaben in Sekunden seit 1970. Das wird auch "Epoch" genannt.
	// Carsten Hölbing liefert den Zeitstempel für eine Vorlesung in diesen Sekunden aus.
	// 1665487800 bspw. ist "Dies entspricht in UTC: Dienstag, 11. Oktober 2022, 11:30:00"
	// 1665484200 "Dies entspricht in UTC: Dienstag, 11. Oktober 2022, 10:30:00"
	//
	// Die Uhrzeit an sich ist ja richtig, aber die Zeitzone ist UTC.
	// Jetzt kommt Java ins Spiel.
	// Java will immer korrekt mit Zeitzonen spielen.
	// Das heißt, die Vorlesung beginnt in Deutschland um 11:30 Uhr.
	// Wenn ich die Zeit in UTC (für Java) erstelle, dann muss die Zeit in England 10:30 Uhr sein,
	// damit aus UTC10:30 dann in MEZ11:30 Uhr daraus wird.
	// Daher ziehen wir dann mal eine Stunde ab, damit die anschließende Interpretation durch Java
	// wieder die richtige "deutsche" Uhrzeit anzeigt.
	// Sommerzeit/Winterzeit:
	// Java ist so pfiffig und berücksichtigt auch noch die Sommerzeit/Winterzeit Umstellung
	// Das heißt, selbst heute im Oktober wird in Java eine Zeitstempel im November korrekt mit einer
	// anderen Zeitumstellung zurückgegeben.
	// Das heißt, bei jedem Zeitstempel müssen wir wohl auch noch schauen, ob Java die Sommer/Winterzeit
	// berücksichtigt.
	// Dann muss aus dem ursprünglichen Zeitstempel 11:30 je nachdem 10:30 (Winterzeit) oder 9:30 (Sommerzeit)
	// werden.
	// Danke Java, ich liebe Dich.

	/**
	 * Convert the time in seconds since 1970 from Carsten Hölbings Stundenplan Server
	 * @param lStartOrEndDateTimeParam in seconds since 1970
	 * @return Date object corrected with Timezone, UTC, Daylightsavings
	 */
	static public Date convertTimeFromStundenplanWebserverDate(long lStartOrEndDateTimeParam ) {

		// convert to milliseconds
		final long lStartOrEndDateTime = lStartOrEndDateTimeParam * 1000 ;

		final Date dateGetStartTime = new Date(lStartOrEndDateTime);

		return dateGetStartTime;
	}

	/**
	 * Convert the time in seconds since 1970 from Carsten Hölbings Stundenplan Server
	 * @param lStartOrEndDateTimeParam in seconds since 1970
	 * @return String corrected with Timezone, UTC, Daylightsavings
	 */
	@NonNull
	static public String convertTimeFromStundenplanWebserverStr(long lStartOrEndDateTimeParam ) {

		// convert to milliseconds
		final long lStartOrEndDateTime = lStartOrEndDateTimeParam * 1000 ;

		final Date dateGetStartTime = new Date(lStartOrEndDateTime);

		final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ROOT);
		// this is the magic thing to advise the SimpleDateFormat to do nothing with the Timezones
		sdf.setTimeZone( TimeZone.getTimeZone("UTC") );

		final String strGetStartTime = sdf.format( dateGetStartTime );
		return strGetStartTime;
	}


}
