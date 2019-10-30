/*
 * Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhe.fhemobile.utils;

import de.fhe.fhemobile.vos.semesterdata.SemesterTimesVo;

/**
 * Created by Paul Cech on 13.05.15.
 */
public class SemesterDataUtils {

    public static String getHeadline(SemesterTimesVo _time) {
        String result = "";

        if(_time.getDate() != null) {
            result = _time.getDate().getName();
        }
        else if(_time.getPeriod() != null) {
            result = _time.getPeriod().getName();
        }

        return result;
    }

    public static String getSubHeadline(SemesterTimesVo _time) {
        String result = "";

        if(_time.getDate() != null) {
            result = _time.getDate().getDate();
        }
        else if(_time.getPeriod() != null) {
            result = _time.getPeriod().getBegin() + "  -  " + _time.getPeriod().getEnd();
        }

        return result;
    }

    private static final String LOG_TAG = SemesterDataUtils.class.getSimpleName();
}
