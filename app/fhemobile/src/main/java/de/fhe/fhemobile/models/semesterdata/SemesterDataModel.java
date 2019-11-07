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
package de.fhe.fhemobile.models.semesterdata;

import de.fhe.fhemobile.events.EventDispatcher;
import de.fhe.fhemobile.events.SimpleEvent;
import de.fhe.fhemobile.vos.semesterdata.SemesterVo;

/**
 * Created by paul on 23.01.14.
 */
public class SemesterDataModel extends EventDispatcher {

    public class ChangeEvent extends SimpleEvent {
        public static final String RECEIVED_SEMESTER_DATA       = "receivedSemesterData";
        public static final String RECEIVED_EMPTY_SEMESTER_DATA = "receivedEmptySemesterData";
        public static final String SEMESTER_SELECTION_CHANGED   = "semesterSelectionChanged";

        public ChangeEvent(String type) {
            super(type);
        }
    }

    public SemesterVo[] getSemesterData() {
        return mData;
    }

    public void setData(SemesterVo[] _Data) {
        mData = _Data;
        if(mData != null) {
            notifyChange(ChangeEvent.RECEIVED_SEMESTER_DATA);
        }
        else {
            notifyChange(ChangeEvent.RECEIVED_EMPTY_SEMESTER_DATA);
        }
    }

    public int getChosenSemester() {
        return mChosenSemester;
    }

    public void setChosenSemester(int _chosenSemester) {
        if(_chosenSemester != mChosenSemester) {
            mChosenSemester = _chosenSemester;
            notifyChange(ChangeEvent.SEMESTER_SELECTION_CHANGED);
        }
    }

    public static SemesterDataModel getInstance() {
        if(ourInstance == null) {
            ourInstance = new SemesterDataModel();
        }
        return ourInstance;
    }

    private SemesterDataModel() {
    }

    private void notifyChange(String type) {
        dispatchEvent(new ChangeEvent(type));
    }

    private static SemesterDataModel ourInstance = null;

    private SemesterVo[] mData           = null;
    private int          mChosenSemester = 0;
}
