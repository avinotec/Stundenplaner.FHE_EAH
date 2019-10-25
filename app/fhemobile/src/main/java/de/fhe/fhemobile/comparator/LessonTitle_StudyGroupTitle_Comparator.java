package de.fhe.fhemobile.comparator;

import android.util.Log;

import java.util.Comparator;

import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class LessonTitle_StudyGroupTitle_Comparator implements Comparator<FlatDataStructure> {
	private static final String TAG = "LessonTitle_StudyGroupT";
	private final static int GREATER=1;
	private final static int EQUAL=0;
	private final static int LESSER=-1;
	@Override
	public int compare(FlatDataStructure o1, FlatDataStructure o2) {
		final String lessonTitle1=FlatDataStructure.cutEventTitle(o1.getEvent().getTitle());
		final String lessonTitle2=FlatDataStructure.cutEventTitle(o2.getEvent().getTitle());

		final String studyGroupID1=o1.getStudyGroup().getTimeTableId();
		final String studyGroupID2=o2.getStudyGroup().getTimeTableId();

		//Vergleiche den CourseTitel vom ersten element mit dem CourseTitel des zweiten Elements.
		//Ist der erste Titel "größer" wird 1 zurückgegeben, ist der zweite Titel größer wird -1 zurückgegeben
		//und sind beide gleich, wird 0 zurückgegeben.
		//Bei unterschiedlichen Titeln lass einfach sortieren, bei gleichem Titel sortiere noch nach StudyGroupTitle
		int courseCompareResult=lessonTitle1.compareTo(lessonTitle2);
		Log.d(TAG, "compare Title1: "+lessonTitle1+" Title2:"+lessonTitle2 +" result: " +courseCompareResult);
		int studyGroupCompareResult=studyGroupID1.compareTo(studyGroupID2);
		Log.d(TAG, "compare GroupID1: "+studyGroupID1+" GroupID2"+studyGroupID2+" result: " +studyGroupCompareResult);
		if(courseCompareResult>0){
			return GREATER;
		}else if(courseCompareResult<0){
			return LESSER;
		}
		else{

			if(studyGroupCompareResult>GREATER){
				return GREATER;
			}else if(studyGroupCompareResult<LESSER){
				return LESSER;
			}else {
				return EQUAL;
			}
		}
	}
}
