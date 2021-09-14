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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.models.mensa.MensaFoodModel;
import de.fhe.fhemobile.models.news.NewsModel;
import de.fhe.fhemobile.models.settings.UserDefaults;

/**
 * Created by paul on 22.01.14.
 */
public class UserSettings {
    private static final UserSettings ourInstance = new UserSettings();

    private static final String LOG_TAG = "UserSettings";

    private static final String PREF_CHOSEN_MENSA           = "chosenMensa";
    private static final String PREF_CHOSEN_MENSA_NAME      = "chosenMensaName";
    private static final String PREF_CHOSEN_NEWS_CATEGORY   = "chosenNewsCategory";

    private final SharedPreferences   mSP;

    private String              mChosenMensa        = null;
    private String              mChosenMensaName    = null;
    private String              mChosenNewsCategory = null;

    /**
     *
     * @return
     */
    public static UserSettings getInstance() {
        return ourInstance;
    }

    /**
     *  constructor
     */
    private UserSettings() {
        mSP = PreferenceManager.getDefaultSharedPreferences(Main.getAppContext());

        mChosenMensa        = mSP.getString(PREF_CHOSEN_MENSA, UserDefaults.DEFAULT_MENSA_ID);
        mChosenMensaName    = mSP.getString(PREF_CHOSEN_MENSA_NAME, UserDefaults.DEFAULT_MENSA_NAME);
        mChosenNewsCategory = mSP.getString(PREF_CHOSEN_NEWS_CATEGORY, UserDefaults.DEFAULT_NEWS_ID);
    }

    /**
     * Mensa
     * @return
     */
    public String getChosenMensa() {
        return mChosenMensa;
    }

    /**
     * Mensa
     * @param _ChosenMensaId
     * @param _ChosenMensaName
     */
    public void setChosenMensa(final String _ChosenMensaId, final String _ChosenMensaName ) {
        mChosenMensa = _ChosenMensaId;
        mSP.edit().putString(PREF_CHOSEN_MENSA, mChosenMensa).apply();

        mChosenMensaName = _ChosenMensaName;
        mSP.edit().putString(PREF_CHOSEN_MENSA_NAME, mChosenMensaName).apply();

        MensaFoodModel.getInstance().setFoodItems(null);
    }

    /**
     * Mensa
     * @return
     */
    public String getChosenMensaName() {
        return mChosenMensaName;
    }

    /**
     *  News
     * @return
     */
    public String getChosenNewsCategory() {
        return mChosenNewsCategory;
    }

    /**
     * News
     * @param _ChosenNewsCategory
     */
    public void setChosenNewsCategory(final String _ChosenNewsCategory) {
        mChosenNewsCategory = _ChosenNewsCategory;
        mSP.edit().putString(PREF_CHOSEN_NEWS_CATEGORY, this.mChosenNewsCategory).apply();
        NewsModel.getInstance().setNewsItems(null);
    }
}
