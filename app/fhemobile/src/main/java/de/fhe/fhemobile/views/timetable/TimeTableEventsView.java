package de.fhe.fhemobile.views.timetable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.viewpagerindicator.CirclePageIndicator;

import org.junit.Assert;

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

    public TimeTableEventsView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setViewListener(final IViewListener _Listener) {
        mListener = _Listener;
    }

    public void initializeView(final FragmentManager _Manager) {
        mFragmentManager = _Manager;
    }

    public void setPagerItems(final ArrayList<TimeTableWeekVo> _Items) {

        mAdapter = new TimeTableWeekPagerAdapter(mFragmentManager, _Items);
        Assert.assertTrue( mAdapter != null );
        mPager.setAdapter(mAdapter);
        Assert.assertTrue( mPager != null );
        Assert.assertTrue( mPageIndicator != null );

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
