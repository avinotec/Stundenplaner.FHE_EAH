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

import de.fhe.fhemobile.vos.mytimetable.MyTimeTableCourseComponent;

/**
 * Edited by Nadja - 01/2022
 */
public class CourseTitleComparator implements Comparator<MyTimeTableCourseComponent> {

	private static final String TAG = "CourseTitleComparator";

	/**
	 * Similar to {@link java.util.Comparator#compare(Object, Object)}, should compare two and
	 * return how they should be ordered.
	 *
	 * @param o1 The first object to compare.
	 * @param o2 The second object to compare.
	 *
	 * @return a negative integer, zero, or a positive integer as the
	 * first argument is less than, equal to, or greater than the second.
	 */
	@Override
	public int compare(final MyTimeTableCourseComponent o1, final MyTimeTableCourseComponent o2) {
		//studyGroups are also taken into account because the title includes the group a certain subset of studygroups belong to
		return o1.getTitle().compareTo(o2.getTitle());
	}
}
