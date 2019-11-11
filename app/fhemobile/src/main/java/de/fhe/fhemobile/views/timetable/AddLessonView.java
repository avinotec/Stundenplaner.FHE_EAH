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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.FragmentManager;

import java.util.Collections;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.timetable.TimeTableLessonAdapter;
import de.fhe.fhemobile.comparator.StudyCourseComperator;
import de.fhe.fhemobile.vos.timetable.StudyCourseVo;
import de.fhe.fhemobile.vos.timetable.StudyGroupVo;
import de.fhe.fhemobile.vos.timetable.TermsVo;
import de.fhe.fhemobile.widgets.picker.StudyCoursePicker;
import de.fhe.fhemobile.widgets.picker.StudyGroupPicker;
import de.fhe.fhemobile.widgets.picker.TermsPicker;
import de.fhe.fhemobile.widgets.picker.base.OnItemChosenListener;

/**
 * Created by paul on 12.03.15.
 */
public class AddLessonView extends LinearLayout {

    public AddLessonView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public AddLessonView(final Context context) {
        super(context);
        mContext = context;
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

        mStudyGroupPicker.setFragmentManager(mFragmentManager);
        mStudyGroupPicker.toggleEnabled(false);
        mStudyGroupPicker.setOnItemChosenListener(mGroupsListener);


//        mSearchButton.setOnClickListener(mSearchClickListener);
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

    public void setStudyGroupItems(final List<StudyGroupVo> _Items) {
        mStudyGroupPicker.setItems(_Items);
        mStudyGroupPicker.toggleEnabled(true);
    }

    public void toggleTermsPickerVisibility(final boolean _Visible) {
        mTermsPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void toggleGroupsPickerVisibility(final boolean _Visible) {
        mStudyGroupPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }
    public void toggleLessonListVisibility(final boolean _Visible){
        mLessonList.setVisibility(_Visible ? VISIBLE : GONE);
    }
    public void setLessonListAdapter(final TimeTableLessonAdapter adapter){
        mLessonList.setAdapter(adapter);

    }

    public void resetTermsPicker() {
        mTermsPicker.reset(true);
    }
    public void setmTermsPickerEnabled(boolean enabled){

        mTermsPicker.toggleEnabled(enabled);
    }
    public void setmGroupPickerEnabled(boolean enabled){
        mStudyGroupPicker.toggleEnabled(enabled);
    }

    public void resetGroupsPicker() {
        mStudyGroupPicker.reset(true);
    }

/*
    public void toggleButtonEnabled(final boolean _Enabled) {
    }
*/

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mStudyCoursePicker = (StudyCoursePicker) findViewById(R.id.add_timetableStudyCoursePicker);
        mTermsPicker       = (TermsPicker)       findViewById(R.id.add_timetableTermsPicker);
        mStudyGroupPicker  = (StudyGroupPicker)  findViewById(R.id.add_timetableStudyGroupPicker);
        mToggleEditModus   = (ToggleButton)      findViewById(R.id.add_tbEditMode);

        mLessonList        = (ListView)          findViewById(R.id.add_lvLessons);
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

    private OnClickListener mSearchClickListener = new OnClickListener() {
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
        //TODO not yet implemented?
        void onTimeTableChosen(String _TimeTableId);
        void onSearchClicked();
    }

    public void setSelectedTermText(String text){
        mTermsPicker.setDisplayValue(text);
    }
    public void setSelectedGroupText(String text){
        mStudyCoursePicker.setDisplayValue(text);
    }

    public void setEmptyText(String text){
        TextView emptyView = new TextView(this.mContext);
        emptyView.setText(text);
        mLessonList.setEmptyView(emptyView);
    }

    private final Context           mContext;
    private FragmentManager   mFragmentManager;
    private IViewListener     mViewListener;

    private StudyCoursePicker mStudyCoursePicker;
    private TermsPicker       mTermsPicker;
    //not used? private LinearLayout      mButtonLayout;
    private ToggleButton      mToggleEditModus;
    private StudyGroupPicker  mStudyGroupPicker;
    //not used? private Button            mSearchButton;
    private ListView          mLessonList;


}
