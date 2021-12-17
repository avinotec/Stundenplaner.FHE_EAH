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
import java.util.LinkedHashMap;
import java.util.ListIterator;

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
import de.fhe.fhemobile.utils.navigation.BuildingFloorKey;
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

    //cellsToWalk here as LinkedHashMap to have a list sorted for flicking through floorplans by buttons
    //sorted by insertion order: first element is the last inserted (which is the destination floor)
    private LinkedHashMap<BuildingFloorKey, ArrayList<Cell>> cellsToWalk;

    //welcher Floorplan gerade angezeigt wird,
    // mit dem Key kann auf die entsprechenden Navigationszellen in cellsToWalk zugegriffen werden,
    // Wert wird vom floorPlanIterator bestimmt
    private BuildingFloorKey currentFloorPlan;
    private ListIterator<BuildingFloorKey> floorPlanIterator;


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

        //get first(= next) floorplan and draw its image and route
        drawNextNavigation();

        return mView;

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Set currentFloorPlan to the next one, display floorplan image and navigationroute
     */
    private void drawNextNavigation(){
        if(floorPlanIterator.hasPrevious()){
            currentFloorPlan = floorPlanIterator.previous();
            drawNavigation();

            updateButtonStatus();
        }
    }

    /**
     * Set currentFloorPlan to the previous one, display floorplan image and navigationroute
     */
    private void drawPrevNavigation(){
        if(floorPlanIterator.hasNext()){
            currentFloorPlan = floorPlanIterator.next();
            drawNavigation();

            updateButtonStatus();
        }
    }

    /**
     * Update buttons to being enabled or disabled
     */
    private  void updateButtonStatus(){
        //floorPlanIterator.hasNext() and hasPrevious() do not work because iterator can still
        // jump behind the start floorplan key and before the destination floorplan key
        //see java documentation:
        //                      Element(0)   Element(1)   Element(2)   ... Element(n-1)
        // cursor positions:  ^            ^            ^            ^                  ^
        //                    0            1            2            3                  n
        if (floorPlanIterator.hasNext() && floorPlanIterator.nextIndex() < cellsToWalk.keySet().size()-1) {
            mView.togglePrevPlanButtonEnabled(true);
        }
        //start floor reached -> disable prevButton
        else{
            mView.togglePrevPlanButtonEnabled(false);
        }

        if(floorPlanIterator.hasPrevious() && floorPlanIterator.nextIndex() > 0){
            mView.toggleNextPlanButtonEnabled(true);
        }
        //destination floor reached -> disable nextButton
        else{
            mView.toggleNextPlanButtonEnabled(false);
        }
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
     * Set floorPlanIterator
     */
    private void getRoute() {
        try {
            RouteCalculator routeCalculator = new RouteCalculator(getContext(),
                    mStartRoom, mDestRoom, floorConnections, exits);
            cellsToWalk = routeCalculator.getWholeRoute();
            floorPlanIterator = new ArrayList<>(cellsToWalk.keySet()).listIterator();

            //set iterator to end (because floorplans are sorted from dest to start)
            while(floorPlanIterator.hasNext()) floorPlanIterator.next();

        } catch (Exception e) {
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
        if (BuildConfig.DEBUG) Assert.assertTrue( currentFloorPlan != null );

        if(!mStartRoom.getRoomName().equals(mDestRoom.getRoomName())) {
            // add route (path of cells) to overlay
            mView.drawAllPathCells(currentFloorPlan, cellsToWalk);
        }

        if(mStartRoom.getComplex().equals(currentFloorPlan.getComplex())
                && mStartRoom.getFloorInt() == (currentFloorPlan.getFloorInt()) ){
            //Add icon for current user location room to overlay
            mView.drawStartLocation(mStartRoom);
        }

        if(mDestRoom.getComplex().equals(currentFloorPlan.getComplex())
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
            String path = getPathToFloorPlanPNG(currentFloorPlan.getComplex(),
                    currentFloorPlan.getFloorString());
            //grid for debugging: path = "floorplan_images/grid_for_debug.png"
            InputStream input = getActivity().getAssets().open(path);
            Drawable image = Drawable.createFromStream(input, null);
            //setFloorPlanImage
            mView.drawFloorPlanImage(image, currentFloorPlan);
        } catch (IOException e) {
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
