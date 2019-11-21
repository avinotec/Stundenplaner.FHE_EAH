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
package de.fhe.fhemobile.comparator;

import org.junit.Assert;

import java.util.Comparator;

import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class LessonTitle_StudyGroupTitle_Comparator implements Comparator<FlatDataStructure> {
	private static final String TAG = "LessonTitle_StudyGroupT";

	private final static int GREATER=1;
	private final static int EQUAL=0;
	private final static int LESSER=-1;

	private static final LessonTitle_StudyGroupTitle_Comparator lessonTitle_StudyGroupTitle_Comparator = new LessonTitle_StudyGroupTitle_Comparator();

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

		final String studyGroupTitle1 = o1.getSetString();
		final String studyGroupTitle2 = o2.getSetString();
		Assert.assertTrue( studyGroupTitle1 != null );
		Assert.assertTrue( studyGroupTitle2 != null );
		//Comperator wird auch ausgeführt, wenn keine elemente vorhanden sind.
//		Assert.assertTrue( studyGroupTitle1.length() > 1 );
//		Assert.assertTrue( studyGroupTitle2.length() > 1 );

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
			final int studyGroupCompareResult = studyGroupTitle1.compareTo(studyGroupTitle2);
//			Log.d(TAG, "compare GroupID1: "+studyGroupTitle1+" GroupID2"+studyGroupTitle2+" result: " +studyGroupCompareResult);

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
