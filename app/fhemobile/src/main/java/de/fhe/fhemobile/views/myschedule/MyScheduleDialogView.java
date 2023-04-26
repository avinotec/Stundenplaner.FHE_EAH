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
package de.fhe.fhemobile.views.myschedule;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import java.util.Collections;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.myschedule.MyScheduleDialogAdapter;
import de.fhe.fhemobile.comparator.StudyProgramComparator;
import de.fhe.fhemobile.vos.timetable.TimetableSemesterVo;
import de.fhe.fhemobile.vos.timetable.TimetableStudyProgramVo;
import de.fhe.fhemobile.widgets.picker.SemesterPicker;
import de.fhe.fhemobile.widgets.picker.StudyProgramPicker;
import de.fhe.fhemobile.widgets.picker.base.OnItemChosenListener;


public class MyScheduleDialogView extends LinearLayout {

    public MyScheduleDialogView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScheduleDialogView(final Context context) {
        super(context);
    }

    public void setViewListener(final IViewListener _Listener) {
        mViewListener = _Listener;
    }

    public void initializeView(final FragmentManager _Manager) {

        mStudyProgramPicker.setFragmentManager(_Manager);
        mStudyProgramPicker.toggleEnabled(false);
        mStudyProgramPicker.setOnItemChosenListener(mStudyProgramListener);

        mSemesterPicker.setFragmentManager(_Manager);
        mSemesterPicker.toggleEnabled(false);
        mSemesterPicker.setOnItemChosenListener(mSemesterListener);

        setEventListEmptyView();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mStudyProgramPicker = (StudyProgramPicker)  findViewById(R.id.studyprogrampicker_myschedule_dialog_2);
        mSemesterPicker     = (SemesterPicker)      findViewById(R.id.semesterpicker_myschedule_dialog);
        mStudyProgramProgressIndicator = (ProgressBar)         findViewById(R.id.progressbar_myschedule_dialog_1);
        mCourseListProgressIndicator = (ProgressBar)         findViewById(R.id.progressbar_myschedule_dialog_2);

        mCourseListView = (ListView) findViewById(R.id.lv_myschedule_dialog_events);
    }


    public void setStudyProgramItems(final List<TimetableStudyProgramVo> _Items) {
        Collections.sort(_Items, new StudyProgramComparator());
        mStudyProgramPicker.setItems(_Items);
        mStudyProgramPicker.toggleEnabled(true);

        setStudyProgramProgressIndicatorVisible(false);
        findViewById(R.id.layout_myschedule_dialog).setVisibility(VISIBLE);
    }

    public void setSemesterItems(final List<TimetableSemesterVo> _Items) {
        mSemesterPicker.setItems(_Items);
        mSemesterPicker.toggleEnabled(true);
    }


    public void setSemesterPickerVisible(final boolean _Visible) {
        mSemesterPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void setEventListVisible(final boolean _Visible){
        mCourseListView.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void setStudyProgramProgressIndicatorVisible(final boolean _Visible){
        mStudyProgramProgressIndicator.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void setCourseListProgressIndicatorVisible(final boolean _Visible){
        mCourseListProgressIndicator.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void setEventListAdapter(final MyScheduleDialogAdapter adapter){
        mCourseListView.setAdapter(adapter);
    }

    public void resetSemesterPicker() {
        mSemesterPicker.reset(true);
    }


    private final OnItemChosenListener mStudyProgramListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null) {
                mViewListener.onStudyProgramChosen(_ItemId);
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


    /**
     * Sets view to show if the course list is empty
     */
    private void setEventListEmptyView(){
        final TextView emptyView = new TextView( getContext() );
        emptyView.setText(getResources().getString(R.string.myschedule_dialog_empty));
        mCourseListView.setEmptyView(emptyView);
    }


    public interface IViewListener {
        void onStudyProgramChosen(String _StudyProgramId);
        void onSemesterChosen(String _SemesterId);
    }


    IViewListener     mViewListener;

    private StudyProgramPicker  mStudyProgramPicker;
    private SemesterPicker      mSemesterPicker;
    private ListView            mCourseListView;
    private ProgressBar         mStudyProgramProgressIndicator;
    private ProgressBar         mCourseListProgressIndicator;


}
