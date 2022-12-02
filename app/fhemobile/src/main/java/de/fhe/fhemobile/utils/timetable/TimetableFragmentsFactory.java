/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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
package de.fhe.fhemobile.utils.timetable;

import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.fragments.timetable.TimetableDialogFragment;
import de.fhe.fhemobile.fragments.timetable.TimetableFragment;

/**
 * Created by paul on 16.03.15.
 */
public final class TimetableFragmentsFactory {

    /* Utility classes have all fields and methods declared as static.
    Creating private constructors in utility classes prevents them from being accidentally instantiated. */
    private TimetableFragmentsFactory() {
    }

    /**
     * Get {@link TimetableFragment} if a timetable id has been saved as favourite,
     * get {@link TimetableDialogFragment} otherwise
     *
     * @return An instance of {@link TimetableFragment} or {@link TimetableDialogFragment}
     */
    public static FeatureFragment getFragment() {
        final String chosenTimeTable = TimetableSettings.getTimetableSelection();
        final FeatureFragment fragment;

        fragment = chosenTimeTable != null ?
                TimetableFragment.newInstance(chosenTimeTable) : TimetableDialogFragment.newInstance();


        return fragment;
    }
}
