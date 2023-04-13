/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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

package de.fhe.fhemobile.utils.myschedule;

import android.util.Log;

import com.google.common.collect.Sets;
import com.google.gson.Gson;

import org.jetbrains.annotations.NonNls;
import org.junit.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.comparator.EventSeriesTitleComparator;
import de.fhe.fhemobile.comparator.MyScheduleEventComparator;
import de.fhe.fhemobile.comparator.MyScheduleEventDateComparator;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventDateVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSetVo;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventVo;

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
	 * Convert the time in long shifted by timezone offset (fetched from the EAH Api)
	 * to the correct time long in UTC
	 * @param time The incorrect time in long
	 * @return The time in correct long. Null, if parsing failed.
	 */
	public static Long convertEahApiTimeToUtc(long time){
		//convert long to string
		final SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ROOT);
		//magic trick no. 1
		sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
		// date string in german time
		String germanDateString = sdf1.format(time * 1000);

		//magic trick no. 2
		//note: do not reuse sdf1 with setting its timezone to Berlin,
		// it is somehow resulting in the timezone being set to Pacific Daylight Time (PDT)
		final SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.GERMANY);
		try {
			Date correctDate = sdf2.parse(germanDateString);
			return correctDate.getTime();
		} catch (ParseException ex){
			Log.e(TAG, "Failed to parse date string", ex);
			return null;
		}
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
		if(title == null){
			Log.e(TAG, "Cannot determine eventseries name. Given title is null."); //NON-NLS
			return "";
		}
		//cut away all ".d" (where d stands for any digit)
		//example: WI/WIEC(BA)Mathe/Ü/01.1 -> WI/WIEC(BA)Mathe/Ü/01
		return title.replaceAll("\\.\\d+$", "");
	}

	/**
	 * Get the title of an {@link MyScheduleEventSeriesVo} without the group number
	 *
	 * @param title
	 * @return
	 */
	public static String getEventSeriesBaseTitle(final String title) {
		if(title == null){
			Log.e(TAG, "Cannot determine eventseries base title. Given title is null."); //NON-NLS
			return "";
		}

		return title.replaceAll("/\\d\\d$", "");
	}

	/**
	 * The given {@link MyScheduleEventSeriesVo} is identified as exam
	 * when its base title ends with APL, PL, mdl. Prfg.,Wdh.-Prfg., Wdh.-APL
	 * @param eventSeries The {@link MyScheduleEventSeriesVo} to check
	 * @return True if the event series is identified as exam
	 */
	public static boolean isExam(MyScheduleEventSeriesVo eventSeries){
		//example: "BT/MT/WT(BA)Biomat./APL/01" -> base title "BT/MT/WT(BA)Biomat./APL" ends with an exam ending
	 	//noinspection HardCodedStringLiteral
		return getEventSeriesBaseTitle(eventSeries.getTitle()).matches(".*(PL)|(Prfg\\.)");
	}

	/**
	 * Group events of the given {@link MyScheduleEventSetVo}s into {@link MyScheduleEventSeriesVo}s
	 * @param _EventSets A map of the {@link MyScheduleEventSetVo}s to group
	 * @return The list of {@link MyScheduleEventSeriesVo}s
	 */
	public static List<MyScheduleEventSeriesVo> groupByEventTitle(final Map<String, MyScheduleEventSetVo> _EventSets) {
		final Map<String, MyScheduleEventSeriesVo> eventSeriesMap = new HashMap<>();

		for (final MyScheduleEventSetVo eventSet : _EventSets.values()) {

			final List<MyScheduleEventVo> eventsToAdd = new ArrayList<>();

			//construct a MyScheduleEventVo from each event date, collect it in eventsToAdd
			for (final MyScheduleEventDateVo eventDate : eventSet.getEventDates()) {

				//new EventVo
				final MyScheduleEventVo event = new MyScheduleEventVo(
						eventSet.getTitle(),
						eventSet.getId(),
						eventDate.getStartTime(),
						eventDate.getEndTime(),
						eventSet.getLecturerList(),
						eventSet.getLocationList());
				eventsToAdd.add(event);
			}

			//create new EventSeriesVo if necessary
			final String seriesTitleOfEventSet = getEventSeriesName(eventSet.getTitle());
			MyScheduleEventSeriesVo eventSeries = eventSeriesMap.get(seriesTitleOfEventSet);
			if (eventSeries == null) {
				eventSeries = new MyScheduleEventSeriesVo(eventSet);

				//add eventVos constructed from eventSet
				eventSeries.addEvents(eventsToAdd);

				//add new eventSeries
				eventSeriesMap.put(seriesTitleOfEventSet, eventSeries);

			} else {
				if (BuildConfig.DEBUG) Assert.assertEquals(eventSeries.getModuleId(), eventSet.getModuleId());
				//add event set id because events of the EventSet are merged
				// with events of another EventSet into one EventSeries
				eventSeries.addEventSetId(eventSet.getId());
				//add eventVos constructed from eventSet
				eventSeries.addEvents(eventsToAdd);
			}


		}
		return new ArrayList<>(eventSeriesMap.values());
	}

	/**
	 * Group the given {@link MyScheduleEventSeriesVo}s by module ID
	 *
	 * @param eventSeriesVos The collection of {@link MyScheduleEventSeriesVo} to group
	 * @return Map containing a list of the module's {@link MyScheduleEventSeriesVo}s
	 * for each module ID found
	 */
	public static Map<String, Map<String, MyScheduleEventSeriesVo>> groupByModuleId(
			final Collection<MyScheduleEventSeriesVo> eventSeriesVos) {

		final Map<String, Map<String, MyScheduleEventSeriesVo>> modules = new HashMap<>();

		for (final MyScheduleEventSeriesVo eventSeries : eventSeriesVos) {

			Map<String, MyScheduleEventSeriesVo> module = modules.get(eventSeries.getModuleId());
			if (module == null) {
				module = new HashMap<>();
				module.put(eventSeries.getTitle(), eventSeries);
				modules.put(eventSeries.getModuleId(), module);
			} else {
				module.put(eventSeries.getTitle(), eventSeries);
			}
		}

		return modules;
	}

	public static Map<String, List<MyScheduleEventVo>> groupByEventSet(final List<MyScheduleEventVo> eventVos) {
		final Map<String, List<MyScheduleEventVo>> eventVosMap = new HashMap<>();

		for (final MyScheduleEventVo eventVo : eventVos) {
			if (eventVosMap.containsKey(eventVo.getEventSetId())) {
				eventVosMap.get(eventVo.getEventSetId()).add(eventVo);
			} else {
				final ArrayList<MyScheduleEventVo> toAdd = new ArrayList<MyScheduleEventVo>();
				toAdd.add(eventVo);
				eventVosMap.put(eventVo.getEventSetId(), toAdd);
			}
		}

		//sort
		for (final List<MyScheduleEventVo> events : eventVosMap.values()) {
			Collections.sort(events, new MyScheduleEventComparator());
		}

		return eventVosMap;
	}

	/**
	 * Compare local and fetched events to detect changes
	 *  @param localEventSeriesSubList A subset of the subscribedEventSeries
	 *                           containing event series that belong to the same module,
	 *                           sorted by eventseries title
	 * @param fetchedEventSetsMap The fetched {@link MyScheduleEventSetVo}s
	 *                               corresponding to the given subset of eventseries,
	 * @return List of updated the subscribedEventSeries
	 */
	public static List<MyScheduleEventSeriesVo> getUpdatedEventSeries(
			final Map<String, MyScheduleEventSeriesVo> localEventSeriesSubList,
			final Map<String, MyScheduleEventSetVo> fetchedEventSetsMap) {

		final ArrayList<MyScheduleEventSeriesVo> eventSeriesToAdd = new ArrayList<>();

		//get fetchedEventSeries from fetched event sets
		final List<MyScheduleEventSeriesVo> fetchedEventSeriesVos = groupByEventTitle(fetchedEventSetsMap);

		//examine fetched EventSeriesVos for changes, deleted and added events
		for (final MyScheduleEventSeriesVo fetchedEventSeries : fetchedEventSeriesVos) {

			final MyScheduleEventSeriesVo localEventSeries = localEventSeriesSubList.get(fetchedEventSeries.getTitle());
			if (localEventSeries != null){
				detectChangesAndUpdateLocal(localEventSeries, fetchedEventSeries, fetchedEventSetsMap);
			} else {
				//if event series corresponds to a new exam (no matter which group), then always add
				if(isExam(fetchedEventSeries)){
					//set every event in the exam series as "added"
					for (final MyScheduleEventVo eventVo : fetchedEventSeries.getEvents()) {
						eventVo.addChange(TimetableChangeType.ADDITION);
					}
					//add exam as subscribed eventseries
					fetchedEventSeries.setSubscribed(true);
					eventSeriesToAdd.add(fetchedEventSeries);
					//inform user that exam has been added
					showExamAddedToast(fetchedEventSeries.getTitle());
				}
			}
		}

		final List<MyScheduleEventSeriesVo> updatedEventSeriesList = new ArrayList<>(localEventSeriesSubList.values());
		Collections.sort(updatedEventSeriesList, new EventSeriesTitleComparator());
		for (final MyScheduleEventSeriesVo eventSeries : updatedEventSeriesList){
			Collections.sort(eventSeries.getEvents(), new MyScheduleEventComparator());
		}
		updatedEventSeriesList.addAll(eventSeriesToAdd);
		return updatedEventSeriesList;
	}

	/**
	 *
	 * @param examTitle
	 */
	private static void showExamAddedToast(final String examTitle){
		Utils.showToast(Main.getAppContext().getString(R.string.exam_added) + ":\n"+ examTitle);
	}

	/**
	 * Detect deleted, added and changed event sets and event properties
	 * @param localEventSeries The local event series
	 * @param fetchedEventSeries The fetched event series to update the local with
	 * @param fetchedEventSetsMap The map of fetched event sets (to detect added event sets and changes event properties)
	 */
	private static void detectChangesAndUpdateLocal(
			final MyScheduleEventSeriesVo localEventSeries,
			final MyScheduleEventSeriesVo fetchedEventSeries,
			final Map<String, MyScheduleEventSetVo> fetchedEventSetsMap){

		//skip change detection if events of the event series' are equal
		final Gson gson = new Gson();
		String localEventsJson = gson.toJson(localEventSeries.getEvents());
		@NonNls final String fetchedEventsJson = gson.toJson(fetchedEventSeries.getEvents());
		//remove change marks from local json to enable comparison with fetched json
		localEventsJson = localEventsJson.replaceAll("\"typesOfChanges\":\\[(\"[A-Z]+,?\")+]","\"typesOfChanges\":[]");
		if(localEventsJson.equals(fetchedEventsJson)) {
			Log.d(TAG, "Detection of my schedule changes skipped because events are equal");
			return;
		}

		//detect added event sets
		final Set<String> eventSetsAdded = Sets.difference(
				fetchedEventSeries.getEventSetIds(), localEventSeries.getEventSetIds());

		//detect deleted event sets
		final Set<String> eventSetsDeleted = Sets.difference(
				localEventSeries.getEventSetIds(), fetchedEventSeries.getEventSetIds());

		//detect changed and deleted events
		// then update changed event properties and mark deleted events as deleted
		final Map<String, List<MyScheduleEventVo>> eventsToBeUpdatedByEventSet = groupByEventSet(localEventSeries.getEvents());

		for (final Map.Entry<String, List<MyScheduleEventVo>> localEventSetEntry : eventsToBeUpdatedByEventSet.entrySet()) {

			//DELETED EVENT SETS: set events from deleted event sets to "deleted"
			if (eventSetsDeleted.contains(localEventSetEntry.getKey())) {
				for (final MyScheduleEventVo deletedEvent : localEventSetEntry.getValue()) {
					deletedEvent.addChange(TimetableChangeType.DELETION);
				}
			}
			//DETECT CHANGED EVENTS PROPERTIES AND EVENTS DELETED WITHIN AN EVENT SET - compare local and fetched events
			else {
				if(fetchedEventSetsMap == null || fetchedEventSetsMap.size() == 0) break;
				final MyScheduleEventSetVo fetchedEventSet = fetchedEventSetsMap.get(localEventSetEntry.getKey());
				//fetchedEventSet != null because it would be contained in eventSetsDeleted otherwise
				if (BuildConfig.DEBUG) Assert.assertNotNull(fetchedEventSet);

				final List<MyScheduleEventDateVo> fetchedEventDates = fetchedEventSet.getEventDates();
				//fetchedEventDates != null because getEventDates returns an empty ArrayList at least
				if (BuildConfig.DEBUG) Assert.assertNotNull(fetchedEventDates);

				final List<MyScheduleEventVo> deletedEvents = new ArrayList<>();
				Collections.sort(fetchedEventDates, new MyScheduleEventDateComparator());

				//FIND DELETED EVENTS
				if (fetchedEventDates.size() < localEventSetEntry.getValue().size()) {

					//detect deleted events by screening localEvent
					for (int i = 0; i < localEventSetEntry.getValue().size(); i++) {
						final MyScheduleEventVo localEvent = localEventSetEntry.getValue().get(i);
						MyScheduleEventDateVo fetchedEvent = null;

						//get fetched event to compare
						if (i < fetchedEventDates.size()) {
							fetchedEvent = fetchedEventDates.get(i);
						}

						if (fetchedEvent == null
								|| fetchedEvent.getStartTime() != localEvent.getStartTime()
								|| fetchedEvent.getEndTime() != localEvent.getEndTime()) {
							//mark as deleted and temporarily save to deletedEvents
							localEvent.addChange(TimetableChangeType.DELETION);
							deletedEvents.add(localEvent);
							localEventSetEntry.getValue().remove(localEvent);

							//reset counter to make the current fetchedEvent being compared to the next localEvent
							i--;
						}
					}
				}
				//FIND ADDED EVENTS
				//note: "if" instead of "else if" needed for event sets that contain deleted and time-edited events
				// (this case already occurred, all events of the event set had been deleted except one
				// but this one simultaneously had been shifted by 15 min. To not loose the event completely
				// and to avoid the assert in line 383 to fail,
				// this if loop needs to run in order to detect the shifted event as added.)
				if (fetchedEventDates.size() > localEventSetEntry.getValue().size()) {

					//note: the workflow fails if an event set contains added and time-edited events.
					// According to the Stundenplanung, this case is not supposed to occur.

					//detect added events in fetchedEventSet
					for (int i = 0; i < fetchedEventDates.size(); i++) {
						final MyScheduleEventDateVo fetchedEvent = fetchedEventDates.get(i);
						MyScheduleEventVo localEvent = null;

						if (i < localEventSetEntry.getValue().size()) {
							localEvent = localEventSetEntry.getValue().get(i);
						}

						if (localEvent == null
								|| fetchedEvent.getStartTime() != localEvent.getStartTime()
								|| fetchedEvent.getEndTime() != localEvent.getEndTime()) {
							//add new event
							final MyScheduleEventVo newEvent = new MyScheduleEventVo(
									fetchedEventSet.getTitle(),
									fetchedEventSet.getId(),
									fetchedEvent.getStartTime(),
									fetchedEvent.getEndTime(),
									fetchedEventSet.getLecturerList(),
									fetchedEventSet.getLocationList());
							newEvent.addChange(TimetableChangeType.ADDITION);
							localEventSetEntry.getValue().add(i, newEvent);
						}
					}
				}
				//If fetchedEventSet size == localEventSet size (note: localEventSet is cleaned from deleted and increased by added events),
				// we assume that we can compare events at the same positions to detect changed properties
				if (BuildConfig.DEBUG) {
					Assert.assertEquals(localEventSetEntry.getValue().size(), fetchedEventSet.getEventDates().size());
				}

				if (localEventSetEntry.getValue().size() == fetchedEventSet.getEventDates().size()) {
					//check for changed property values
					for (int k = 0; k < localEventSetEntry.getValue().size(); k++) {
						final MyScheduleEventDateVo fetchedEvent = fetchedEventSet.getEventDates().get(k);
						final MyScheduleEventVo localEvent = localEventSetEntry.getValue().get(k);

						if (localEvent.getTypesOfChanges().contains(TimetableChangeType.ADDITION)) {
							continue;
						}

						//start date time changed
						if (fetchedEvent.getStartTime() != localEvent.getStartTime()) {
							localEvent.addChange(TimetableChangeType.EDIT_TIME);
							localEvent.setStartDateTimeInSec(fetchedEvent.getStartTime());
						}
						//end date changed
						if (fetchedEvent.getEndTime() != localEvent.getEndTime()) {
							localEvent.addChange(TimetableChangeType.EDIT_TIME);
							localEvent.setEndDateTimeInSec(fetchedEvent.getEndTime());
						}
						//location changed
						if (!fetchedEventSet.getLocationList().equals(localEvent.getLocationList())) {
							localEvent.addChange(TimetableChangeType.EDIT_LOCATION);
							localEvent.setLocationList(fetchedEventSet.getLocationList());
						}
						//lecturer changed
						if (!fetchedEventSet.getLecturerList().equals(localEvent.getLecturerList())) {
							localEvent.addChange(TimetableChangeType.EDIT_LECTURER);
							localEvent.setLecturerList(fetchedEventSet.getLecturerList());
						}
						//title has been updated
						if (!fetchedEventSet.getTitle().equals(localEvent.getTitle())) {
							//note: we don't want to mark this change
							localEvent.setTitle(fetchedEventSet.getTitle());
						}
					}
				}
				//add deleted events for documentation
				localEventSetEntry.getValue().addAll(deletedEvents);

			}
		}

		//add new event sets
		if ( /* _always true___ eventSetsAdded != null && */ !eventSetsAdded.isEmpty()) {
			for (final String eventSetId : eventSetsAdded) {
				final MyScheduleEventSetVo eventSet = fetchedEventSetsMap.get(eventSetId);
				final List<MyScheduleEventVo> eventListToAdd = new ArrayList<>();

				for (final MyScheduleEventDateVo addedEventDate : eventSet.getEventDates()) {
					final MyScheduleEventVo eventToAdd = new MyScheduleEventVo(
							eventSet.getTitle(),
							eventSet.getId(),
							addedEventDate.getStartTime(),
							addedEventDate.getEndTime(),
							eventSet.getLecturerList(),
							eventSet.getLocationList());
					eventToAdd.addChange(TimetableChangeType.ADDITION);
					eventListToAdd.add(eventToAdd);
				}
				eventsToBeUpdatedByEventSet.put(eventSetId, eventListToAdd);
			}
		}

		//flatten updated event list
		final List<MyScheduleEventVo> updatedEvents = new ArrayList<>();
		for (final List<MyScheduleEventVo> events : eventsToBeUpdatedByEventSet.values()) {
			updatedEvents.addAll(events);
		}
		//set updated events
		localEventSeries.setEvents(updatedEvents, eventsToBeUpdatedByEventSet.keySet());
	}

}
