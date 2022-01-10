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
package de.fhe.fhemobile.views.mytimetable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import java.util.Collections;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.mytimetable.MyTimeTableCourseAdapter;
import de.fhe.fhemobile.comparator.StudyCourseComperator;
import de.fhe.fhemobile.vos.timetable.StudyCourseVo;
import de.fhe.fhemobile.vos.timetable.SemesterVo;
import de.fhe.fhemobile.widgets.picker.StudyCoursePicker;
import de.fhe.fhemobile.widgets.picker.SemesterPicker;
import de.fhe.fhemobile.widgets.picker.base.OnItemChosenListener;

/**
 * Created on 12.03.15.
 */
public class MyTimeTableDialogView extends LinearLayout {

    public MyTimeTableDialogView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTimeTableDialogView(final Context context) {
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

    }

    public void setStudyCourseItems(final List<StudyCourseVo> _Items) {
        StudyCourseVo.alterTitle(_Items);
        Collections.sort(_Items,new StudyCourseComperator());
        mStudyCoursePicker.setItems(_Items);
        mStudyCoursePicker.toggleEnabled(true);
    }

    public void setSemesterItems(final List<SemesterVo> _Items) {
        mSemesterPicker.setItems(_Items);
        mSemesterPicker.toggleEnabled(true);
    }


    public void toggleSemesterPickerVisibility(final boolean _Visible) {
        mSemesterPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void toggleCourseListVisibility(final boolean _Visible){
        mCourseList.setVisibility(_Visible ? VISIBLE : GONE);
    }
    public void setCourseListAdapter(final MyTimeTableCourseAdapter adapter){
        mCourseList.setAdapter(adapter);

    }

    // if study course changes, reset the Terms
    public void resetSemesterPicker() {
        mSemesterPicker.reset(true);
    }
    public void setSemesterPickerEnabled(final boolean enabled){

        mSemesterPicker.toggleEnabled(enabled);
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mStudyCoursePicker = (StudyCoursePicker) findViewById(R.id.add_timetableStudyCoursePicker);
        mSemesterPicker = (SemesterPicker)       findViewById(R.id.add_timetableSemesterPicker);

        mCourseList = (ListView)          findViewById(R.id.my_time_table_dialog_listview_courses);
    }

    // Returns the chosen TermsId
    private final OnItemChosenListener mCourseListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null) {
                mViewListener.onStudyCourseChosen(_ItemId);
            }
        }
    };

    // Returns the GroupId
    private final OnItemChosenListener mTermsListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null) {
                mViewListener.onSemesterChosen(_ItemId);
            }
        }
    };


    public interface IViewListener {
        void onStudyCourseChosen(String _TermId);
        void onSemesterChosen(String _GroupId);
        //TODO not yet implemented?
        //not used? void onStudyGroupChosen(String _TimeTableId);
        //not used? void onSearchClicked();
    }

    public void setSelectedSemesterText(final String text){
        mSemesterPicker.setDisplayValue(text);
    }
    public void setSelectedGroupText(final String text){
        mStudyCoursePicker.setDisplayValue(text);
    }

    public void setEmptyText(final String text){
        final TextView emptyView = new TextView( getContext() );
        emptyView.setText(text);
        mCourseList.setEmptyView(emptyView);
    }

    private IViewListener     mViewListener;

    private StudyCoursePicker mStudyCoursePicker;
    private SemesterPicker mSemesterPicker;
    private ListView mCourseList;


}
