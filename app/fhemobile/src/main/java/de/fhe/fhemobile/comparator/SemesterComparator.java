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

import de.fhe.fhemobile.vos.timetable.TimeTableSemesterVo;

/**
 * Comparator for comparing semester numbers
 * (note: number is a string and can contain non-digit characters)
 */
public class SemesterComparator implements Comparator<TimeTableSemesterVo> {

	@Override
	public int compare(final TimeTableSemesterVo t1, final TimeTableSemesterVo t2) {
		return t1.getNumber().compareTo(t2.getNumber());
	}
}
