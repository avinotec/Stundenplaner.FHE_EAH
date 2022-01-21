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
import de.fhe.fhemobile.vos.timetable.TimeTableSemesterVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyCourseVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyGroupVo;
import de.fhe.fhemobile.vos.timetable.TimeTableDayVo;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;


/**
 * edited by Nadja - 01/2022
 */
public class MyTimeTableCourse implements Parcelable {

	private static final String TAG = "MyTimeTableCourse";

	public MyTimeTableCourse(){	}

	public MyTimeTableCourse(TimeTableStudyCourseVo studyCourse,
							 TimeTableSemesterVo semester,
							 TimeTableStudyGroupVo studyGroup,
							 TimeTableWeekVo eventWeek,
							 TimeTableDayVo eventDay,
							 TimeTableEventVo event,
							 boolean subscribed) {
		this.studyCourse = studyCourse;
		this.semester = semester;
		this.studyGroup = studyGroup;
		this.eventWeek = eventWeek;
		this.eventDay = eventDay;
		this.event = event;
		this.subscribed = subscribed;
	}

	private MyTimeTableCourse(final Parcel in) {
		studyCourse = in.readParcelable(TimeTableStudyCourseVo.class.getClassLoader());
		semester = in.readParcelable(TimeTableSemesterVo.class.getClassLoader());
		studyGroup = in.readParcelable(TimeTableStudyGroupVo.class.getClassLoader());
		eventWeek = in.readParcelable(TimeTableWeekVo.class.getClassLoader());
		eventDay = in.readParcelable(TimeTableDayVo.class.getClassLoader());
		event = in.readParcelable(TimeTableEventVo.class.getClassLoader());
		in.readStringList(sets);
	}

	public final TimeTableStudyCourseVo getStudyCourse() { return studyCourse; }

	public final TimeTableSemesterVo getSemester() { return semester; }

	public final TimeTableStudyGroupVo getStudyGroup() { return studyGroup; }

	public TimeTableWeekVo getEventWeek() { return eventWeek; }

	public TimeTableDayVo getEventDay() { return eventDay; }

	public final TimeTableEventVo getEvent() { return event; }

	public final void setStudyCourse(final TimeTableStudyCourseVo course) { this.studyCourse = course; }

	public final void setSemester(final TimeTableSemesterVo semester) { this.semester = semester; }

	public final void setStudyGroup(final TimeTableStudyGroupVo studyGroup) { this.studyGroup = studyGroup; }

	public final void setEventWeek(final TimeTableWeekVo eventWeek) { this.eventWeek = eventWeek; }

	public final void setEventDay(final TimeTableDayVo eventDay) { this.eventDay = eventDay; }

	public final void setEvent(final TimeTableEventVo event) { this.event = event; }

	public List<String> getSets() { return sets; }

	public void setSets(List<String> sets) { this.sets = sets; }

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

	public boolean isEqual(@NonNull MyTimeTableCourse other){
		return MyTimeTableUtils.cutEventTitle(this.getEvent().getTitle())
				.equals(MyTimeTableUtils.cutEventTitle(other.getEvent().getTitle()));
	}

	public MyTimeTableCourse copy(){
		final MyTimeTableCourse copy = new MyTimeTableCourse();
		copy.id = MyTimeTableCourse.incId++;
		copy.setStudyCourse(this.getStudyCourse());
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
		dest.writeParcelable(studyCourse, flags);
		dest.writeParcelable(semester, flags);
		dest.writeParcelable(studyGroup, flags);
		dest.writeParcelable(eventWeek, flags);
		dest.writeParcelable(eventDay, flags);
		dest.writeParcelable(event, flags);
		dest.writeStringList(sets);

	}

	public static final Creator<MyTimeTableCourse> CREATOR = new Creator<MyTimeTableCourse>() {
		@Override
		public MyTimeTableCourse createFromParcel(Parcel in) {
			return new MyTimeTableCourse(in);
		}

		@Override
		public MyTimeTableCourse[] newArray(int size) {
			return new MyTimeTableCourse[size];
		}
	};


	@SerializedName("studyCourse")
	private TimeTableStudyCourseVo studyCourse;

	@SerializedName("semester")
	private TimeTableSemesterVo semester;

	@SerializedName("studyGroup")
	private TimeTableStudyGroupVo studyGroup;

	@SerializedName("eventWeek")
	private TimeTableWeekVo eventWeek;

	@SerializedName("eventDay")
	private TimeTableDayVo eventDay;

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
		return this.getStudyCourse().getTitle() + "-->"
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

