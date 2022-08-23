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

import de.fhe.fhemobile.vos.canteen.CanteenDishVo;
import de.fhe.fhemobile.vos.canteen.CanteenMenuDayVo;

/**
 * Created by paul on 20.03.15.
 */
public final class CanteenUtils {

    /* Utility classes have all fields and methods declared as static.
    Creating private constructors in utility classes prevents them from being accidentally instantiated. */
    private CanteenUtils() {
    }

    /**
     *
     * @param _Items
     * @return
     */
    public static List<CanteenMenuDayVo> sortCanteenItems(final CanteenDishVo[] _Items) {

        final List<CanteenMenuDayVo> result = new ArrayList<>();

        long lastDate = _Items[0].getDate();
        String lastDateString = _Items[0].getDateString();
        List<CanteenDishVo> tempItems = new ArrayList<CanteenDishVo>();

        for (final CanteenDishVo item : _Items) {
            final long currentDate = item.getDate();
            if (currentDate != lastDate) {
                result.add(new CanteenMenuDayVo(tempItems, lastDateString));
                lastDate = currentDate;
                lastDateString = item.getDateString();
                tempItems = new ArrayList<>();
            }
            tempItems.add(item);
        }

        result.add(new CanteenMenuDayVo(tempItems, lastDateString));

        return result;
    }

    /**
     * Group CanteenFoodItems per day and store each day in a {@link CanteenMenuDayVo}
     * @param _Items Array of {@link CanteenDishVo} objects, sorted by date
     * @return list of {@link CanteenMenuDayVo} objects
     */
    public static List<CanteenMenuDayVo> groupPerDay(final CanteenDishVo[] _Items) {

        final List<CanteenMenuDayVo> result = new ArrayList<>();

        //helpers and flags for for-loop
        List<CanteenDishVo> itemsOfSameDate = new ArrayList<CanteenDishVo>();
        long lastDate = _Items[0].getDate();
        String lastDateString = _Items[0].getDateString();

        for (final CanteenDishVo canteenFoodItem : _Items) {

            final long currentDate = canteenFoodItem.getDate();
            //if current item belongs to different date than item before,
            // then all items for the last date are collected and can be stored in a CanteenMenuDayVo
            if (currentDate != lastDate) {
                result.add(new CanteenMenuDayVo(itemsOfSameDate, lastDateString));
                lastDate = currentDate;
                lastDateString = canteenFoodItem.getDateString();
                itemsOfSameDate = new ArrayList<>();
            }
            itemsOfSameDate.add(canteenFoodItem);
        }

        result.add(new CanteenMenuDayVo(itemsOfSameDate, lastDateString));

        return result;
    }

}
