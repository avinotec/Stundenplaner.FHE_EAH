package de.fhe.fhemobile.vos.timetable;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class FlatDataStructure {
	private static final String TAG = "FlatDataStructure";



	public FlatDataStructure() {
	}
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
	public void select(){
		selected = true;
	}
	public void unselect(){
		selected = false;
	}

	public boolean isSelected() {
		return selected;
	}

	private static List<FlatDataStructure> queryGetEventsByEventTitle(List<FlatDataStructure> list, String eventTitle){
		List<FlatDataStructure> filteredEvents = new ArrayList<>();
		for (FlatDataStructure event : list) {
			if(event.getEvent().getTitle().equals(eventTitle)){
				filteredEvents.add(event);
			}
		}
		return filteredEvents;
	}
	private static List<FlatDataStructure> queryGetEventsByStudyGroupTitle(List<FlatDataStructure>list, String studyGroupTitle){
		List<FlatDataStructure> filteredEvents = new ArrayList<>();
		for(FlatDataStructure event : list){
			if(event.getStudyGroup().getTitle().equals(studyGroupTitle)){
				filteredEvents.add(event);
			}
		}
		return filteredEvents;
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
	public static void groupEvents(List<List<List<FlatDataStructure>>> list, FlatDataStructure data){


		Log.d(TAG, "groupEvents id:"+data.getId());
		//Wenn die Liste komplett leer ist, erstelle die Struktur für das erste Element
		if(list.size()==0){
			Log.d(TAG, "Vergleich : First New List: "+data.getEvent().getTitle());
			List<List<FlatDataStructure>>eventList = new ArrayList<>();
			List<FlatDataStructure> studygroupList = new ArrayList<>();

			studygroupList.add(data);
			eventList.add(studygroupList);
			list.add(eventList);

			return;
		}else{
			for(List<List<FlatDataStructure>>eventList:list){
				if(eventList.size()>=1){
					Log.d(TAG, "event Vergleich: "+eventList.get(0).get(0).getEvent().getTitle()+" : "+data.getEvent().getTitle());

					//Ist das Studienfach mit diesem Titel schon in der Liste?
					if(eventList.get(0).get(0).getEvent().getTitle().equals(data.getEvent().getTitle())){
						for(List<FlatDataStructure> studygroupList:eventList) {
							if(studygroupList.size()>=1){
								Log.d(TAG, "groupEvents Vergleich: " + studygroupList.get(0).getStudyGroup().getTitle() + " : " + data.getStudyGroup().getTitle());

								//Ist das Set mit der ID schon in der Liste?
								if (studygroupList.get(0).getStudyGroup().getTimeTableId().equals(data.getStudyGroup().getTimeTableId())) {

									//Studienfach und SetID gefunden, füge den Datensatz hinein.
									studygroupList.add(data);
									return;
								}
							}
						}
						//SetID wurde nicht gefunden --> Erstelle Liste für das Set und lege den Datensatz hinein.
						Log.d(TAG, "Vergleich : Neue Studygroup");
						List<FlatDataStructure> newStudygroupList=new ArrayList<>();
						newStudygroupList.add(data);
						eventList.add(newStudygroupList);
						return;
					}

				}

			}
			//Studienfach nicht gefunden, Erstelle eine Liste für das Fach und darin eine Liste für das Set. Füge alles inklusive dem Datensatz zusammen.
			Log.d(TAG, "Vergleich : Neuer Event: "+data.getEvent().getTitle());
			List<List<FlatDataStructure>> newEventList= new ArrayList<>();
			List<FlatDataStructure> newStudygroupList = new ArrayList<>();

			newStudygroupList.add(data);
			newEventList.add(newStudygroupList);
			list.add(newEventList);

			return;
		}

	}

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
	private StudyCourseVo course;
	private TermsVo semester;
	private StudyGroupVo studyGroup;
	private TimeTableWeekVo eventWeek;
	private TimeTableDayVo eventDay;
	private TimeTableEventVo event;
	private boolean selected = false;

	public int getId() {
		return id;
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
