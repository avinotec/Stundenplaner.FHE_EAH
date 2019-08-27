package de.fhe.fhemobile.vos.timetable;

import java.util.ArrayList;
import java.util.List;

public class FlatDataStructure {
	public FlatDataStructure() {

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
	public static boolean listContainsEvent(List<FlatDataStructure> list, FlatDataStructure data){
		for(FlatDataStructure event:list){
			if(event.getEvent().getUid()==data.getEvent().getUid()){
				if(event.getEventDay()==data.getEventDay()){
					if(event.getEventWeek()==data.getEventWeek()){
						if(event.getStudyGroup().getTimeTableId()==data.getStudyGroup().getTimeTableId()){
							if(event.getSemester().getId()==data.getSemester().getId()){
								if(event.getCourse().getId()==data.getCourse().getId()){
									return true;
								}
							}
						}
					}
				}

			}
		}
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

	private StudyCourseVo course;
	private TermsVo semester;
	private StudyGroupVo studyGroup;
	private TimeTableWeekVo eventWeek;
	private TimeTableDayVo eventDay;
	private TimeTableEventVo event;
	private boolean selected = false;


}
