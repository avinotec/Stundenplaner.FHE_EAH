package de.fhe.fhemobile.utils.myschedule;

import static de.fhe.fhemobile.utils.Utils.correctUmlauts;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSetVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventDateVo;

/**
 * Utility class
 */
public final class MyScheduleUtils {

	private static final String TAG = MyScheduleUtils.class.getSimpleName();

	/**
	 * Utility class, no constructor
	 */
	private MyScheduleUtils() {
	}

	/**
	 * Returns the event title without the number of the time table entry.
	 * (note: event titles have no such number if entered for the first time,
	 * but later added events belonging to the same event series get one)
	 *
	 * @param title the title of an event that belongs to the {@link MyScheduleEventSetVo}
	 * @return string corresponding to the title that identifies a {@link MyScheduleEventSeriesVo}
	 */
	public static String getEventSeriesName(final String title) {
		//cut away all ".d" (where d stands for any digit)
		return title.replaceAll("\\.\\d+$", "");
	}

	/**
	 * Get the title of an {@link MyScheduleEventSeriesVo} without the group number
	 *
	 * @param title
	 * @return
	 */
	public static String getEventSeriesBaseTitle(String title) {
		return title.replaceAll("/\\d\\d$", "");
	}

	/**
	 * Returns the name of the course which equals the event's name without numbers at the end
	 *
	 * @param title the title of an event in the course
	 * @return course name
	 */
	public static String getCourseName(final String title) {
		//cut away all "/dd.dd" (where d stands for any digit)
		return title.replaceAll("/\\d\\d(\\.\\d*)?$", "");
	}

	public static String getEventTitleWithoutEndingNumbers(final String title) {

		//cuts away everything after the last letter (a-z|A-Z|ä|Ä|ü|Ü|ö|Ö|ß), which means that "/01.2" is cut
		final Pattern p = Pattern.compile("^(.*[a-zA-ZäÄüÜöÖß])"); //$NON-NLS

		String changeEventTitle = correctUmlauts(title);
		try {
			final Matcher m = p.matcher(title);
			if (m.find()) {
				changeEventTitle = m.group(1);
			} else {
				changeEventTitle = title;
			}

		} catch (final RuntimeException e) {
			Log.e(TAG, "onResponse: ", e);
		}

		changeEventTitle = changeEventTitle.replaceAll("^[|)/]", "");
		return changeEventTitle;
	}

	public static List<MyScheduleEventSeriesVo> groupByEventTitle(Map<String, MyScheduleEventSetVo> _EventSets) {
		final Map<String, MyScheduleEventSeriesVo> eventSeriesMap = new HashMap<>();

		for (final MyScheduleEventSetVo eventSet : _EventSets.values()) {

			List<MyScheduleEventVo> eventsToAdd = new ArrayList<>();

			//construct a MyScheduleEventVo from each event date, collect it in eventsToAdd
			for (final MyScheduleEventDateVo eventDate : eventSet.getEventDates()) {

				//new EventVo
				final MyScheduleEventVo event = new MyScheduleEventVo(
						eventSet.getTitle(),
						eventSet.getId(),
						eventDate.getStartDateTimeInSec(),
						eventDate.getEndDateTimeInSec(),
						eventSet.getLecturerList(),
						eventSet.getLocationList());
				eventsToAdd.add(event);
			}

			//create new EventSeriesVo if necessary
			final String seriesTitleOfEventSet = getEventSeriesName(eventSet.getTitle());
			MyScheduleEventSeriesVo eventSeries = eventSeriesMap.get(seriesTitleOfEventSet);
			if(eventSeries == null){
				eventSeries = new MyScheduleEventSeriesVo(eventSet);
				//add eventVos constructed from eventSet
				eventSeries.addEvents(eventsToAdd);
				//add new eventSeries
				eventSeriesMap.put(seriesTitleOfEventSet, eventSeries);
			} else {
				//add event set id because events of the EventSet are added to events of another EventSet
				eventSeries.addEventSetId(eventSet.getId());
				//add eventVos constructed from eventSet
				eventSeries.addEvents(eventsToAdd);
			}



		}
		return new ArrayList<>(eventSeriesMap.values());
	}

	//todo: auskommentiert wegen Umbau von MySchedule
//    public static List<MyScheduleEventSetVo> queryfutureEvents(final List<MyScheduleEventSetVo>list){
//
//        final List<MyScheduleEventSetVo> filteredEvents = new ArrayList<>();
//        for(final MyScheduleEventSetVo event : list){
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

	//todo: auskommentiert im Zuge der Umbauarbeiten für MySchedule
    /*public static final MyScheduleEventSetVo getEventByID(final List<MyScheduleEventSetVo> list, final String ID){
        for ( final MyScheduleEventSetVo event:list ) {
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
//    public static final boolean listContainsEvent(final List<MyScheduleEventSeriesVo> list, final MyScheduleEventSeriesVo data){
//        for(final MyScheduleEventSeriesVo event : list){
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

//		Log.d(TAG, "listContainsEvent: Contains not!");
//        return false;
//    }

//don't use SimpleDateFormat.getDateTimeInstance() because it includes seconds
//    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy H:mm");
}

