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

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.fhe.fhemobile.utils.MyTimeTableUtils;
import de.fhe.fhemobile.vos.timetable.SemesterVo;
import de.fhe.fhemobile.vos.timetable.StudyCourseVo;
import de.fhe.fhemobile.vos.timetable.StudyGroupVo;
import de.fhe.fhemobile.vos.timetable.TimeTableDayVo;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;

public class FlatDataStructure implements Parcelable {
	private static final String TAG = "FlatDataStructure";

	public FlatDataStructure(){
	}

	public FlatDataStructure(FlatStudyCourse course,
							 FlatSemesters semester,
							 StudyGroupVo studyGroup,
							 FlatTimeTableWeek eventWeek,
							 FlatTimeTableDay eventDay,
							 TimeTableEventVo event,
							 boolean subscribed) {
		this.course = course;
		this.semester = semester;
		this.studyGroup = studyGroup;
		this.eventWeek = eventWeek;
		this.eventDay = eventDay;
		this.event = event;
		this.subscribed = subscribed;
	}

	private FlatDataStructure(final Parcel in) {
		course = in.readParcelable(FlatStudyCourse.class.getClassLoader());
		semester = in.readParcelable(FlatSemesters.class.getClassLoader());
		studyGroup = in.readParcelable(StudyGroupVo.class.getClassLoader());
		eventWeek = in.readParcelable(FlatTimeTableWeek.class.getClassLoader());
		eventDay = in.readParcelable(FlatTimeTableDay.class.getClassLoader());
		event = in.readParcelable(TimeTableEventVo.class.getClassLoader());
		in.readStringList(sets);
	}

	public final FlatStudyCourse getCourse() {
		return course;
	}

	public final FlatSemesters getSemester() {
		return semester;
	}

	public final StudyGroupVo getStudyGroup() {
		return studyGroup;
	}

	public FlatTimeTableWeek getEventWeek() {
		return eventWeek;
	}

	public FlatTimeTableDay getEventDay() {
		return eventDay;
	}

	public final TimeTableEventVo getEvent() {
		return event;
	}

	public final void setStudyCourse(final StudyCourseVo course) {
		this.course = new FlatStudyCourse();
		this.course.setId(course.getId());
		this.course.setTitle(course.getTitle());
	}

	private final void setStudyCourse(final FlatStudyCourse course) {
		this.course = course;
	}

	private final void setSemester(final FlatSemesters semester) {
		this.semester = semester;
	}

	public final void setSemester(final SemesterVo semester) {
		this.semester = new FlatSemesters();
		this.semester.setId(semester.getId());
		this.semester.setTitle(semester.getTitle());
	}

	public final void setStudyGroup(final StudyGroupVo studyGroup) {
		this.studyGroup = studyGroup;
	}

	public final void setEventWeek(final TimeTableWeekVo eventWeek) {
		this.eventWeek = new FlatTimeTableWeek();
		this.eventWeek.setWeekInYear(eventWeek.getWeekInYear());
		this.eventWeek.setYear(eventWeek.getYear());
	}

	public final void setEventWeek(final FlatTimeTableWeek eventWeek) {
		this.eventWeek = eventWeek;
	}
	public final void setEventDay(final TimeTableDayVo eventDay) {
		this.eventDay = new FlatTimeTableDay();
		this.eventDay.setDayInWeek(eventDay.getDayInWeek());
		this.eventDay.setName(eventDay.getName());
	}

	public final void setEventDay(final FlatTimeTableDay eventDay) {
		this.eventDay = eventDay;
	}
	public final void setEvent(final TimeTableEventVo event) {
		this.event = event;
	}

	public List<String> getSets() {
		return sets;
	}

	public void setSets(List<String> sets) {
		this.sets = sets;
	}

	/**
	 * adds all set names to one long string and returns it
	 * @return
	 */
	public String getSetString(){
		Collections.sort(sets);
		StringBuilder combinedStudyGroups = new StringBuilder();
		if(sets.size() > 0) {
			for (String _studyGroupTitle : sets) {
				combinedStudyGroups.append(_studyGroupTitle).append(", ");
			}
			return combinedStudyGroups.substring(0, combinedStudyGroups.length() - 2);
		}
		else{
			return combinedStudyGroups.toString();
		}
	}

	public final int getId() {
		return id;
	}

	public void setVisible(final boolean visible) {
		this.visible = visible;
	}

	public final boolean isVisible() {
		return visible;
	}

	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(boolean added) {
		this.subscribed = added;
	}

	public boolean isEqual(@NonNull FlatDataStructure other){
		return MyTimeTableUtils.cutEventTitle(this.getEvent().getTitle())
				.equals(MyTimeTableUtils.cutEventTitle(other.getEvent().getTitle()));
	}

	public FlatDataStructure copy(){
		final FlatDataStructure copy = new FlatDataStructure();
		//		Log.d(TAG, "FlatDataStructure: incID: "+FlatDataStructure.incId);
		copy.id = FlatDataStructure.incId++;
		copy.setStudyCourse(this.getCourse());
		copy.setSemester(this.getSemester());
		copy.setStudyGroup(this.getStudyGroup());
		copy.setSets(new ArrayList<>(this.getSets()));
		return copy;
	}


	// PARCELABLE --------------------------------------------------------------------------------

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeParcelable(course, flags);
		dest.writeParcelable(semester, flags);
		dest.writeParcelable(studyGroup, flags);
		dest.writeParcelable(eventWeek, flags);
		dest.writeParcelable(eventDay, flags);
		dest.writeParcelable(event, flags);
		dest.writeStringList(sets);

	}

	public static final Creator<FlatDataStructure> CREATOR = new Creator<FlatDataStructure>() {
		@Override
		public FlatDataStructure createFromParcel(Parcel in) {
			return new FlatDataStructure(in);
		}

		@Override
		public FlatDataStructure[] newArray(int size) {
			return new FlatDataStructure[size];
		}
	};



	@SerializedName("course")
	private FlatStudyCourse course;

	@SerializedName("semester")
	private FlatSemesters semester;

	@SerializedName("studyGroup")
	private StudyGroupVo studyGroup;

	@SerializedName("eventWeek")
	private FlatTimeTableWeek eventWeek;

	@SerializedName("eventDay")
	private FlatTimeTableDay eventDay;

	@SerializedName("event")
	private TimeTableEventVo event;

	@SerializedName("sets")
	private List<String> sets = new ArrayList<>();

	private boolean visible = false;
	private boolean subscribed = false;

	private static int incId = 0;
	private int id;


	/*
	Gesamtliste:[
		Liste der Sets für Studienfach 1 [
			Liste der einzelnen Events für Set 1 [

				Event 1,
				Event 2,

			],
			Liste der einzelnen Events für Set 2 [

				Event 1,
				Event 2,

			],
		],
		Liste der Sets für Studienfach 2 [
			...
		]

	]
	 */


/*
	@NonNull
	@Override
	public final String toString() {
		return this.getCourse().getTitle() + "-->"
				+ this.getSemester().getTitle() + "-->"
				+ this.getStudyGroup().getTitle() + "-->"
				+ this.getEventWeek().getWeekInYear() + "-->"
				+ this.getEventDay().getDayInWeek() + "-->"
				+ this.getEvent().getUid() + "-->"
				+ this.getEvent().getTitle() + "-->"
				+ this.getSets().size();

	}
*/
}

