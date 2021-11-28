/*
 *  Copyright (c) 2014-2021 Ernst-Abbe-Hochschule Jena
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

package de.fhe.fhemobile.activities;

import static de.fhe.fhemobile.utils.Define.Navigation.FLOORCONNECTION_TYPE_ELEVATOR;
import static de.fhe.fhemobile.utils.Define.Navigation.FLOORCONNECTION_TYPE_EXIT;
import static de.fhe.fhemobile.utils.Define.Navigation.FLOORCONNECTION_TYPE_STAIR;
import static de.fhe.fhemobile.utils.Define.Navigation.cellgrid_height;
import static de.fhe.fhemobile.utils.Define.Navigation.cellgrid_width;
import static de.fhe.fhemobile.utils.navigation.NavigationUtils.Complex;
import static de.fhe.fhemobile.utils.navigation.NavigationUtils.getPathToFloorPlanPNG;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Locale;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.Exit;
import de.fhe.fhemobile.models.navigation.FloorConnection;
import de.fhe.fhemobile.models.navigation.FloorConnectionCell;
import de.fhe.fhemobile.models.navigation.Room;
import de.fhe.fhemobile.utils.navigation.JSONHandler;
import de.fhe.fhemobile.utils.navigation.RouteCalculator;

/**
 *  Activity for showing navigation route
 *  source: Bachelor Thesis from Tim Münziger from SS2020
 *  edit and integration: Nadja 09.2021
 */
public class NavigationActivity extends BaseActivity {

    //Constants
    private static final String TAG = "NavigationActivity"; //$NON-NLS
    private static final String JUST_LOCATION = "location"; //$NON-NLS


    //Variables
    private String destinationQRCode;
    private String currentLocation;
    private Room startLocation;
    private Room destinationLocation;

    private static ArrayList<Room> rooms;
    private static ArrayList<Exit> exits;
    private static ArrayList<FloorConnection> floorConnections = new ArrayList<>();
    private ArrayList<Cell> cellsToWalk = new ArrayList<>();

    private double cellWidth;
    private double cellHeight;
    private RelativeLayout navigationLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get extra from parent
        Intent intendActivity = getIntent();
        currentLocation = intendActivity.getStringExtra("startLocation");
        destinationQRCode = intendActivity.getStringExtra("destinationLocation");
        final String roomsJSON = intendActivity.getStringExtra("rooms");
        JSONHandler jsonHandler = new JSONHandler();
        rooms = jsonHandler.parseJsonRooms(roomsJSON);

        //Spinner select floor plans
        final ArrayList<String> floorPlans = new ArrayList<>(getSpinnerItemsList());

