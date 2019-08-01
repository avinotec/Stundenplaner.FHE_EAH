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
    private static UserSettings ourInstance = new UserSettings();

    private static final String LOG_TAG = "UserSettings";

    private static final String PREF_CHOSEN_MENSA           = "chosenMensa";
    private static final String PREF_CHOSEN_MENSA_NAME      = "chosenMensaName";
    private static final String PREF_CHOSEN_NEWS_CATEGORY   = "chosenNewsCategory";

    private SharedPreferences   mSP;

    private String              mChosenMensa        = null;
    private String              mChosenMensaName    = null;
    private String              mChosenNewsCategory = null;

    public static UserSettings getInstance() {
        return ourInstance;
    }

    private UserSettings() {
        mSP = PreferenceManager.getDefaultSharedPreferences(Main.getAppContext());

        mChosenMensa        = mSP.getString(PREF_CHOSEN_MENSA, UserDefaults.DEFAULT_MENSA_ID);
        mChosenMensaName    = mSP.getString(PREF_CHOSEN_MENSA_NAME, UserDefaults.DEFAULT_MENSA_NAME);
        mChosenNewsCategory = mSP.getString(PREF_CHOSEN_NEWS_CATEGORY, UserDefaults.DEFAULT_NEWS_ID);
    }

    public String getChosenMensa() {
        return mChosenMensa;
    }

    public void setChosenMensa(String _ChosenMensaId, String _ChosenMensaName ) {
        mChosenMensa = _ChosenMensaId;
        mSP.edit().putString(PREF_CHOSEN_MENSA, mChosenMensa).apply();

        mChosenMensaName = _ChosenMensaName;
        mSP.edit().putString(PREF_CHOSEN_MENSA_NAME, mChosenMensaName).apply();

        MensaFoodModel.getInstance().setFoodItems(null);
    }

    public String getChosenMensaName() { return mChosenMensaName; }


    public String getChosenNewsCategory() {
        return mChosenNewsCategory;
    }

    public void setChosenNewsCategory(String _ChosenNewsCategory) {
        mChosenNewsCategory = _ChosenNewsCategory;
        mSP.edit().putString(PREF_CHOSEN_NEWS_CATEGORY, this.mChosenNewsCategory).apply();
        NewsModel.getInstance().setNewsItems(null);
    }
}
