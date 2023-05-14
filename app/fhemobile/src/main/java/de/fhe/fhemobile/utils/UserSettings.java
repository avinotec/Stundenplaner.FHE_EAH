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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Assert;

import java.lang.reflect.Type;
import java.util.ArrayList;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.models.canteen.CanteenModel;
import de.fhe.fhemobile.models.news.NewsModel;
import de.fhe.fhemobile.models.settings.UserDefaults;
import de.fhe.fhemobile.vos.canteen.CanteenVo;

/**
 * Created by paul on 22.01.14
 * Edited by Nadja - 04/2022
 */
public final class UserSettings {
    private static final UserSettings ourInstance = new UserSettings();

    private static final String TAG = UserSettings.class.getSimpleName();

    private final SharedPreferences   mSP;

    private final ArrayList<CanteenVo> mSelectedCanteens ; //redundant = new ArrayList<>();
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
        String json = mSP.getString(Define.Canteen.PREF_SELECTED_CANTEENS, "");
        Log.d(TAG, "json loaded: " + json);

        //if json is empty, set default
        if (json.isEmpty() || "null".equals(json) || "[]".equals(json)) {
            json = UserDefaults.DEFAULT_CANTEEN_SELECTION;
        }
        //set canteen selection
        final Gson gson = new Gson();
        final Type listType = new TypeToken<ArrayList<CanteenVo>>(){}.getType();
        mSelectedCanteens = gson.fromJson(json, listType);
        Log.d(TAG, "Loaded selected canteens " + mSelectedCanteens);


        //News
        mChosenNewsCategory = mSP.getString(Define.News.PREF_CHOSEN_NEWS_CATEGORY, UserDefaults.DEFAULT_NEWS_ID);
    }


    public ArrayList<String> getSelectedCanteenIds(){

        if(BuildConfig.DEBUG) Assert.assertNotNull(mSelectedCanteens);

        final ArrayList<String> ids = new ArrayList<>();
        for(final CanteenVo canteen : mSelectedCanteens){
            if(canteen == null) {
                if(BuildConfig.DEBUG) Assert.assertNotNull(canteen);
                Log.e(TAG, "CanteenVo in selectedCanteens is null");
                continue;
            }
            ids.add(canteen.getCanteenId());
        }
        return ids;
    }

    /**
     *
     * @param canteenId
     * @return null if not found, or CanteenVo if found
     */
    public CanteenVo getSelectedCanteen(final String canteenId){
        if(BuildConfig.DEBUG) Assert.assertNotNull(mSelectedCanteens);

        for(final CanteenVo canteenVo : mSelectedCanteens){
            if(canteenVo.getCanteenId().equals(canteenId)){
                return canteenVo;
            }
        }
        Log.e(TAG, "No canteen with id " + canteenId + " found in selectedCanteens");
        return null;
    }


    /**
     * Add the {@link CanteenVo} with the corresponding id if it is not contained in selectedCanteens yet.
     * Remove it from selectedCanteen if its contained.
     * @param canteenId
     */
    public void addOrRemoveFromSelectedCanteens(final String canteenId){

        boolean canteenFound = false;

        for(final CanteenVo canteenVo : mSelectedCanteens){
            if(canteenVo.getCanteenId().equals(canteenId)){
                mSelectedCanteens.remove(canteenVo);
                canteenFound = true;
                break;
            }
        }

        if(!canteenFound) {
            final CanteenVo canteenToAdd = CanteenModel.getInstance().getCanteen(canteenId);
            if(canteenToAdd != null){
                mSelectedCanteens.add(canteenToAdd);
            } else {
                Log.e(TAG, "Canteen " + canteenId + " is null / not found in CanteenModel");
            }
        }

        final Gson gson = new Gson();
        final String json = gson.toJson(mSelectedCanteens, ArrayList.class);
        mSP.edit().putString(Define.Canteen.PREF_SELECTED_CANTEENS, json).apply();

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
        mSP.edit().putString(Define.News.PREF_CHOSEN_NEWS_CATEGORY, this.mChosenNewsCategory).apply();
        NewsModel.getInstance().setNewsItems(null);
    }
}
