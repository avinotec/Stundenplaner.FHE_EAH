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
import android.view.View;
import android.view.ViewGroup;

import org.junit.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeMap;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.Exit;
import de.fhe.fhemobile.models.navigation.FloorConnection;
import de.fhe.fhemobile.models.navigation.Room;
import de.fhe.fhemobile.models.navigation.RouteCalculator;
import de.fhe.fhemobile.utils.navigation.JSONHandler;
import de.fhe.fhemobile.utils.navigation.NavigationUtils;
import de.fhe.fhemobile.utils.navigation.NavigationUtils.BuildingFloorKey;
import de.fhe.fhemobile.views.navigation.NavigationView;

/**
 *  Fragment for showing navigation route
 *  based on: Bachelor Thesis from Tim Münziger from SS2020
 *  created by: Nadja - 13.12.2021
 */
public class NavigationFragment extends FeatureFragment {

    private static final String TAG = "NavigationFragment"; //$NON-NLS


    public static final String PARAM_START = "paramStartRoom"; //$NON-NLS
    public static final String PARAM_DEST = "paramDestRoom"; //$NON-NLS

    //Variables
    private NavigationView mView;

    private Room mStartRoom;
    private Room mDestRoom;

    private static ArrayList<Exit> exits                        = new ArrayList<>();
    private static ArrayList<FloorConnection> floorConnections  = new ArrayList<>();

    //cellsToWalk here as TreeMap to have a list sorted for flicking through floorplans by buttons
    private TreeMap<BuildingFloorKey, ArrayList<Cell>> cellsToWalk = new TreeMap<>((o1, o2) -> {

        int value = o1.compareTo(o2);
        Log.d(TAG,o1.getComplex().toString() +"."+o1.getFloorString() +" vs "
                + o2.getComplex().toString() +"."+o2.getFloorString() + " -> "+value);
        return value;

    });

    //welcher Floorplan gerade angezeigt wird,
    // mit dem Key kann auf die entsprechenden Navigationszellen in cellsToWalk zugegriffen werden
    private BuildingFloorKey currentlyDisplayedPlan;


    public NavigationFragment(){
        // Required empty public constructor
    }

