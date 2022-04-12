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

import java.util.ArrayList;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.models.canteen.CanteenFoodModel;
import de.fhe.fhemobile.models.news.NewsModel;
import de.fhe.fhemobile.models.settings.UserDefaults;

/**
 * Created by paul on 22.01.14.
 */
public class UserSettings {
    private static final UserSettings ourInstance = new UserSettings();

    private static final String LOG_TAG = "UserSettings";

    private static final String PREF_CHOSEN_CANTEEN = "chosenCanteen";        // $NON-NLS
    private static final String PREF_CHOSEN_CANTEEN_NAME = "chosenCanteenName";    // $NON-NLS
    private static final String PREF_CHOSEN_NEWS_CATEGORY   = "chosenNewsCategory"; // $NON-NLS

    private final SharedPreferences   mSP;

    private String mChosenCanteenId = null;
    private ArrayList<String> mChosenCanteens = new ArrayList<>();
    private String mChosenCanteenName = null;
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

        mChosenCanteenId = mSP.getString(PREF_CHOSEN_CANTEEN, UserDefaults.DEFAULT_CANTEEN_ID);
        mChosenCanteenName = mSP.getString(PREF_CHOSEN_CANTEEN_NAME, UserDefaults.DEFAULT_CANTEEN_NAME);
        mChosenNewsCategory = mSP.getString(PREF_CHOSEN_NEWS_CATEGORY, UserDefaults.DEFAULT_NEWS_ID);
    }

    /**
     * Canteen
     * @return
     */
    public String getChosenCanteenId() {
        return mChosenCanteenId;
    }

    /**
     * Canteen
     * @param _ChosenCanteenId
     * @param _ChosenCanteenName
     */
    public void setChosenCanteen(final String _ChosenCanteenId, final String _ChosenCanteenName) {
        mChosenCanteenId = _ChosenCanteenId;
        mSP.edit().putString(PREF_CHOSEN_CANTEEN, mChosenCanteenId).apply();

        mChosenCanteenName = _ChosenCanteenName;
        mSP.edit().putString(PREF_CHOSEN_CANTEEN_NAME, mChosenCanteenName).apply();

        CanteenFoodModel.getInstance().setFoodItems(null);
    }

    /**
     * Canteen
     * @return
     */
    public String getChosenCanteenName() {
        return mChosenCanteenName;
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
