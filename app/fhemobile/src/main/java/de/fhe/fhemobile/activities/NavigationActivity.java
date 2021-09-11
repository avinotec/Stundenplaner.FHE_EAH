package de.fhe.fhemobile.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Locale;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.models.navigation.*;
import de.fhe.fhemobile.utils.navigation.*;


public class NavigationActivity extends BaseActivity {

    //Constants
    private static final String TAG = "NavigationActivity"; //$NON-NLS

    private static final String BUILDING_03_02_01_FLOOR_UG = "building_03_02_01_floor_ug";
    private static final String BUILDING_03_02_01_FLOOR_00 = "building_03_02_01_floor_00";
    private static final String BUILDING_03_02_01_FLOOR_01 = "building_03_02_01_floor_01";
    private static final String BUILDING_03_02_01_FLOOR_02 = "building_03_02_01_floor_02";
    private static final String BUILDING_03_02_01_FLOOR_03 = "building_03_02_01_floor_03";
    private static final String BUILDING_03_02_01_FLOOR_04 = "building_03_02_01_floor_04";
    private static final String BUILDING_04_FLOOR_UG = "building_04_floor_ug";
    private static final String BUILDING_04_FLOOR_00 = "building_04_floor_00";
    private static final String BUILDING_04_FLOOR_01 = "building_04_floor_01";
    private static final String BUILDING_04_FLOOR_02 = "building_04_floor_02";
    private static final String BUILDING_04_FLOOR_03 = "building_04_floor_03";
    private static final String BUILDING_05_FLOOR_UG = "building_05_floor_ug";
    private static final String BUILDING_05_FLOOR_00 = "building_05_floor_00";
    private static final String BUILDING_05_FLOOR_01 = "building_05_floor_01";
    private static final String BUILDING_05_FLOOR_02 = "building_05_floor_02";
    private static final String BUILDING_05_FLOOR_03 = "building_05_floor_03";

    private static final String BUILDING_01 = "01";
    private static final String BUILDING_02 = "02";
    private static final String BUILDING_03 = "03";
    private static final String BUILDING_04 = "04";
    private static final String BUILDING_05 = "05";

    private static final String TRANSITION_TYPE_STAIR = "stair";
    private static final String TRANSITION_TYPE_ELEVATOR = "elevator";
    private static final String TRANSITION_TYPE_CROSSING = "crossing";

    private static final String JSON_FILE_ROOMS = "rooms.json";
    private static final String JSON_FILE_TRANSITIONS = "transitions.json";

    private static final String JUST_LOCATION = "location";

    private static final int X_SCALING_PORTRAIT = 9;
    private static final int Y_SCALING_PORTRAIT = 9;
    private static final int X_OFFSET_PORTRAIT = 0;
    private static final int Y_OFFSET_PORTRAIT = 31;
    private static final int X_SCALING_LANDSCAPE = 16;
    private static final int Y_SCALING_LANDSCAPE = 16;
    private static final int X_OFFSET_LANDSCAPE = 0;
    private static final int Y_OFFSET_LANDSCAPE = 12;

    //Variables
    private String destinationQRCode;
    private String ownLocation;
    private Room startLocation;
    private Room destinationLocation;

    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<Transition> transitions = new ArrayList<>();
    private ArrayList<Cell> cellsToWalk = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get extra from parent
        Intent intendScannerActivity = getIntent();
        ownLocation = intendScannerActivity.getStringExtra("startLocation");
        destinationQRCode = intendScannerActivity.getStringExtra("destinationLocation");

        //Spinner select floor plans
        final ArrayList<String> floorPlans = new ArrayList<>(getItemsSpinner());

