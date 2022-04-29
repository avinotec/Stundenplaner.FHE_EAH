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

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.vos.timetable.TimeTableSemesterVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyGroupVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyProgramVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * @param <T>
 */
public class MyTimeTableCallback<T> implements Callback<T> {

    private static final String TAG = MyTimeTableCallback.class.getSimpleName();
    final private TimeTableStudyProgramVo studyProgram;
	final private TimeTableSemesterVo semester;
	final private TimeTableStudyGroupVo studyGroup;


	/**
	 * @param studyProgram the study program
	 * @param semester the semester
	 * @param studyGroup the study group
	 */
	public MyTimeTableCallback(@NonNull TimeTableStudyProgramVo studyProgram,
							   @NonNull TimeTableSemesterVo semester,
							   @NonNull TimeTableStudyGroupVo studyGroup) {
		this.studyProgram = studyProgram;
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
		showErrorToast();
		Log.d(TAG, "failure: request " + call.request().url() + " - "+ t.getMessage());
	}

	private void showErrorToast() {
		Toast.makeText(Main.getAppContext(), "Cannot establish connection!",
				Toast.LENGTH_LONG).show();
	}

	public TimeTableSemesterVo getSemester() {
		return semester;
	}

	public TimeTableStudyProgramVo getStudyProgram() {
		return studyProgram;
	}

	public TimeTableStudyGroupVo getStudyGroup() {
		return studyGroup;
	}
}
