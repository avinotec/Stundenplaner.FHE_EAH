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
package de.fhe.fhemobile.events;

public class SemesterDatesChangeEvent extends SimpleEvent {
    public static final String RECEIVED_SEMESTER_DATES = "receivedSemesterDates";
    public static final String RECEIVED_EMPTY_SEMESTER_DATES = "receivedEmptySemesterDates";
    public static final String SEMESTER_SELECTION_CHANGED   = "semesterSelectionChanged";


    public SemesterDatesChangeEvent(final String type) {
        super(type);
    }

}