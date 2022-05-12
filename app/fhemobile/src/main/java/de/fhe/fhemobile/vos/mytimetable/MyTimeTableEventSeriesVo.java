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

import static de.fhe.fhemobile.Main.getSubscribedEventSeries;
import static de.fhe.fhemobile.utils.mytimetable.MyTimeTableUtils.getEventSeriesName;
import static de.fhe.fhemobile.utils.mytimetable.MyTimeTableUtils.getCourseName;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fhe.fhemobile.comparator.MyTimeTableEventTimeComparator;
import de.fhe.fhemobile.comparator.StudyGroupComparator;
import de.fhe.fhemobile.utils.mytimetable.MyTimeTableUtils;
import de.fhe.fhemobile.vos.timetable.LecturerVo;
import de.fhe.fhemobile.vos.timetable.TimeTableLocationVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyGroupVo;


/**
 * Class stores information of an time table event series,
 *
 * An event series is a set of mEventTimes that create a unit
 * and are shared by timetables of the study groups specified
 *
 * by Nadja - 01/2022
 */
public class MyTimeTableEventSeriesVo implements Parcelable {

	private static final String TAG = MyTimeTableEventSeriesVo.class.getSimpleName();

	public MyTimeTableEventSeriesVo(){
		//empty constructor needed
	}

//	/**
//	 * Creates a new instance of {@link MyTimeTableEventSeriesVo}
//	 * and automatically sets the subscribed status
//	 * @param event the event to initialize the event list with
//	 * @param studyGroup the study group the course is dedicated to and that to initialize the study group list with
//	 */
//	public MyTimeTableEventSeriesVo(final TimeTableEventVo event, final String studyGroup) {
//		this.mTitle = getEventSeriesName(event);
//		this.mTitle = getCourseName(event);
//		mEventTimes.add(event);
//		mStudyGroups.add(studyGroup);
//		checkAndSetSubscribed();
//	}


	public final int getId() {
		return mId;
	}

	public String getTitle() { return mTitle; }

	public void setTitle(String title) { this.mTitle = title; }

	public String getEventSeriesName() {
		if(mEventSeriesName == null) {
			mEventSeriesName = MyTimeTableUtils.getEventSeriesName(mTitle);
		}
		return mEventSeriesName;
	}

	public List<MyTimeTableEventTimeVo> getEvents() { return new ArrayList<>(mEventTimes.values()); }

	public void setEvents(Map<String, MyTimeTableEventTimeVo> events) { this.mEventTimes = events; }

	public List<TimeTableStudyGroupVo> getStudyGroups() { return new ArrayList<>(mStudyGroups.values()); }

	public Map<String, LecturerVo> getLecturerMap() { return mLecturerList; }

	public List<TimeTableLocationVo> getLocationList() { return new ArrayList<>(mLocationList.values()); }

	public Map<String, TimeTableLocationVo> getLocationMap() {
		return mLocationList;
	}

	public void addStudyGroup(final TimeTableStudyGroupVo studyGroup) {
		this.mStudyGroups.put(studyGroup.getStudyGroupId(), studyGroup);
	}

	public void addEvent(MyTimeTableEventTimeVo eventTime) {
		this.mEventTimes.put(eventTime.getStartDateTimeAsString(), eventTime);
	}

	public MyTimeTableEventTimeVo getFirstEvent() {
		List<MyTimeTableEventTimeVo> eventTimes = new ArrayList<>(mEventTimes.values());
		Collections.sort(eventTimes, new MyTimeTableEventTimeComparator());
		return mEventTimes.size() > 0 ? eventTimes.get(0) : null;
	}


	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(final boolean added) {
		this.subscribed = added;
	}

	/**
	 * Check if two {@link MyTimeTableEventSeriesVo} instances actually belong to the same event series,
	 * i. e. they only differ in entry number, and thus can be merged
	 * @param other the {@link MyTimeTableEventSeriesVo} to compare to
	 * @return true if components belong to the same course
	 */
	public boolean canBeGroupedForDisplay(final MyTimeTableEventSeriesVo other){
		return getEventSeriesName().equals(other.getEventSeriesName());
	}

	/**
	 * Adds all names of the study groups list to one long string
	 * @return study group list as string
	 */
	public String getStudyGroupListString(){

		List<TimeTableStudyGroupVo> studyGroups = new ArrayList<>(mStudyGroups.values());
		Collections.sort(studyGroups, new StudyGroupComparator());
		StringBuilder combinedStudyGroups = new StringBuilder();

		if(mStudyGroups.size() > 0) {
			for (TimeTableStudyGroupVo studyGroup : studyGroups) {
				combinedStudyGroups.append(studyGroup.getNumber()).append(", ");
			}
			return combinedStudyGroups.substring(0, combinedStudyGroups.length() - 2);

		} else {
			return "";
		}
	}

	// Comparable ------------------------------------------------------------------------------

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MyTimeTableEventSeriesVo)) return false;
		MyTimeTableEventSeriesVo that = (MyTimeTableEventSeriesVo) o;
		return mTitle.equals(that.mTitle);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[]{mTitle});
	}

	public void checkAndSetSubscribed(){
		//check if this event series belongs to a subscribed event series
		for(MyTimeTableEventSeriesVo subscribedEventSeries : getSubscribedEventSeries()){
			if (this.equals(subscribedEventSeries)){

				this.setSubscribed(true);
			}
		}
	}

	// PARCELABLE --------------------------------------------------------------------------------

	private MyTimeTableEventSeriesVo(final Parcel in) {
		mTitle = in.readString();
		mEventSeriesName = in.readString();
		in.readMap(mStudyGroups, TimeTableStudyGroupVo.class.getClassLoader());
		in.readMap(mEventTimes, MyTimeTableEventTimeVo.class.getClassLoader());
		subscribed = in.readByte() != 0;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(mTitle);
		dest.writeString(mEventSeriesName);
		dest.writeMap(mStudyGroups);
		dest.writeMap(mEventTimes);
		dest.writeByte((byte) (subscribed ? 1 : 0));

	}

	public static final Creator<MyTimeTableEventSeriesVo> CREATOR = new Creator<MyTimeTableEventSeriesVo>() {
		@Override
		public MyTimeTableEventSeriesVo createFromParcel(Parcel in) {
			return new MyTimeTableEventSeriesVo(in);
		}

		@Override
		public MyTimeTableEventSeriesVo[] newArray(int size) {
			return new MyTimeTableEventSeriesVo[size];
		}
	};



	@SerializedName("activityName")
	private String mTitle;

	@SerializedName("eventSeriesName")
	private String mEventSeriesName;

	@SerializedName("dataStudentset")
	private Map<String, TimeTableStudyGroupVo> mStudyGroups = new HashMap<>();

	@SerializedName("dataStaff")
	private Map<String, LecturerVo> mLecturerList;

	@SerializedName("dataLocation")
	private Map<String, TimeTableLocationVo> mLocationList;

	@SerializedName("dataDatetime")
	private Map<String, MyTimeTableEventTimeVo> mEventTimes = new HashMap<>();

	@SerializedName("subscribed")
	private boolean subscribed = false;


	private static int incId = 0;
	private int mId;

}

