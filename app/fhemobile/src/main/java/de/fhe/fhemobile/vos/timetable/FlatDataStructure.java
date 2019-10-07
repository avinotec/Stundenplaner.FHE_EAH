package de.fhe.fhemobile.vos.timetable;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlatDataStructure implements Parcelable {
	private static final String TAG = "FlatDataStructure";



	public FlatDataStructure(){
	}


	protected FlatDataStructure(Parcel in) {
		course = in.readParcelable(StudyCourseVo.class.getClassLoader());
		semester = in.readParcelable(TermsVo.class.getClassLoader());
		studyGroup = in.readParcelable(StudyGroupVo.class.getClassLoader());
		eventWeek = in.readParcelable(TimeTableWeekVo.class.getClassLoader());
		eventDay = in.readParcelable(TimeTableDayVo.class.getClassLoader());
		event = in.readParcelable(TimeTableEventVo.class.getClassLoader());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(course, flags);
		dest.writeParcelable(semester, flags);
		dest.writeParcelable(studyGroup, flags);
		dest.writeParcelable(eventWeek, flags);
		dest.writeParcelable(eventDay, flags);
		dest.writeParcelable(event, flags);
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
		FlatDataStructure copy = new FlatDataStructure();
		FlatDataStructure.incId++;
		Log.d(TAG, "FlatDataStructure: incID: "+FlatDataStructure.incId);
		copy.id=incId;
		copy.setCourse(this.getCourse());
		copy.setSemester(this.getSemester());
		copy.setStudyGroup(this.getStudyGroup());
		return copy;
	}

	public static String cutEventTitle(String title){
		String changeEventTitle="";
		try {
			Pattern p = Pattern.compile("^(.*[a-z|A-Z|ä|Ä|ü|Ü|ö|Ö|ß])");
			Matcher m = p.matcher(title);
			if(m.find()){
				changeEventTitle =m.group(1);
				Log.d(TAG, "eventTitle: "+changeEventTitle);
			}
			else {
				changeEventTitle = title;
			}

		}
		catch (Exception e){
			changeEventTitle = title;
			Log.e(TAG, "onResponse: ",e );
		}
		return changeEventTitle;
	}


	public static List<FlatDataStructure> queryGetEventsByEventTitle(List<FlatDataStructure> list, String eventTitle){
		List<FlatDataStructure> filteredEvents = new ArrayList<>();
		for (FlatDataStructure event : list) {
			if(event.getEvent().getTitle().contains(eventTitle)){
				filteredEvents.add(event);
			}
		}
		return filteredEvents;
	}
	public static List<FlatDataStructure> queryGetEventsByStudyGroupTitle(List<FlatDataStructure>list, String studyGroupTitle){
		List<FlatDataStructure> filteredEvents = new ArrayList<>();
		for(FlatDataStructure event : list){
			if(event.getStudyGroup().getTitle().equals(studyGroupTitle)){
				filteredEvents.add(event);
			}
		}
		return filteredEvents;
	}

	public static List<FlatDataStructure> queryfutureEvents(List<FlatDataStructure>list){
		List<FlatDataStructure> filteredEvents = new ArrayList<>();
		for(FlatDataStructure event : list){
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy H:mm");
			Date eventDate = null;
			try {
				eventDate= sdf.parse(event.getEvent().getDate()+" "+event.getEvent().getStartTime());
			} catch (ParseException e) {
				Log.e(TAG, "Fehler beim Parsen der Daten: ",e );
			}
			if(eventDate.after(new Date())){
				filteredEvents.add(event);
			}
		}
		return filteredEvents;
	}

	public static FlatDataStructure getEventByID (List<FlatDataStructure> list, String ID){
		for(FlatDataStructure event:list){
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
	public static int counter=0;


	public static boolean listContainsEvent(List<FlatDataStructure> list, FlatDataStructure data){
		for(FlatDataStructure event:list){
			Log.d(TAG, "Eventvergleich1: "+event);
			Log.d(TAG, "Eventvergleich2: "+data);
//			Log.d(TAG, "listContainsEvent: "+event.getEvent().getTitle()+" "+data.getEvent().getTitle());
//			Log.d(TAG, "listContainsEvent: "+event.getEventDay().getDayInWeek()+" "+data.getEventDay().getDayInWeek());
//			Log.d(TAG, "listContainsEvent: "+event.getEventWeek().getWeekInYear()+" "+data.getEventWeek().getWeekInYear());
//			Log.d(TAG, "listContainsEvent: "+event.getStudyGroup().getTimeTableId()+" "+data.getStudyGroup().getTimeTableId());
//			Log.d(TAG, "listContainsEvent: "+event.getSemester().getId()+" "+data.getSemester().getId());
//			Log.d(TAG, "listContainsEvent: "+event.getCourse().getId()+" "+data.getCourse().getId());
			if(event.getEvent().getTitle().equals(data.getEvent().getTitle())){
				Log.d(TAG, "EventTitle: true");
				if(event.getEventDay().getDayInWeek().equals(data.getEventDay().getDayInWeek())){
					Log.d(TAG, "EventDay: true");
					if(event.getEventWeek().getWeekInYear()==data.getEventWeek().getWeekInYear()){
						Log.d(TAG, "EventWeek: true");
						if(event.getStudyGroup().getTimeTableId().equals(data.getStudyGroup().getTimeTableId())){
							Log.d(TAG, "StudieGroup: true");
							if(event.getSemester().getId().equals(data.getSemester().getId())){
								Log.d(TAG, "Semester: true");
								if(event.getCourse().getId().equals(data.getCourse().getId())){
									Log.d(TAG, "Course: true");
									return true;
								}
							}
						}
					}
				}

			}
		}
		Log.d(TAG, "listContainsEvent: Contains not!");
		return false;
	}

	public StudyCourseVo getCourse() {
		return course;
	}

	public TermsVo getSemester() {
		return semester;
	}

	public StudyGroupVo getStudyGroup() {
		return studyGroup;
	}

	public TimeTableWeekVo getEventWeek() {
		return eventWeek;
	}

	public TimeTableDayVo getEventDay() {
		return eventDay;
	}

	public TimeTableEventVo getEvent() {
		return event;
	}

	public FlatDataStructure setCourse(StudyCourseVo course) {
		this.course = course;
		return this;
	}

	public FlatDataStructure setSemester(TermsVo semester) {
		this.semester = semester;
		return this;
	}

	public FlatDataStructure setStudyGroup(StudyGroupVo studyGroup) {
		this.studyGroup = studyGroup;
		return this;
	}

	public FlatDataStructure setEventWeek(TimeTableWeekVo eventWeek) {
		this.eventWeek = eventWeek;
		return this;
	}

	public FlatDataStructure setEventDay(TimeTableDayVo eventDay) {
		this.eventDay = eventDay;
		return this;
	}

	public FlatDataStructure setEvent(TimeTableEventVo event) {
		this.event = event;
		return this;
	}

	private static int incId=0;
	private int id;
	@SerializedName("course")
	private StudyCourseVo course;
	@SerializedName("semester")
	private TermsVo semester;
	@SerializedName("studyGroup")
	private StudyGroupVo studyGroup;
	@SerializedName("eventWeek")
	private TimeTableWeekVo eventWeek;
	@SerializedName("eventDay")
	private TimeTableDayVo eventDay;
	@SerializedName("event")
	private TimeTableEventVo event;





	public int getId() {
		return id;
	}
	private boolean visible = false;

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	@NonNull
	@Override
	public String toString() {
		return this.getCourse().getTitle() + "-->"
				+ this.getSemester().getTitle() + "-->"
				+ this.getStudyGroup().getTitle() + "-->"
				+ this.getEventWeek().getWeekInYear() + "-->"
				+ this.getEventDay().getDayInWeek() + "-->"
				+ this.getEvent().getUid() + "-->"
				+ this.getEvent().getTitle();
	}


}
