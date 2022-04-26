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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fhe.fhemobile.events.EventDispatcher;
import de.fhe.fhemobile.events.SimpleEvent;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.vos.canteen.CanteenMenuDayVo;
import de.fhe.fhemobile.vos.canteen.CanteenVo;

/**
 * Created by paul on 23.01.14
 * Edited by Nadja - 04/2022
 */
public class CanteenModel extends EventDispatcher {

    private static final String TAG = CanteenModel.class.getSimpleName();

    public static class ChangeEvent extends SimpleEvent {
        public static final String RECEIVED_FOOD_DATA       = "receivedFoodData";
        public static final String RECEIVED_EMPTY_FOOD_DATA = "receivedEmptyFoodData";

        public static final String RECEIVED_CHOICE_ITEMS    = "receivedChoiceItems";

        public ChangeEvent(final String type) {
            super(type);
        }

    }

    public Map<String, List<CanteenMenuDayVo>> getMenus() {
        return mMenus;
    }

    public void addMenu(final String canteenId, final List<CanteenMenuDayVo> mMenuDays) {
        if(canteenId != null && mMenuDays != null && mMenuDays.size() > 0) {
            this.mMenus.put(canteenId, mMenuDays);
            notifyChange(ChangeEvent.RECEIVED_FOOD_DATA);
        }
        else {
            notifyChange(ChangeEvent.RECEIVED_EMPTY_FOOD_DATA);
        }
    }

    public CanteenVo[] getChoiceItems() {
        return mCanteenChoiceItems;
    }

    public void setChoiceItems(final CanteenVo[] mCanteenChoiceItems) {
        final ArrayList<Integer> selectedIds = UserSettings.getInstance().getListOfChosenCanteenIdsAsInt();
        for(int i = 0; i <= mCanteenChoiceItems.length; i++) {
            for(Integer id : selectedIds){
                if(id.equals(mCanteenChoiceItems[i])){
                    mSelectedItemPositions.add(i);
                }
            }
        }

        this.mCanteenChoiceItems = mCanteenChoiceItems;
        notifyChange(ChangeEvent.RECEIVED_CHOICE_ITEMS);
    }

    public ArrayList<Integer> getSelectedItemPositions() {
        return mSelectedItemPositions;
    }

    public void addSelectedItemPosition(final int mSelectedItemPosition) {
        mSelectedItemPositions.add(mSelectedItemPosition);
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
        dispatchEvent(new ChangeEvent(type));
    }

    private static CanteenModel ourInstance;

    private Map<String, List<CanteenMenuDayVo>> mMenus = new HashMap();

    private CanteenVo[] mCanteenChoiceItems;
    private ArrayList<Integer> mSelectedItemPositions = new ArrayList();
}
