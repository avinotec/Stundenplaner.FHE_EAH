package de.fhe.fhemobile.comparator;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;

public class TimeTableEventComparator implements Comparator<TimeTableEventVo> {

    private static final String TAG = "TimeTableEventCompare";

    //don't use SimpleDateFormat.getDateTimeInstance() because it includes seconds
    final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy H:mm");


    @Override
    public int compare(TimeTableEventVo o1, TimeTableEventVo o2) {
//        try{
//            Date eventDate = sdf.parse(o1.getDate()+" "+o1.getStartTime());
//            Date otherEventDate = sdf.parse(o2.getDate()+" "+o2.getStartTime());
//            return eventDate.compareTo(otherEventDate);
//
//        } catch (ParseException e){
//            Log.e(TAG, "Error parsing TimeTableEvent Dates", e);
//            return 0;
//        }
        return 0;
    }
}
