/*
 *  Copyright (c) 2019-2021 Ernst-Abbe-Hochschule Jena
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
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.navigation.NavigationDialogPagerAdapter;
import de.fhe.fhemobile.models.navigation.NavigationDialogModel;

/**
 * Created by Nadja 02.12.2021
 */
public class NavigationDialogView extends LinearLayout {

    private final Context mContext;
    // model for handling room list, person list and Vo (contains start and destination location,
    // needed for transferring to NavigationActivityOLD)
    private NavigationDialogModel mModel;


    private final OnClickListener mBtnStartNavigationListener;
    //todo: add listeners for alle textinputs, buttons and spinners


    public interface  ViewListener{

    }

    public NavigationDialogView(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext = context;
        mModel = NavigationDialogModel.getInstance();

        mBtnStartNavigationListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                //startNavigationActivity();
            }
        };
    }

    public void initializeView(@NonNull final FragmentManager _Manager, @NonNull Lifecycle _Lifecycle) {

        //set up tab layout
        TabLayout tabLayout = findViewById(R.id.navigationDialogTablayout);
        ViewPager2 viewPager = findViewById(R.id.navigationDialogViewpager);
        viewPager.setAdapter(new NavigationDialogPagerAdapter(_Manager, _Lifecycle));

        tabLayout.addTab(tabLayout.newTab().setText(R.string.tablayout_tabtext_roomsearch));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tablayout_tabtext_personsearch));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });



    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();

    }

    //start Navigation activity and send intent
    private void startNavigationActivity(final String userInputStartLocation, final ArrayList<String> roomNames, final boolean skipScanner) {

        //todo: update to new NavigationDialog
//        final Intent intentNavigationActivity = new Intent(getActivity(), NavigationActivityOLD.class);
//        intentNavigationActivity.putExtra("startLocation", userInputStartLocation);
//        intentNavigationActivity.putExtra("destinationLocation", destinationQRCode);
//        intentNavigationActivity.putExtra("rooms", roomsJson);
//        mContext.startActivity();

    }



}
