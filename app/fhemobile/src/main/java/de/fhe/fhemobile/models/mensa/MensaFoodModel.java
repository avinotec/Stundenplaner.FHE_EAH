package de.fhe.fhemobile.models.mensa;

import android.util.Log;

import de.fhe.fhemobile.events.EventDispatcher;
import de.fhe.fhemobile.events.SimpleEvent;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.vos.mensa.MensaChoiceItemVo;
import de.fhe.fhemobile.vos.mensa.MensaFoodItemCollectionVo;

/**
 * Created by paul on 23.01.14.
 */
public class MensaFoodModel extends EventDispatcher {

    public class ChangeEvent extends SimpleEvent {
        public static final String RECEIVED_FOOD_DATA       = "receivedFoodData";
        public static final String RECEIVED_EMPTY_FOOD_DATA = "receivedEmptyFoodData";

        public static final String RECEIVED_CHOICE_ITEMS    = "receivedChoiceItems";

        public ChangeEvent(String type) {
            super(type);
        }
    }

    public MensaFoodItemCollectionVo[] getFoodItems() {
        return mFoodItems;
    }

    public void setFoodItems(MensaFoodItemCollectionVo[] mFoodItems) {
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

    public void setChoiceItems(MensaChoiceItemVo[] mChoiceItems) {
        Integer selectedId = Integer.valueOf(UserSettings.getInstance().getChosenMensa());
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

    public void setSelectedItemPosition(Integer mSelectedItemPosition) {
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

    private void notifyChange(String type) {
        dispatchEvent(new ChangeEvent(type));
    }

    private static final String LOG_TAG = MensaFoodModel.class.getSimpleName();

    private static MensaFoodModel       ourInstance             = null;

    private MensaFoodItemCollectionVo[] mFoodItems              = null;
    private MensaChoiceItemVo[]         mChoiceItems            = null;

    private Integer                     mSelectedItemPosition   = 0;
    private String                      mSelectedItemName       = "Mensa";
}
