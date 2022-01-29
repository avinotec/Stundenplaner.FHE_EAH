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
import de.fhe.fhemobile.adapters.mytimetable.MyTimeTableDialogAdapter;
import de.fhe.fhemobile.comparator.StudyCourseComparator;
import de.fhe.fhemobile.vos.timetable.TimeTableStudyCourseVo;
import de.fhe.fhemobile.vos.timetable.TimeTableSemesterVo;
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
        mSemesterPicker.setOnItemChosenListener(mSemesterListener);

        setCourseListEmptyView();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mStudyCoursePicker = (StudyCoursePicker) findViewById(R.id.studycoursepicker_mytimetable_dialog);
        mSemesterPicker =   (SemesterPicker)     findViewById(R.id.semesterpicker_mytimetable_dialog);

        mCourseListView = (ListView) findViewById(R.id.listview_mytimetable_dialog_courses);
    }


    public void setStudyCourseItems(final List<TimeTableStudyCourseVo> _Items) {
        TimeTableStudyCourseVo.alterTitle(_Items);
        Collections.sort(_Items, new StudyCourseComparator());
        mStudyCoursePicker.setItems(_Items);
        mStudyCoursePicker.toggleEnabled(true);
    }

    public void setSemesterItems(final List<TimeTableSemesterVo> _Items) {
        mSemesterPicker.setItems(_Items);
        mSemesterPicker.toggleEnabled(true);
    }


    public void toggleSemesterPickerVisibility(final boolean _Visible) {
        mSemesterPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void toggleCourseListVisibility(final boolean _Visible){
        mCourseListView.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void setCourseListAdapter(final MyTimeTableDialogAdapter adapter){
        mCourseListView.setAdapter(adapter);
    }

    public void resetSemesterPicker() {
        mSemesterPicker.reset(true);
    }

    public void setSemesterPickerEnabled(final boolean enabled){
        mSemesterPicker.toggleEnabled(enabled);
    }



    private final OnItemChosenListener mCourseListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null) {
                mViewListener.onStudyCourseChosen(_ItemId);
            }
        }
    };

    private final OnItemChosenListener mSemesterListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null) {
                mViewListener.onSemesterChosen(_ItemId);
            }
        }
    };


    public void setSelectedSemesterText(final String text){
        mSemesterPicker.setDisplayValue(text);
    }

    public void setSelectedGroupText(final String text){
        mStudyCoursePicker.setDisplayValue(text);
    }

    /**
     * Sets view to show if the course list is empty
     */
    private void setCourseListEmptyView(){
        final TextView emptyView = new TextView( getContext() );
        emptyView.setText(getResources().getString(R.string.my_time_table_empty_text_select));
        mCourseListView.setEmptyView(emptyView);
    }


    public interface IViewListener {
        void onStudyCourseChosen(String _TermId);
        void onSemesterChosen(String _GroupId);
    }


    private IViewListener     mViewListener;

    private StudyCoursePicker   mStudyCoursePicker;
    private SemesterPicker      mSemesterPicker;
    private ListView            mCourseListView;


}
