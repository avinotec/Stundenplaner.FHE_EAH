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
    public static List<MensaFoodItemCollectionVo> orderMensaItems(final MensaFoodItemVo[] _Items) {

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
