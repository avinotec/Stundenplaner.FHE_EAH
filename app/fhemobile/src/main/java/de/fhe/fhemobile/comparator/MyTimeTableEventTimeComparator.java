package de.fhe.fhemobile.comparator;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventTimeVo;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;

public class MyTimeTableEventTimeComparator implements Comparator<MyTimeTableEventTimeVo> {

    @Override
    public int compare(MyTimeTableEventTimeVo o1, MyTimeTableEventTimeVo o2) {
        return Long.compare(o1.getStartDateTimeInSec(), o2.getStartDateTimeInSec());
    }
}
