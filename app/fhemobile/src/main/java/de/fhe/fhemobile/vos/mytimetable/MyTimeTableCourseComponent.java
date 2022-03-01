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

import static de.fhe.fhemobile.Main.getSubscribedCourseComponents;
import static de.fhe.fhemobile.utils.mytimetable.MyTimeTableUtils.getCourseComponentName;
import static de.fhe.fhemobile.utils.mytimetable.MyTimeTableUtils.getCourseName;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.fhe.fhemobile.comparator.TimeTableEventComparator;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;
import de.fhe.fhemobile.vos.timetable.TimeTableSemesterVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyGroupVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyProgramVo;


/**
 * Class stores information of course for a certain subset of study groups
 * (example: math training is on Monday for groups 1 to 5 and on Tuesday for groups 6 to 10
 * -> each subset creates a new MyTimeTableCourseComponent)
 *
 * by Nadja - 01/2022
 */
public class MyTimeTableCourseComponent implements Parcelable {

	private static final String TAG = "MyTimeTableCourseComponent";

	public MyTimeTableCourseComponent(){
		//empty constructor needed
	}


	public MyTimeTableCourseComponent(
									  //final TimeTableStudyProgramVo studyProgram, //currently not used - Nadja 02/2022
									  //final TimeTableSemesterVo semester, //currently not used - Nadja 02/2022
									  final TimeTableEventVo event,
									  final String studyGroup,
									  final boolean subscribed) {
		this.mTitle = getCourseComponentName(event);
		this.mCourse = getCourseName(event);
		//this.studyProgram = studyProgram; //currently not used - Nadja 02/2022
		//this.semester = semester; //currently not used - Nadja 02/2022
		events.add(event);
		studyGroups.add(studyGroup);
		this.subscribed = subscribed;
	}

	/**
	 * Creates a new instance of {@link MyTimeTableCourseComponent}
	 * and automatically sets the subscribed status
	// * @param studyProgram study program the course belongs to
	 //* @param semester semester the course belongs to
	 * @param event the event to initialize the event list with
	 * @param studyGroup the study group the course is dedicated to and that to initialize the study group list with
	 */
	public MyTimeTableCourseComponent(
								      //final TimeTableStudyProgramVo studyProgram, //currently not used - Nadja 02/2022
									  //final TimeTableSemesterVo semester, //currently not used - Nadja 02/2022
									  final TimeTableEventVo event,
									  final TimeTableStudyGroupVo studyGroup) {
		this.mTitle = getCourseComponentName(event.getTitle());
		this.mCourse = getCourseName(event);
		//this.studyProgram = studyProgram; //currently not used - Nadja 02/2022
		//this.semester = semester; //currently not used - Nadja 02/2022
		events.add(event);
		studyGroups.add(studyGroup.getShortTitle());

		checkAndSetSubscribed();
	}


	public String getTitle() { return mTitle; }

	public void setTitle(String title) {
		this.mTitle = getCourseComponentName(title);
	}

	public String getCourse() { return mCourse; }

	//currently not used - Nadja 02/2022
	//public final TimeTableStudyProgramVo getStudyProgram() { return studyProgram; }

	//currently not used - Nadja 02/2022
	//public final TimeTableSemesterVo getSemester() { return semester; }

	public List<TimeTableEventVo> getEvents() { return events; }

	public TimeTableEventVo getFirstEvent() {  return events.size() > 0 ? events.get(0) : null; }

	//currently not used - Nadja 02/2022
	//public final void setStudyProgram(final TimeTableStudyProgramVo course) { this.studyProgram = course; }

	//currently not used - Nadja 02/2022
	//public final void setSemester(final TimeTableSemesterVo semester) { this.semester = semester; }

	public void setEvents(List<TimeTableEventVo> events) {
		this.events = events;
		Collections.sort(this.events, new TimeTableEventComparator());
	}

	public void addEvent(TimeTableEventVo event) {
		this.events.add(event);
		Collections.sort(this.events, new TimeTableEventComparator());
	}

	public List<String> getStudyGroups() { return studyGroups; }

	public void setStudyGroups(final List<String> studyGroups) { this.studyGroups = studyGroups; }

	public void addStudyGroup(final TimeTableStudyGroupVo studyGroup) {
		this.studyGroups.add(studyGroup.getShortTitle());
	}

	public final int getId() {
		return id;
	}

	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(final boolean added) {
		this.subscribed = added;
	}

