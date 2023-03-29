/*
 *  Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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
package de.fhe.fhemobile.models.semesterdates;

import java.util.Calendar;
import java.util.Locale;

import de.fhe.fhemobile.events.EventDispatcher;
import de.fhe.fhemobile.events.SemesterDatesChangeEvent;
import de.fhe.fhemobile.vos.semesterdates.SemesterVo;

/**
 * Created by paul on 23.01.14.
 */
public final class SemesterDatesModel extends EventDispatcher {

    static final String SEMESTER_TYPE_WS = "WS";
    static final String SEMESTER_TYPE_SS = "SS";

    private SemesterDatesModel() {
    }

    public static SemesterDatesModel getInstance() {
        if (ourInstance == null) {
            ourInstance = new SemesterDatesModel();
        }
        return ourInstance;
    }

    public SemesterVo[] getSemesterVos() {
        return mSemesterVos;
    }


    public void setData(final SemesterVo[] _Data) {
        mSemesterVos = _Data;
        if (mSemesterVos != null) {
            notifyChange(SemesterDatesChangeEvent.RECEIVED_SEMESTER_DATES);

            //set chosen semester to current semester
            setChosenSemesterToCurrent();

        } else {
            notifyChange(SemesterDatesChangeEvent.RECEIVED_EMPTY_SEMESTER_DATES);
        }
    }


    public int getChosenSemester() {
        return mChosenSemester;
    }

    public void setChosenSemester(final int _chosenSemester) {
        if (_chosenSemester != mChosenSemester) {
            mChosenSemester = _chosenSemester;
            notifyChange(SemesterDatesChangeEvent.SEMESTER_SELECTION_CHANGED);
        }
    }

    /**
     * Get the {@link SemesterVo} of the chosen semester
     *
     * @return
     */
    public SemesterVo getChosenSemesterVo() {
        if(mSemesterVos == null) return null;

        return mSemesterVos[mChosenSemester];
    }

    /**
     * Set chosen semester to current
     */
    private void setChosenSemesterToCurrent() {
        if (mSemesterVos == null) return;

        // Semester change dates
        // (actually holiday dates, but we want to display the next semester after holiday started)

        // Calendar instances 1st of March
        final Calendar calWsHolidayStart = Calendar.getInstance(Locale.GERMANY);
        calWsHolidayStart.set(Calendar.MONTH, Calendar.MARCH);
        calWsHolidayStart.set(Calendar.DAY_OF_MONTH, 1);
        // Calendar instances 1st of September
        final Calendar calSsHolidayStart = Calendar.getInstance(Locale.GERMANY);
        calSsHolidayStart.set(Calendar.MONTH, Calendar.SEPTEMBER);
        calSsHolidayStart.set(Calendar.DAY_OF_MONTH, 1);
        // today
        final Calendar now = Calendar.getInstance();


        //find current semester
        Integer currentYear;
        String semesterType;

        if (now.before(calWsHolidayStart)) {
            currentYear = calWsHolidayStart.get(Calendar.YEAR);
            semesterType = SEMESTER_TYPE_WS;
        } else if (now.after(calWsHolidayStart) && now.before(calSsHolidayStart)) {
            currentYear = calSsHolidayStart.get(Calendar.YEAR);
            semesterType = SEMESTER_TYPE_SS;
        } else {
            currentYear = calSsHolidayStart.get(Calendar.YEAR) + 1;
            semesterType = SEMESTER_TYPE_WS;
        }

        if (/* ___always true___ currentYear != null && */ /* semesterType != null __always true */ true ) {

            //set chosen semester to current
            for (int i = 0; i < mSemesterVos.length; i++) {
                final String semesterName = mSemesterVos[i].getName(); //e.g. WS 2022/2023 or SS 2022

                // if semester name is "SS currentYear"
                if (semesterName.endsWith(currentYear.toString())
                        && semesterName.startsWith(semesterType)) {
                    setChosenSemester(i);
                }
            }
        }
    }

    private void notifyChange(final String type) {
        dispatchEvent(new SemesterDatesChangeEvent(type));
    }


    private static SemesterDatesModel ourInstance = null;

    private SemesterVo[] mSemesterVos = null;
    private int mChosenSemester;
}
