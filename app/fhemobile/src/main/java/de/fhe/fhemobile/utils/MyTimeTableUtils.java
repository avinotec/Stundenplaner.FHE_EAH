package de.fhe.fhemobile.utils;

import static de.fhe.fhemobile.utils.Utils.correctUmlauts;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fhe.fhemobile.vos.mytimetable.FlatDataStructure;

public class MyTimeTableUtils {

    private static final String TAG = "MyTimeTableUtils";


    public static String cutEventTitle(final String title) {

        final Pattern p = Pattern.compile("^(.*[a-z|A-Z|ä|Ä|ü|Ü|ö|Ö|ß])"); //$NON-NLS

        // WI/WIEC(BA)Cloudtech./V/01
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
            Log.e(TAG, "onResponse: ",e );
        }
        // WI/WIEC(BA)Cloudtech./V
        return changeEventTitle;
    }

    /**
     * Returns a list filtered by the given event title
     * @param list
     * @param eventTitle
     * @return
     */
    public static List<FlatDataStructure> getCoursesByEventTitle(final List<FlatDataStructure> list,
                                                                 final String eventTitle){

        final List<FlatDataStructure> filteredEvents = new ArrayList<>();
        for (final FlatDataStructure event : list) {
            if(event.getEvent().getTitle().contains(eventTitle)){
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    /**
     * Returns a list filtered by the given study group
     * @param list
     * @param studyGroupTitle
     * @return
     */
    public static List<FlatDataStructure> getCoursesByStudyGroupTitle(
            final List<FlatDataStructure>list, final String studyGroupTitle){
        final List<FlatDataStructure> filteredEvents = new ArrayList<>();
        for(final FlatDataStructure event : list){
            if(event.getSetString().equals(studyGroupTitle)){
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    public static List<FlatDataStructure> queryfutureEvents(final List<FlatDataStructure>list){

        final List<FlatDataStructure> filteredEvents = new ArrayList<>();
        for(final FlatDataStructure event : list){

            Date eventDate = null;
            try {
                eventDate= sdf.parse(event.getEvent().getDate()+" "+event.getEvent().getStartTime());
            } catch (final ParseException e) {
                Log.e(TAG, "Fehler beim Parsen der Daten: ",e );
            }
            if(eventDate.after(new Date())){
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    /**
     *
     * @param list
     * @param ID
     * @return
     */
    public static final FlatDataStructure getEventByID (final List<FlatDataStructure> list, final String ID){
        for ( final FlatDataStructure event:list ) {
            if(ID.equals(event.getEvent().getUid())){
                return event;
            }
        }
        return null;
    }

    /**
     *
     * @param list
     * @param data
     * @return
     */
    public static final boolean listContainsEvent(final List<FlatDataStructure> list, final FlatDataStructure data){
        for(FlatDataStructure event:list){
//			Log.d(TAG, "Eventvergleich1: "+event);
//			Log.d(TAG, "Eventvergleich2: "+data);
//			Log.d(TAG, "listContainsEvent: "+event.getEvent().getTitle()+" "+data.getEvent().getTitle());
//			Log.d(TAG, "listContainsEvent: "+event.getEventDay().getDayInWeek()+" "+data.getEventDay().getDayInWeek());
//			Log.d(TAG, "listContainsEvent: "+event.getEventWeek().getWeekInYear()+" "+data.getEventWeek().getWeekInYear());
//			Log.d(TAG, "listContainsEvent: "+event.getStudyGroup().getTimeTableId()+" "+data.getStudyGroup().getTimeTableId());
//			Log.d(TAG, "listContainsEvent: "+event.getSemester().getId()+" "+data.getSemester().getId());
//			Log.d(TAG, "listContainsEvent: "+event.getCourse().getId()+" "+data.getCourse().getId());
            if(event.getEvent().getTitle().equals(data.getEvent().getTitle())){
//				Log.d(TAG, "EventTitle: true");
                if(event.getEventDay().getDayInWeek().equals(data.getEventDay().getDayInWeek())){
//					Log.d(TAG, "EventDay: true");
                    if(event.getEventWeek().getWeekInYear()==data.getEventWeek().getWeekInYear()){
//						Log.d(TAG, "EventWeek: true");
                        if(event.getStudyGroup().getTimeTableId().equals(data.getStudyGroup().getTimeTableId())){
//							Log.d(TAG, "StudyGroup: true");
                            if(event.getSemester().getId().equals(data.getSemester().getId())){
//								Log.d(TAG, "Semester: true");
                                if(event.getCourse().getId().equals(data.getCourse().getId())){
//									Log.d(TAG, "Course: true");
                                    return true;
                                }
                            }
                        }
                    }
                }

            }
        }
//		Log.d(TAG, "listContainsEvent: Contains not!");
        return false;
    }

    //don't use SimpleDateFormat.getDateTimeInstance() because it includes seconds
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy H:mm");
}
