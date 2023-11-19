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
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.comparator.MyScheduleEventComparator;
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
	 * Regex for the entry number of an event title, e.g. ".1" at WI/WIEC(BA)Mathe/Ü/01.1
	 * */
	private static final String regexEventEntryNumber = "\\.\\d+"; //$NON-NLS
	/**
	 * Regex for the event set number of an event title,
	 * e.g. "01" in WI/WIEC(BA)Mathe/Ü/01 or "01_02" in WI/WIEC(BA)Mathe/Ü/01_02
	 * */
	private static final String regexEventSetNumber = "\\d\\d(?:_\\d\\d)*"; //$NON-NLS
	//"(?:X)" stands for a non-capturing group with pattern X
	private static final String regexMergedEventSetNumber = "\\d\\d(?:_\\d\\d)+"; //$NON-NLS
	private static final Pattern patternEventSetNumber = Pattern.compile("/" + regexEventSetNumber + "$");

	/**
	 * Utility class, no constructor
	 */
	private MyScheduleUtils() {
	}

	/**
	 * Convert the time in long shifted by timezone offset (fetched from the EAH Api)
	 * to the correct time long in UTC
	 * @param time The incorrect time in long
	 * @return The time in correct long. null, if parsing failed.
	 */
	public static Long convertEahApiTimeToUtc(final long time){
		//!!!NOTE: do not debug timezones in the emulator - it's a mess, it is never set as you expected

		//convert long to string
		final SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ROOT);
		//magic trick no. 1
		sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
		// date string in german time
		final String germanDateString = sdf1.format(time * 1000);

		//magic trick no. 2
		final SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.GERMANY);
		try {
			final Date correctDate = sdf2.parse(germanDateString);
			return correctDate.getTime();
		} catch (final ParseException ex){
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
		return title.replaceAll(regexEventEntryNumber + "$", "");
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

		return title.replaceAll("/"  +regexEventSetNumber + "$", "");
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
				final MyScheduleEventVo event = createEventVo(eventDate, eventSet);
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

	/**
	 * Group the {@link MyScheduleEventVo}s by event set
	 * @param eventVos The list of {@link MyScheduleEventVo}s
	 * @return The resulting Map containing a list of {@link MyScheduleEventVo}s for each event set
	 */
	public static Map<String, List<MyScheduleEventVo>> groupByEventSet(final List<MyScheduleEventVo> eventVos) {
		final Map<String, List<MyScheduleEventVo>> eventVosMap = new HashMap<>();

		for (final MyScheduleEventVo eventVo : eventVos) {
			if (eventVosMap.containsKey(eventVo.getEventSetId())) {
				eventVosMap.get(eventVo.getEventSetId()).add(eventVo);
			} else {
				final ArrayList<MyScheduleEventVo> toAdd = new ArrayList<>();
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
	 * Construct a new {@link MyScheduleEventVo} from the given {@link MyScheduleEventDateVo}
	 * and {@link MyScheduleEventSetVo}
	 *
	 * @param eventDate A {@link MyScheduleEventDateVo}
	 * @param eventSet The {@link MyScheduleEventSetVo} the eventDate belongs to
	 * @return The constructed {@link MyScheduleEventVo}
	 */
	public static MyScheduleEventVo createEventVo(final MyScheduleEventDateVo eventDate,
												  final MyScheduleEventSetVo eventSet){

		return new MyScheduleEventVo(
				eventSet.getTitle(),
				eventSet.getId(),
				eventDate.getGermanStartTime(),
				eventDate.getGermanEndTime(),
				eventSet.getLecturerList(),
				eventSet.getLocationList());
	}


	/**
	 * The given {@link MyScheduleEventSeriesVo} is identified as exam
	 * when its base title ends with APL, PL, mdl. Prfg.,Wdh.-Prfg., Wdh.-APL
	 * @param eventSeries The {@link MyScheduleEventSeriesVo} to check
	 * @return True if the event series is identified as exam
	 */
	public static boolean isExam(final MyScheduleEventSeriesVo eventSeries){
		//example: "BT/MT/WT(BA)Biomat./APL/01" -> base title "BT/MT/WT(BA)Biomat./APL" ends with an exam ending
		//noinspection HardCodedStringLiteral
		return getEventSeriesBaseTitle(eventSeries.getTitle()).matches(".*(PL)|(Prfg\\.)");
	}

	/**
	 * The given {@link MyScheduleEventSeriesVo} is a merged event series
	 * @param eventSeries The {@link MyScheduleEventSeriesVo} to check
	 * @return True if the given event series resulted from merging multiple event series
	 */
	public static boolean isMergedEventSeries(final MyScheduleEventSeriesVo eventSeries){
		//e.g. true for WI/WIEC(BA)Mathe/Ü/01_02, false for WI/WIEC(BA)Mathe/Ü/01
		return eventSeries.getTitle().matches(".*/" + regexMergedEventSetNumber + "$");
	}

	/**
	 * Check whether the single event series is one of the events series that has been merged into the merged event series.
	 * @param singleEventSeries The title of the single event series
	 * @param mergedEventSeries The title of the event series that has been created by merging multiple event series,
	 *                            e.g. putting together two event sets for a practise series.
	 * @return True, if the single event series is part of the merged event series
	 */
	public static boolean containedInMergedEventSeries(final String singleEventSeries, final String mergedEventSeries){

		//check if both titles belong to the same study program, subject and event type e.g. WI/WIEC(BA)Mathe/Ü
		if(getEventSeriesBaseTitle(singleEventSeries).equals(getEventSeriesBaseTitle(mergedEventSeries))){

			final Matcher matcherSingle = patternEventSetNumber.matcher(singleEventSeries);
			final Matcher matcherMerged = patternEventSetNumber.matcher(mergedEventSeries);

			//occurrences/event set numbers for both event series were found
			if(matcherSingle.find() && matcherMerged.find()){
				final String eventSetSingle = matcherSingle.group(0);
				final String eventSetsMerged = matcherMerged.group(0);

				if(eventSetsMerged.contains(eventSetSingle)){
					//merged event series contains event set number of the single event series
					return true;
				}
			}
		}

		return false;
	}
}
