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
package de.fhe.fhemobile.utils.semesterdates;

import de.fhe.fhemobile.vos.semesterdates.SemesterPeriodOrDateVo;

/**
 * Created by Paul Cech on 13.05.15.
 */
public final class SemesterDatesUtils {

    private static final String LOG_TAG = SemesterDatesUtils.class.getSimpleName();

    /* Utility classes have all fields and methods declared as static.
    Creating private constructors in utility classes prevents them from being accidentally instantiated. */
    private SemesterDatesUtils() {
	}

	/**
     *
     * @param _time
     * @return
     */
    public static String getHeadline(final SemesterPeriodOrDateVo _time) {
        String result = "";

        if(_time.getDate() != null) {
            result = _time.getDate().getName();
        }
        else if(_time.getPeriod() != null) {
            result = _time.getPeriod().getName();
        }

        return result;
    }

    /**
     *
     * @param _time
     * @return
     */
    public static String getSubHeadline(final SemesterPeriodOrDateVo _time) {
        String result = "";

        if(_time.getDate() != null) {
            result = _time.getDate().getDate();
        }
        else if(_time.getPeriod() != null) {
            result = _time.getPeriod().getStartDateString() + " – " + _time.getPeriod().getEndDateString();
        }

        return result;
    }

}
