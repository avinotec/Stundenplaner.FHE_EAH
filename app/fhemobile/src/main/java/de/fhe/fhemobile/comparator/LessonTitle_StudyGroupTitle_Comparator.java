package de.fhe.fhemobile.comparator;

import org.junit.Assert;

import java.util.Comparator;

import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class LessonTitle_StudyGroupTitle_Comparator implements Comparator<FlatDataStructure> {
	private static final String TAG = "LessonTitle_StudyGroupT";

	private final static int GREATER=1;
	private final static int EQUAL=0;
	private final static int LESSER=-1;

	private static LessonTitle_StudyGroupTitle_Comparator lessonTitle_StudyGroupTitle_Comparator = new LessonTitle_StudyGroupTitle_Comparator();

	public static int compareStatic(final FlatDataStructure o1, final FlatDataStructure o2) {
		return lessonTitle_StudyGroupTitle_Comparator.compare(o1, o2);
	}

	/**
	 * Similar to {@link java.util.Comparator#compare(Object, Object)}, should compare two and
	 * return how they should be ordered.
	 *
	 * @param o1 The first object to compare.
	 * @param o2 The second object to compare.
	 *
	 * @return a negative integer, zero, or a positive integer as the
	 * first argument is less than, equal to, or greater than the
	 * second.
	 */
	@Override
	public int compare(final FlatDataStructure o1, final FlatDataStructure o2) {

		final String title1 = o1.getEvent().getTitle();
		final String title2 = o2.getEvent().getTitle();
		final String lessonTitle1 = FlatDataStructure.cutEventTitle(title1);
		final String lessonTitle2 = FlatDataStructure.cutEventTitle(title2);
		Assert.assertTrue( lessonTitle1 != null );
		Assert.assertTrue( lessonTitle2 != null );
		Assert.assertTrue( lessonTitle1.length() > 3 );
		Assert.assertTrue( lessonTitle2.length() > 3 );

		final String studyGroupID1 = o1.getStudyGroup().getTimeTableId();
		final String studyGroupID2 = o2.getStudyGroup().getTimeTableId();
		Assert.assertTrue( studyGroupID1 != null );
		Assert.assertTrue( studyGroupID2 != null );
		Assert.assertTrue( studyGroupID1.length() > 3 );
		Assert.assertTrue( studyGroupID2.length() > 3 );

		//Vergleiche den CourseTitel vom ersten element mit dem CourseTitel des zweiten Elements.
		//Ist der erste Titel "größer" wird 1 zurückgegeben, ist der zweite Titel größer wird -1 zurückgegeben
		//und sind beide gleich, wird 0 zurückgegeben.
		//Bei unterschiedlichen Titeln lass einfach sortieren, bei gleichem Titel sortiere noch nach StudyGroupTitle
		final int courseCompareResult = lessonTitle1.compareTo(lessonTitle2);
//		Log.d(TAG, "compare Title1: "+lessonTitle1+" Title2:"+lessonTitle2 +" result: " +courseCompareResult);
		if (courseCompareResult > 0) {
			return GREATER;
		} else if(courseCompareResult < 0) {
			return LESSER;
		}
		else{
			final int studyGroupCompareResult = studyGroupID1.compareTo(studyGroupID2);
//			Log.d(TAG, "compare GroupID1: "+studyGroupID1+" GroupID2"+studyGroupID2+" result: " +studyGroupCompareResult);

			if(studyGroupCompareResult > 0){
				return GREATER;
			}else if(studyGroupCompareResult < 0){
				return LESSER;
			}else {
				return EQUAL;
			}
		}
	}
}
