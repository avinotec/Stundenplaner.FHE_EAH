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
package de.fhe.fhemobile.comparator;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class Date_Comparator implements Comparator<FlatDataStructure> {
	private static final String TAG = "LessonTitle_StudyGroupT";
	private final static int GREATER=1;
	private final static int EQUAL=0;
	private final static int LESSER=-1;

	@Override
	public int compare(final FlatDataStructure o1, final FlatDataStructure o2) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy H:mm");
		Date lessonDate1 = null;
		Date lessonDate2 = null;
		try {
			lessonDate1= sdf.parse(o1.getEvent().getDate()+" "+o1.getEvent().getStartTime());
			lessonDate2= sdf.parse(o2.getEvent().getDate()+" "+o2.getEvent().getStartTime());
		} catch (ParseException e) {
			Log.e(TAG, "Fehler beim Parsen der Daten: ",e );
			return EQUAL;
		}


		//Vergleiche das Datum vom ersten element mit dem Datum des zweiten Elements.
		//Ist das erste Datum "größer" wird 1 zurückgegeben, ist das zweite größer wird -1 zurückgegeben
		//und sind beide gleich, wird 0 zurückgegeben.
		final int dateCompareResult = lessonDate1.compareTo(lessonDate2);

		if ( dateCompareResult > 0 ) {
			return GREATER;
		}else if( dateCompareResult < 0 ){
			return LESSER;
		}
		else{
			return EQUAL;
		}
	}
}
