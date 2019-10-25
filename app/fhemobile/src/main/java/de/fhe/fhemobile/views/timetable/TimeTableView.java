package de.fhe.fhemobile.views.timetable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentManager;

import java.util.List;

import de.fhe.fhemobile.R;
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
public class TimeTableView extends LinearLayout {

    public TimeTableView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public TimeTableView(final Context context) {
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

        mSearchButton.setOnClickListener(mSearchClickListener);
    }

    public void setStudyCourseItems(final List<StudyCourseVo> _Items) {
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

    public void resetTermsPicker() {
        mTermsPicker.reset(true);
    }

    public void resetGroupsPicker() {
        mStudyGroupPicker.reset(true);
    }

    public boolean isRememberActivated() {
        return mRememberSwitch.isChecked();
    }

    public void toggleButtonEnabled(final boolean _Enabled) {
        mSearchButton.setEnabled(_Enabled);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mStudyCoursePicker = (StudyCoursePicker) findViewById(R.id.timetableStudyCoursePicker);
        mTermsPicker       = (TermsPicker)       findViewById(R.id.timetableTermsPicker);
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
    private OnItemChosenListener mTermsListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null) {
                mViewListener.onGroupChosen(_ItemId);
            }
        }
    };

    // Returns the TimeTableId
    private OnItemChosenListener mGroupsListener = new OnItemChosenListener() {
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
        public void onTermChosen(String _TermId);
        public void onGroupChosen(String _GroupId);
        public void onTimeTableChosen(String _TimeTableId);
        public void onSearchClicked();
    }

    private Context           mContext;
    private FragmentManager   mFragmentManager;
    private IViewListener     mViewListener;

    private StudyCoursePicker mStudyCoursePicker;
    private TermsPicker       mTermsPicker;
    private StudyGroupPicker  mStudyGroupPicker;

    private SwitchCompat      mRememberSwitch;
    private Button            mSearchButton;

}
