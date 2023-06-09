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
package de.fhe.fhemobile.models.canteen;

import static de.fhe.fhemobile.Main.getAppContext;
import static de.fhe.fhemobile.utils.Define.Canteen.SP_KEY_CANTEEN;
import static de.fhe.fhemobile.utils.Define.Canteen.SP_CANTEEN;
import static de.fhe.fhemobile.utils.Define.Canteen.SP_KEY_CANTEEN_CARD;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.canteencardbalance.canteencardreader.CardBalance;
import de.fhe.fhemobile.events.CanteenChangeEvent;
import de.fhe.fhemobile.events.EventDispatcher;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.vos.canteen.CanteenMenuDayVo;
import de.fhe.fhemobile.vos.canteen.CanteenVo;

/**
 * Class to store canteen data und send canteen data related events
 *
 * Created by paul on 23.01.14
 * Edited by Nadja - 04/2022 and 12/2022
 */
public final class CanteenModel extends EventDispatcher {

    private static final String TAG = CanteenModel.class.getSimpleName();

    /* Utility classes have all fields and methods declared as static.
    Creating private constructors in utility classes prevents them from being accidentally instantiated. */
    private CanteenModel() {
    }

    public static CanteenModel getInstance() {
        if(ourInstance == null) {
            ourInstance = new CanteenModel();

            final SharedPreferences sharedPreferences = Main.getAppContext().getSharedPreferences(SP_CANTEEN, Context.MODE_PRIVATE);
            CardBalance balance = new Gson().fromJson(sharedPreferences.getString(SP_KEY_CANTEEN_CARD, ""), CardBalance.class);
            ourInstance.setCanteenCardBalance(balance);

        }
        return ourInstance;
    }

    /**
     * Get menu of the given canteen
     * @param canteenId The canteen
     * @return List of {@link CanteenMenuDayVo}s
     */
    public List<CanteenMenuDayVo> getMenu(final String canteenId){
        return mMenus.get(canteenId);
    }

    /**
     * Set the list of {@link CanteenMenuDayVo}s for the given canteen
     * @param canteenId The id of the canteen
     * @param mMenuDays The {@link CanteenMenuDayVo} objects
     */
    public void setMenu(@NonNull final String canteenId, final List<CanteenMenuDayVo> mMenuDays) {
        if(mMenuDays != null && !mMenuDays.isEmpty()) {
            //save menu to shared preferences
            final Gson gson = new Gson();
            final String json = gson.toJson(mMenuDays);
            final SharedPreferences sharedPreferences = getAppContext().getSharedPreferences(SP_CANTEEN, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SP_KEY_CANTEEN + canteenId, json);
            editor.apply();
        }

        //needed for distinguishing menu days fetched from data loaded from shared preferences
        this.mMenus.put(canteenId, mMenuDays);
        notifyChange(CanteenChangeEvent.getReceivedCanteenMenuEventWithCanteenId(canteenId));
    }

    public CanteenVo[] getCanteens() {
        if(mCanteens == null){
            NetworkHandler.getInstance().fetchAvailableCanteens();
        }
        return mCanteens;
    }

    /**
     * Get canteen with the specified id.
     * Note that, this is null if canteens have not been fetched before.
     *
     * To get the the {@link CanteenVo} of an selected canteen,
     * use {@link de.fhe.fhemobile.utils.UserSettings#getSelectedCanteen(String)}.
     *
     * @param canteenId The id of the canteen
     * @return The {@link CanteenVo} of the canteen with the given id
     */
    public CanteenVo getCanteen(final String canteenId){
        if(mCanteens == null){
            Log.e(TAG, "mCanteens is null");
            return null;
        }

        for(final CanteenVo canteen : mCanteens){
            if (canteen == null)
                continue;
            if (canteen.getCanteenId().isEmpty())
                continue;
            if(canteen.getCanteenId().equals(canteenId)){
                return canteen;
            }
        }
        return null;
    }

    public void setCanteens(final CanteenVo[] mCanteenChoiceItems) {
        this.mCanteens = mCanteenChoiceItems;
        notifyChange(CanteenChangeEvent.RECEIVED_CANTEENS);
    }

    public CardBalance getCanteenCardBalance() {
        return mCanteenCardBalance;
    }

    /**
     * Set canteen card balance in the {@link CanteenModel},
     * notify listeners to update the corresponding text views
     * and save the card balance to shared preferences
     * @param cardBalance The {@link CardBalance} to set
     */
    public void setCanteenCardBalance(CardBalance cardBalance) {
        this.mCanteenCardBalance = cardBalance;
        //notify listeners
        notifyChange(CanteenChangeEvent.RECEIVED_CANTEEN_CARD_BALANCE);

        //store canteen card balance in shared preferences
        final SharedPreferences.Editor editor = Main.getAppContext().getSharedPreferences(SP_CANTEEN, Context.MODE_PRIVATE).edit();
        editor.putString(SP_KEY_CANTEEN_CARD, new Gson().toJson(mCanteenCardBalance));
        editor.apply();
    }

    public void notifyChange(final String type) {
        dispatchEvent(new CanteenChangeEvent(type));
    }



    private static CanteenModel ourInstance;

    private final HashMap<String, List<CanteenMenuDayVo>> mMenus = new HashMap<>();
    private CanteenVo[] mCanteens;

    private CardBalance mCanteenCardBalance;
}
