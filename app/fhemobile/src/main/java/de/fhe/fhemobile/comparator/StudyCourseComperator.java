package de.fhe.fhemobile.comparator;

import java.util.Comparator;

import de.fhe.fhemobile.vos.timetable.StudyCourseVo;

public class StudyCourseComperator implements Comparator <StudyCourseVo> {
	@Override
	public int compare(StudyCourseVo t1, StudyCourseVo t2) {
		return t1.getTitle().compareTo(t2.getTitle());
	}
}
