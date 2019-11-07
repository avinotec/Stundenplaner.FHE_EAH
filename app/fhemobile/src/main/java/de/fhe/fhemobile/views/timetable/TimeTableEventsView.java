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
        //Assert.assertTrue( mPageIndicator != null );

        //TODO Simon, warum ist das null?
        //mPageIndicator.setViewPager(mPager);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mPager         = (ViewPager)           findViewById(R.id.eventPager);

    }

    private final Context                     mContext;
    private IViewListener               mListener;
    private FragmentManager             mFragmentManager;

    private ViewPager                   mPager;
    private CirclePageIndicator         mPageIndicator;

    private TimeTableWeekPagerAdapter   mAdapter;
}
