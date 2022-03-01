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

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import org.junit.Assert;

import java.util.ArrayList;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.timetable.TimeTableWeekPagerAdapter;
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

    public void setViewListener(final IViewListener _Listener) {
        // --Commented out by Inspection (02.11.2021 17:33):private final Context                     mContext;
    }

    public void initializeView(final FragmentManager _Manager) {
        mFragmentManager = _Manager;
    }

    public void setPagerItems(final ArrayList<TimeTableWeekVo> _Items) {

        mAdapter = new TimeTableWeekPagerAdapter(mFragmentManager, _Items);
        if (BuildConfig.DEBUG) Assert.assertNotNull(mAdapter);
        mPager.setAdapter(mAdapter);
        if (BuildConfig.DEBUG) Assert.assertNotNull(mPager);
        //if (BuildConfig.DEBUG) Assert.assertTrue( mPageIndicator != null );

        //TODO Simon, warum ist das null?
        //mPageIndicator.setViewPager(mPager);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPager         = (ViewPager)           findViewById(R.id.eventPager);
    }

    private FragmentManager             mFragmentManager;

    private ViewPager                   mPager;
    //private CirclePageIndicator         mPageIndicator;

    private TimeTableWeekPagerAdapter   mAdapter;
}