	public boolean containsEvent(TimeTableEventVo _Event){
		for(TimeTableEventVo event : events){
			if(event.getTitle().equals(_Event.getTitle())
					&& event.getFullDateWithStartTime() == _Event.getFullDateWithStartTime()
					&& event.getEndTime().equals(_Event.getEndTime())
					&& event.getRoom().equals(_Event.getRoom())){
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if to components belong to the same course
	 * @param other the {@link MyTimeTableCourseComponent} to compare to
	 * @return true if components belong to the same course
	 */
	public boolean isSameCourse(final MyTimeTableCourseComponent other){
		return mCourse.equals(other.getCourse());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MyTimeTableCourseComponent)) return false;
		MyTimeTableCourseComponent that = (MyTimeTableCourseComponent) o;
		return mTitle.equals(that.mTitle);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[]{mTitle});
	}

	public void checkAndSetSubscribed(){
		//check if this course belongs to a subscribed course
		for(MyTimeTableCourseComponent subscribedCourseComponent : getSubscribedCourseComponents()){
			if (this.equals(subscribedCourseComponent)){

				this.setSubscribed(true);
			}
		}
	}

	public MyTimeTableCourseComponent copy(){
		final MyTimeTableCourseComponent copy = new MyTimeTableCourseComponent();
		copy.id = MyTimeTableCourseComponent.incId++;

		//currently not used - Nadja 02/2022
		/*copy.setStudyProgram(this.getStudyProgram());
		copy.setSemester(this.getSemester());*/

		copy.setStudyGroups(this.getStudyGroups());
		copy.setSubscribed(this.subscribed);
		return copy;
	}

	/**
	 * Adds all names of the study groups list to one long string
	 * @return study group list as string
	 */
	public String getStudyGroupListString(){
		Collections.sort(studyGroups);
		StringBuilder combinedStudyGroups = new StringBuilder();

		if(studyGroups.size() > 0) {
			for (String studyGroupTitle : studyGroups) {

				//reduce title e.g. "08(KMT)" to numerical
				studyGroupTitle = studyGroupTitle.replaceAll("\\D","");

				combinedStudyGroups.append(studyGroupTitle).append(", ");
			}
			return combinedStudyGroups.substring(0, combinedStudyGroups.length() - 2);
		}
		else{
			return "";
		}
	}


	// PARCELABLE --------------------------------------------------------------------------------

	private MyTimeTableCourseComponent(final Parcel in) {
		mTitle = in.readString();
		mCourse = in.readString();
		//currently not used - Nadja 02/2022
		/*studyProgram = in.readParcelable(TimeTableStudyProgramVo.class.getClassLoader());
		semester = in.readParcelable(TimeTableSemesterVo.class.getClassLoader()); */

		studyGroups = new ArrayList<>();
		int sizeStudyGroups = in.readInt();
		for(int i = 0; i < sizeStudyGroups; i ++){
			studyGroups.add(in.readString());
		}

		events = new ArrayList<>();
		int sizeEvents = in.readInt();
		for(int i = 0; i < sizeEvents; i ++){
			events.add(in.readParcelable(TimeTableEventVo.class.getClassLoader()));
		}

		subscribed = in.readByte() != 0;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(mTitle);
		dest.writeString(mCourse);

		//currently not used - Nadja 02/2022
		/*dest.writeParcelable(studyProgram, flags);
		dest.writeParcelable(semester, flags); */

		//sizes needed for reading parcelable
		dest.writeInt(studyGroups.size());
		for(String studyGroup : studyGroups){
			dest.writeString(studyGroup);
		}
		dest.writeInt(events.size());
		for(TimeTableEventVo event : events){
			dest.writeParcelable(event, flags);
		}

		dest.writeByte((byte) (subscribed ? 1 : 0));

	}

	public static final Creator<MyTimeTableCourseComponent> CREATOR = new Creator<MyTimeTableCourseComponent>() {
		@Override
		public MyTimeTableCourseComponent createFromParcel(Parcel in) {
			return new MyTimeTableCourseComponent(in);
		}

		@Override
		public MyTimeTableCourseComponent[] newArray(int size) {
			return new MyTimeTableCourseComponent[size];
		}
	};


	@SerializedName("title")
	private String mTitle;

	@SerializedName("course")
	private String mCourse;

	//currently not used - Nadja 02/2022
	/*@SerializedName("studyProgram")
	private TimeTableStudyProgramVo studyProgram; */

	//currently not used - Nadja 02/2022
	/*@SerializedName("semester")
	private TimeTableSemesterVo semester; */

	@SerializedName("studyGroups")
	private List<String> studyGroups = new ArrayList<>();

	@SerializedName("events")
	private List<TimeTableEventVo> events = new ArrayList<>();

	@SerializedName("subscribed")
	private boolean subscribed = false;


	private static int incId = 0;
	private int id;

}

