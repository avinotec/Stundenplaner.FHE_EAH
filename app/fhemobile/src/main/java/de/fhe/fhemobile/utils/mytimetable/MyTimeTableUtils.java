package de.fhe.fhemobile.utils.mytimetable;

import static de.fhe.fhemobile.utils.Utils.correctUmlauts;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventSeriesVo;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;

public class MyTimeTableUtils {

    private static final String TAG = "MyTimeTableUtils";

    /**
     * Returns the event title without the number of the time table entry
     * (event titles have no such number if entered for the first time,
     * but later added events belonging to the same course get one)
     *
     * @param event an event that belongs to the {@link MyTimeTableEventSeriesVo}
     * @return string corresponding to the title that identifies a {@link MyTimeTableEventSeriesVo}
     */
    public static String getEventSeriesName(final TimeTableEventVo event){
        return getEventSeriesName(event.getTitle());
    }

    /**
     * Returns the event title without the number of the time table entry
     * (event titles have no such number if entered for the first time,
     * but later added events belonging to the same course get one)
     *
     * @param title the title of an event that belongs to the {@link MyTimeTableEventSeriesVo}
     * @return string corresponding to the title that identifies a {@link MyTimeTableEventSeriesVo}
     */
    public static String getEventSeriesName(final String title){
        //cut away all ".d" (where d stands for any digit)
        return title.replaceAll("\\.\\d+$","");
    }

    /**
     * Returns the name of the course which equals the event's name without numbers at the end
     * @param event an event in the course
     * @return course name
     */
    public static String getCourseName(final TimeTableEventVo event){
        //cut away all "/dd.dd" (where d stands for any digit)
        return getCourseName(event.getTitle());
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

    //todo: auskommentiert wegen Umbau von MyTimeTable
//    /**
//     * Returns a list filtered by the given event title
//     * @param list
//     * @param eventTitle
//     * @return
//     */
//    public static List<MyTimeTableEventSeriesVo> getCoursesByEventTitle(
//            final List<MyTimeTableEventSeriesVo> list, final String eventTitle){
//
//        final List<MyTimeTableEventSeriesVo> filteredEvents = new ArrayList<>();
//        for (final MyTimeTableEventSeriesVo event : list) {
//            if(event.getFirstEvent().getTitle().contains(eventTitle)){
//                filteredEvents.add(event);
//            }
//        }
//        return filteredEvents;
//    }

    /**
     * Returns a list filtered by the given study group
     * @param list
     * @param studyGroupTitle
     * @return
     */
    public static List<MyTimeTableEventSeriesVo> getCoursesByStudyGroupTitle(
            final List<MyTimeTableEventSeriesVo>list, final String studyGroupTitle){

        final List<MyTimeTableEventSeriesVo> filteredEvents = new ArrayList<>();
        for(final MyTimeTableEventSeriesVo event : list){
            if(event.getStudyGroupListString().equals(studyGroupTitle)){
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    //todo: auskommentiert wegen Umbau von MyTimeTable
//    public static List<MyTimeTableEventSeriesVo> queryfutureEvents(final List<MyTimeTableEventSeriesVo>list){
//
//        final List<MyTimeTableEventSeriesVo> filteredEvents = new ArrayList<>();
//        for(final MyTimeTableEventSeriesVo event : list){
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
    /*public static final MyTimeTableEventSeriesVo getEventByID(final List<MyTimeTableEventSeriesVo> list, final String ID){
        for ( final MyTimeTableEventSeriesVo event:list ) {
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
