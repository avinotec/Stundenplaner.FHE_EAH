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
import java.util.HashMap;

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
import de.fhe.fhemobile.views.navigation.NavigationView;
import de.fhe.fhemobile.utils.navigation.NavigationUtils.BuildingFloorKey;

/**
 *  Fragment for showing navigation route
 *  based on: Bachelor Thesis from Tim Münziger from SS2020
 *  created by: Nadja - 13.12.2021
 */
public class NavigationFragment extends FeatureFragment {

    private static final String TAG = "navigationFragment"; //$NON-NLS


    public static final String PARAM_START = "paramStartRoom"; //$NON-NLS
    public static final String PARAM_DEST = "paramDestRoom"; //$NON-NLS

    //Variables
    private NavigationView mView;

    private Room mStartRoom;
    private Room mDestRoom;

    private static ArrayList<Exit> exits                        = new ArrayList<>();
    private static ArrayList<FloorConnection> floorConnections  = new ArrayList<>();
    private HashMap<BuildingFloorKey, ArrayList<Cell>> cellsToWalk = new HashMap<>();


    //TODO: Variablen für Ersatz Spinnerauswahl (Liste aller benötigter Pläne + aktuell angezeigter)


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
        //TODO: solve how to clear floorplan and navigation without messing up deleting the floorplan image view too (only needs to be replace not removed)
        //mView.removeRoute();

        drawFloorPlan(mStartRoom.getComplex(), mStartRoom.getFloorString());
        drawRoute(mStartRoom.getComplex(), mStartRoom.getFloorString());

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
     * Display route at the particular floor
     * @param displayedComplex
     * @param displayedFloor
     */
    public void drawRoute(NavigationUtils.Complex displayedComplex, String displayedFloor){

        if(!mStartRoom.getRoomName().equals(mDestRoom.getRoomName())) {
            mView.drawAllPathCells(displayedComplex, displayedFloor, cellsToWalk); // add route (path of cells) to overlay
        }

        if(mStartRoom.getComplex().equals(displayedComplex)
                && mStartRoom.getFloorString().equals(displayedFloor) ){
            mView.drawStartLocation(mStartRoom); //Add icon for current user location room to overlay
        }

        if(mDestRoom.getComplex().equals(displayedComplex)
                && mDestRoom.getFloorString().equals(displayedFloor)) {
            mView.drawDestinationLocation(mDestRoom); //Add destination location room icon to overlay
        }
        //drawAllFloorConnections(displayedComplex, floor, cellsToWalk); //Add floorConnection icons (like stairs, lifts, ...) to overlay
    }

    /**
     * Sets the floor plan image, old floor plan image and icons are removed
     * @param complex of the floorplan
     * @param floor of the floorplan
     */
    public void drawFloorPlan(NavigationUtils.Complex complex, String floor){
        try {
            //get image of the first displayed floorplan
            String path = getPathToFloorPlanPNG(complex, floor);
            //grid for debugging: path = "floorplan_images/grid_for_debug.png"
            InputStream input = getActivity().getAssets().open(path);
            Drawable image = Drawable.createFromStream(input, null);
            //setFloorPlanImage includes removing all old views from the relative layout
            mView.setFloorPlanImage(image);
        } catch (IOException e) {
            Log.e(TAG, "Loading Floorplan Image from assets failed", e);
        }
    }




    //Listeners-------------------------------------------------------------------------------------
    private final NavigationView.IViewListener mViewListener = new NavigationView.IViewListener() {
        @Override
        public void onPrevPlanClicked() {
            //TODO: aktuell angezeigten Plan eins runterzählen und Anzeige aktualisieren
//            drawFloorPlan(,);
//            drawRoute();
        }

        @Override
        public void onNextPlanClicked() {
            //TODO: aktuell angezeigten Plan ein hochzählen und Anzeige aktualisieren
//            drawFloorPlan();
//            drawRoute();
        }
    };

}