    public static NavigationFragment newInstance(Room _startRoom, Room _destRoom) {
        NavigationFragment fragment = new NavigationFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_START, _startRoom.getRoomName());
        args.putString(PARAM_DEST, _destRoom.getRoomName());
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) Assert.assertTrue( getArguments() != null );
        String start = getArguments().getString(PARAM_START);
        String dest = getArguments().getString(PARAM_DEST);

        mStartRoom = null;
        mDestRoom = null;

        for(Room room : MainActivity.rooms){
            if(room.getRoomName().equals(start)){
                mStartRoom = room;
            }
            if(room.getRoomName().equals(dest)){
                mDestRoom = room;
            }

            if(mStartRoom != null && mDestRoom != null) break;
        }

        getFloorConnections();
        getExits();
        getRoute();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = (NavigationView) inflater.inflate(R.layout.fragment_navigation, container, false);
        mView.setViewListener(mViewListener);

        mView.initializeView(mStartRoom, mDestRoom);

        drawNavigation(mStartRoom.getComplex(), mStartRoom.getFloorInt());

        return mView;

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    //load from assets -----------------------------------------------------------------------------
    /**
     * Load stairs, elevators and exits from JSON
     */
    private void getFloorConnections() {
        try {
            String json;

            // read only once
            if(exits.isEmpty()){
                json = JSONHandler.readFromAssets(getContext(), "exits");
                exits = JSONHandler.parseJsonExits(json);
            }
        } catch (Exception e) {
            Log.e(TAG, "error reading or parsing JSON files:", e);
        }
    }

    /**
     * Load exits from JSON
     */
    private void getExits() {
        try {
            String json;

            // read only once
            if (floorConnections.isEmpty()) {
                json = JSONHandler.readFromAssets(getContext(), "floorconnections");
                floorConnections = JSONHandler.parseJsonFloorConnection(json);
            }
        } catch (Exception e) {
            Log.e(TAG, "error reading or parsing JSON files:", e);
        }
    }


    //calculations ---------------------------------------------------------------------------------
    /**
     * Calculate route using the class RouteCalculator
     * Get an Arraylist of cells to walk through and add them to cellsToWalk
     */
    private void getRoute() {
        try {
            RouteCalculator routeCalculator = new RouteCalculator(getContext(),
                    mStartRoom, mDestRoom, floorConnections, exits);
            cellsToWalk.putAll(routeCalculator.getWholeRoute());
        } catch (Exception e) {
            Log.e(TAG,"error calculating route:", e);
        }
    }

    //drawing --------------------------------------------------------------------------------------

    /**
     * Display floorplan and route on it and sets currentlyDisplayedPlan (overloaded method)
     * @param complexToDisplay
     * @param floorToDisplay
     */
    public void drawNavigation(NavigationUtils.Complex complexToDisplay, int floorToDisplay){
        drawNavigation(new BuildingFloorKey(complexToDisplay, floorToDisplay));
    }

    /**
     * Display floorplan and route on it, sets currentlyDisplayedPlan
     * @param buildingFloorKey
     */
    public void drawNavigation(BuildingFloorKey buildingFloorKey){

        //removing old image and route icons needed when updating/changing the floor
        mView.removeRoute();

        currentlyDisplayedPlan = buildingFloorKey;

        //if currentlyDisplayedPlan is first plan on the route, then disable buttonPrevPlan
        if(currentlyDisplayedPlan.equals(cellsToWalk.firstKey())){
            mView.togglePrevPlanButtonEnabled(false);
        } else{
            mView.togglePrevPlanButtonEnabled(true);
        }

        //if currentlyDisplayedPlan is last plan on the route, then disable buttonNextPlan
        if(currentlyDisplayedPlan.equals(cellsToWalk.lastKey())){
            mView.toggleNextPlanButtonEnabled(false);
        } else{
            mView.toggleNextPlanButtonEnabled(true);
        }


        drawFloorPlan();
        drawRoute();
    }


    /**
     * Display route at the currently set building and floor (BuildingFloorKey)
     */
    private void drawRoute(){
        if (BuildConfig.DEBUG) Assert.assertTrue( currentlyDisplayedPlan != null );

        if(!mStartRoom.getRoomName().equals(mDestRoom.getRoomName())) {
            // add route (path of cells) to overlay
            mView.drawAllPathCells(currentlyDisplayedPlan, cellsToWalk);
        }

        if(mStartRoom.getComplex().equals(currentlyDisplayedPlan.getComplex())
                && mStartRoom.getFloorInt() == (currentlyDisplayedPlan.getFloorInt()) ){
            //Add icon for current user location room to overlay
            mView.drawStartLocation(mStartRoom);
        }

        if(mDestRoom.getComplex().equals(currentlyDisplayedPlan.getComplex())
                && mDestRoom.getFloorInt() == currentlyDisplayedPlan.getFloorInt()) {
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
            String path = getPathToFloorPlanPNG(currentlyDisplayedPlan.getComplex(),
                    currentlyDisplayedPlan.getFloorString());
            //grid for debugging: path = "floorplan_images/grid_for_debug.png"
            InputStream input = getActivity().getAssets().open(path);
            Drawable image = Drawable.createFromStream(input, null);
            //setFloorPlanImage
            mView.drawFloorPlanImage(image);
        } catch (IOException e) {
            Log.e(TAG, "Loading Floorplan Image from assets failed", e);
        }
    }


    //Listeners-------------------------------------------------------------------------------------
    private final NavigationView.IViewListener mViewListener = new NavigationView.IViewListener() {

        @Override
        public void onPrevPlanClicked() {
            //needed to get desired direction (if start is floor-upwards or floor-downwards)
            //example: startRoom 04.02, destRoom 04.01 -> floorDifference = 1
            int floorDifference = currentlyDisplayedPlan.compareTo(new BuildingFloorKey(mStartRoom));

            BuildingFloorKey prevFloorPlan = null;
            if (floorDifference > 0){
                prevFloorPlan= cellsToWalk.lowerKey(currentlyDisplayedPlan);

            } else if (floorDifference < 0){
                prevFloorPlan = cellsToWalk.higherKey(currentlyDisplayedPlan);
            }

            if(prevFloorPlan != null) {
                Log.d(TAG, "now: " + currentlyDisplayedPlan.getComplex().toString()+"."+currentlyDisplayedPlan.getFloorString() +
                        ", prev: " + prevFloorPlan.getComplex().toString()+"."+prevFloorPlan.getFloorString());
                drawNavigation(prevFloorPlan);
            }

        }


        @Override
        public void onNextPlanClicked() {

            //needed to get desired direction (if destination is floor-upwards or floor-downwards)
            //example: startRoom 03.03, destRoom 03.01 -> floorDifference = -2
            int floorDifference = currentlyDisplayedPlan.compareTo(new BuildingFloorKey(mDestRoom));

            BuildingFloorKey nextFloorPlan = null;
            if (floorDifference > 0){
                 nextFloorPlan = cellsToWalk.lowerKey(currentlyDisplayedPlan);

            } else if (floorDifference < 0){
                nextFloorPlan = cellsToWalk.higherKey(currentlyDisplayedPlan);
            }

            if (nextFloorPlan != null) {
                Log.d(TAG, "now: " + currentlyDisplayedPlan.getComplex().toString()+"."+currentlyDisplayedPlan.getFloorString() +
                        ", prev: " + nextFloorPlan.getComplex().toString()+"."+nextFloorPlan.getFloorString());
                drawNavigation(nextFloorPlan);
            }
        }
    };

}
