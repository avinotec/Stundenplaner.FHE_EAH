package de.fhe.fhemobile.adapters.timetable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import de.fhe.fhemobile.fragments.timetable.TimeTableWeekFragment;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;

/**
 * Created by paul on 23.02.14.
 */
public class TimeTableWeekPagerAdapter extends FragmentStatePagerAdapter {

    public TimeTableWeekPagerAdapter(FragmentManager fm, ArrayList<TimeTableWeekVo> _Data) {
        super(fm);
        mData = _Data;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return TimeTableWeekFragment.newInstance(mData.get(position));
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mData.size();
    }

    private ArrayList<TimeTableWeekVo> mData;
}
