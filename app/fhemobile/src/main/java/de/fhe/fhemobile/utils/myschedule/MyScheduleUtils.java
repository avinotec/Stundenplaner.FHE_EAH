package de.fhe.fhemobile.utils.myschedule;

import static de.fhe.fhemobile.Main.getEventsOfAllSubscribedEventSeries;
import static de.fhe.fhemobile.Main.subscribedEventSeries;
import static de.fhe.fhemobile.activities.MainActivity.addToSubscribedEventSeriesAndUpdateAdapters;
import static de.fhe.fhemobile.activities.MainActivity.myScheduleCalendarAdapter;
import static de.fhe.fhemobile.activities.MainActivity.myScheduleSettingsAdapter;
import static de.fhe.fhemobile.activities.MainActivity.saveSubscribedEventSeriesToSharedPreferences;
import static de.fhe.fhemobile.utils.Utils.correctUmlauts;

import android.util.Log;

import com.google.common.collect.Sets;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.comparator.MyScheduleEventComparator;
import de.fhe.fhemobile.comparator.MyScheduleEventDateComparator;
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
			if (eventSeries == null) {
				eventSeries = new MyScheduleEventSeriesVo(eventSet);

				//add eventVos constructed from eventSet
				eventSeries.addEvents(eventsToAdd);

				//add new eventSeries
				eventSeriesMap.put(seriesTitleOfEventSet, eventSeries);

			} else {
				if (BuildConfig.DEBUG)
					Assert.assertEquals(eventSeries.getModuleId(), eventSet.getModuleId());
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
	 * Group given list of {@link MyScheduleEventSeriesVo}s by module id
	 *
	 * @param eventSeriesVos
	 * @return Map containing a list of all {@link MyScheduleEventSeriesVo}s with the same module id
	 * for each module id
	 */
	public static Map<String, Map<String, MyScheduleEventSeriesVo>> groupByModuleId(
			List<MyScheduleEventSeriesVo> eventSeriesVos) {

		Map<String, Map<String, MyScheduleEventSeriesVo>> modules = new HashMap<>();

		for (MyScheduleEventSeriesVo eventSeries : eventSeriesVos) {

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

	public static Map<String, List<MyScheduleEventVo>> groupByEventSet(List<MyScheduleEventVo> eventVos) {
		Map<String, List<MyScheduleEventVo>> eventVosMap = new HashMap<>();

		for (MyScheduleEventVo eventVo : eventVos) {
			if (eventVosMap.containsKey(eventVo.getEventSetId())) {
				eventVosMap.get(eventVo.getEventSetId()).add(eventVo);
			} else {
				eventVosMap.put(eventVo.getEventSetId(), Arrays.asList(eventVo));
			}
		}

		//sort
		for (List<MyScheduleEventVo> events : eventVosMap.values()) {
			Collections.sort(events, new MyScheduleEventComparator());
		}

		return eventVosMap;
	}

	/**
	 * Compare local and fetched events to detect changes
	 * and update the local {@link de.fhe.fhemobile.Main#subscribedEventSeries}
	 *  @param localModuleList A subset of {@link Main#subscribedEventSeries}
	 *                           belonging to the same module,
	 *                           by eventseries title
	 * @param fetchedEventSetsMap The fetched {@link MyScheduleEventSetVo}s
	 *                               corresponding to the given subset of eventseries,
	 * @return Collection of updated {@link de.fhe.fhemobile.Main#subscribedEventSeries}
	 */
	public static Collection<MyScheduleEventSeriesVo> updateSubscribedEventSeries(
			Map<String, MyScheduleEventSeriesVo> localModuleList,
			Map<String, MyScheduleEventSetVo> fetchedEventSetsMap) {

		//get fetchedEventSeries from fetched event sets
		List<MyScheduleEventSeriesVo> fetchedEventSeriesVos = groupByEventTitle(fetchedEventSetsMap);

		//examine fetched EventSeriesVos for changes, deleted and added events
		for (MyScheduleEventSeriesVo fetchedEventSeries : fetchedEventSeriesVos) {

			MyScheduleEventSeriesVo localEventSeries = localModuleList.get(fetchedEventSeries.getTitle());
			if (localEventSeries == null){
				//if event series corresponds to an new exam, than always add
				if(getEventSeriesBaseTitle(fetchedEventSeries.getTitle()).matches(".*(PL)|(Prfg.)|(APL)")){
					//set every event in the exam series as "added"
					for(MyScheduleEventVo eventVo : fetchedEventSeries.getEvents()){
						eventVo.addChange(TimetableChangeType.ADDITION);
					}
					//add exam as subscribed eventseries
					fetchedEventSeries.setSubscribed(true);
					subscribedEventSeries.add(fetchedEventSeries);
				}

				//skip non-subscribed eventseries and newly added exam eventseries
				continue;
			}

			//detect added event sets
			Set<String> eventSetsAdded = Sets.difference(
					fetchedEventSeries.getEventSetIds(), localEventSeries.getEventSetIds());

			//detect deleted event sets
			Set<String> eventSetsDeleted = Sets.difference(
					localEventSeries.getEventSetIds(), fetchedEventSeries.getEventSetIds());

			//detect changed events + update deleted and changed events
			Map<String, List<MyScheduleEventVo>> localEventsByEventSet = groupByEventSet(localEventSeries.getEvents());
			for (Map.Entry<String, List<MyScheduleEventVo>> localEventSetEntry : localEventsByEventSet.entrySet()) {

				//set events deleted
				if (eventSetsDeleted.contains(localEventSetEntry.getKey())) {
					for (MyScheduleEventVo deletedEvent : localEventSetEntry.getValue()) {
						deletedEvent.addChange(TimetableChangeType.DELETION);
					}
				}

				//compare local and fetched events to detect changes
				else {
					MyScheduleEventSetVo fetchedEventSet = fetchedEventSetsMap.get(localEventSetEntry.getKey());
					Collections.sort(fetchedEventSet.getEventDates(), new MyScheduleEventDateComparator());
					List<MyScheduleEventVo> deletedEvents = new ArrayList<>();

					//find deleted events
					if (fetchedEventSet.getEventDates() != null
							&& fetchedEventSet.getEventDates().size() < localEventSetEntry.getValue().size()) {

						//detect deleted events in localEventSet
						for (int i = 0; i < localEventSetEntry.getValue().size(); i++) {
							MyScheduleEventDateVo fetchedEvent = fetchedEventSet.getEventDates().get(i);
							MyScheduleEventVo localEvent = localEventSetEntry.getValue().get(i);

							if (fetchedEvent.getStartDateTimeInSec() != localEvent.getStartDateTimeInSec()
									|| fetchedEvent.getEndDateTimeInSec() != localEvent.getEndDateTimeInSec()) {
								//mark as deleted and temporarily save to deletedEvents
								localEvent.addChange(TimetableChangeType.DELETION);
								deletedEvents.add(localEvent);
								localEventSetEntry.getValue().remove(localEvent);
							}
						}
					}
					//find added events
					else if (fetchedEventSet.getEventDates() != null
							&& fetchedEventSet.getEventDates().size() < localEventSetEntry.getValue().size()) {

						//detect added events in fetchedEventSet
						for (int i = 0; i < fetchedEventSet.getEventDates().size(); i++) {
							MyScheduleEventDateVo fetchedEvent = fetchedEventSet.getEventDates().get(i);
							MyScheduleEventVo localEvent = localEventSetEntry.getValue().get(i);

							if (fetchedEvent.getStartDateTimeInSec() != localEvent.getStartDateTimeInSec()
									|| fetchedEvent.getEndDateTimeInSec() != localEvent.getEndDateTimeInSec()) {
								//add new event
								final MyScheduleEventVo newEvent = new MyScheduleEventVo(
										fetchedEventSet.getTitle(),
										fetchedEventSet.getId(),
										fetchedEvent.getStartDateTimeInSec(),
										fetchedEvent.getEndDateTimeInSec(),
										fetchedEventSet.getLecturerList(),
										fetchedEventSet.getLocationList());
								newEvent.addChange(TimetableChangeType.ADDITION);
								localEventSetEntry.getValue().add(newEvent);
							}
						}
					}

					//If fetchedEventSet size == localEventSet size (note: localEventSet is cleaned from deleted and increased by added events),
					// we assume that we can compare events at the same positions to detect changed properties

					//check for changed property values
					for (int k = 0; k < localEventSetEntry.getValue().size(); k++) {
						MyScheduleEventDateVo fetchedEvent = fetchedEventSet.getEventDates().get(k);
						MyScheduleEventVo localEvent = localEventSetEntry.getValue().get(k);

						if (localEvent.getTypesOfChanges().contains(TimetableChangeType.ADDITION)) {
							continue;
						}

						//start date time changed
						if (fetchedEvent.getStartDateTimeInSec() != localEvent.getStartDateTimeInSec()) {
							localEvent.addChange(TimetableChangeType.EDIT_TIME);
							localEvent.setStartDateTimeInSec(fetchedEvent.getStartDateTimeInSec());
						}
						//end date changed
						if (fetchedEvent.getEndDateTimeInSec() != localEvent.getEndDateTimeInSec()) {
							localEvent.addChange(TimetableChangeType.EDIT_TIME);
							localEvent.setEndDateTimeInSec(fetchedEvent.getEndDateTimeInSec());
						}
						//location changed
						if (!fetchedEventSet.getLocationList().equals(localEvent.getLocationList())) {
							localEvent.addChange(TimetableChangeType.EDIT_LOCATION);
							localEvent.setLecturerList(fetchedEventSet.getLecturerList());
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

					//add deleted events for documentation
					localEventSetEntry.getValue().addAll(deletedEvents);

				}
			}

			//add new eventsets
			for (String eventSetId : eventSetsAdded) {
				MyScheduleEventSetVo eventSet = fetchedEventSetsMap.get(eventSetId);
				List<MyScheduleEventVo> eventListToAdd = new ArrayList<>();

				for (MyScheduleEventDateVo addedEventDate : eventSet.getEventDates()) {
					final MyScheduleEventVo eventToAdd = new MyScheduleEventVo(
							eventSet.getTitle(),
							eventSet.getId(),
							addedEventDate.getStartDateTimeInSec(),
							addedEventDate.getEndDateTimeInSec(),
							eventSet.getLecturerList(),
							eventSet.getLocationList());
					eventListToAdd.add(eventToAdd);
				}
				localEventsByEventSet.put(eventSetId, eventListToAdd);
			}

			//flatten updated event list
			List<MyScheduleEventVo> updatedEvents = new ArrayList<>();
			for (List<MyScheduleEventVo> events : localEventsByEventSet.values()) {
				updatedEvents.addAll(events);
			}
			//set updated events
			localEventSeries.setEvents(updatedEvents, localEventsByEventSet.keySet());
			//todo: check if reference is sufficient or if subscribedEventSeries need to be updated

			//update shared preferences and adapters
			saveSubscribedEventSeriesToSharedPreferences();
			myScheduleCalendarAdapter.setItems(getEventsOfAllSubscribedEventSeries());
			myScheduleCalendarAdapter.notifyDataSetChanged();
			myScheduleSettingsAdapter.notifyDataSetChanged();
			Main.setLastUpdateSubscribedEventSeries(new Date());

		}

		return localModuleList.values();
	}

}