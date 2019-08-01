package de.fhe.fhemobile.widgets.picker;

import android.content.Context;
import android.util.AttributeSet;

import java.util.List;

import de.fhe.fhemobile.vos.timetable.StudyCourseVo;
import de.fhe.fhemobile.widgets.picker.base.IdPicker;

/**
 * Created by paul on 12.03.15.
 */
public class StudyCoursePicker extends IdPicker {

    public StudyCoursePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setItems(List<StudyCourseVo> _items) {
        mItems = _items;
        if (mItems == null) {
            throw new AssertionError("StudyCourse items cannot be null!");
        }
    }

    @Override
    protected String getId(int _Position) {
        return mItems.get(_Position).getId();
    }

    @Override
    protected String getName(int _Position) {
        return mItems.get(_Position).getTitle();
    }

    @Override
    protected int getCount() {
        return mItems.size();
    }

    private List<StudyCourseVo> mItems;
}
