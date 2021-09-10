package de.fhe.fhemobile.fragments.navigation;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.ScannerActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.navigation.Room;
import de.fhe.fhemobile.utils.navigation.JSONHandler;

/**
 * Fragment for dialog (asking for direct input or if QR-Scanner should be opened) before starting navigation - Nadja 9.9.21
 */
public class NavigationDialogFragment extends FeatureFragment implements View.OnClickListener{

    //Constants
    private static final String TAG = "NavigationDialogFrag"; //$NON-NLS //nur "Frag" weil Tag sonst zu lang

    private static final String JUST_LOCATION = "location";
    private static final String JSON_FILE_ROOMS = "rooms.json";

    //Variables
    private String destinationQRCode;
    private ArrayList<Room> rooms = new ArrayList<>();
    private TextInputLayout startLocationLayoutText;
    private TextInputEditText startLocationEditText;
    private TextInputLayout destinationLocationLayoutText;
    private TextInputEditText destinationLocationEditText;
    private int roomsIndex = 0;
    private int personsIndex = 0;

    private View      mView;

    public NavigationDialogFragment(){
        // Required empty public constructor
    }

    public static NavigationDialogFragment newInstance() {
        NavigationDialogFragment fragment = new NavigationDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_navigation_dialog, container, false);

        //Spinners
        ArrayList[] roomNamesAndPersons = loadRoomNamesAndPersons();
        Spinner[] spinners = getSpinners(roomNamesAndPersons[0], roomNamesAndPersons[1]);
        final Spinner searchByRoomSpinner = spinners[0];
        final Spinner searchByPersonSpinner = spinners[1];

