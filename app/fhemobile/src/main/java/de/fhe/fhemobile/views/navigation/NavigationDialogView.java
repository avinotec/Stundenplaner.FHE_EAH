/*
 *  Copyright (c) 2019-2022 Ernst-Abbe-Hochschule Jena
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

package de.fhe.fhemobile.views.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import com.google.android.material.tabs.TabLayout;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.navigation.NavigationDialogPagerAdapter;

/**
 * Created by Nadja 02.12.2021
 */
public class NavigationDialogView extends LinearLayout {

    public NavigationDialogView(final Context context, final AttributeSet attrs){
        super(context, attrs);
    }

    public void initializeView(@NonNull final FragmentManager _Manager, @NonNull final Lifecycle _Lifecycle) {

        //set up tab layout
        final TabLayout tabLayout = findViewById(R.id.tablayout_navigation_dialog);
        final ViewPager2 viewPager = findViewById(R.id.viewpager_navigation_dialog);
        viewPager.setAdapter(new NavigationDialogPagerAdapter(_Manager, _Lifecycle));

        tabLayout.addTab(tabLayout.newTab().setText(R.string.tablayout_tabtext_roomsearch));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tablayout_tabtext_personsearch));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(final TabLayout.Tab tab) {            }

            @Override
            public void onTabReselected(final TabLayout.Tab tab) {            }
        });

        viewPager.registerOnPageChangeCallback(new OnPageChangeCallback() {
            @Override
            public void onPageSelected(final int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }


}
