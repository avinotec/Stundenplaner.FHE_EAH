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
package de.fhe.fhemobile.vos.myschedule;

import static de.fhe.fhemobile.Main.getSubscribedEventSeries;
import static de.fhe.fhemobile.utils.myschedule.MyScheduleUtils.getEventSeriesBaseTitle;
import static de.fhe.fhemobile.utils.myschedule.MyScheduleUtils.getEventSeriesName;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.fhe.fhemobile.comparator.MyScheduleEventComparator;
import de.fhe.fhemobile.comparator.StudyGroupComparator;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyGroupVo;


/**
 * Class stores information of an time table event series,
 *
 * An event series is a set of events creating an (arbitrary) unit
 * that is marked for a certain of study groups.
 *
 * by Nadja - 01/2022
 */
public class MyScheduleEventSeriesVo implements Parcelable{

	private static final String TAG = MyScheduleEventSeriesVo.class.getSimpleName();


	public MyScheduleEventSeriesVo(){
		//empty constructor needed
	}

	/**
	 * Create a new instance of {@link MyScheduleEventSeriesVo} from an {@link MyScheduleEventSetVo}
	 * and automatically set the subscribed status
	 * @param eventSet The {@link MyScheduleEventSetVo} to use for initializing
	 */
	public MyScheduleEventSeriesVo(final MyScheduleEventSetVo eventSet) {
		this.mTitle = getEventSeriesName(eventSet.getTitle());
		this.mStudyGroups.addAll(eventSet.getStudyGroups());
		this.mModuleId = eventSet.getModuleId();
		this.mEventSetIds.add(eventSet.getId());
		this.mEvents = new ArrayList<>();
		checkAndSetSubscribed();
	}


	public String getTitle() { return mTitle; }

	public void setTitle(final String title) { this.mTitle = title; }

	public List<TimeTableStudyGroupVo> getStudyGroups() { return mStudyGroups; }

	public void addEventSetId(final String eventSetId){ mEventSetIds.add(eventSetId);}

	public String getModuleId() { return mModuleId;	}

	public Set<String> getEventSetIds() { return mEventSetIds;	}

	public List<MyScheduleEventVo> getEvents() { return mEvents; }

	public MyScheduleEventVo getFirstEvent() {
		Collections.sort(mEvents, new MyScheduleEventComparator());
		return !mEvents.isEmpty() ? mEvents.get(0) : null;
	}

	public void addEvents(final List<MyScheduleEventVo> events) { this.mEvents.addAll(events); }

	// TODO parameter eventSetIds wird nicht verwendet
	public void setEvents(final List<MyScheduleEventVo> mEvents, final Set<String> evenSetIds) {
		this.mEvents = mEvents;
	}

	//unused
	//public void removeEvent(final MyScheduleEventVo event){ this.mEvents.remove(event); }
	//unused
	//public void removeEventSetId(final String eventSetId){
	//                                                  TODO wrong type, eventSetID ist String, die Liste hat aber MyScheduleEventVo
	//	if(mEventSetIds.contains(eventSetId)) mEvents.remove(eventSetId); }

	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(final boolean added) {
		this.subscribed = added;
	}

	/**
	 * Check if two {@link MyScheduleEventSeriesVo} have the same base title,
	 * i. e. they only differ in group number
	 * @param other the {@link MyScheduleEventSeriesVo} to compare to
	 * @return true if components belong to the same course
	 */
	public boolean canBeGroupedForDisplay(final MyScheduleEventSeriesVo other){
		final String baseTitle = getEventSeriesBaseTitle(mTitle);
		final String otherBaseTitle = getEventSeriesBaseTitle(other.getTitle());
		return baseTitle.equals(otherBaseTitle);
	}

	/**
	 * Adds all names of the study groups list to one long string
	 * @return study group list as string
	 */
	public String getStudyGroupListString(){
		Collections.sort(mStudyGroups, new StudyGroupComparator());
		final StringBuilder studyGroupsString = new StringBuilder();

		if(!mStudyGroups.isEmpty()) {
			for (final TimeTableStudyGroupVo studyGroup : mStudyGroups) {
				studyGroupsString.append(studyGroup.getNumber()).append(", ");
			}
			return studyGroupsString.substring(0, studyGroupsString.length() - 2);

		} else {
			return "";
		}
	}

	public void checkAndSetSubscribed(){
		//check if this event series belongs to a subscribed event series
		for(final MyScheduleEventSeriesVo subscribedEventSeries : getSubscribedEventSeries()){
			if (this.getTitle().equals(subscribedEventSeries.getTitle())){

				this.setSubscribed(true);
			}
		}
	}

	// PARCELABLE --------------------------------------------------------------------------------

	MyScheduleEventSeriesVo(final Parcel in) {
		mTitle = in.readString();
		in.readList(mStudyGroups, TimeTableStudyGroupVo.class.getClassLoader());
		mModuleId = in.readString();
		mEventSetIds = new HashSet(Arrays.asList(in.readArray(String.class.getClassLoader())));
		in.readList(mEvents, MyScheduleEventDateVo.class.getClassLoader());
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
		dest.writeString(mModuleId);
		dest.writeArray(mEventSetIds.toArray());
		dest.writeList(mEvents);
		dest.writeByte((byte) (subscribed ? 1 : 0));
	}

	public static final Creator<MyScheduleEventSeriesVo> CREATOR = new Creator<MyScheduleEventSeriesVo>() {
		@Override
		public MyScheduleEventSeriesVo createFromParcel(final Parcel in) {
			return new MyScheduleEventSeriesVo(in);
		}

		@Override
		public MyScheduleEventSeriesVo[] newArray(final int size) {
			return new MyScheduleEventSeriesVo[size];
		}
	};

	// End PARCELABLE --------------------------------------------------------------------------------


	@SerializedName("title")
	private String mTitle;

	@SerializedName("studyGroups")
	private final List<TimeTableStudyGroupVo> mStudyGroups = new ArrayList<>();

	@SerializedName("moduleId")
	private String mModuleId;

	@SerializedName("eventSetIds")
	private Set<String> mEventSetIds = new HashSet<>();

	@SerializedName("events")
	private List<MyScheduleEventVo> mEvents = new ArrayList<>();

	@SerializedName("subscribed")
	private boolean subscribed = false;

}

