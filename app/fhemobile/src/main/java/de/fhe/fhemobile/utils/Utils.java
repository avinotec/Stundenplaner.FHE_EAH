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
import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import org.junit.Assert;

import de.fhe.fhemobile.BuildConfig;
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

    //getResourceId---------------------------------------------------------------------------------

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
        Toast.makeText(_Context, _ResId, Toast.LENGTH_SHORT).show();
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

    public static void showToastFromBackgroundTask(final String _Text){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Utils.showToast( _Text);
            }
        });
    }

    public static void showToastFromBackgroundTask(@StringRes final int _ResId){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Utils.showToast( _ResId);
            }
        });
    }

    /**
     * Show toast for 3.5s instead of only 2s
     * @param _ResId
     */
    public static void showToastLong(@StringRes final int _ResId){
        Toast.makeText(Main.getAppContext(), _ResId, Toast.LENGTH_LONG).show();
    }


	// Der Stundenplan liefert Zeitangaben in Sekunden seit 1970. Das wird auch "Epoch (Unix) Time" genannt.
	// Die EAH API liefert den Zeitstempel für eine Vorlesung in dieser Sekundenzahl aus.
    // ABER: es ist gar nicht die Zeit in UTC, sondern in lokaler Zeit.
    // Er sendet zwar die Sekunden, die ausgerechnete Uhrzeit z.B. 11:30 Uhr, ist aber die deutsche,
    // lokale Zeit und NICHT UTC. Das heißt, Carsten ignoriert die Zeitzone und sendet immer die
    // lokale, örtliche Uhrzeit.
	// 1665487800 bspw. entspricht in UTC: Dienstag, 11. Oktober 2022, 11:30:00
	// 1665484200 entspricht in UTC: Dienstag, 11. Oktober 2022, 10:30:00
	//
	// Die Uhrzeit an sich ist ja richtig, aber die Zeitzone sollte UTC sein. Ist es aber doch nicht.
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
	//
	// Das alles hat nicht funktioniert.
	// Erkenntnis 1:
	// Das Date Objekt covert nur den Zeitstempel. Mehr nicht. Keine Interpretation, keine Zeitzone.
	// Erkenntnis 2:
    // Das Durcheinander fängt mit dem SimpleDateFormatter an. Hier kommen erst die Zeitzonen und
    // Sommer- und Winterzeit ins Spiel.
    // erst "sdf.setTimeZone( TimeZone.getTimeZone("UTC") );" das Festlegen der Zeitzone auf UTC
    // überzeugt die Methode, die Zeit in UTC auszugeben.
    // Das stimmt zwar dann immer noch nicht, aber damit haben wir Carstens Uhrzeit als UTC entgegen
    // genommen und unverändert als UTC ausgegeben. Also falsch (da ja lokale Zeit),
    // aber konsequent (als UTC behandelt), und damit dann doch wieder richtig.
    //
	// Danke Java, ich liebe Dich.



}
