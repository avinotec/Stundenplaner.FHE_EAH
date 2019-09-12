package de.fhe.fhemobile.comparator;

import java.util.Comparator;

import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class LessonTitle_StudyGroupTitle_Comparator implements Comparator<FlatDataStructure> {
	private static final String TAG = "LessonTitle_StudyGroupT";
	private final static int GREATER=1;
	private final static int EQUAL=0;
	private final static int LESSER=-1;
	@Override
	public int compare(FlatDataStructure o1, FlatDataStructure o2) {
		final String lessonTitle1=o1.getEvent().getTitle();
		final String lessonTitle2=o2.getEvent().getTitle();

		final String studyGroupTitle1=o1.getStudyGroup().getTitle();
		final String studyGroupTitle2=o2.getStudyGroup().getTitle();

		//Vergleiche den CourseTitel vom ersten element mit dem CourseTitel des zweiten Elements.
		//Ist der erste Titel "größer" wird 1 zurückgegeben, ist der zweite Titel größer wird -1 zurückgegeben
		//und sind beide gleich, wird 0 zurückgegeben.
		//Bei unterschiedlichen Titeln lass einfach sortieren, bei gleichem Titel sortiere noch nach StudyGroupTitle
		int courseCompareResult=lessonTitle1.compareTo(lessonTitle2);
		int studyGroupCompareResult=studyGroupTitle1.compareTo(studyGroupTitle2);
		if(courseCompareResult>=0){
			return GREATER;
		}else if(courseCompareResult<=0){
			return LESSER;
		}
		else{

			if(studyGroupCompareResult>=GREATER){
				return GREATER;
			}else if(studyGroupCompareResult<=LESSER){
				return LESSER;
			}else {
				return EQUAL;
			}
		}
	}
}
