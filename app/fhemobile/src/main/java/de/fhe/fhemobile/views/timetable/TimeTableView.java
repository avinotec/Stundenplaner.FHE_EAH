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
package de.fhe.fhemobile.views.timetable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Date;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.timetable.TimeTablePagerAdapter;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;

//import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by paul on 13.03.15.
 */
public class TimeTableView extends LinearLayout {

    public interface IViewListener {

    }

    public TimeTableView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public void initializeView(final FragmentManager _Manager, final Lifecycle _Lifecycle) {
        mFragmentManager = _Manager;
        mLifecycle = _Lifecycle;
    }

    public void setPagerItems(final ArrayList<TimeTableWeekVo> _Items) {

        TimeTablePagerAdapter mAdapter = new TimeTablePagerAdapter(mFragmentManager, mLifecycle, _Items);
        if (BuildConfig.DEBUG) Assert.assertNotNull(mAdapter);
        mPager.setAdapter(mAdapter);
        if (BuildConfig.DEBUG) Assert.assertNotNull(mPager);
        //set current pager item to current semester week
        for(TimeTableWeekVo item : _Items){
            //if the item's week end is after or equal now
            // and the item's week start is before or equal now
            if(!item.getWeekEnd().before(new Date())){
                if(!item.getWeekStart().after(new Date())){
                    //set current pager item
                    mPager.setCurrentItem(_Items.indexOf(item));
                }
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPager = (ViewPager2) findViewById(R.id.timetable_eventPager);
    }

    private FragmentManager     mFragmentManager;
    private Lifecycle           mLifecycle;

    private ViewPager2                   mPager;

}
