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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class TimetableUtils {

    /* Utility classes have all fields and methods declared as static.
    Creating private constructors in utility classes prevents them from being accidentally instantiated. */
	private TimetableUtils() {
	}

	/**
     * if there are more than one lecturer with this name the department e.g. SciTec is added in brackets
     * this prettifies the person's name if a whitespace between name and bracket is missing
     * @param person string already corrected for umlauts
     * @return name string
     */
    public static String prettifyName(String person){
        //method added by Nadja - 05.10.2021

        if (person.matches("[a-zA-Z- ,._]+[^ ][(][a-zA-Z-]+[)]")){
            person = person.replaceFirst("[(]", " (");
        }
        return person;
    }


    public static String getWeekDayName(final int dayNumber){
        final Calendar cal = Calendar.getInstance();
        //+2 because in calender first day is sunday and days in calendar are counted 1-based
        cal.set(Calendar.DAY_OF_WEEK, dayNumber+2);
        return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    }

    public static String getWeekDayName(final Date date){
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    }


    public static String cutStudyProgramPrefix(final String eventTitle){
        return eventTitle.replaceFirst("^[A-Za-z]+(/[A-Za-z]+)*\\(((BA)|(MA)|(BA/MA))\\)/?","");
    }
}