        //Start location input field
        startLocationLayoutText = mView.findViewById(R.id.input_field_search_start_room_layout);
        startLocationEditText = mView.findViewById(R.id.input_field_search_start_room_edit);
        startLocationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    handleUserInputErrorAndIntent(textView);
                }
                return false;
            }
        });

        //Destination location input field
        destinationLocationLayoutText = mView.findViewById(R.id.input_field_search_destination_room_layout);
        destinationLocationEditText = mView.findViewById(R.id.input_field_search_destination_room_edit);
        destinationLocationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                searchByRoomSpinner.setSelection(0);
                searchByPersonSpinner.setSelection(0);
                roomsIndex = 0;
                personsIndex = 0;

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    handleUserInputErrorAndIntent(textView);
                }
                return false;
            }
        });

        //Find own location button qr-code
        Button findOwnLocationButtonQR = mView.findViewById(R.id.button_location_qr);
        findOwnLocationButtonQR.setOnClickListener(this);

        //Find own location button text
        Button findOwnLocationButtonText = mView.findViewById(R.id.button_location_text);
        findOwnLocationButtonText.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View view) {
        try {

            //QR button
            if (view.getId() == R.id.button_location_qr) {
                handleUserInputErrorAndIntent(view);
            }

            //Search button
            if (view.getId() == R.id.button_location_text) {
                handleUserInputErrorAndIntent(view);
            }
        } catch (Exception e) {
            Log.e(TAG, "onClick exception", e);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private ArrayList[] loadRoomNamesAndPersons(){
        //Get lists of rooms and names for spinners
        JSONHandler jsonHandler = new JSONHandler();
        String json;

        try {
            json = jsonHandler.readJsonFromAssets(getContext(), JSON_FILE_ROOMS);
            rooms = jsonHandler.parseJsonRooms(json);
        } catch (Exception e) {
            Log.e(TAG, "error reading or parsing JSON file:", e);
        }

        //Get lists of room names and persons for spinners
        Resources resource = getResources();
        final ArrayList<String> roomNames = new ArrayList<>();
        ArrayList<String> persons = new ArrayList<>();

        try {
            for (int i = 0; i < rooms.size(); i++) {

                String name;
                name = rooms.get(i).getRoomName();
                roomNames.add(name);

                if (!rooms.get(i).getPersons().isEmpty()) {

                    for (int j = 0; j < rooms.get(i).getPersons().size(); j++) {
                        String person;
                        person = rooms.get(i).getPersons().get(j);
                        persons.add(person);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG,"error creating lists for spinners:", e);
        }

        //Sort rooms and persons alphabetically
        Collections.sort(roomNames, new Comparator<String>() {
            @Override
            public int compare(String stringOne, String stringTwo) {
                return stringOne.compareTo(stringTwo);
            }
        });
        Collections.sort(persons, new Comparator<String>() {
            @Override
            public int compare(String stringOne, String stringTwo) {
                return stringOne.compareTo(stringTwo);
            }
        });

        //Default elements
        String defaultSelection = resource.getString(R.string.select_from_spinner);
        roomNames.add(0, defaultSelection);
        persons.add(0, defaultSelection);

        ArrayList[] result = {roomNames, persons};
        return result;
    }


    private Spinner[] getSpinners(ArrayList<String> roomNames, ArrayList<String> persons){
        Spinner searchByRoomSpinner = mView.findViewById(R.id.spinner_by_room);
        Spinner searchByPersonSpinner = mView.findViewById(R.id.spinner_by_person);

        //Spinner for room search
        ArrayAdapter<String> searchByRoomAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, roomNames);
        searchByRoomSpinner.setAdapter(searchByRoomAdapter);
        searchByRoomSpinner.setSelection(0, false);
        searchByRoomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long id) {

                Object item = adapterView.getItemAtPosition(index);

                if (item != null && index != 0) {

                    for (int i = 0; i < rooms.size(); i++) {

                        String checkQRCode = item.toString();
                        checkQRCode.replace(".", "");

                        if (checkQRCode.equals(rooms.get(i).getRoomName())) {

                            destinationQRCode = rooms.get(i).getQRCode();
                            roomsIndex = i;
                            searchByPersonSpinner.setSelection(0);
                            destinationLocationEditText.setText("");
                        }
                    }
                }

                if (item != null && index == 0) {
                    roomsIndex = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        //Spinner for person search
        ArrayAdapter<String> searchByPersonAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, persons);
        searchByPersonSpinner.setAdapter(searchByPersonAdapter);
        searchByPersonSpinner.setSelection(0, false);
        searchByPersonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long id) {

                Object item = adapterView.getItemAtPosition(index);

                if (item != null && index != 0) {

                    for (int i = 0; i < rooms.size(); i++) {

                        for (int j = 0; j < rooms.get(i).getPersons().size(); j++) {

                            if (item.equals(rooms.get(i).getPersons().get(j))) {

                                destinationQRCode = rooms.get(i).getQRCode();
                                personsIndex = i;
                                searchByRoomSpinner.setSelection(0);
                                destinationLocationEditText.setText("");
                            }
                        }
                    }
                }

                if (item != null && index == 0) {
                    personsIndex = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        Spinner[] spinners = {searchByRoomSpinner,searchByPersonSpinner};
        return spinners;
    }


    //User input error handling and input combinations error handling
    private void handleUserInputErrorAndIntent(View view) {

        String userInputStartLocation = Objects.requireNonNull(startLocationEditText.getText()).toString().replace(".", "");
        String userInputDestinationLocation = Objects.requireNonNull(destinationLocationEditText.getText()).toString().replace(".", "");

        ArrayList<String> roomNames = new ArrayList<>();

        //Get available rooms
        for (int index = 0; index < rooms.size(); index++) {

            roomNames.add(rooms.get(index).getRoomName());
        }

        //User start location input error handling
        if (!startLocationEditText.getText().toString().equals("")
                && !roomNames.contains(startLocationEditText.getText().toString())) {

            startLocationLayoutText.setError(getText(R.string.error_message_room_input));
        }

        if (startLocationEditText.getText().toString().equals("")
                || roomNames.contains(startLocationEditText.getText().toString())) {

            startLocationLayoutText.setError(null);
        }

        //User destination location input error handling
        if (!destinationLocationEditText.getText().toString().equals("")
                && !roomNames.contains(destinationLocationEditText.getText().toString())) {

            destinationLocationLayoutText.setError(getText(R.string.error_message_room_input));
        }

        if (destinationLocationEditText.getText().toString().equals("")
                || roomNames.contains(destinationLocationEditText.getText().toString())) {

            destinationLocationLayoutText.setError(null);
        }

        //Input combinations error handling
        if (startLocationLayoutText.getError() == null && destinationLocationLayoutText.getError() == null) {

            //Use start QR-Code to show own position
            if (roomsIndex == 0 && personsIndex == 0 && userInputStartLocation.equals("")
                    && userInputDestinationLocation.equals("") && view.getId() == R.id.button_location_qr) {

                destinationQRCode = JUST_LOCATION;

                doIntent(userInputStartLocation, roomNames, false);
            }

            //Use user start input to show own location
            if (roomsIndex == 0 && personsIndex == 0 && !userInputStartLocation.equals("")
                    && userInputDestinationLocation.equals("") && view.getId() == R.id.button_location_text) {

                destinationQRCode = JUST_LOCATION;

                doIntent(userInputStartLocation, roomNames, true);
            }

            //Use start QR-Code and destination selection to perform navigation
            if ((roomsIndex != 0 || personsIndex != 0) && userInputStartLocation.equals("")
                    && userInputDestinationLocation.equals("") && view.getId() == R.id.button_location_qr) {

                doIntent(userInputStartLocation, roomNames, false);
            }

            //Use start QR-Code and user destination input to perform navigation
            if (roomsIndex == 0 && personsIndex == 0 && userInputStartLocation.equals("")
                    && !userInputDestinationLocation.equals("") && view.getId() == R.id.button_location_qr) {

                destinationQRCode = userInputDestinationLocation;

                doIntent(userInputStartLocation, roomNames, false);
            }

            //Use user start input and destination selection to perform navigation
            if ((roomsIndex != 0 || personsIndex != 0) && !userInputStartLocation.equals("")
                    && userInputDestinationLocation.equals("") && view.getId() == R.id.button_location_text) {

                doIntent(userInputStartLocation, roomNames, true);
            }

            //Use user start and destination input to perform navigation
            if (roomsIndex == 0 && personsIndex == 0 && !userInputStartLocation.equals("")
                    && !userInputDestinationLocation.equals("") && view.getId() == R.id.button_location_text) {

                destinationQRCode = userInputDestinationLocation;

                doIntent(userInputStartLocation, roomNames, true);
            }
        }
    }


    //Intent
    private void doIntent(String userInputStartLocation, ArrayList<String> roomNames, boolean skipScanner) {

        Intent intentScannerActivity = new Intent(getContext(), ScannerActivity.class);
        intentScannerActivity.putExtra("destinationLocation", destinationQRCode);
        intentScannerActivity.putExtra("startLocation", userInputStartLocation);
        intentScannerActivity.putExtra("skipScanner", skipScanner);
        intentScannerActivity.putExtra("availableRooms", roomNames);
        startActivity(intentScannerActivity);
    }



}
