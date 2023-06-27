/*
 *  Copyright (c) 2023-2023 Ernst-Abbe-Hochschule Jena
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * Utils for detecting timetable changes
 *
 * Created by Nadja - 27.06.2023
 */
public class TimetableChangeDetectionUtils {

    private static final String TAG = TimetableChangeDetectionUtils.class.getSimpleName();

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
        final List<MyScheduleEventSeriesVo> fetchedEventSeriesVos = MyScheduleUtils.groupByEventTitle(fetchedEventSetsMap);

        //examine fetched EventSeriesVos for changes, deleted and added events
        for (final MyScheduleEventSeriesVo fetchedEventSeries : fetchedEventSeriesVos) {

            final MyScheduleEventSeriesVo localEventSeries = localEventSeriesSubList.get(fetchedEventSeries.getTitle());
            if (localEventSeries != null){
                detectChangesAndUpdateLocal(localEventSeries, fetchedEventSeries, fetchedEventSetsMap);
            } else {
                //if event series corresponds to a new exam (no matter which group), then always add
                if(MyScheduleUtils.isExam(fetchedEventSeries)){
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
        //sort events and event series in updated list
        final List<MyScheduleEventSeriesVo> updatedEventSeriesList = new ArrayList<>(localEventSeriesSubList.values());
        Collections.sort(updatedEventSeriesList, new EventSeriesTitleComparator());
        for (final MyScheduleEventSeriesVo eventSeries : updatedEventSeriesList){
            Collections.sort(eventSeries.getEvents(), new MyScheduleEventComparator());
        }
        //add added event series
        updatedEventSeriesList.addAll(eventSeriesToAdd);
        return updatedEventSeriesList;
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
        final Map<String, List<MyScheduleEventVo>> eventsToBeUpdatedByEventSet = MyScheduleUtils.groupByEventSet(localEventSeries.getEvents());

        for (final Map.Entry<String, List<MyScheduleEventVo>> localEventSetEntry : eventsToBeUpdatedByEventSet.entrySet()) {

            //DELETED EVENT SETS: set events from deleted event sets to "deleted"
            if (eventSetsDeleted.contains(localEventSetEntry.getKey())) {
                for (final MyScheduleEventVo deletedEvent : localEventSetEntry.getValue()) {
                    deletedEvent.addChange(TimetableChangeType.DELETION);
                }
            }
            //DETECT CHANGED EVENT PROPERTIES AND EVENTS DELETED WITHIN AN EVENT SET - compare local and fetched events
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
                // and to avoid the assert in line 383 to fail, this if-loop needs to run in order
                // to detect the shifted event as added.)
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
                            final MyScheduleEventVo newEvent = MyScheduleUtils.createEventVo(fetchedEvent, fetchedEventSet);
                            newEvent.addChange(TimetableChangeType.ADDITION);
                            localEventSetEntry.getValue().add(i, newEvent);
                        }
                    }
                }
                //If fetchedEventSet size == localEventSet size (note: localEventSet is cleaned from deleted and increased by added events),
                // we assume that we can compare events at the same positions to detect changed properties
                if (BuildConfig.DEBUG && false) {
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
                    final MyScheduleEventVo eventToAdd = MyScheduleUtils.createEventVo(addedEventDate, eventSet);
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

    /**
     * Show toast that an exam has been added automatically
     * @param examTitle The title of the exam
     */
    private static void showExamAddedToast(final String examTitle){
        Utils.showToast(Main.getAppContext().getString(R.string.exam_added) + ":\n"+ examTitle);
    }
}
