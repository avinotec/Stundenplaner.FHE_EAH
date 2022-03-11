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
package de.fhe.fhemobile.views.mensa;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.mensa.MensaPagerAdapter;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.mensa.MensaFoodModel;
import de.fhe.fhemobile.utils.headerlistview.HeaderListView;
import de.fhe.fhemobile.vos.mensa.MensaDayVo;
import de.fhe.fhemobile.vos.mensa.MensaFoodItemCollectionVo;
import de.fhe.fhemobile.vos.mensa.MensaFoodItemVo;
import de.fhe.fhemobile.widgets.stickyHeaderList.DefaultHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.MensaImageRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.MensaRowItem;

/**
 * Created by Nadja - 03/2022
 */
public class MensaViewNEW extends LinearLayout {

    public MensaViewNEW(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public void initializeView(final FragmentManager _Manager, final Lifecycle _Lifecycle) {

        mFragmentManager = _Manager;
        mLifecycle = _Lifecycle;
    }

    public void setPagerItems(final List<MensaDayVo> _Items) {

        mAdapter = new MensaPagerAdapter(mFragmentManager, mLifecycle, _Items);
        if (BuildConfig.DEBUG) Assert.assertNotNull(mAdapter);
        mPager.setAdapter(mAdapter);
        if (BuildConfig.DEBUG) Assert.assertNotNull(mPager);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPager      = (ViewPager2)   findViewById(R.id.viewpager_mensa);

    }


    private ViewPager2          mPager;
    private MensaPagerAdapter   mAdapter;
    private FragmentManager     mFragmentManager;
    private Lifecycle           mLifecycle;

}
