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
package de.fhe.fhemobile.comparator;

import java.util.Comparator;

import de.fhe.fhemobile.vos.mytimetable.MyTimeTableCourse;

/**
 * This Comparator compares two {@link MyTimeTableCourse} objects
 * based on the next upcoming event in there event list
 */
public class CourseDateComparator implements Comparator<MyTimeTableCourse> {

	@Override
	public int compare(final MyTimeTableCourse o1, final MyTimeTableCourse o2) {
		return new TimeTableEventComparator().compare(o1.getFirstEvent(), o2.getFirstEvent());
	}
}
