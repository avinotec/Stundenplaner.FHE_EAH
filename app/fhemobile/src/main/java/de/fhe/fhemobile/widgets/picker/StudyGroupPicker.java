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
package de.fhe.fhemobile.widgets.picker;

import android.content.Context;
import android.util.AttributeSet;

import java.util.List;

import de.fhe.fhemobile.vos.timetable.TimetableStudyGroupVo;
import de.fhe.fhemobile.widgets.picker.baseMoses.IdPickerMoses;

/**
 * Created by paul on 12.03.15
 */
public class StudyGroupPicker extends IdPickerMoses {

    public StudyGroupPicker(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public void setItems(final List<TimetableStudyGroupVo> _items) {
        mItems = _items;
        if (mItems == null) {
            throw new AssertionError("StudyGroup items cannot be null!");
        }
    }

    @Override
    protected String getId(final int _Position) {
        return mItems.get(_Position).getGroupId().toString();
    }

    @Override
    protected String getName(final int _Position) {
        return mItems.get(_Position).getNumber();
    }

    @Override
    protected Integer getMosesObjectId(int _Position) {
        return mItems.get(_Position).getGroupId();
    }

    @Override
    protected int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    private List<TimetableStudyGroupVo> mItems;
}
