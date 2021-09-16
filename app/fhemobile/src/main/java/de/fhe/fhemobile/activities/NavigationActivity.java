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
import de.fhe.fhemobile.models.navigation.Cell;
import de.fhe.fhemobile.models.navigation.FloorConnection;
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

    private static final String FLOORCONNECTION_TYPE_STAIR = "stair";
    private static final String FLOORCONNECTION_TYPE_ELEVATOR = "elevator";
    private static final String FLOORCONNECTION_TYPE_BRIDGE = "bridge";

    private static final String JSON_FILE_ROOMS = "rooms.json";
    private static final String JSON_FILE_TRANSITIONS = "transitions.json";

    private static final String JUST_LOCATION = "location";

    // Size of the grid overlying the floorplan (unit: cells)
    private static final int cellgrid_width = 40;
    private static final int cellgrid_height = 23;

    //Variables
    private String destinationQRCode;
    private String currentLocation;
    private Room startLocation;
    private Room destinationLocation;

    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<FloorConnection> floorConnections = new ArrayList<>();
    private ArrayList<Cell> cellsToWalk = new ArrayList<>();

    private float cellWidth;
    private float cellHeight;
    private RelativeLayout navigationLayout;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get extra from parent
        Intent intendScannerActivity = getIntent();
        currentLocation = intendScannerActivity.getStringExtra("startLocation");
        destinationQRCode = intendScannerActivity.getStringExtra("destinationLocation");

        //Spinner select floor plans
        final ArrayList<String> floorPlans = new ArrayList<>(getItemsSpinner());

        final Spinner floorPlansSpinner = findViewById(R.id.spinner_floor_plans);
        ArrayAdapter<String> floorPlansAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_text, floorPlans);
        floorPlansSpinner.setAdapter(floorPlansAdapter);
        floorPlansSpinner.setSelection(0, false);
        floorPlansSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //draw map and navigation when floor is selected
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long id) {

                Object item = adapterView.getItemAtPosition(index);

                if (item != null && index != 0) {
                    try {
                        ArrayList<String> helperBuildingAndFloor = getBuildingAndFloor((String) item);

                        drawNavigationAndRoute(helperBuildingAndFloor.get(0), helperBuildingAndFloor.get(1));
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

        //Get rooms, stairs, elevators and the bridge from JSON
        // lädt die verfügbaren Räume und Aufgänge aus den entsprechenden JSON-Dateien
        getRoomsAndTransitions();

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
            drawNavigationAndRoute(startLocation.getBuilding(), startLocation.getFloor());
        } catch (Exception e) {
            Log.e(TAG,"error drawing floor plan or navigation:", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //Draw graphical output
    private void drawNavigationAndRoute(String building, String floor) {
        //Remove views from layouts before redrawing
        if (navigationLayout != null) navigationLayout.removeAllViews();

        //Set the floor plan ImageView in the relative layout where the whole navigation is drawn in
        ImageView floorPlanView = new ImageView(this);
        floorPlanView.setImageResource(getResources().getIdentifier("drawable/" +
                getFloorPlan(building, floor), null, getPackageName()));

        if (navigationLayout != null) navigationLayout.addView(floorPlanView);

        //The actual displayed size of the floorplans ImageView is needed to determine correct positioning if the icon
        //But width and height are not available in the onCreate() (because the ImageView has not really be drawn at this time)
        //solution: call the post method and override run() - this code then gets executed after the drawing phase so width and height are now existing (not 0)
        floorPlanView.post(new Runnable() {
            @Override
            public void run() {
                //get display size of the floorplan's ImageView
                int floorplanDisplayWidth = floorPlanView.getMeasuredWidth();
                int floorplanDisplayHeight = floorPlanView.getMeasuredHeight();

                //get height and width of a single cell
                cellWidth = floorplanDisplayWidth / cellgrid_width;
                cellHeight = floorplanDisplayHeight / cellgrid_height;

                //Draw navigation
                drawPathCells(building, floor); // add route (path of cells) to overlay

                drawStartLocation(building, floor); //Add icon for current user location room to overlay

                drawDestinationLocation(building, floor); //Add destination location room icon to overlay

                drawFloorConnections(building, floor); //Add floorConnection icons (like stairs, lifts, ...) to overlay
            }
        });

    }

    //converts the cell position from unit cellnumber to a unit for displaying
    private int convertCellCoordX(int x){
        return Math.round(x * cellWidth);
    }

    //converts the cell position from unit cellnumber to a unit for displaying
    private int convertCellCoordY(int y){
        return Math.round(y * cellHeight);
    }

    //Draw all path cells of the calculated route
    private void drawPathCells(String building, String floor) {
        boolean buildingsThreeTwoOne = building.equals(BUILDING_03) || building.equals(BUILDING_02)
                || building.equals(BUILDING_01);

        try {
            if (!destinationQRCode.equals(JUST_LOCATION)) {
                for (int j = 0; j < cellsToWalk.size(); j++) {
                    if (cellsToWalk.get(j).getBuilding().equals(building)
                            && cellsToWalk.get(j).getFloor().equals(floor)) {

                        drawPathCell(j);
                    }else if ((cellsToWalk.get(j).getBuilding().equals(BUILDING_03)
                                || cellsToWalk.get(j).getBuilding().equals(BUILDING_02)
                                || cellsToWalk.get(j).getBuilding().equals(BUILDING_01))
                            && buildingsThreeTwoOne && cellsToWalk.get(j).getFloor().equals(floor)) {

                        drawPathCell(j);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error drawing route:",e);
        }

    }

    // adds one cell icon to display a walkable cell of the calculated route
    private void drawPathCell(int index){
        ImageView pathCellIcon = new ImageView(this);
//        ViewGroup.LayoutParams params = pathCellIcon.getLayoutParams();
//        pathCellIcon.setAdjustViewBounds(true);
//        params.width = Math.round(cellWidth);
//        params.height = Math.round(cellHeight);
////        pathCellIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        pathCellIcon.setImageResource(R.drawable.path_cell_icon);
        pathCellIcon.setX(convertCellCoordX(cellsToWalk.get(index).getXCoordinate()));
        pathCellIcon.setY(convertCellCoordY(cellsToWalk.get(index).getYCoordinate()));

        if (navigationLayout != null) navigationLayout.addView(pathCellIcon);
    }

    //Draw start location
    private void drawStartLocation(String building, String floor) {
        boolean buildingsThreeTwoOne = building.equals(BUILDING_03) || building.equals(BUILDING_02)
                || building.equals(BUILDING_01);

        try {
            //check if input valid
            boolean valid = false;
            if (startLocation.getBuilding().equals(building) && startLocation.getFloor().equals(floor)) {
                valid = true;
            }else if ((startLocation.getBuilding().equals(BUILDING_03)
                        || startLocation.getBuilding().equals(BUILDING_02)
                        || startLocation.getBuilding().equals(BUILDING_01))
                        && (buildingsThreeTwoOne)
                        && startLocation.getFloor().equals(floor)) {

                valid = true;
            }

            //add start icon
            if(valid){
                ImageView startIcon = new ImageView(this);
                startIcon.setImageResource(R.drawable.start_icon);
                startIcon.setX(convertCellCoordX(startLocation.getXCoordinate()));
                startIcon.setY(convertCellCoordY(startLocation.getYCoordinate()));

                if (navigationLayout != null) navigationLayout.addView(startIcon);
            }
        } catch (Exception e) {
            Log.e(TAG, "error drawing current location room:", e);
        }

    }

    //Draw destination location
    private void drawDestinationLocation(String building, String floor) {
        try {
            boolean buildingsThreeTwoOne = building.equals(BUILDING_03) || building.equals(BUILDING_02)
                    || building.equals(BUILDING_01);

            //check if input valid
            boolean valid = false;
            if (!destinationQRCode.equals(JUST_LOCATION)) {
                if (destinationLocation.getBuilding().equals(building) && floor.equals(destinationLocation.getFloor())) {
                    valid = true;
                }else if ((destinationLocation.getBuilding().equals(BUILDING_03)
                        || destinationLocation.getBuilding().equals(BUILDING_02)
                        || destinationLocation.getBuilding().equals(BUILDING_01))
                        && buildingsThreeTwoOne && floor.equals(destinationLocation.getFloor())) {
                    valid = true;
                }

            }
            //add destination icon
            if(valid){
                ImageView destinationIcon = new ImageView(this);
                destinationIcon.setImageResource(R.drawable.destination_icon);
                destinationIcon.setX(convertCellCoordX(destinationLocation.getXCoordinate()));
                destinationIcon.setY(convertCellCoordY(destinationLocation.getYCoordinate()));

                if (navigationLayout != null) navigationLayout.addView(destinationIcon);
            }
        } catch (Exception e) {
            Log.e(TAG,"error drawing destination location room:", e);
        }
    }

    //add all elevators, staircases and the bridge
    private void drawFloorConnections(String building, String floor){
        try {
            for (int i = 0; i < floorConnections.size(); i++) {

                for (int j = 0; j < floorConnections.get(i).getConnectedCells().size(); j++) {

                    if (building.equals(BUILDING_01) || building.equals(BUILDING_02) || building.equals(BUILDING_03)) {
                        drawFloorConnection(BUILDING_01, floor, i, j);
                        drawFloorConnection(BUILDING_02, floor, i, j);
                        drawFloorConnection(BUILDING_03, floor, i, j);
                    }

                    if (building.equals(BUILDING_04) || building.equals(BUILDING_05)) {
                        drawFloorConnection(building, floor, i, j);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error drawing floorConnections:", e);
        }
    }

    //Draw floorConnections
    private void drawFloorConnection(String building, String floor, int i, int j) {

        if (floorConnections.get(i).getConnectedCells().get(j).getBuilding().equals(building)
                && floorConnections.get(i).getConnectedCells().get(j).getFloor().equals(floor)) {

            if (floorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_STAIR)) {
                ImageView stairIcon = new ImageView(this);
                stairIcon.setImageResource(R.drawable.stair_icon);
                stairIcon.setX(convertCellCoordX(floorConnections.get(i).getConnectedCells().get(j).getXCoordinate()));
                stairIcon.setY(convertCellCoordY(floorConnections.get(i).getConnectedCells().get(j).getYCoordinate()));

                if (navigationLayout != null) navigationLayout.addView(stairIcon);
            }

            if (floorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_ELEVATOR)) {
                ImageView elevatorIcon = new ImageView(this);
                elevatorIcon.setImageResource(R.drawable.elevator_icon);
                elevatorIcon.setX(convertCellCoordX(floorConnections.get(i).getConnectedCells().get(j).getXCoordinate()));
                elevatorIcon.setY(convertCellCoordY(floorConnections.get(i).getConnectedCells().get(j).getYCoordinate()));

                if (navigationLayout != null) navigationLayout.addView(elevatorIcon);
            }

            if (floorConnections.get(i).getTypeOfFloorConnection().equals(FLOORCONNECTION_TYPE_BRIDGE)) {
                ImageView bridgeIcon = new ImageView(this);
                bridgeIcon.setImageResource(R.drawable.bridge_icon);
                bridgeIcon.setX(convertCellCoordX(floorConnections.get(i).getConnectedCells().get(j).getXCoordinate()));
                bridgeIcon.setY(convertCellCoordY(floorConnections.get(i).getConnectedCells().get(j).getYCoordinate()));

                if (navigationLayout != null) navigationLayout.addView(bridgeIcon);
            }
        }
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


    //Get rooms, stairs, elevators and the bridge from JSON
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getRoomsAndTransitions() {
        try {
            JSONHandler jsonHandler = new JSONHandler();
            String json;

            json = jsonHandler.readJsonFromAssets(this, JSON_FILE_ROOMS);
            rooms = jsonHandler.parseJsonRooms(json);

            json = jsonHandler.readJsonFromAssets(this, JSON_FILE_TRANSITIONS);
            floorConnections = jsonHandler.parseJsonTransitions(json);

        } catch (Exception e) {
            Log.e(TAG, "error reading or parsing JSON files:", e);
        }
    }


    //get current user location room
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
    //wird ein Objekt vom Typ „RouteCalculator“ initiiert und mittels diesem die Berechnung der Navigation durchgeführt und eine Liste mit allen zu begehenden Zellen zurück gegeben
    private void getRoute() {
        try {
            RouteCalculator routeCalculator = new RouteCalculator(this, startLocation, destinationLocation, floorConnections);
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
