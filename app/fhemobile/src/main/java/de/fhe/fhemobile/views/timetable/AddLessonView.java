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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import java.util.Collections;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.timetable.TimeTableLessonAdapter;
import de.fhe.fhemobile.comparator.StudyCourseComperator;
import de.fhe.fhemobile.vos.timetable.StudyCourseVo;
import de.fhe.fhemobile.vos.timetable.TermsVo;
import de.fhe.fhemobile.widgets.picker.StudyCoursePicker;
import de.fhe.fhemobile.widgets.picker.TermsPicker;
import de.fhe.fhemobile.widgets.picker.base.OnItemChosenListener;

/**
 * Created on 12.03.15.
 */
public class AddLessonView extends LinearLayout {

    public AddLessonView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public AddLessonView(final Context context) {
        super(context);
    }

    public void setViewListener(final IViewListener _Listener) {
        mViewListener = _Listener;
    }

    public void initializeView(final FragmentManager _Manager) {
        mFragmentManager = _Manager;

        mStudyCoursePicker.setFragmentManager(mFragmentManager);
        mStudyCoursePicker.toggleEnabled(false);
        mStudyCoursePicker.setOnItemChosenListener(mCourseListener);

        mTermsPicker.setFragmentManager(mFragmentManager);
        mTermsPicker.toggleEnabled(false);
        mTermsPicker.setOnItemChosenListener(mTermsListener);

    }

    public void setStudyCourseItems(final List<StudyCourseVo> _Items) {
        StudyCourseVo.alterTitle(_Items);
        Collections.sort(_Items,new StudyCourseComperator());
        mStudyCoursePicker.setItems(_Items);
        mStudyCoursePicker.toggleEnabled(true);
    }

    public void setTermsItems(final List<TermsVo> _Items) {
        mTermsPicker.setItems(_Items);
        mTermsPicker.toggleEnabled(true);
    }


    public void toggleTermsPickerVisibility(final boolean _Visible) {
        mTermsPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void toggleLessonListVisibility(final boolean _Visible){
        mLessonList.setVisibility(_Visible ? VISIBLE : GONE);
    }
    public void setLessonListAdapter(final TimeTableLessonAdapter adapter){
        mLessonList.setAdapter(adapter);

    }

    // if study course changes, reset the Terms
    public void resetTermsPicker() {
        mTermsPicker.reset(true);
    }
    public void setmTermsPickerEnabled(boolean enabled){

        mTermsPicker.toggleEnabled(enabled);
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mStudyCoursePicker = (StudyCoursePicker) findViewById(R.id.add_timetableStudyCoursePicker);
        mTermsPicker       = (TermsPicker)       findViewById(R.id.add_timetableTermsPicker);

        mLessonList        = (ListView)          findViewById(R.id.add_lvLessons);
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
        void onStudyGroupChosen(String _TimeTableId);
        void onSearchClicked();
    }

    public void setSelectedTermText(String text){
        mTermsPicker.setDisplayValue(text);
    }
    public void setSelectedGroupText(String text){
        mStudyCoursePicker.setDisplayValue(text);
    }

    public void setEmptyText(String text){
        TextView emptyView = new TextView( getContext() );
        emptyView.setText(text);
        mLessonList.setEmptyView(emptyView);
    }

    private FragmentManager   mFragmentManager;
    private IViewListener     mViewListener;

    private StudyCoursePicker mStudyCoursePicker;
    private TermsPicker       mTermsPicker;
    private ListView          mLessonList;


}
