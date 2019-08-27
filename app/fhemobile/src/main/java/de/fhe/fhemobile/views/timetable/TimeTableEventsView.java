package de.fhe.fhemobile.views.timetable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.timetable.TimeTableWeekPagerAdapter;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;

/**
 * Created by paul on 13.03.15.
 */
public class TimeTableEventsView extends LinearLayout {

    public interface IViewListener {

    }

    public TimeTableEventsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setViewListener(IViewListener _Listener) {
        mListener = _Listener;
    }

    public void initializeView(FragmentManager _Manager) {
        mFragmentManager = _Manager;
    }

    public void setPagerItems(ArrayList<TimeTableWeekVo> _Items) {
        mAdapter = new TimeTableWeekPagerAdapter(mFragmentManager, _Items);
        mPager.setAdapter(mAdapter);

        mPageIndicator.setViewPager(mPager);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mPager         = (ViewPager)           findViewById(R.id.eventPager);

    }

    private Context                     mContext;
    private IViewListener               mListener;
    private FragmentManager             mFragmentManager;

    private ViewPager                   mPager;
    private CirclePageIndicator         mPageIndicator;

    private TimeTableWeekPagerAdapter   mAdapter;
}
