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
package de.fhe.fhemobile.utils.mensa;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.vos.mensa.MensaDayVo;
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

        final List<MensaFoodItemCollectionVo> result = new ArrayList<MensaFoodItemCollectionVo>();

        long lastDate = _Items[0].getDate();
        String lastDateString = _Items[0].getDateString();
        List<MensaFoodItemVo> tempItems = new ArrayList<MensaFoodItemVo>();

        for (final MensaFoodItemVo item : _Items) {
            final long currentDate = item.getDate();
            if (currentDate != lastDate) {
                result.add(new MensaFoodItemCollectionVo(tempItems, lastDateString));
                lastDate = currentDate;
                lastDateString = item.getDateString();
                tempItems = new ArrayList<>();
            }
            tempItems.add(item);
        }

        result.add(new MensaFoodItemCollectionVo(tempItems, lastDateString));

        return result;
    }

    /**
     * Group MensaFoodItems per day and store each day in a {@link MensaDayVo}
     * @param _Items Array of {@link MensaFoodItemVo} objects, sorted by date
     * @return list of {@link MensaDayVo} objects
     */
    public static List<MensaDayVo> groupPerDay(final MensaFoodItemVo[] _Items) {

        final List<MensaDayVo> result = new ArrayList<>();

        //helpers and flags for for-loop
        List<MensaFoodItemVo> itemsOfSameDate = new ArrayList<MensaFoodItemVo>();
        long lastDate = _Items[0].getDate();
        String lastDateString = _Items[0].getDateString();

        for (final MensaFoodItemVo mensaFoodItem : _Items) {

            final long currentDate = mensaFoodItem.getDate();
            //if current item belongs to different date than item before,
            // then all items for the last date are collected and can be stored in a MensaDayVo
            if (currentDate != lastDate) {
                result.add(new MensaDayVo(itemsOfSameDate, lastDateString));
                lastDate = currentDate;
                lastDateString = mensaFoodItem.getDateString();
                itemsOfSameDate = new ArrayList<>();
            }
            itemsOfSameDate.add(mensaFoodItem);
        }

        result.add(new MensaDayVo(itemsOfSameDate, lastDateString));

        return result;
    }

}