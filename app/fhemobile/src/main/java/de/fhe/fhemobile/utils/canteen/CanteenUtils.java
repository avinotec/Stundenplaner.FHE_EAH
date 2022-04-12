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
package de.fhe.fhemobile.utils.canteen;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.vos.canteen.CanteenDayVo;
import de.fhe.fhemobile.vos.canteen.CanteenFoodItemCollectionVo;
import de.fhe.fhemobile.vos.canteen.CanteenFoodItemVo;

/**
 * Created by paul on 20.03.15.
 */
public class CanteenUtils {

    /**
     *
     * @param _Items
     * @return
     */
    public static List<CanteenFoodItemCollectionVo> orderCanteenItems(final CanteenFoodItemVo[] _Items) {

        final List<CanteenFoodItemCollectionVo> result = new ArrayList<CanteenFoodItemCollectionVo>();

        long lastDate = _Items[0].getDate();
        String lastDateString = _Items[0].getDateString();
        List<CanteenFoodItemVo> tempItems = new ArrayList<CanteenFoodItemVo>();

        for (final CanteenFoodItemVo item : _Items) {
            final long currentDate = item.getDate();
            if (currentDate != lastDate) {
                result.add(new CanteenFoodItemCollectionVo(tempItems, lastDateString));
                lastDate = currentDate;
                lastDateString = item.getDateString();
                tempItems = new ArrayList<>();
            }
            tempItems.add(item);
        }

        result.add(new CanteenFoodItemCollectionVo(tempItems, lastDateString));

        return result;
    }

    /**
     * Group CanteenFoodItems per day and store each day in a {@link CanteenDayVo}
     * @param _Items Array of {@link CanteenFoodItemVo} objects, sorted by date
     * @return list of {@link CanteenDayVo} objects
     */
    public static List<CanteenDayVo> groupPerDay(final CanteenFoodItemVo[] _Items) {

        final List<CanteenDayVo> result = new ArrayList<>();

        //helpers and flags for for-loop
        List<CanteenFoodItemVo> itemsOfSameDate = new ArrayList<CanteenFoodItemVo>();
        long lastDate = _Items[0].getDate();
        String lastDateString = _Items[0].getDateString();

        for (final CanteenFoodItemVo canteenFoodItem : _Items) {

            final long currentDate = canteenFoodItem.getDate();
            //if current item belongs to different date than item before,
            // then all items for the last date are collected and can be stored in a CanteenDayVo
            if (currentDate != lastDate) {
                result.add(new CanteenDayVo(itemsOfSameDate, lastDateString));
                lastDate = currentDate;
                lastDateString = canteenFoodItem.getDateString();
                itemsOfSameDate = new ArrayList<>();
            }
            itemsOfSameDate.add(canteenFoodItem);
        }

        result.add(new CanteenDayVo(itemsOfSameDate, lastDateString));

        return result;
    }

}
