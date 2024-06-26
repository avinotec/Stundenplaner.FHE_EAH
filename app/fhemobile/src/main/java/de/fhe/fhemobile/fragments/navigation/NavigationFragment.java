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

package de.fhe.fhemobile.fragments.navigation;

import static de.fhe.fhemobile.utils.navigation.NavigationUtils.getPathToFloorPlanPNG;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;

import org.junit.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.RouteCalculator;
import de.fhe.fhemobile.utils.navigation.BuildingFloorKey;
import de.fhe.fhemobile.utils.navigation.FloorPlanIterator;
import de.fhe.fhemobile.utils.navigation.NavigationUtils;
import de.fhe.fhemobile.views.navigation.NavigationView;
import de.fhe.fhemobile.vos.navigation.BuildingExitVo;
import de.fhe.fhemobile.vos.navigation.FloorConnectionVo;
import de.fhe.fhemobile.vos.navigation.RoomVo;

/**
 *  Fragment for showing navigation route
 *  based on: Bachelor Thesis from Tim Münziger from SS2020
 *  created by: Nadja - 13.12.2021
 */
public class NavigationFragment extends FeatureFragment {

    public static final String TAG = NavigationFragment.class.getSimpleName();


    public static final String PARAM_START = "paramStartRoom"; //$NON-NLS
    public static final String PARAM_DEST = "paramDestRoom"; //$NON-NLS
    public static ArrayList<RoomVo> rooms = new ArrayList<>();

    //Variables
    private NavigationView mView;

    private RoomVo mStartRoom;
    private RoomVo mDestRoom;

    public static ArrayList<BuildingExitVo> buildingExits = new ArrayList<>();
    public static ArrayList<FloorConnectionVo> floorConnections  = new ArrayList<>();

    //cellsToWalk here as LinkedHashMap to have a list sorted for flicking through floorplans by buttons
    //sorted by insertion order: first element is the last inserted (which is the destination floor)
    private LinkedHashMap<BuildingFloorKey, ArrayList<Cell>> cellsToWalk;

    //welcher Floorplan gerade angezeigt wird,
    // mit dem Key kann auf die entsprechenden Navigationszellen in cellsToWalk zugegriffen werden,
    // Wert wird vom floorPlanIterator bestimmt
    private BuildingFloorKey currentFloorPlan;
    private FloorPlanIterator floorPlanIterator;


    public NavigationFragment(){
        super(TAG);
    }

    public static NavigationFragment newInstance(final RoomVo _startRoom, final RoomVo _destRoom) {
        final NavigationFragment fragment = new NavigationFragment();
        final Bundle args = new Bundle();
        args.putString(PARAM_START, _startRoom.getRoomName());
        args.putString(PARAM_DEST, _destRoom.getRoomName());
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) Assert.assertNotNull(getArguments());
        final String start = getArguments().getString(PARAM_START);
        final String dest = getArguments().getString(PARAM_DEST);

        mStartRoom = null;
        mDestRoom = null;

        for(final RoomVo room : rooms){
            if(room.getRoomName().equals(start)){
                mStartRoom = room;
            }
            if(room.getRoomName().equals(dest)){
                mDestRoom = room;
            }

            if(mStartRoom != null && mDestRoom != null) break;
        }

