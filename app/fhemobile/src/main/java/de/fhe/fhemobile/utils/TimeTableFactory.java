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

import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.fragments.timetable.TimeTableFragment;
import de.fhe.fhemobile.fragments.timetable.TimeTableDialogFragment;

/**
 * Created by paul on 16.03.15.
 */
public class TimeTableFactory {

    /**
     *
     * @return
     */
    public static FeatureFragment getTimeTableFragment() {
        final String chosenTimeTable = TimeTableSettings.fetchTimeTableSelection();
        FeatureFragment fragment = null;

        if (chosenTimeTable != null) {
            fragment = TimeTableFragment.newInstance(chosenTimeTable);
        }
        else {
            fragment = TimeTableDialogFragment.newInstance();
        }

        return fragment;
    }
}