        final Spinner floorPlansSpinner = findViewById(R.id.spinner_floor_plans);
        ArrayAdapter<String> floorPlansAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, floorPlans);
        floorPlansSpinner.setAdapter(floorPlansAdapter);
        floorPlansSpinner.setSelection(0, false);
        floorPlansSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long id) {

                Object item = adapterView.getItemAtPosition(index);

                if (item != null && index != 0) {

                    try {
                        ArrayList<String> helperBuildingAndFloor = getBuildingAndFloor((String) item);
                        drawNavigation(helperBuildingAndFloor.get(0), helperBuildingAndFloor.get(1));
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

        //Get rooms, stairs, elevators and crossings from JSON
        getRoomsAndTransitions();

        //Get own location room
        getOwnLocation();

        //Get destination location room
        if (!JUST_LOCATION.equals(destinationQRCode)) {
            getDestinationLocation();
        }

        //Calculate route (get ArrayList<Cell> of cells to walk)
        if (!JUST_LOCATION.equals(destinationQRCode)) {
            getRoute();
        }

        //Draw navigation
        drawNavigation(startLocation.getBuilding(), startLocation.getFloor());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //Get items for spinner floor plans
    private ArrayList<String> getItemsSpinner() {

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
        spinnerItems.add(resource.getString(R.string.building_05_floor_ug));
        spinnerItems.add(resource.getString(R.string.building_05_floor_00));
        spinnerItems.add(resource.getString(R.string.building_05_floor_01));
        spinnerItems.add(resource.getString(R.string.building_05_floor_02));
        spinnerItems.add(resource.getString(R.string.building_05_floor_03));

        return spinnerItems;
    }

    //Get rooms, stairs, elevators and crossings from JSON
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getRoomsAndTransitions() {

        try {
            JSONHandler jsonHandler = new JSONHandler();
            String json;

            json = jsonHandler.readJsonFromAssets(this, JSON_FILE_ROOMS);
            rooms = jsonHandler.parseJsonRooms(json);

            json = jsonHandler.readJsonFromAssets(this, JSON_FILE_TRANSITIONS);
            transitions = jsonHandler.parseJsonTransitions(json);

        } catch (Exception e) {
            Log.e(TAG, "error reading or parsing JSON files:", e);
        }
    }

    //get own location room
    private void getOwnLocation() {

        try {
            for (int i = 0; i < rooms.size(); i++) {

                if (rooms.get(i).getQRCode().equals(ownLocation)) {
                    startLocation = rooms.get(i);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "QR-Code invalid:", e);
        }
    }

    //Get destination location room
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

    //Calculate route (get ArrayList<Cell> of cells to walk through buildings and floors)
    private void getRoute() {
        try {
            RouteCalculator routeCalculator = new RouteCalculator(this, startLocation, destinationLocation, transitions);
            cellsToWalk.addAll(routeCalculator.getNavigationCells());
        } catch (Exception e) {
            Log.e(TAG,"error calculating route:", e);
        }
    }

    //Get floor plan String without ending (.jpeg) from building and floor
    private String getFloorPlan(String building, String floor) {

        String floorPlan = "";

        try {
            switch (building + "." + floor) {
                case "01.ug":
                case "02.ug":
                case "03.ug":
                    floorPlan = BUILDING_03_02_01_FLOOR_UG;
                    break;
                case "01.00":
                case "02.00":
                case "03.00":
                    floorPlan = BUILDING_03_02_01_FLOOR_00;
                    break;
                case "01.01":
                case "02.01":
                case "03.01":
                    floorPlan = BUILDING_03_02_01_FLOOR_01;
                    break;
                case "01.02":
                case "02.02":
                case "03.02":
                    floorPlan = BUILDING_03_02_01_FLOOR_02;
                    break;
                case "01.03":
                case "02.03":
                case "03.03":
                    floorPlan = BUILDING_03_02_01_FLOOR_03;
                    break;
                case "01.04":
                case "02.04":
                case "03.04":
                    floorPlan = BUILDING_03_02_01_FLOOR_04;
                    break;
                case "04.ug":
                    floorPlan = BUILDING_04_FLOOR_UG;
                    break;
                case "04.00":
                    floorPlan = BUILDING_04_FLOOR_00;
                    break;
                case "04.01":
                    floorPlan = BUILDING_04_FLOOR_01;
                    break;
                case "04.02":
                    floorPlan = BUILDING_04_FLOOR_02;
                    break;
                case "04.03":
                    floorPlan = BUILDING_04_FLOOR_03;
                    break;
                case "05.ug":
                    floorPlan = BUILDING_05_FLOOR_UG;
                    break;
                case "05.00":
                    floorPlan = BUILDING_05_FLOOR_00;
                    break;
                case "05.01":
                    floorPlan = BUILDING_05_FLOOR_01;
                    break;
                case "05.02":
                    floorPlan = BUILDING_05_FLOOR_02;
                    break;
                case "05.03":
                    floorPlan = BUILDING_05_FLOOR_03;
                    break;
                default:
                    floorPlan = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "error getting floor plan:", e);
        }
        return floorPlan;
    }

    //Get building and floor Strings from floor plan String
    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<String> getBuildingAndFloor(String in) {

        ArrayList<String> helperBuildingAndFloor = new ArrayList<>();

        Locale currentLocale = getResources().getConfiguration().getLocales().get(0);

        try {
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_03_02_01_floor_ug))) {
                helperBuildingAndFloor.add("03");
                helperBuildingAndFloor.add("ug");
            }
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_03_02_01_floor_00))) {
                helperBuildingAndFloor.add("03");
                helperBuildingAndFloor.add("00");
            }
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_03_02_01_floor_01))) {
                helperBuildingAndFloor.add("03");
                helperBuildingAndFloor.add("01");
            }
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_03_02_01_floor_02))) {
                helperBuildingAndFloor.add("03");
                helperBuildingAndFloor.add("02");
            }
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_03_02_01_floor_03))) {
                helperBuildingAndFloor.add("03");
                helperBuildingAndFloor.add("03");
            }
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_03_02_01_floor_04))) {
                helperBuildingAndFloor.add("03");
                helperBuildingAndFloor.add("04");
            }
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_04_floor_ug))) {
                helperBuildingAndFloor.add("04");
                helperBuildingAndFloor.add("ug");
            }
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_04_floor_00))) {
                helperBuildingAndFloor.add("04");
                helperBuildingAndFloor.add("00");
            }
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_04_floor_01))) {
                helperBuildingAndFloor.add("04");
                helperBuildingAndFloor.add("01");
            }
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_04_floor_02))) {
                helperBuildingAndFloor.add("04");
                helperBuildingAndFloor.add("02");
            }
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_04_floor_03))) {
                helperBuildingAndFloor.add("04");
                helperBuildingAndFloor.add("03");
            }
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_05_floor_ug))) {
                helperBuildingAndFloor.add("05");
                helperBuildingAndFloor.add("ug");
            }
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_05_floor_00))) {
                helperBuildingAndFloor.add("05");
                helperBuildingAndFloor.add("00");
            }
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_05_floor_01))) {
                helperBuildingAndFloor.add("05");
                helperBuildingAndFloor.add("01");
            }
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_05_floor_02))) {
                helperBuildingAndFloor.add("05");
                helperBuildingAndFloor.add("02");
            }
            if (in.equals(getLocaleStringResource(currentLocale, R.string.building_05_floor_03))) {
                helperBuildingAndFloor.add("05");
                helperBuildingAndFloor.add("03");
            }
        } catch (Exception e) {
            Log.e(TAG, "error getting building and floor:", e);
        }
        return helperBuildingAndFloor;
    }

    //Get locale
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private String getLocaleStringResource(Locale currentLocale, int floorPlan) {

        String localeString = "";

        try {
            Configuration configuration = new Configuration(this.getResources().getConfiguration());
            configuration.setLocale(currentLocale);
            localeString = this.createConfigurationContext(configuration).getString(floorPlan);
        } catch (Exception e) {
            Log.e(TAG,"error getting locale:", e);
        }
        return localeString;
    }

    //Density-Points to pixels
    private int dpToPx(int dp) {

        float density = getResources().getDisplayMetrics().density;

        return Math.round((float) dp * density);
    }

    //Draw path cells
    private void drawPathCell(int index, int xOffset, int xScaling, int yOffset, int yScaling, RelativeLayout relativeLayout) {

        ImageView pathCellIcon = new ImageView(this);
        pathCellIcon.setImageResource(R.drawable.path_cell_icon);
        pathCellIcon.setX(dpToPx(cellsToWalk.get(index).getXCoordinate() + xOffset) * xScaling);
        pathCellIcon.setY(dpToPx(cellsToWalk.get(index).getYCoordinate() + yOffset) * yScaling);

        if (relativeLayout != null) {
            relativeLayout.addView(pathCellIcon);
        }
    }

    //Draw start location
    private void drawStartLocation(int xOffset, int xScaling, int yOffset, int yScaling, RelativeLayout relativeLayout) {

        ImageView startIcon = new ImageView(this);
        startIcon.setImageResource(R.drawable.start_icon);
        startIcon.setX(dpToPx(startLocation.getXCoordinate() + xOffset) * xScaling);
        startIcon.setY(dpToPx(startLocation.getYCoordinate() + yOffset) * yScaling);

        if (relativeLayout != null) {
            relativeLayout.addView(startIcon);
        }
    }

    //Draw destination location
    private void drawDestinationLocation(int xOffset, int xScaling, int yOffset, int yScaling, RelativeLayout relativeLayout) {

        ImageView destinationIcon = new ImageView(this);
        destinationIcon.setImageResource(R.drawable.destination_icon);
        destinationIcon.setX(dpToPx(destinationLocation.getXCoordinate() + xOffset) * xScaling);
        destinationIcon.setY(dpToPx(destinationLocation.getYCoordinate() + yOffset) * yScaling);

        if (relativeLayout != null) {
            relativeLayout.addView(destinationIcon);
        }
    }

    //Draw transitions
    private void drawTransition(String building, String floor, int xScaling, int yScaling, int xOffset, int yOffset, int i, int j, RelativeLayout relativeLayout) {

        if (transitions.get(i).getConnectedCells().get(j).getBuilding().equals(building)
                && transitions.get(i).getConnectedCells().get(j).getFloor().equals(floor)) {

            if (transitions.get(i).getTypeOfTransition().equals(TRANSITION_TYPE_STAIR)) {

                ImageView stairIcon = new ImageView(this);
                stairIcon.setImageResource(R.drawable.stair_icon);
                stairIcon.setX(dpToPx(transitions.get(i).getConnectedCells().get(j).getXCoordinate() + xOffset) * xScaling);
                stairIcon.setY(dpToPx(transitions.get(i).getConnectedCells().get(j).getYCoordinate() + yOffset) * yScaling);

                if (relativeLayout != null) {
                    relativeLayout.addView(stairIcon);
                }
            }

            if (transitions.get(i).getTypeOfTransition().equals(TRANSITION_TYPE_ELEVATOR)) {

                ImageView elevatorIcon = new ImageView(this);
                elevatorIcon.setImageResource(R.drawable.elevator_icon);
                elevatorIcon.setX(dpToPx(transitions.get(i).getConnectedCells().get(j).getXCoordinate() + xOffset) * xScaling);
                elevatorIcon.setY(dpToPx(transitions.get(i).getConnectedCells().get(j).getYCoordinate() + yOffset) * yScaling);

                if (relativeLayout != null) {
                    relativeLayout.addView(elevatorIcon);
                }
            }

            if (transitions.get(i).getTypeOfTransition().equals(TRANSITION_TYPE_CROSSING)) {

                ImageView crossingIcon = new ImageView(this);
                crossingIcon.setImageResource(R.drawable.crossing_icon);
                crossingIcon.setX(dpToPx(transitions.get(i).getConnectedCells().get(j).getXCoordinate() + xOffset) * xScaling);
                crossingIcon.setY(dpToPx(transitions.get(i).getConnectedCells().get(j).getYCoordinate() + yOffset) * yScaling);

                if (relativeLayout != null) {
                    relativeLayout.addView(crossingIcon);
                }
            }
        }
    }

    //Draw graphical output
    private void drawNavigation(String building, String floor) {

        int xScaling = 0;
        int yScaling = 0;
        int xOffset = 0;
        int yOffset = 0;

        boolean buildingsThreeTwoOne = building.equals(BUILDING_03) || building.equals(BUILDING_02)
                || building.equals(BUILDING_01);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            xScaling = X_SCALING_PORTRAIT;
            yScaling = Y_SCALING_PORTRAIT;
            xOffset = X_OFFSET_PORTRAIT;
            yOffset = Y_OFFSET_PORTRAIT;
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            xScaling = X_SCALING_LANDSCAPE;
            yScaling = Y_SCALING_LANDSCAPE;
            xOffset = X_OFFSET_LANDSCAPE;
            yOffset = Y_OFFSET_LANDSCAPE;
        }

        //Relative layout to add views to
        RelativeLayout relativeLayout = findViewById(R.id.navigation_placeholder);

        //Remove views from layouts before redraw
        if (relativeLayout != null) {
            relativeLayout.removeAllViews();
        }

        //Add floor plan JPEG from drawable to ConstraintLayout as ImageView
        try {
            ImageView floorPlan = new ImageView(this);
            floorPlan.setImageResource(getResources().getIdentifier("drawable/" + getFloorPlan(building, floor), null, getPackageName()));

            if (relativeLayout != null) {
                relativeLayout.addView(floorPlan);
            }

        } catch (Exception e) {
            Log.e(TAG,"error drawing floor plan:", e);
        }

        //Add route path to ConstraintLayout
        try {
            if (!destinationQRCode.equals(JUST_LOCATION)) {

                for (int j = 0; j < cellsToWalk.size(); j++) {

                    if (cellsToWalk.get(j).getBuilding().equals(BUILDING_05) && building.equals(BUILDING_05)
                            && cellsToWalk.get(j).getFloor().equals(floor)) {

                        drawPathCell(j, xOffset, xScaling, yOffset, yScaling, relativeLayout);
                    }

                    if (cellsToWalk.get(j).getBuilding().equals(BUILDING_04) && building.equals(BUILDING_04)
                            && cellsToWalk.get(j).getFloor().equals(floor)) {

                        drawPathCell(j, xOffset, xScaling, yOffset, yScaling, relativeLayout);
                    }

                    if ((cellsToWalk.get(j).getBuilding().equals(BUILDING_03)
                            || cellsToWalk.get(j).getBuilding().equals(BUILDING_02)
                            || cellsToWalk.get(j).getBuilding().equals(BUILDING_01))
                            && buildingsThreeTwoOne && cellsToWalk.get(j).getFloor().equals(floor)) {

                        drawPathCell(j, xOffset, xScaling, yOffset, yScaling, relativeLayout);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error drawing route:",e);
        }

        //Add own location room icon to Overlay
        try {
            if (startLocation.getBuilding().equals(BUILDING_05) && building.equals(BUILDING_05) && startLocation.getFloor().equals(floor)) {

                drawStartLocation(xOffset, xScaling, yOffset, yScaling, relativeLayout);
            }

            if (startLocation.getBuilding().equals(BUILDING_04) && building.equals(BUILDING_04) && startLocation.getFloor().equals(floor)) {

                drawStartLocation(xOffset, xScaling, yOffset, yScaling, relativeLayout);
            }

            if ((startLocation.getBuilding().equals(BUILDING_03)
                    || startLocation.getBuilding().equals(BUILDING_02)
                    || startLocation.getBuilding().equals(BUILDING_01))
                    && (buildingsThreeTwoOne)
                    && startLocation.getFloor().equals(floor)) {

                drawStartLocation(xOffset, xScaling, yOffset, yScaling, relativeLayout);
            }
        } catch (Exception e) {
            Log.e(TAG, "error drawing own location room:", e);
        }

        //Add destination location room icon to ConstraintLayout
        try {
            if (!destinationQRCode.equals(JUST_LOCATION)) {

                if (destinationLocation.getBuilding().equals(BUILDING_05) && building.equals(BUILDING_05) && floor.equals(destinationLocation.getFloor())) {

                    drawDestinationLocation(xOffset, xScaling, yOffset, yScaling, relativeLayout);
                }

                if (destinationLocation.getBuilding().equals(BUILDING_04) && building.equals(BUILDING_04) && floor.equals(destinationLocation.getFloor())) {

                    drawDestinationLocation(xOffset, xScaling, yOffset, yScaling, relativeLayout);
                }

                if ((destinationLocation.getBuilding().equals(BUILDING_03)
                        || destinationLocation.getBuilding().equals(BUILDING_02)
                        || destinationLocation.getBuilding().equals(BUILDING_01))
                        && buildingsThreeTwoOne && floor.equals(destinationLocation.getFloor())) {

                    drawDestinationLocation(xOffset, xScaling, yOffset, yScaling, relativeLayout);
                }

            }
        } catch (Exception e) {
            Log.e(TAG,"error drawing destination location room:", e);
        }

        //Add transitions icons to ConstraintLayout
        try {
            for (int i = 0; i < transitions.size(); i++) {

                for (int j = 0; j < transitions.get(i).getConnectedCells().size(); j++) {

                    if (building.equals(BUILDING_01) || building.equals(BUILDING_02) || building.equals(BUILDING_03)) {

                        drawTransition(BUILDING_01, floor, xScaling, yScaling, xOffset, yOffset, i, j, relativeLayout);
                        drawTransition(BUILDING_02, floor, xScaling, yScaling, xOffset, yOffset, i, j, relativeLayout);
                        drawTransition(BUILDING_03, floor, xScaling, yScaling, xOffset, yOffset, i, j, relativeLayout);
                    }

                    if (building.equals(BUILDING_04) || building.equals(BUILDING_05)) {

                        drawTransition(building, floor, xScaling, yScaling, xOffset, yOffset, i, j, relativeLayout);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error drawing transitions:", e);
        }
    }

    //Get all walkable cells of a floor, helper method for manual mapping of JSON files
    /*
    private ArrayList<Cell> getWalkableCells(String building, String floor) {

        ArrayList<Cell> walkableCells = new ArrayList<>();
        JSONHandler jsonHandler = new JSONHandler();
        String json;

        try {
            json = jsonHandler.readJsonFromAssets(this, getFloorPlan(building, floor) + ".json");
            walkableCells = jsonHandler.parseJsonWalkableCells(json);
        } catch (Exception e) {
            Log.e(TAG, "error getting all walkable cells:", e);
        }
        return walkableCells;
    }
    */
}
