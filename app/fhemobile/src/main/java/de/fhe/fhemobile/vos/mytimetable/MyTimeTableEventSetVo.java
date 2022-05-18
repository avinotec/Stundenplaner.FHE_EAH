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
package de.fhe.fhemobile.vos.mytimetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fhe.fhemobile.vos.timetable.LecturerVo;
import de.fhe.fhemobile.vos.timetable.TimeTableLocationVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyGroupVo;


/**
 * Class stores information of a set of grouped events.
 *
 * Use this class to parse the API response
 * and construct {@link MyTimeTableEventSetVo}s from grouped {@link MyTimeTableEventSetVo}s.
 *
 * by Nadja - 05/2022
 */
public class MyTimeTableEventSetVo implements Parcelable {

	private static final String TAG = MyTimeTableEventSetVo.class.getSimpleName();

	public MyTimeTableEventSetVo(){
		//empty constructor needed
	}


	public final String getId() {
		return mId;
	}

	public String getTitle() { return mTitle; }

	public void setTitle(String title) { this.mTitle = title; }

	public List<MyTimeTableEventDateVo> getEventDates() { return new ArrayList<>(mEventDates.values()); }

	public void setEvents(Map<String, MyTimeTableEventDateVo> events) { this.mEventDates = events; }

	public List<TimeTableStudyGroupVo> getStudyGroups() { return new ArrayList<>(mStudyGroups.values()); }

	public List<LecturerVo> getLecturerList() { return new ArrayList<>(mLecturerMap.values());	}

	public List<TimeTableLocationVo> getLocationList() { return new ArrayList<>(mLocationMap.values()); }

	// PARCELABLE --------------------------------------------------------------------------------

	private MyTimeTableEventSetVo(final Parcel in) {
		mTitle = in.readString();
		in.readMap(mStudyGroups, TimeTableStudyGroupVo.class.getClassLoader());
		in.readMap(mEventDates, MyTimeTableEventDateVo.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(mTitle);
		dest.writeMap(mStudyGroups);
		dest.writeMap(mEventDates);

	}

	public static final Creator<MyTimeTableEventSetVo> CREATOR = new Creator<MyTimeTableEventSetVo>() {
		@Override
		public MyTimeTableEventSetVo createFromParcel(Parcel in) {
			return new MyTimeTableEventSetVo(in);
		}

		@Override
		public MyTimeTableEventSetVo[] newArray(int size) {
			return new MyTimeTableEventSetVo[size];
		}
	};

	// End PARCELABLE --------------------------------------------------------------------------------


	@SerializedName("activityId")
	private String mId;

	@SerializedName("activityName")
	private String mTitle;

	@SerializedName("dataStudentset")
	private Map<String, TimeTableStudyGroupVo> mStudyGroups = new HashMap<>();

	@SerializedName("dataStaff")
	private Map<String, LecturerVo> mLecturerMap;

	@SerializedName("dataLocation")
	private Map<String, TimeTableLocationVo> mLocationMap;

	@SerializedName("dataDatetime")
	private Map<String, MyTimeTableEventDateVo> mEventDates = new HashMap<>();


}

