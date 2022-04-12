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
package de.fhe.fhemobile.views.canteen;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import org.junit.Assert;

import java.util.List;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.canteen.CanteenPagerAdapter;
import de.fhe.fhemobile.vos.canteen.CanteenDayVo;

/**
 * Created by Nadja - 03/2022
 */
public class CanteenViewNEW extends LinearLayout {

    public CanteenViewNEW(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public void initializeView(final FragmentManager _Manager, final Lifecycle _Lifecycle) {

        mFragmentManager = _Manager;
        mLifecycle = _Lifecycle;
    }

    public void setPagerItems(final List<CanteenDayVo> _Items) {

        mAdapter = new CanteenPagerAdapter(mFragmentManager, mLifecycle, _Items);
        if (BuildConfig.DEBUG) Assert.assertNotNull(mAdapter);
        mPager.setAdapter(mAdapter);
        if (BuildConfig.DEBUG) Assert.assertNotNull(mPager);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPager      = (ViewPager2)   findViewById(R.id.viewpager_canteen);

    }


    private ViewPager2          mPager;
    private CanteenPagerAdapter mAdapter;
    private FragmentManager     mFragmentManager;
    private Lifecycle           mLifecycle;

}
