package de.fhe.fhemobile.utils;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.vos.mensa.MensaFoodItemCollectionVo;
import de.fhe.fhemobile.vos.mensa.MensaFoodItemVo;

/**
 * Created by paul on 20.03.15.
 */
public class MensaUtils {

    /**
     *
     * @param _Items
     * @return
     */
    public static List<MensaFoodItemCollectionVo> orderMensaItems(MensaFoodItemVo[] _Items) {
        List<MensaFoodItemCollectionVo> result = new ArrayList<MensaFoodItemCollectionVo>();

        long lastDate = _Items[0].getDate();
        String lastDateString = _Items[0].getDateString();
        List<MensaFoodItemVo> tempItems = new ArrayList<MensaFoodItemVo>();

        for (MensaFoodItemVo item : _Items) {
            long currentDate = item.getDate();
            if (currentDate == lastDate) {
                tempItems.add(item);
            } else {
                result.add(new MensaFoodItemCollectionVo(tempItems, lastDateString));
                lastDate = currentDate;
                lastDateString = item.getDateString();
                tempItems = new ArrayList<>();
                tempItems.add(item);
            }
        }

        result.add(new MensaFoodItemCollectionVo(tempItems, lastDateString));

        return result;
    }

}
