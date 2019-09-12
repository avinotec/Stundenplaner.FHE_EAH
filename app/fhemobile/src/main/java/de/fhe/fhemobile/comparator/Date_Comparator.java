package de.fhe.fhemobile.comparator;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

public class Date_Comparator implements Comparator<FlatDataStructure> {
	private static final String TAG = "LessonTitle_StudyGroupT";
	private final static int GREATER=1;
	private final static int EQUAL=0;
	private final static int LESSER=-1;
	@Override
	public int compare(FlatDataStructure o1, FlatDataStructure o2) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy H:mm");
		Date lessonDate1 = null;
		Date lessonDate2 = null;
		try {
			lessonDate1= sdf.parse(o1.getEvent().getDate()+" "+o1.getEvent().getStartTime());
			lessonDate2= sdf.parse(o2.getEvent().getDate()+" "+o2.getEvent().getStartTime());
		} catch (ParseException e) {
			Log.e(TAG, "Fehler beim Parsen der Daten: ",e );
		}


		//Vergleiche das Datum vom ersten element mit dem Datum des zweiten Elements.
		//Ist das erste Datum "größer" wird 1 zurückgegeben, ist das zweite größer wird -1 zurückgegeben
		//und sind beide gleich, wird 0 zurückgegeben.
		int dateCompareResult=lessonDate1.compareTo(lessonDate2);
		if(dateCompareResult>=0){
			return GREATER;
		}else if(dateCompareResult<=0){
			return LESSER;
		}
		else{
			return EQUAL;
		}
	}
}
