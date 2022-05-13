package de.fhe.fhemobile.utils.mytimetable;

import static de.fhe.fhemobile.utils.Utils.correctUmlauts;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventDateVo;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventSeriesVo;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventSetVo;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventVo;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;

public class MyTimeTableUtils {

    private static final String TAG = "MyTimeTableUtils";

    /**
     * Returns the event title without the number of the time table entry.
     * (note: event titles have no such number if entered for the first time,
     * but later added events belonging to the same event series get one)
     *
     * @param title the title of an event that belongs to the {@link MyTimeTableEventSetVo}
     * @return string corresponding to the title that identifies a {@link MyTimeTableEventSeriesVo}
     */
    public static String getEventSeriesName(final String title){
        //cut away all ".d" (where d stands for any digit)
        return title.replaceAll("\\.\\d+$","");
    }

    /**
     * Get the title of an {@link MyTimeTableEventSeriesVo} without the group number
     * @param title
     * @return
     */
    public static String getEventSeriesBaseTitle(String title){
        return title.replaceAll("\\d\\d$","");
    }

    /**
     * Returns the name of the course which equals the event's name without numbers at the end
     * @param title the title of an event in the course
     * @return course name
     */
    public static String getCourseName(final String title){
        //cut away all "/dd.dd" (where d stands for any digit)
        return title.replaceAll("/\\d\\d(\\.\\d*)?$","");
    }



    public static String getEventTitleWithoutEndingNumbers(final String title) {

        //cuts away everything after the last letter (a-z|A-Z|ä|Ä|ü|Ü|ö|Ö|ß), which means that "/01.2" is cut
        final Pattern p = Pattern.compile("^(.*[a-zA-ZäÄüÜöÖß])"); //$NON-NLS

        String changeEventTitle = correctUmlauts(title);
        try {
            final Matcher m = p.matcher(title);
            if (m.find() ) {
                changeEventTitle = m.group(1);
            }
            else {
                changeEventTitle = title;
            }

        }
        catch (final Exception e){
            Log.e(TAG, "onResponse: ", e);
        }

        changeEventTitle = changeEventTitle.replaceAll("^[|)/]","");
        return changeEventTitle;
    }

    public static List<MyTimeTableEventSeriesVo> groupByEventTitle(Map<String, MyTimeTableEventSetVo> _EventSets){
        Map<String, MyTimeTableEventSeriesVo> eventSeriesMap = new HashMap<>();

        for(MyTimeTableEventSetVo eventSet : _EventSets.values()){

            //construct a MyTimeTableEventVo from each event date
            // and add it to the corresponding EventSeries
            for(MyTimeTableEventDateVo eventDate : eventSet.getEventDates()){

                //new EventVo
                MyTimeTableEventVo eventToAdd = new MyTimeTableEventVo(
                        eventSet.getTitle(),
                        eventDate.getStartDateTimeInSec(),
                        eventDate.getEndDateTimeInSec(),
                        eventSet.getLecturerList(),
                        eventSet.getLocationList());

                String seriesTitleOfEventSet = getEventSeriesName(eventSet.getTitle());
                //create new EventSeriesVo if necessary and add eventVo
                if(!eventSeriesMap.containsKey(seriesTitleOfEventSet)){

                    //new EventSeriesVo
                    MyTimeTableEventSeriesVo eventSeriesToAdd = new MyTimeTableEventSeriesVo(
                            seriesTitleOfEventSet,
                            eventSet.getStudyGroups(),
                            new ArrayList<>()
                    );

                    eventSeriesMap.put(seriesTitleOfEventSet, eventSeriesToAdd);
                }
                //add event
                eventSeriesMap.get(seriesTitleOfEventSet).addEvent(eventToAdd);
            }

        }
        return new ArrayList<>(eventSeriesMap.values());
    }

    //todo: auskommentiert wegen Umbau von MyTimeTable
//    public static List<MyTimeTableEventSetVo> queryfutureEvents(final List<MyTimeTableEventSetVo>list){
//
//        final List<MyTimeTableEventSetVo> filteredEvents = new ArrayList<>();
//        for(final MyTimeTableEventSetVo event : list){
//
//            Date eventDate = null;
//            try {
//                eventDate = sdf.parse(event.getFirstEvent().getStartDate()+" "+event.getFirstEvent().getStartTime());
//            } catch (final ParseException e) {
//                Log.e(TAG, "Fehler beim Parsen der Daten: ",e );
//            }
//            if(eventDate.after(new Date())){
//                filteredEvents.add(event);
//            }
//        }
//        return filteredEvents;
//    }

    //todo: auskommentiert im Zuge der Umbauarbeiten für MyTimeTable
    /*public static final MyTimeTableEventSetVo getEventByID(final List<MyTimeTableEventSetVo> list, final String ID){
        for ( final MyTimeTableEventSetVo event:list ) {
            if(ID.equals(event.getFirstEvent().getId())){
                return event;
            }
        }
        return null;
    }*/

    /**
     *
     * @param list
     * @param data
     * @return
     */
    public static final boolean listContainsEvent(final List<MyTimeTableEventSeriesVo> list, final MyTimeTableEventSeriesVo data){
        for(final MyTimeTableEventSeriesVo event : list){
//			Log.d(TAG, "Eventvergleich1: "+event);
//			Log.d(TAG, "Eventvergleich2: "+data);
//			Log.d(TAG, "listContainsEvent: "+event.getEvent().getTitle()+" "+data.getEvent().getTitle());
//			Log.d(TAG, "listContainsEvent: "+event.getEventDay().getDayInWeek()+" "+data.getEventDay().getDayInWeek());
//			Log.d(TAG, "listContainsEvent: "+event.getEventWeek().getWeekInYear()+" "+data.getEventWeek().getWeekInYear());
//			Log.d(TAG, "listContainsEvent: "+event.getStudyGroup().getTimeTableId()+" "+data.getStudyGroup().getTimeTableId());
//			Log.d(TAG, "listContainsEvent: "+event.getSemester().getId()+" "+data.getSemester().getId());
//			Log.d(TAG, "listContainsEvent: "+event.getStudyProgram().getId()+" "+data.getStudyProgram().getId());
            //todo: auskommentiert im Zuge von Umbauarbeiten
           /* if(event.getFirstEvent().getTitle().equals(data.getFirstEvent().getTitle())){
//				Log.d(TAG, "EventTitle: true");
                if(event.getFirstEventDay().getDayInWeek().equals(data.getEventDay().getDayInWeek())){
//					Log.d(TAG, "EventDay: true");
                    if(event.getEventWeek().getWeekInYear()==data.getEventWeek().getWeekInYear()){
//						Log.d(TAG, "EventWeek: true");
                        if(event.getStudyGroup().getTimeTableId().equals(data.getStudyGroup().getTimeTableId())){
//							Log.d(TAG, "StudyGroup: true");
                            if(event.getSemester().getId().equals(data.getSemester().getId())){
//								Log.d(TAG, "Semester: true");
                                if(event.getStudyProgram().getId().equals(data.getStudyProgram().getId())){
//									Log.d(TAG, "Course: true");
                                    return true;
                                }
                            }
                        }
                    }
                }

            }*/
        }
//		Log.d(TAG, "listContainsEvent: Contains not!");
        return false;
    }

    //don't use SimpleDateFormat.getDateTimeInstance() because it includes seconds
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy H:mm");
}