        NavigationUtils.getFloorConnections();
        NavigationUtils.getExits();
        getRoute();
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = (NavigationView) inflater.inflate(R.layout.fragment_navigation, container, false);
        mView.setViewListener(mViewListener);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                getActivity().onBackPressed();
            }
        });

        mView.initializeView(mStartRoom, mDestRoom);

        //get first(= next) floorplan and draw its image and route
        drawNextNavigation();

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //replacement of deprecated setHasOptionsMenu(), onCreateOptionsMenu() and onOptionsItemSelected()
        // see https://developer.android.com/jetpack/androidx/releases/activity#1.4.0-alpha01
        final MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull final Menu menu, @NonNull final MenuInflater menuInflater) {
                // Add menu items here
                menu.clear();
                menuInflater.inflate(R.menu.menu_main, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull final MenuItem menuItem) {
                // Handle the menu selection
                return false;
            }
        });
    }

    /**
     * Set currentFloorPlan to the next one, display floorplan image and navigation route
     */
    void drawNextNavigation(){
        if(floorPlanIterator.hasNext()){
            currentFloorPlan = floorPlanIterator.next();
            drawNavigation();

            updateButtonStatus();
        }
    }

    /**
     * Set currentFloorPlan to the previous one, display floorplan image and navigationroute
     */
    void drawPrevNavigation(){
        if(floorPlanIterator.hasPrevious()){
            currentFloorPlan = floorPlanIterator.previous();
            drawNavigation();

            updateButtonStatus();
        }
    }

    /**
     * Update buttons to being enabled or disabled
     */
    private  void updateButtonStatus(){
	    //start floor reached -> disable prevButton
	    mView.togglePrevPlanButtonEnabled(floorPlanIterator.hasPrevious());

	    //destination floor reached -> disable nextButton
	    mView.toggleNextPlanButtonEnabled(floorPlanIterator.hasNext());
    }


    //calculations ---------------------------------------------------------------------------------

    /**
     * Calculate route using the class RouteCalculator
     * Get an Arraylist of cells to walk through and add them to cellsToWalk
     * Set floorPlanIterator
     */
    private void getRoute() {
        try {
            final RouteCalculator routeCalculator =
                    new RouteCalculator(mStartRoom, mDestRoom, floorConnections, buildingExits);
            cellsToWalk = routeCalculator.getWholeRoute();
            floorPlanIterator = new FloorPlanIterator(new ArrayList<>(cellsToWalk.keySet()));

        } catch (final RuntimeException e) {
            Log.e(TAG,"error calculating route:", e);
        }
    }

    //drawing --------------------------------------------------------------------------------------

    /**
     * Displays floorplan image and route corresponding to the current value of currentFloorPlan
     */
    private void drawNavigation(){

        //removing old image and route icons needed when updating/changing the floor
        mView.removeRoute();

        drawFloorPlan();
        drawRoute();
    }


    /**
     * Display route at the currently set building and floor (BuildingFloorKey)
     */
    private void drawRoute(){
        if (BuildConfig.DEBUG) Assert.assertNotNull(currentFloorPlan);

        if(!mStartRoom.getRoomName().equals(mDestRoom.getRoomName())) {
            // add route (path of cells) to overlay
            mView.drawAllPathCells(currentFloorPlan, cellsToWalk);
        }

        if(mStartRoom.getComplex() == currentFloorPlan.getComplex()
                && mStartRoom.getFloorInt() == (currentFloorPlan.getFloorInt()) ){
            //Add icon for current user location room to overlay
            mView.drawStartLocation(mStartRoom);
        }

        if(mDestRoom.getComplex() == currentFloorPlan.getComplex()
                && mDestRoom.getFloorInt() == currentFloorPlan.getFloorInt()) {
            mView.drawDestinationLocation(mDestRoom); //Add destination location room icon to overlay
        }

        //drawAllFloorConnections(displayedComplex, floor, cellsToWalk); //Add floorConnection icons (like stairs, lifts, ...) to overlay
    }

    /**
     * Sets the floor plan image corresponding to the currently set BuildingFloorKey
     */
    private void drawFloorPlan(){
        try {
            //get image of the first displayed floorplan
            final String path = getPathToFloorPlanPNG(currentFloorPlan.getComplex(),
                    currentFloorPlan.getFloorString());
            //grid for debugging: path = "floorplan_images/grid_for_debug.png"
            final InputStream input = getContext().getAssets().open(path);
            final Drawable image = Drawable.createFromStream(input, null);
            //setFloorPlanImage
            mView.drawFloorPlanImage(image, currentFloorPlan);
        } catch (final IOException e) {
            Log.e(TAG, "Loading Floorplan Image from assets failed", e);
        }
    }

    //Listeners-------------------------------------------------------------------------------------
    private final NavigationView.IViewListener mViewListener = new NavigationView.IViewListener() {

        @Override
        public void onPrevPlanClicked() {
            drawPrevNavigation();
        }


        @Override
        public void onNextPlanClicked() {
            drawNextNavigation();
        }
    };

}
