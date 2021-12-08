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
import de.fhe.fhemobile.comparator.StudyCourseComperator;
import de.fhe.fhemobile.vos.timetable.StudyCourseVo;
import de.fhe.fhemobile.vos.timetable.StudyGroupVo;
import de.fhe.fhemobile.vos.timetable.SemesterVo;
import de.fhe.fhemobile.widgets.picker.StudyCoursePicker;
import de.fhe.fhemobile.widgets.picker.StudyGroupPicker;
import de.fhe.fhemobile.widgets.picker.SemesterPicker;
import de.fhe.fhemobile.widgets.picker.base.OnItemChosenListener;

/**
 * Created by paul on 12.03.15.
 */
public class TimeTableView extends LinearLayout {

    public TimeTableView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeTableView(final Context context) {
        super(context);
    }

    public void setViewListener(final IViewListener _Listener) {
        mViewListener = _Listener;
    }

    public void initializeView(final FragmentManager _Manager) {

        mStudyCoursePicker.setFragmentManager(_Manager);
        mStudyCoursePicker.toggleEnabled(false);
        mStudyCoursePicker.setOnItemChosenListener(mCourseListener);

        mSemesterPicker.setFragmentManager(_Manager);
        mSemesterPicker.toggleEnabled(false);
        mSemesterPicker.setOnItemChosenListener(mTermsListener);

        mStudyGroupPicker.setFragmentManager(_Manager);
        mStudyGroupPicker.toggleEnabled(false);
        mStudyGroupPicker.setOnItemChosenListener(mGroupsListener);

        mSearchButton.setOnClickListener(mSearchClickListener);
        toggleButtonEnabled(false);
    }

    public void setStudyCourseItems(final List<StudyCourseVo> _Items) {
        StudyCourseVo.alterTitle(_Items);
        Collections.sort(_Items,new StudyCourseComperator());
        mStudyCoursePicker.setItems(_Items);
        mStudyCoursePicker.toggleEnabled(true);
    }

    public void setTermsItems(final List<SemesterVo> _Items) {
        mSemesterPicker.setItems(_Items);
        mSemesterPicker.toggleEnabled(true);
    }

    public void setStudyGroupItems(final List<StudyGroupVo> _Items) {
        mStudyGroupPicker.setItems(_Items);
        mStudyGroupPicker.toggleEnabled(true);
    }

    public void toggleTermsPickerVisibility(final boolean _Visible) {
        mSemesterPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void toggleGroupsPickerVisibility(final boolean _Visible) {
        mStudyGroupPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void resetTermsPicker() {
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

        //Studiengang
        mStudyCoursePicker = (StudyCoursePicker) findViewById(R.id.timetableStudyCoursePicker);
        //Semester
        mSemesterPicker = (SemesterPicker)       findViewById(R.id.timetableSemesterPicker);
        //Set
        mStudyGroupPicker  = (StudyGroupPicker)  findViewById(R.id.timetableStudyGroupPicker);
        mRememberSwitch    = (SwitchCompat)      findViewById(R.id.timetableRememberSelection);
        mSearchButton      = (Button)            findViewById(R.id.timetableSearchButton);
    }

    // Returns the chosen TermsId
    private final OnItemChosenListener mCourseListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null) {
                mViewListener.onTermChosen(_ItemId);
            }
        }
    };

    // Returns the GroupId
    private final OnItemChosenListener mTermsListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null) {
                mViewListener.onGroupChosen(_ItemId);
            }
        }
    };

    // Returns the TimeTableId
    private final OnItemChosenListener mGroupsListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null) {
                mViewListener.onTimeTableChosen(_ItemId);
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
        void onTermChosen(String _TermId);
        void onGroupChosen(String _GroupId);
        void onTimeTableChosen(String _TimeTableId);
        void onSearchClicked();
    }

    private IViewListener     mViewListener;

    private StudyCoursePicker mStudyCoursePicker;
    private SemesterPicker mSemesterPicker;
    private StudyGroupPicker  mStudyGroupPicker;

    private SwitchCompat      mRememberSwitch;
    private Button            mSearchButton;

}
