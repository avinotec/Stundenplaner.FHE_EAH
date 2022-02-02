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
package de.fhe.fhemobile.models.mensa;

import de.fhe.fhemobile.events.EventDispatcher;
import de.fhe.fhemobile.events.SimpleEvent;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.vos.mensa.MensaChoiceItemVo;
import de.fhe.fhemobile.vos.mensa.MensaFoodItemCollectionVo;

/**
 * Created by paul on 23.01.14.
 */
public class MensaFoodModel extends EventDispatcher {

    public static class ChangeEvent extends SimpleEvent {
        public static final String RECEIVED_FOOD_DATA       = "receivedFoodData";
        public static final String RECEIVED_EMPTY_FOOD_DATA = "receivedEmptyFoodData";

        public static final String RECEIVED_CHOICE_ITEMS    = "receivedChoiceItems";

        public ChangeEvent(final String type) {
            super(type);
        }
    }

    public MensaFoodItemCollectionVo[] getFoodItems() {
        return mFoodItems;
    }

    public void setFoodItems(final MensaFoodItemCollectionVo[] mFoodItems) {
        this.mFoodItems = mFoodItems;
        if(this.mFoodItems != null && this.mFoodItems.length > 0) {
            notifyChange(ChangeEvent.RECEIVED_FOOD_DATA);
        }
        else {
            notifyChange(ChangeEvent.RECEIVED_EMPTY_FOOD_DATA);
        }
    }

    public MensaChoiceItemVo[] getChoiceItems() {
        return mChoiceItems;
    }

    public void setChoiceItems(final MensaChoiceItemVo[] mChoiceItems) {
        final Integer selectedId = Integer.valueOf(UserSettings.getInstance().getChosenMensa());
        for(int i = 0; i < mChoiceItems.length; i++) {
            if(selectedId.equals(mChoiceItems[i].getId())) {
                mSelectedItemPosition = i;
                break;
            }
        }

        this.mChoiceItems = mChoiceItems;
        notifyChange(ChangeEvent.RECEIVED_CHOICE_ITEMS);
    }

    public Integer getSelectedItemPosition() {
        return mSelectedItemPosition;
    }

    public void setSelectedItemPosition(final Integer mSelectedItemPosition) {
        this.mSelectedItemPosition = mSelectedItemPosition;
    }

    public static MensaFoodModel getInstance() {
        if(ourInstance == null) {
            ourInstance = new MensaFoodModel();
        }
        return ourInstance;
    }

    private MensaFoodModel() {
    }

    private void notifyChange(final String type) {
        dispatchEvent(new ChangeEvent(type));
    }

    private static final String LOG_TAG = MensaFoodModel.class.getSimpleName();

    private static MensaFoodModel       ourInstance;

    private MensaFoodItemCollectionVo[] mFoodItems;
    private MensaChoiceItemVo[]         mChoiceItems;

    private Integer                     mSelectedItemPosition   = 0;
    private final String                      mSelectedItemName       = "Mensa";
}
