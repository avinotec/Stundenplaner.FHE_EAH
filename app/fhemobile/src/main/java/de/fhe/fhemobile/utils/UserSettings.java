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

import static de.fhe.fhemobile.utils.Define.MyTimeTable.PREF_SUBSCRIBED_COURSES;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.models.canteen.CanteenModel;
import de.fhe.fhemobile.models.news.NewsModel;
import de.fhe.fhemobile.models.settings.UserDefaults;
import de.fhe.fhemobile.vos.canteen.CanteenVo;

/**
 * Created by paul on 22.01.14
 * Edited by Nadja - 04/2022
 */
public class UserSettings {
    private static final UserSettings ourInstance = new UserSettings();

    private static final String TAG = UserSettings.class.getSimpleName();

    private static final String PREF_CHOSEN_CANTEENS        = "chosenCanteens";        // $NON-NLS
    private static final String PREF_CHOSEN_NEWS_CATEGORY   = "chosenNewsCategory"; // $NON-NLS

    private final SharedPreferences   mSP;

    private ArrayList<CanteenVo> mChosenCanteens = new ArrayList<>();
    private String mChosenNewsCategory = null;

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

        //Canteen
        final String json = mSP.getString(PREF_CHOSEN_CANTEENS,"");
        //skip if json is empty
        if ( !"".equals(json) && !"null".equals(json)) {
            final Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<CanteenVo>>(){}.getType();
            mChosenCanteens = gson.fromJson(json, listType);
        }

        //News
        mChosenNewsCategory = mSP.getString(PREF_CHOSEN_NEWS_CATEGORY, UserDefaults.DEFAULT_NEWS_ID);
    }

    public ArrayList<CanteenVo> getChosenCanteens() {
        return mChosenCanteens;
    }

    public ArrayList<Integer> getListOfChosenCanteenIdsAsInt(){

        ArrayList<Integer> ids = new ArrayList<>();
        for(CanteenVo canteen : mChosenCanteens){
            ids.add(Integer.valueOf(canteen.getCanteenId()));
        }
        return ids;
    }

    public ArrayList<String> getListOfChosenCanteenIdsAsString(){

        ArrayList<String> ids = new ArrayList<>();
        for(CanteenVo canteen : mChosenCanteens){
            ids.add(canteen.getCanteenId());
        }
        return ids;
    }

    public void setChosenCanteens(final ArrayList<CanteenVo> _ChosenCanteens) {

        mChosenCanteens = _ChosenCanteens;

        final Gson gson = new Gson();
        final String json = gson.toJson(mChosenCanteens, ArrayList.class);
        mSP.edit().putString(PREF_SUBSCRIBED_COURSES, json).apply();

        //todo: warum???
        //CanteenModel.getInstance().addMenu(null);
    }

    public void addChosenCanteens(final CanteenVo _ChosenCanteen){
        mChosenCanteens.add(_ChosenCanteen);

        final Gson gson = new Gson();
        final String json = gson.toJson(mChosenCanteens, ArrayList.class);
        mSP.edit().putString(PREF_SUBSCRIBED_COURSES, json).apply();
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