        final Spinner floorPlansSpinner = findViewById(R.id.spinner_floor_plans);
        ArrayAdapter<String> floorPlansAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_text, floorPlans);
        floorPlansSpinner.setAdapter(floorPlansAdapter);
        floorPlansSpinner.setSelection(0, false);
        floorPlansSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //draw map and navigation when floor is selected
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long id) {

                Object item = adapterView.getItemAtPosition(index);

                if (item != null && index != 0) {
                    try {
                        ArrayList<String> helperBuildingAndFloor = getBuildingAndFloor((String) item);

                        drawNavigationAndRoute(Complex.getEnum(helperBuildingAndFloor.get(0)), helperBuildingAndFloor.get(1));
                    } catch (Exception e) {
                        Log.e(TAG, " error changing map:", e);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        //Get rooms, stairs, elevators, exits, bridge from JSON
        // lädt die verfügbaren Räume und Aufgänge aus den entsprechenden JSON-Dateien
        getFloorConnectionsAndExits();

        //Get current user location room
        //setzt den übermittelten Standort den Startraums, bzw. die aktuelle Position des Nutzers
        getCurrentLocation();

        //Get destination location room
        if (!JUST_LOCATION.equals(destinationQRCode)) getDestinationLocation();

        //Calculate route (get ArrayList<Cell> of cells to walk)
        if (!JUST_LOCATION.equals(destinationQRCode)) getRoute();

        //get relative layout to add views to
        navigationLayout = findViewById(R.id.navigation_placeholder);

        try {
            //draws maps, navigation and route
            drawNavigationAndRoute(startLocation.getComplex(), startLocation.getFloorString());
        } catch (Exception e) {
            Log.e(TAG,"error drawing floor plan or navigation:", e);
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//    }

//    @Override
//    public void onPause() {
//        super.onPause();
//    }


    /**
     * Draw floorplan, navigation and route
     * by adding the corresponding elements to the relativeLayout (navigationLayout)
     * @param displayedComplex that currently should be displayed
     * @param floor to be displayed (floorplan)
     */
    private void drawNavigationAndRoute(final Complex displayedComplex, final String floor) {
        //Remove views from layouts before redrawing
        if (navigationLayout != null) navigationLayout.removeAllViews();

        //Set the floor plan ImageView in the relative layout where the whole navigation is drawn in
        ImageView floorPlanView = new ImageView(this);
        floorPlanView.setImageResource(getResources().getIdentifier(
                getPathToFloorPlanPNG(displayedComplex, floor), null, getPackageName()));
        //grid for debugging
        //floorPlanView.setImageResource(getResources().getIdentifier("drawable/grid_for_debug", null, getPackageName()));
        if (navigationLayout != null) navigationLayout.addView(floorPlanView);
        floorPlanView.setX(0);
        floorPlanView.setY(0);

        //The actual displayed size of the floorplans ImageView is needed to determine correct positioning if the icon
        //But width and height are not available in the onCreate() (because the ImageView has not really be drawn at this time)
        //solution: call the post method and override run() - this code then gets executed after the drawing phase so width and height are now existing (not 0)
        floorPlanView.post(new Runnable() {
            @Override
            public void run() {
                //get display size of the floorplan's ImageView
                int floorplanDisplayWidth = getResources().getDisplayMetrics().widthPixels;
                //Dreisatz Rescaling //getWidth and getHeight deliver the original size of the picture
                int floorplanDisplayHeight = floorPlanView.getHeight() * floorplanDisplayWidth / floorPlanView.getWidth();

                //get height and width of a single cell
                cellWidth = floorplanDisplayWidth / cellgrid_width;
                cellHeight = floorplanDisplayHeight / cellgrid_height;

                //rescale floorplan to match display width
                ViewGroup.LayoutParams layoutParams = floorPlanView.getLayoutParams();
                layoutParams.width = floorplanDisplayWidth;
                layoutParams.height = floorplanDisplayHeight;
                floorPlanView.setLayoutParams(layoutParams);
                //fits image height and width (aspect ratio of the original resource is not maintained -> width and height must be set properly)
                floorPlanView.setScaleType(ImageView.ScaleType.FIT_XY);

                //Draw navigation
                drawAllPathCells(displayedComplex, floor); // add route (path of cells) to overlay
                drawStartLocation(displayedComplex, floor); //Add icon for current user location room to overlay
                drawDestinationLocation(displayedComplex, floor); //Add destination location room icon to overlay
                drawAllFloorConnections(displayedComplex, floor); //Add floorConnection icons (like stairs, lifts, ...) to overlay
            }
        });

    }

    /**
     * Convert cell position from unit "cell number" to a unit for displaying
     * @param x position in cells
     * @return x in the displaying unit
     */
    private int convertCellCoordX(int x){
        return (int) Math.round( x * cellWidth);
    }

    /**
     * Convert the cell position from unit cellnumber to a unit for displaying
     * @param y position in cells
     * @return y in the displaying unit
     */
    private int convertCellCoordY(int y){
        return (int) Math.round(y * cellHeight);
    }

    /**
     * Draw all path cells of the calculated route
     * @param complex that is displayed (floorplan)
     * @param floor that is displayed (floorplan)
     */
    private void drawAllPathCells(Complex complex, String floor) {
        try {
            if (!destinationQRCode.equals(JUST_LOCATION)) {
                for (int j = 0; j < cellsToWalk.size(); j++) {
                    if (cellsToWalk.get(j).getComplex().equals(complex)
                            && cellsToWalk.get(j).getFloorString().equals(floor)) {

                        drawPathCell(cellsToWalk.get(j));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error drawing route:",e);
        }
    }


    /**
     * Add a cell icon to display a walkable cell of the calculated route
     * @param cell to draw
     */
    private void drawPathCell(Cell cell){
        ImageView pathCellIcon = new ImageView(this);
        pathCellIcon.setImageResource(R.drawable.path_cell_icon);
        if (navigationLayout != null) navigationLayout.addView(pathCellIcon);

        fitOneCell(pathCellIcon);
        pathCellIcon.setX(convertCellCoordX(cell.getXCoordinate()));
        pathCellIcon.setY(convertCellCoordY(cell.getYCoordinate()));


    }

    /**
     * Draw start location
     * @param complex that is displayed (floorplan)
     * @param floor that is displayed (floorplan)
     */
    private void drawStartLocation(Complex complex, String floor) {
        try {
            //check if input valid
            if (startLocation.getComplex().equals(complex) && startLocation.getFloorString().equals(floor)) {
                //add start icon
                ImageView startIcon = new ImageView(this);
                startIcon.setImageResource(R.drawable.start_icon);
                if (navigationLayout != null) navigationLayout.addView(startIcon);
                fitOneCell(startIcon);
                startIcon.setX(convertCellCoordX(startLocation.getXCoordinate()));
                startIcon.setY(convertCellCoordY(startLocation.getYCoordinate()));
            }
        } catch (Exception e) {
            Log.e(TAG, "error drawing current location room:", e);
        }

    }

    /**
     * Draw destination location
     * @param complex that is displayed (floorplan)
     * @param floor that is displayed (floorplan)
     */
    private void drawDestinationLocation(Complex complex, String floor) {
        try {

            if (!destinationQRCode.equals(JUST_LOCATION)) {

                if (destinationLocation.getComplex().equals(complex) && floor.equals(destinationLocation.getFloorString())) {

                    //add destination icon
                    ImageView destinationIcon = new ImageView(this);
                    destinationIcon.setImageResource(R.drawable.destination_icon);
                    if (navigationLayout != null) navigationLayout.addView(destinationIcon);
                    fitOneCell(destinationIcon);
                    destinationIcon.setX(convertCellCoordX(destinationLocation.getXCoordinate()));
                    destinationIcon.setY(convertCellCoordY(destinationLocation.getYCoordinate()));

                }
            }
        } catch (Exception e) {
            Log.e(TAG,"error drawing destination location room:", e);
        }
    }

    /**
     * Add all elevators, staircases, exits on displayed floorplan image
     * @param complex that is displayed (floorplan)
     * @param floor that is displayed (floorplan)
     */
    private void drawAllFloorConnections(Complex complex, String floor){
        try {
            for (FloorConnection fc : floorConnections) {
                for (FloorConnectionCell cell : fc.getConnectedCells()) {
                    drawFloorConnection(complex, floor, cell);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error drawing floorConnections:", e);
        }
    }

    /**
     * Draw floorconnection icon corresponding to the floorconnection type (stairs, elevator, exits, birdge)
     * @param complex that is displayed (floorplan)
     * @param floor that is displayed (floorplan)
     * @param fCell FloorConnectionCell to draw
     */
    private void drawFloorConnection(Complex complex, String floor, FloorConnectionCell fCell) {

        if (fCell.getComplex().equals(complex)
                && fCell.getFloorString().equals(floor)) {

            if (fCell.getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_STAIR)) {
                ImageView stairIcon = new ImageView(this);
                stairIcon.setImageResource(R.drawable.stairs_icon);
                if (navigationLayout != null) navigationLayout.addView(stairIcon);

                fitOneCell(stairIcon);
                stairIcon.setX(convertCellCoordX(fCell.getXCoordinate()));
                stairIcon.setY(convertCellCoordY(fCell.getYCoordinate()));
            }

            if (fCell.getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_ELEVATOR)) {
                ImageView elevatorIcon = new ImageView(this);
                elevatorIcon.setImageResource(R.drawable.elevator_icon);
                if (navigationLayout != null) navigationLayout.addView(elevatorIcon);

                fitOneCell(elevatorIcon);
                elevatorIcon.setX(convertCellCoordX(fCell.getXCoordinate()));
                elevatorIcon.setY(convertCellCoordY(fCell.getYCoordinate()));
            }

            if (fCell.getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_EXIT)) {
                ImageView exitIcon = new ImageView(this);
                exitIcon.setImageResource(R.drawable.exit_icon);
                if (navigationLayout != null) navigationLayout.addView(exitIcon);

                fitOneCell(exitIcon);
                exitIcon.setX(convertCellCoordX(fCell.getXCoordinate()));
                exitIcon.setY(convertCellCoordY(fCell.getYCoordinate()));
            }

            //bridge is ignored
//            if (fCell.getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_BRIDGE)) {
//                ImageView bridgeIcon = new ImageView(this);
//                bridgeIcon.setImageResource(R.drawable.bridge_icon);
//                if (navigationLayout != null) navigationLayout.addView(bridgeIcon);
//
//                fitOneCell(bridgeIcon);
//                bridgeIcon.setX(convertCellCoordX(fCell.getXCoordinate()));
//                bridgeIcon.setY(convertCellCoordY(fCell.getYCoordinate()));
//            }
        }
    }

    /**
     * Make the Icon Picture fit exactly one cell
     * @param icon ImageView containing the icon (view must be added to parent view before calling)
     */
    private void fitOneCell(ImageView icon){
        //set height and width of the ImageView
        ViewGroup.LayoutParams layoutParams = icon.getLayoutParams();
        layoutParams.height = (int) Math.round(cellHeight*0.95); //fill 95% of the cell
        layoutParams.width = (int) Math.round(cellWidth*0.95);
        icon.setLayoutParams(layoutParams);
        //make picture fit image height and width (aspect ratio of the resource is not maintained -> width and height must be set properly)
        icon.setScaleType(ImageView.ScaleType.FIT_XY);
    }


    /**
     * Loads resources to get the items for the floorplan spinner
     * @return list of spinner items
     */
    private ArrayList<String> getSpinnerItemsList() {

        ArrayList<String> spinnerItems = new ArrayList<>();
        Resources resource = getResources();
        String defaultSelection = resource.getString(R.string.select_from_spinner);

        spinnerItems.add(defaultSelection);
        spinnerItems.add(resource.getString(R.string.building_03_02_01_floor_ug));
        spinnerItems.add(resource.getString(R.string.building_03_02_01_floor_00));
        spinnerItems.add(resource.getString(R.string.building_03_02_01_floor_01));
        spinnerItems.add(resource.getString(R.string.building_03_02_01_floor_02));
        spinnerItems.add(resource.getString(R.string.building_03_02_01_floor_03));
        spinnerItems.add(resource.getString(R.string.building_03_02_01_floor_04));
        spinnerItems.add(resource.getString(R.string.building_04_floor_ug));
        spinnerItems.add(resource.getString(R.string.building_04_floor_00));
        spinnerItems.add(resource.getString(R.string.building_04_floor_01));
        spinnerItems.add(resource.getString(R.string.building_04_floor_02));
        spinnerItems.add(resource.getString(R.string.building_04_floor_03));
        spinnerItems.add(resource.getString(R.string.building_05_floor_ug2));
        spinnerItems.add(resource.getString(R.string.building_05_floor_ug1));
        spinnerItems.add(resource.getString(R.string.building_05_floor_00));
        spinnerItems.add(resource.getString(R.string.building_05_floor_01));
        spinnerItems.add(resource.getString(R.string.building_05_floor_02));
        spinnerItems.add(resource.getString(R.string.building_05_floor_03_level1));
        spinnerItems.add(resource.getString(R.string.building_05_floor_03_level2));

        return spinnerItems;
    }


    /**
     * Load stairs, elevators and exits from JSON
     */
    private void getFloorConnectionsAndExits() {
        try {
            JSONHandler jsonHandler = new JSONHandler();
            String json;

            // read only once
            if (floorConnections.isEmpty()) {
                json = JSONHandler.readFromAssets(this, "floorconnections");
                floorConnections = jsonHandler.parseJsonFloorConnection(json);
            }
            if(exits.isEmpty()){
                json = JSONHandler.readFromAssets(this, "exits");
                exits = jsonHandler.parseJsonExits(json);
            }

        } catch (Exception e) {
            Log.e(TAG, "error reading or parsing JSON files:", e);
        }
    }


    /**
     * Globally set the user's current location room
     */
    private void getCurrentLocation() {
        try {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).getQRCode().equals(currentLocation)) {
                    startLocation = rooms.get(i);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "QR-Code invalid:", e);
        }
    }


    /**
     * Globally set the user's destination room
     */
    private void getDestinationLocation() {

        try {
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).getQRCode().equals(destinationQRCode)) {
                    destinationLocation = rooms.get(i);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error getting destination location room", e);
        }
    }


    /**
     * Calculate route using the class RouteCalculator
     * Get an Arraylist of cells to walk through and add them to cellsToWalk
     */
    private void getRoute() {
        try {
            RouteCalculator routeCalculator = new RouteCalculator(this,
                    startLocation, destinationLocation, floorConnections, rooms, exits);
            cellsToWalk.addAll(routeCalculator.getWholeRoute());
        } catch (Exception e) {
            Log.e(TAG,"error calculating route:", e);
        }
    }


    /**
     * Get building and floor Strings from floor plan String
     * @param in name of the building and floor in the Spinner
     * @return
     */
    private ArrayList<String> getBuildingAndFloor(final String in) {
        //TODO: Methode umstrukturieren im Zuge der Spinnerüberarbeitung

        ArrayList<String> helperBuildingAndFloor = new ArrayList<>();


        try {
            if (in.equals(getLocaleStringResource(R.string.building_03_02_01_floor_ug))) {
                helperBuildingAndFloor.add("03");
                helperBuildingAndFloor.add("ug1");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_03_02_01_floor_00))) {
                helperBuildingAndFloor.add("03");
                helperBuildingAndFloor.add("00");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_03_02_01_floor_01))) {
                helperBuildingAndFloor.add("03");
                helperBuildingAndFloor.add("01");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_03_02_01_floor_02))) {
                helperBuildingAndFloor.add("03");
                helperBuildingAndFloor.add("02");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_03_02_01_floor_03))) {
                helperBuildingAndFloor.add("03");
                helperBuildingAndFloor.add("03");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_03_02_01_floor_04))) {
                helperBuildingAndFloor.add("03");
                helperBuildingAndFloor.add("04");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_04_floor_ug))) {
                helperBuildingAndFloor.add("04");
                helperBuildingAndFloor.add("ug1");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_04_floor_00))) {
                helperBuildingAndFloor.add("04");
                helperBuildingAndFloor.add("00");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_04_floor_01))) {
                helperBuildingAndFloor.add("04");
                helperBuildingAndFloor.add("01");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_04_floor_02))) {
                helperBuildingAndFloor.add("04");
                helperBuildingAndFloor.add("02");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_04_floor_03))) {
                helperBuildingAndFloor.add("04");
                helperBuildingAndFloor.add("03");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_05_floor_ug1))) {
                helperBuildingAndFloor.add("05");
                helperBuildingAndFloor.add("ug1");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_05_floor_ug2))) {
                helperBuildingAndFloor.add("05");
                helperBuildingAndFloor.add("ug2");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_05_floor_00))) {
                helperBuildingAndFloor.add("05");
                helperBuildingAndFloor.add("00");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_05_floor_01))) {
                helperBuildingAndFloor.add("05");
                helperBuildingAndFloor.add("01");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_05_floor_02))) {
                helperBuildingAndFloor.add("05");
                helperBuildingAndFloor.add("02");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_05_floor_03_level1))) {
                helperBuildingAndFloor.add("05");
                helperBuildingAndFloor.add("03");
            }
            else if (in.equals(getLocaleStringResource(R.string.building_05_floor_03_level2))) {
                helperBuildingAndFloor.add("05");
                helperBuildingAndFloor.add("03");
                //todo: Unterscheidung level1 und level2
            }
        } catch (Exception e) {
            Log.e(TAG, "error getting building and floor:", e);
        }
        return helperBuildingAndFloor;
    }


    /**
     * get locale
     * @param floorPlan ResourceID from Floorplan i.e. R.string.floorplan ....
     * @return
     */
    private String getLocaleStringResource(int floorPlan) {

        String localeString = "";

        final Locale currentLocale;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentLocale = getResources().getConfiguration().getLocales().get(0);
        } else {
            currentLocale = getResources().getConfiguration().locale;
        }


        try {
            Configuration configuration = new Configuration(this.getResources().getConfiguration());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(currentLocale);
            } else {
                configuration.locale = currentLocale;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                localeString = this.createConfigurationContext(configuration).getString(floorPlan);
            } else {
                this.getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
                localeString = this.getResources().getString(floorPlan);
            }

        } catch (Exception e) {
            Log.e(TAG,"error getting locale:", e);
        }
        return localeString;
    }


}
