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
package de.fhe.fhemobile.vos.timetable;

import static de.fhe.fhemobile.utils.Utils.correctUmlauts;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlatDataStructure implements Parcelable {
	private static final String TAG = "FlatDataStructure";

	public FlatDataStructure(){
	}

	private FlatDataStructure(final Parcel in) {
		course = in.readParcelable(FlatStudyCourse.class.getClassLoader());
		semester = in.readParcelable(FlatTerms.class.getClassLoader());
		studyGroup = in.readParcelable(StudyGroupVo.class.getClassLoader());
		eventWeek = in.readParcelable(FlatTimeTableWeek.class.getClassLoader());
		eventDay = in.readParcelable(FlatTimeTableDay.class.getClassLoader());
		event = in.readParcelable(TimeTableEventVo.class.getClassLoader());
		in.readStringList(sets);
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

	@Override
	public int describeContents() {
		return 0;
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

	public FlatDataStructure copy(){
		final FlatDataStructure copy = new FlatDataStructure();
		//		Log.d(TAG, "FlatDataStructure: incID: "+FlatDataStructure.incId);
		copy.id = FlatDataStructure.incId++;
		copy.setCourse(this.getCourse());
		copy.setSemester(this.getSemester());
		copy.setStudyGroup(this.getStudyGroup());
		copy.setSets(new ArrayList<>(this.getSets()));
		return copy;
	}

	// "Bachelor: E-Commerce-->EC(BA)7-->EC(BA)7.01-->45-->3-->SPLUSA3E2FAs-2-->WI(BA)ERP-Sys.GPA/P/S/01"
	@SuppressWarnings("Annotator")
	private final static Pattern p = Pattern.compile("^(.*[a-z|A-Z|ä|Ä|ü|Ü|ö|Ö|ß])"); //$NON-NLS
	public static String cutEventTitle(final String title) {

		// WI/WIEC(BA)Cloudtech./V/01
		String changeEventTitle = correctUmlauts(title);
		try {
			final Matcher m = p.matcher(title);
			if (m.find() ) {
				changeEventTitle = m.group(1);
//				Log.d(TAG, "eventTitle: "+changeEventTitle);
			}
			else {
				changeEventTitle = title;
			}

		}
		catch (final Exception e){
			Log.e(TAG, "onResponse: ",e );
		}
		// WI/WIEC(BA)Cloudtech./V
		return changeEventTitle;
	}


	public static List<FlatDataStructure> queryGetEventsByEventTitle(final List<FlatDataStructure> list, final String eventTitle){

		final List<FlatDataStructure> filteredEvents = new ArrayList<>();
		for (final FlatDataStructure event : list) {
			if(event.getEvent().getTitle().contains(eventTitle)){
				filteredEvents.add(event);
			}
		}
		return filteredEvents;
	}

	public static List<FlatDataStructure> queryGetEventsByStudyGroupTitle(final List<FlatDataStructure>list, final String studyGroupTitle){
		final List<FlatDataStructure> filteredEvents = new ArrayList<>();
		for(final FlatDataStructure event : list){
			if(event.getSetString().equals(studyGroupTitle)){
				filteredEvents.add(event);
			}
		}
		return filteredEvents;
	}

	//private final static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy H:mm");
	private final static DateFormat sdf = SimpleDateFormat.getDateTimeInstance();

	public static List<FlatDataStructure> queryfutureEvents(final List<FlatDataStructure>list){

		final List<FlatDataStructure> filteredEvents = new ArrayList<>();
		for(final FlatDataStructure event : list){

			Date eventDate = null;
			try {
				eventDate= sdf.parse(event.getEvent().getDate()+" "+event.getEvent().getStartTime());
			} catch (final ParseException e) {
				Log.e(TAG, "Fehler beim Parsen der Daten: ",e );
			}
			if(eventDate.after(new Date())){
				filteredEvents.add(event);
			}
		}
		return filteredEvents;
	}

	public static final FlatDataStructure getEventByID (final List<FlatDataStructure> list, final String ID){
		for ( final FlatDataStructure event:list ) {
			if(ID.equals(event.getEvent().getUid())){
				return event;
			}
		}
		return null;
	}

//In dieser Funktion werden die Daten auf folgende Struktur gebracht:
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

	public final static boolean listContainsEvent(final List<FlatDataStructure> list, final FlatDataStructure data){
		for(FlatDataStructure event:list){
//			Log.d(TAG, "Eventvergleich1: "+event);
//			Log.d(TAG, "Eventvergleich2: "+data);
//			Log.d(TAG, "listContainsEvent: "+event.getEvent().getTitle()+" "+data.getEvent().getTitle());
//			Log.d(TAG, "listContainsEvent: "+event.getEventDay().getDayInWeek()+" "+data.getEventDay().getDayInWeek());
//			Log.d(TAG, "listContainsEvent: "+event.getEventWeek().getWeekInYear()+" "+data.getEventWeek().getWeekInYear());
//			Log.d(TAG, "listContainsEvent: "+event.getStudyGroup().getTimeTableId()+" "+data.getStudyGroup().getTimeTableId());
//			Log.d(TAG, "listContainsEvent: "+event.getSemester().getId()+" "+data.getSemester().getId());
//			Log.d(TAG, "listContainsEvent: "+event.getCourse().getId()+" "+data.getCourse().getId());
			if(event.getEvent().getTitle().equals(data.getEvent().getTitle())){
//				Log.d(TAG, "EventTitle: true");
				if(event.getEventDay().getDayInWeek().equals(data.getEventDay().getDayInWeek())){
//					Log.d(TAG, "EventDay: true");
					if(event.getEventWeek().getWeekInYear()==data.getEventWeek().getWeekInYear()){
//						Log.d(TAG, "EventWeek: true");
						if(event.getStudyGroup().getTimeTableId().equals(data.getStudyGroup().getTimeTableId())){
//							Log.d(TAG, "StudyGroup: true");
							if(event.getSemester().getId().equals(data.getSemester().getId())){
//								Log.d(TAG, "Semester: true");
								if(event.getCourse().getId().equals(data.getCourse().getId())){
//									Log.d(TAG, "Course: true");
									return true;
								}
							}
						}
					}
				}

			}
		}
//		Log.d(TAG, "listContainsEvent: Contains not!");
		return false;
	}

	public final FlatStudyCourse getCourse() {
		return course;
	}

	public final FlatTerms getSemester() {
		return semester;
	}

	public final StudyGroupVo getStudyGroup() {
		return studyGroup;
	}

	private FlatTimeTableWeek getEventWeek() {
		return eventWeek;
	}

	private FlatTimeTableDay getEventDay() {
		return eventDay;
	}

	public final TimeTableEventVo getEvent() {
		return event;
	}

	public final FlatDataStructure setCourse(final StudyCourseVo course) {
		this.course = new FlatStudyCourse();
		this.course.setId(course.getId());
		this.course.setTitle(course.getTitle());
		return this;
	}
	private void setCourse(final FlatStudyCourse course) {
		this.course = course;
	}

	public final FlatDataStructure setSemester(final TermsVo semester) {
		this.semester = new FlatTerms();
		this.semester.setId(semester.getId());
		this.semester.setTitle(semester.getTitle());
		return this;
	}
	private void setSemester(final FlatTerms semester) {
		this.semester = semester;
	}


	public final FlatDataStructure setStudyGroup(final StudyGroupVo studyGroup) {
		this.studyGroup = studyGroup;
		return this;
	}

	public final FlatDataStructure setEventWeek(final TimeTableWeekVo eventWeek) {
		this.eventWeek = new FlatTimeTableWeek();
		this.eventWeek.setWeekInYear(eventWeek.getWeekInYear());
		this.eventWeek.setYear(eventWeek.getYear());
		return this;
	}
	public final FlatDataStructure setEventWeek(final FlatTimeTableWeek eventWeek) {
		this.eventWeek = eventWeek;
		return this;
	}

	public final FlatDataStructure setEventDay(final TimeTableDayVo eventDay) {
		this.eventDay = new FlatTimeTableDay();
		this.eventDay.setDayInWeek(eventDay.getDayInWeek());
		this.eventDay.setName(eventDay.getName());
		return this;
	}
	public final FlatDataStructure setEventDay(final FlatTimeTableDay eventDay) {
		this.eventDay = eventDay;
		return this;
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

	private static int incId=0;
	private int id;
	@SerializedName("course")
	private FlatStudyCourse course;
	@SerializedName("semester")
	private FlatTerms semester;
	@SerializedName("studyGroup")
	private StudyGroupVo studyGroup;
	@SerializedName("eventWeek")
	private FlatTimeTableWeek eventWeek;
	@SerializedName("eventDay")
	private FlatTimeTableDay eventDay;
	@SerializedName("event")
	private TimeTableEventVo event;



	@SerializedName("sets")
	private List<String> sets=new ArrayList<>();

	public final int getId() {
		return id;
	}
	private boolean visible = false;
	private boolean added = false;

	public void setVisible(final boolean visible) {
		this.visible = visible;
	}

	public final boolean isVisible() {
		return visible;
	}

	public boolean isAdded() {
		return added;
	}

	public void setAdded(boolean added) {
		this.added = added;
	}

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
}
