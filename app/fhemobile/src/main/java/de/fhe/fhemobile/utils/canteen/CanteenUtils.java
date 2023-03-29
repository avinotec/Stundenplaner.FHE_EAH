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

}
