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
package de.fhe.fhemobile.widgets.picker;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Collections;
import java.util.List;

import de.fhe.fhemobile.comparator.StudyProgramComparator;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyProgramVo;
import de.fhe.fhemobile.widgets.picker.base.IdPicker;

/**
 * Created by paul on 12.03.15
 * Edited by Nadja - 04/2022
 */
public class StudyProgramPicker extends IdPicker {

    public StudyProgramPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setItems(List<TimeTableStudyProgramVo> _Items) {
        Collections.sort(_Items, new StudyProgramComparator());
        mItems = _Items;
        if (mItems == null) {
            throw new AssertionError("Study Program items cannot be null!");
        }
    }

    @Override
    protected String getId(int _Position) {
        return mItems.get(_Position).getId();
    }

    @Override
    protected String getName(int _Position) {
        return mItems.get(_Position).getGuiName();
    }

    @Override
    protected int getCount() {
        return mItems.size();
    }

    private List<TimeTableStudyProgramVo> mItems;
}
