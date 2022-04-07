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
package de.fhe.fhemobile.views.timetable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentManager;

import java.util.Collections;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.comparator.StudyProgramComparator;
import de.fhe.fhemobile.comparator.StudyProgramComparator2;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyProgramVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyGroupVo;
import de.fhe.fhemobile.vos.timetable.TimeTableSemesterVo;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyProgramVo2;
import de.fhe.fhemobile.widgets.picker.StudyProgramPicker;
import de.fhe.fhemobile.widgets.picker.StudyGroupPicker;
import de.fhe.fhemobile.widgets.picker.SemesterPicker;
import de.fhe.fhemobile.widgets.picker.StudyProgramPicker2;
import de.fhe.fhemobile.widgets.picker.base.OnItemChosenListener;

/**
 * Created by paul on 12.03.15.
 */
public class TimeTableDialogView extends LinearLayout {

    public TimeTableDialogView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeTableDialogView(final Context context) {
        super(context);
    }

    public void setViewListener(final IViewListener _Listener) {
        mViewListener = _Listener;
    }

    public void initializeView(final FragmentManager _Manager) {

        mStudyProgramPicker2.setFragmentManager(_Manager);
        mStudyProgramPicker2.toggleEnabled(false);
        mStudyProgramPicker2.setOnItemChosenListener(mCourseListener);

        mSemesterPicker.setFragmentManager(_Manager);
        mSemesterPicker.toggleEnabled(false);
        mSemesterPicker.setOnItemChosenListener(mSemesterListener);

        mStudyGroupPicker.setFragmentManager(_Manager);
        mStudyGroupPicker.toggleEnabled(false);
        mStudyGroupPicker.setOnItemChosenListener(mGroupsListener);

        mSearchButton.setOnClickListener(mSearchClickListener);
        toggleButtonEnabled(false);
    }

    /* OLD: version for working with OPML and API from FH Erfurt
    public void setStudyCourseItems(final List<TimeTableStudyProgramVo> _Items) {
        TimeTableStudyProgramVo.alterTitle(_Items);
        Collections.sort(_Items, new StudyProgramComparator());
        mStudyProgramPicker.setItems(_Items);
        mStudyProgramPicker.toggleEnabled(true);
    }*/

    public void setStudyCourseItems(final List<TimeTableStudyProgramVo2> _Items) {
        Collections.sort(_Items, new StudyProgramComparator2());
        mStudyProgramPicker2.setItems(_Items);
        mStudyProgramPicker2.toggleEnabled(true);
    }

    public void setSemesterItems(final List<TimeTableSemesterVo> _Items) {
        mSemesterPicker.setItems(_Items);
        mSemesterPicker.toggleEnabled(true);
    }

    public void setStudyGroupItems(final List<TimeTableStudyGroupVo> _Items) {
        mStudyGroupPicker.setItems(_Items);
        mStudyGroupPicker.toggleEnabled(true);
    }

    public void toggleSemesterPickerVisibility(final boolean _Visible) {
        mSemesterPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void toggleGroupsPickerVisibility(final boolean _Visible) {
        mStudyGroupPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void resetSemesterPicker() {
        mSemesterPicker.reset(true);
    }

    public void resetGroupsPicker() {
        mStudyGroupPicker.reset(true);
    }

    public boolean isRememberActivated() {
        return mRememberSwitch.isChecked();
    }

    public void toggleButtonEnabled(final boolean _Enabled) {
        mSearchButton.setEnabled(_Enabled);
        if(_Enabled){
            mSearchButton.setTextColor(getResources().getColor(R.color.primary_button));
            mSearchButton.setBackgroundResource(R.drawable.buttonshape);
        }
        else{
            mSearchButton.setTextColor(getResources().getColor(R.color.primary_button_deactivated));
            mSearchButton.setBackgroundResource(R.drawable.buttonshape_disabled);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //Study Program
        mStudyProgramPicker2 = (StudyProgramPicker2) findViewById(R.id.timetableStudyCoursePicker);
        //Semester
        mSemesterPicker    = (SemesterPicker)    findViewById(R.id.timetableSemesterPicker);
        //Set
        mStudyGroupPicker  = (StudyGroupPicker)  findViewById(R.id.timetableStudyGroupPicker);
        mRememberSwitch    = (SwitchCompat)      findViewById(R.id.timetableRememberSelection);
        mSearchButton      = (Button)            findViewById(R.id.timetableSearchButton);
    }

    // Returns the chosen SemesterId
    private final OnItemChosenListener mCourseListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null) {
                mViewListener.onStudyProgramChosen(_ItemId);
            }
        }
    };

    // Returns the GroupId
    private final OnItemChosenListener mSemesterListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null) {
                mViewListener.onSemesterChosen(_ItemId);
            }
        }
    };

    // Returns the TimeTableId
    private final OnItemChosenListener mGroupsListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null) {
                mViewListener.onGroupChosen(_ItemId);
            }
        }
    };

    private final OnClickListener mSearchClickListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (mViewListener != null) {
                mViewListener.onSearchClicked();
            }
        }
    };

    public interface IViewListener {
        void onStudyProgramChosen(String _StudyCourseId);
        void onSemesterChosen(String _SemesterId);
        void onGroupChosen(String _TimeTableId);
        void onSearchClicked();
    }

    private IViewListener     mViewListener;

    private StudyProgramPicker2 mStudyProgramPicker2;
    private SemesterPicker      mSemesterPicker;
    private StudyGroupPicker    mStudyGroupPicker;

    private SwitchCompat      mRememberSwitch;
    private Button            mSearchButton;

}
