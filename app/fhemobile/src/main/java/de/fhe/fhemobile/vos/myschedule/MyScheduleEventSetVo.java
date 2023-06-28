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
package de.fhe.fhemobile.vos.myschedule;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fhe.fhemobile.vos.timetable.LecturerVo;
import de.fhe.fhemobile.vos.timetable.TimetableLocationVo;
import de.fhe.fhemobile.vos.timetable.TimetableStudyGroupVo;


/**
 * Class stores information of a set of grouped events.
 *
 * Use this class to parse the API response
 * and construct {@link MyScheduleEventSetVo}s from grouped {@link MyScheduleEventSetVo}s.
 *
 * by Nadja - 05/2022
 */
public class MyScheduleEventSetVo {

	private static final String TAG = MyScheduleEventSetVo.class.getSimpleName();

	public MyScheduleEventSetVo(){
		//empty constructor needed
	}



	public final String getId() {
		return mId;
	}

	public String getModuleId() { return mModuleId; }

	public String getTitle() { return mTitle; }

	public void setTitle(final String title) { this.mTitle = title; }

	public List<MyScheduleEventDateVo> getEventDates() { return new ArrayList<>(mEventDates.values()); }

	public void setEvents(final Map<String, MyScheduleEventDateVo> events) { this.mEventDates = events; }

	public List<TimetableStudyGroupVo> getStudyGroups() { return new ArrayList<>(mStudyGroups.values()); }

	public List<LecturerVo> getLecturerList() { return new ArrayList<>(mLecturerMap.values());	}

	public List<TimetableLocationVo> getLocationList() { return new ArrayList<>(mLocationMap.values()); }


	@SerializedName("activityId")
	private String mId;

	@SerializedName("activityName")
	private String mTitle;

	@SerializedName("moduleId")
	private String mModuleId;

	@SerializedName("moduleName")
	private String mModuleName;

	@SerializedName("dataStudentset")
	private final Map<String, TimetableStudyGroupVo> mStudyGroups = new HashMap<>();

	@SerializedName("dataStaff")
	private final Map<String, LecturerVo> mLecturerMap = new HashMap<>();

	@SerializedName("dataLocation")
	private final Map<String, TimetableLocationVo> mLocationMap = new HashMap<>();

	@SerializedName("dataDatetime")
	private Map<String, MyScheduleEventDateVo> mEventDates = new HashMap<>();


}

