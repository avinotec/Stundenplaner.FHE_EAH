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

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fhe.fhemobile.events.CanteenChangeEvent;
import de.fhe.fhemobile.events.EventDispatcher;
import de.fhe.fhemobile.vos.canteen.CanteenMenuDayVo;
import de.fhe.fhemobile.vos.canteen.CanteenVo;

/**
 * Created by paul on 23.01.14
 * Edited by Nadja - 04/2022
 */
public class CanteenModel extends EventDispatcher {

    private static final String TAG = CanteenModel.class.getSimpleName();

    public Map<String, List<CanteenMenuDayVo>> getMenus() {
        return mMenus;
    }

    public List<CanteenMenuDayVo> getMenu(String canteenId){
        return mMenus.get(canteenId);
    }

    public void addMenu(final @NonNull String canteenId, final List<CanteenMenuDayVo> mMenuDays) {
        if(mMenuDays != null && mMenuDays.size() > 0) {
            this.mMenus.put(canteenId, mMenuDays);
            notifyChange(CanteenChangeEvent.getReceivedCanteenMenuEventWithCanteenId(canteenId));

        } else {
            notifyChange(CanteenChangeEvent.getReceivedEmptyMenuEventWithCanteenId(canteenId));
        }
    }

    public CanteenVo[] getCanteens() {
        return mCanteens;
    }

    public CanteenVo getCanteen(String canteenId){
        for(CanteenVo canteen : mCanteens){
            if(canteen.getCanteenId().equals(canteenId)){
                return canteen;
            }
        }
        return null;
    }

    public void setChoiceItems(final CanteenVo[] mCanteenChoiceItems) {
        this.mCanteens = mCanteenChoiceItems;
        notifyChange(de.fhe.fhemobile.events.CanteenChangeEvent.RECEIVED_CANTEENS);
    }

    public static CanteenModel getInstance() {
        if(ourInstance == null) {
            ourInstance = new CanteenModel();
        }
        return ourInstance;
    }

    private CanteenModel() {
    }

    private void notifyChange(final String type) {
        dispatchEvent(new CanteenChangeEvent(type));
    }

    private static CanteenModel ourInstance;

    private Map<String, List<CanteenMenuDayVo>> mMenus = new HashMap();

    private CanteenVo[] mCanteens;
}
