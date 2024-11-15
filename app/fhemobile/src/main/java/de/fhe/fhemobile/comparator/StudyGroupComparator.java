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

package de.fhe.fhemobile.comparator;

import java.util.Comparator;

import de.fhe.fhemobile.vos.timetable.TimetableStudyGroupVo;

/**
 * Comparator for comparing study groups by number
 *
 * Created by Nadja - 05/2022
 */
public class StudyGroupComparator implements Comparator <TimetableStudyGroupVo> {

	@Override
	public int compare(final TimetableStudyGroupVo t1, final TimetableStudyGroupVo t2) {
		return t1.getNumber().compareTo(t2.getNumber());
	}
}

