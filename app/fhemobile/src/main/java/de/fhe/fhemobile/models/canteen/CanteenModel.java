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

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fhe.fhemobile.events.CanteenChangeEvent;
import de.fhe.fhemobile.events.EventDispatcher;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.vos.canteen.CanteenMenuDayVo;
import de.fhe.fhemobile.vos.canteen.CanteenVo;

/**
 * Created by paul on 23.01.14
 * Edited by Nadja - 04/2022
 */
public final class CanteenModel extends EventDispatcher {

    private static final String TAG = CanteenModel.class.getSimpleName();

    public Map<String, List<CanteenMenuDayVo>> getMenus() {
        return mMenus;
    }

    public List<CanteenMenuDayVo> getMenu(final String canteenId){
        return mMenus.get(canteenId);
    }

    public void addMenu(@NonNull final String canteenId, final List<CanteenMenuDayVo> mMenuDays) {
        if(mMenuDays != null && !mMenuDays.isEmpty()) {
            this.mMenus.put(canteenId, mMenuDays);
            notifyChange(CanteenChangeEvent.getReceivedCanteenMenuEventWithCanteenId(canteenId));

        } else {
            notifyChange(CanteenChangeEvent.getReceivedEmptyMenuEventWithCanteenId(canteenId));
        }
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
        notifyChange(de.fhe.fhemobile.events.CanteenChangeEvent.RECEIVED_CANTEENS);
    }

    public static CanteenModel getInstance() {
        if(ourInstance == null) {
            ourInstance = new CanteenModel();
        }
        return ourInstance;
    }

    /* Utility classes have all fields and methods declared as static.
    Creating private constructors in utility classes prevents them from being accidentally instantiated. */
    private CanteenModel() {
    }

    private void notifyChange(final String type) {
        dispatchEvent(new CanteenChangeEvent(type));
    }

    private static CanteenModel ourInstance;

    private final HashMap<String, List<CanteenMenuDayVo>> mMenus = new HashMap();

    private CanteenVo[] mCanteens;
}
