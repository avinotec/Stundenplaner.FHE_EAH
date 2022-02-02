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
package de.fhe.fhemobile.network;

import androidx.annotation.NonNull;

import de.fhe.fhemobile.vos.timetable.TimeTableSemesterVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyCourseVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyGroupVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * @param <T>
 */
public class MyTimeTableCallback<T> implements Callback<T> {

	final private TimeTableStudyCourseVo studyCourse;
	final private TimeTableSemesterVo semester;
	final private TimeTableStudyGroupVo studyGroup;


	/**
	 * @param studyCourse
	 * @param semester
	 * @param studyGroup
	 */
	public MyTimeTableCallback(@NonNull TimeTableStudyCourseVo studyCourse,
							   @NonNull TimeTableSemesterVo semester,
							   @NonNull TimeTableStudyGroupVo studyGroup) {
		this.studyCourse = studyCourse;
		this.semester = semester;
		this.studyGroup = studyGroup;
	}

	/**
	 *
	 * @param call
	 * @param response
	 */
	@Override
	public void onResponse(Call<T> call, Response<T> response) {

	}

	/**
	 *
	 * @param call
	 * @param t
	 */
	@Override
	public void onFailure(Call<T> call, Throwable t) {

	}

	public TimeTableSemesterVo getSemester() {
		return semester;
	}

	public TimeTableStudyCourseVo getStudyCourse() {
		return studyCourse;
	}

	public TimeTableStudyGroupVo getStudyGroup() {
		return studyGroup;
	}
}
