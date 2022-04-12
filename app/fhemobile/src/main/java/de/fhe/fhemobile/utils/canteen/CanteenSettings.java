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
package de.fhe.fhemobile.utils.canteen;

import android.content.Context;
import android.content.SharedPreferences;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.utils.Define;

public class CanteenSettings {

    public static String TAG = "CanteenSettings";

    /**
     * Saves chosen canteen to Shared Preferences
     * @param _CanteenId the id of the chosen canteen
     */
    public static void saveCanteenChoice(final String _CanteenId) {
        final SharedPreferences sp = Main.getAppContext().getSharedPreferences(Define.Canteen.SP_CANTEEN_SETTINGS, Context.MODE_PRIVATE);
        sp.edit().putString(Define.Canteen.PREF_CHOSEN_CANTEEN_ID, _CanteenId).apply();
    }

    /**
     * Loads the id of the canteen chosen by the user from Shared Preferences
     * @return the canteen id
     */
    public static String getCanteenChoice(){
        final SharedPreferences sp = Main.getAppContext().getSharedPreferences(Define.Canteen.SP_CANTEEN_SETTINGS, Context.MODE_PRIVATE);
        final String result = sp.getString(Define.Canteen.PREF_CHOSEN_CANTEEN_ID, null);

        return result;
    }
}
