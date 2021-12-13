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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.Exit;
import de.fhe.fhemobile.models.navigation.FloorConnection;
import de.fhe.fhemobile.models.navigation.Room;
import de.fhe.fhemobile.models.navigation.RouteCalculator;
import de.fhe.fhemobile.utils.navigation.JSONHandler;
import de.fhe.fhemobile.utils.navigation.NavigationUtils;
import de.fhe.fhemobile.views.navigation.NavigationView;

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

    private Room startLocation;
    private Room destinationLocation;

    private static ArrayList<Exit> exits                        = new ArrayList<>();
    private static ArrayList<FloorConnection> floorConnections  = new ArrayList<>();
    private ArrayList<Cell> cellsToWalk                         = new ArrayList<>();


    //TODO: Variablen für Ersatz Spinnerauswahl (Liste aller benötigter Pläne + aktuell angezeigter)


    public NavigationFragment(){
        // Required empty public constructor
    }

    public static NavigationFragment newInstance(Room _startRoom, Room _destRoom) {
        NavigationFragment fragment = new NavigationFragment();
        fragment.startLocation = _startRoom;
        fragment.destinationLocation = _destRoom;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFloorConnections();
        getExits();
        getRoute();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = (NavigationView) inflater.inflate(R.layout.fragment_navigation, container, false);
        mView.setViewListener(mViewListener);

        try {
            //get image of the first displayed floorplan
            String path = getPathToFloorPlanPNG(startLocation.getComplex(), startLocation.getFloorString());
            //grid for debugging: path = "floorplan_images/grid_for_debug.png"
            InputStream input = getActivity().getAssets().open(path);
            Drawable image = Drawable.createFromStream(input, null);
            mView.initializeView(image);
        } catch (IOException e) {
            Log.e(TAG, "Loading Floorplan Image from assets failed", e);
        }

        drawNavigation(startLocation.getComplex(), startLocation.getFloorString());

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
                    startLocation, destinationLocation, floorConnections, exits);
            cellsToWalk.addAll(routeCalculator.getWholeRoute());
        } catch (Exception e) {
            Log.e(TAG,"error calculating route:", e);
        }
    }

    //drawing --------------------------------------------------------------------------------------
    /**
     * Display navigation at the particular floor
     * @param displayedComplex
     * @param displayedFloor
     */
    public void drawNavigation(NavigationUtils.Complex displayedComplex, String displayedFloor){

        if(!startLocation.getRoomName().equals(destinationLocation.getRoomName())) {
            mView.drawAllPathCells(displayedComplex, displayedFloor, cellsToWalk); // add route (path of cells) to overlay
        }

        if(startLocation.getComplex().equals(displayedComplex)
                && startLocation.getFloorString().equals(displayedFloor) ){
            mView.drawStartLocation(displayedComplex, displayedFloor, startLocation); //Add icon for current user location room to overlay
        }

        if(destinationLocation.getComplex().equals(displayedComplex)
                && destinationLocation.getFloorString().equals(displayedFloor)) {
            mView.drawDestinationLocation(displayedComplex, displayedFloor, destinationLocation); //Add destination location room icon to overlay
        }
        //drawAllFloorConnections(displayedComplex, floor, cellsToWalk); //Add floorConnection icons (like stairs, lifts, ...) to overlay
    }




    //Listeners-------------------------------------------------------------------------------------
    private final NavigationView.IViewListener mViewListener = new NavigationView.IViewListener() {
        @Override
        public void onPrevPlanClicked() {
            //TODO: aktuell angezeigten Plan eins runterzählen und Anzeige aktualisieren
            //drawNavigation()
        }

        @Override
        public void onNextPlanClicked() {
            //TODO: aktuell angezeigten Plan ein hochzählen und Anzeige aktualisieren
            //drawNavigation()
        }
    };

}
