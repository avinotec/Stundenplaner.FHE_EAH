/*
 * Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
