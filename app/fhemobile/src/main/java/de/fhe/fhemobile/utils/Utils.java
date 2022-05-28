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
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.BuildConfig;

import org.junit.Assert;

import de.fhe.fhemobile.Main;

/**
 * Created by paul on 04.03.15.
 */
public final class Utils {

    public static final String TAG = Utils.class.getSimpleName();

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



    
}
