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
import static de.fhe.fhemobile.utils.mytimetable.MyTimeTableUtils.getEventSeriesBaseTitle;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.fhe.fhemobile.comparator.MyTimeTableEventComparator;
import de.fhe.fhemobile.comparator.StudyGroupComparator;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyGroupVo;


/**
 * Class stores information of an time table event series,
 *
 * An event series is a set of mEvents that create a unit
 * and are shared by timetables of the study groups specified
 *
 * by Nadja - 01/2022
 */
public class MyTimeTableEventSeriesVo implements Parcelable{

	private static final String TAG = MyTimeTableEventSeriesVo.class.getSimpleName();

	public MyTimeTableEventSeriesVo(){
		//empty constructor needed
	}

	/**
	 * Create a new instance of {@link MyTimeTableEventSeriesVo}
	 * and automatically set the subscribed status
	 * @param title The title of the event series
	 * @param studyGroups The studyGroups the events in this {@link MyTimeTableEventSetVo} are registered for
	 * @param events The events of this event series
	 */
	public MyTimeTableEventSeriesVo(final String title,
									final List<TimeTableStudyGroupVo> studyGroups,
									final List<MyTimeTableEventVo> events) {
		this.mTitle = title;
		this.mStudyGroups = studyGroups;
		this.mEvents = events;
		checkAndSetSubscribed();
	}


	public String getTitle() { return mTitle; }

	public void setTitle(String title) { this.mTitle = title; }

	public List<MyTimeTableEventVo> getEvents() { return mEvents; }

	public List<TimeTableStudyGroupVo> getStudyGroups() { return mStudyGroups; }

	public void addEvent(MyTimeTableEventVo event) {
		this.mEvents.add(event);
	}

	public MyTimeTableEventVo getFirstEvent() {
		Collections.sort(mEvents, new MyTimeTableEventComparator());
		return !mEvents.isEmpty() ? mEvents.get(0) : null;
	}


	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(final boolean added) {
		this.subscribed = added;
	}

	/**
	 * Check if two {@link MyTimeTableEventSeriesVo} have the same base title,
	 * i. e. they only differ in group number
	 * @param other the {@link MyTimeTableEventSeriesVo} to compare to
	 * @return true if components belong to the same course
	 */
	public boolean canBeGroupedForDisplay(final MyTimeTableEventSeriesVo other){
		String baseTitle = getEventSeriesBaseTitle(mTitle);
		String otherBaseTitle = getEventSeriesBaseTitle(other.getTitle());
		return baseTitle.equals(otherBaseTitle);
	}

	/**
	 * Adds all names of the study groups list to one long string
	 * @return study group list as string
	 */
	public String getStudyGroupListString(){
		Collections.sort(mStudyGroups, new StudyGroupComparator());
		StringBuilder studyGroupsString = new StringBuilder();

		if(!mStudyGroups.isEmpty()) {
			for (TimeTableStudyGroupVo studyGroup : mStudyGroups) {
				studyGroupsString.append(studyGroup.getNumber()).append(", ");
			}
			return studyGroupsString.substring(0, studyGroupsString.length() - 2);

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
		in.readList(mStudyGroups, TimeTableStudyGroupVo.class.getClassLoader());
		in.readList(mEvents, MyTimeTableEventDateVo.class.getClassLoader());
		subscribed = in.readByte() != 0;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(mTitle);
		dest.writeList(mStudyGroups);
		dest.writeList(mEvents);
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

	@SerializedName("title")
	private String mTitle;

	@SerializedName("studyGroups")
	private List<TimeTableStudyGroupVo> mStudyGroups = new ArrayList<>();

	@SerializedName("events")
	private List<MyTimeTableEventVo> mEvents = new ArrayList<>();

	@SerializedName("subscribed")
	private boolean subscribed = false;

}

