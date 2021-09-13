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
import de.fhe.fhemobile.activities.NavigationActivity;
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
    private TextInputLayout startLocationDisplayedErrorText;
    private TextInputLayout destinationLocationDisplayedErrorText;
    private TextInputEditText startLocationInputText;
    private TextInputEditText destinationLocationInputText;
    private int roomsIndex = 0; //roomsIndex und personsIndex dienen zur Überprüfung zulässiger Eingabekombination (start + destination)
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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
        startLocationDisplayedErrorText = mView.findViewById(R.id.input_field_search_start_room_layout);
        startLocationInputText = mView.findViewById(R.id.input_field_search_start_room_edit);
        startLocationInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    processUserInput(textView);
                }
                return false;
            }
        });

        //Destination location input field
        destinationLocationDisplayedErrorText = mView.findViewById(R.id.input_field_search_destination_room_layout);
        destinationLocationInputText = mView.findViewById(R.id.input_field_search_destination_room_edit);
        destinationLocationInputText.setOnEditorActionListener((textView, actionId, keyEvent) -> {

            searchByRoomSpinner.setSelection(0);
            searchByPersonSpinner.setSelection(0);
            roomsIndex = 0;
            personsIndex = 0;

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                processUserInput(textView);
            }
            return false;
        });

        //Find current location button qr-code
        Button findCurrentLocationByQRButton = mView.findViewById(R.id.button_location_qr);
        findCurrentLocationByQRButton.setOnClickListener(this);

        //Find current location button text
        Button findCurrentLocationbyTextinputButton = mView.findViewById(R.id.button_location_text);
        findCurrentLocationbyTextinputButton.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View view) {
        try {
            //QR button or search button
            if (view.getId() == R.id.button_location_qr
                    || view.getId() == R.id.button_location_text) {
                processUserInput(view);
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


    //set roomNames and persons as options at searchByRoomSpinner and at searchByPersonSpinner
    //returns configured spinners as array: {searchByRoomSpinner,searchByPersonSpinner}
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
                            destinationLocationInputText.setText("");
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
                                destinationLocationInputText.setText("");
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


    //User input processing, error handling and input combinations error handling
    private void processUserInput(View view) {

        String userInputStartLocation = Objects.requireNonNull(startLocationInputText.getText()).toString().replace(".", "");
        String userInputDestinationLocation = Objects.requireNonNull(destinationLocationInputText.getText()).toString().replace(".", "");

        ArrayList<String> roomNames = new ArrayList<>();

        //Get available rooms
        for (int index = 0; index < rooms.size(); index++) {
            roomNames.add(rooms.get(index).getRoomName());
        }

        //User start location input error handling
        if (!startLocationInputText.getText().toString().equals("")
                && !roomNames.contains(startLocationInputText.getText().toString())) {

            startLocationDisplayedErrorText.setError(getText(R.string.error_message_room_input));
        }

        if (startLocationInputText.getText().toString().equals("")
                || roomNames.contains(startLocationInputText.getText().toString())) {

            startLocationDisplayedErrorText.setError(null);
        }

        //User destination location input error handling
        if (!destinationLocationInputText.getText().toString().equals("")
                && !roomNames.contains(destinationLocationInputText.getText().toString())) {

            destinationLocationDisplayedErrorText.setError(getText(R.string.error_message_room_input));
        }

        if (destinationLocationInputText.getText().toString().equals("")
                || roomNames.contains(destinationLocationInputText.getText().toString())) {

            destinationLocationDisplayedErrorText.setError(null);
        }

        //user input was correct - finding position or navigation can be performed
        if (destinationLocationDisplayedErrorText.getError() == null
                && startLocationDisplayedErrorText.getError() == null) {

            Boolean skipScanner = false;
            Boolean inputValid = false;

            //Use start QR-Code to show current position
            if (roomsIndex == 0 && personsIndex == 0 && userInputStartLocation.equals("")
                    && userInputDestinationLocation.equals("") && view.getId() == R.id.button_location_qr) {

                destinationQRCode = JUST_LOCATION;
                skipScanner = false;
                inputValid = true;
            }

            //Use user start input to show current location
            if (roomsIndex == 0 && personsIndex == 0 && !userInputStartLocation.equals("")
                    && userInputDestinationLocation.equals("") && view.getId() == R.id.button_location_text) {

                destinationQRCode = JUST_LOCATION;
                skipScanner = true;
                inputValid = true;
            }

            //Use start QR-Code and destination selection to perform navigation
            if ((roomsIndex != 0 || personsIndex != 0) && userInputStartLocation.equals("")
                    && userInputDestinationLocation.equals("") && view.getId() == R.id.button_location_qr) {

                skipScanner = false;
                inputValid = true;
            }

            //Use start QR-Code and user destination input to perform navigation
            if (roomsIndex == 0 && personsIndex == 0 && userInputStartLocation.equals("")
                    && !userInputDestinationLocation.equals("") && view.getId() == R.id.button_location_qr) {

                destinationQRCode = userInputDestinationLocation;
                skipScanner = false;
                inputValid = true;
            }

            //Use user start input and destination selection to perform navigation
            if ((roomsIndex != 0 || personsIndex != 0) && !userInputStartLocation.equals("")
                    && userInputDestinationLocation.equals("") && view.getId() == R.id.button_location_text) {

                skipScanner = true;
                inputValid = true;
            }

            //Use user start and destination input to perform navigation
            if (roomsIndex == 0 && personsIndex == 0 && !userInputStartLocation.equals("")
                    && !userInputDestinationLocation.equals("") && view.getId() == R.id.button_location_text) {

                destinationQRCode = userInputDestinationLocation;
                skipScanner = true;
                inputValid = true;
            }

            if(inputValid) {
                doIntentAndStartActivity(userInputStartLocation, roomNames, skipScanner);
            }

        }
    }


    //Intent
    private void doIntentAndStartActivity(String userInputStartLocation, ArrayList<String> roomNames, boolean skipScanner) {

        if(skipScanner){ //start location determined by user input
            Intent intentNavigationActivity = new Intent(getActivity(), NavigationActivity.class);
            intentNavigationActivity.putExtra("startLocation", userInputStartLocation);
            intentNavigationActivity.putExtra("destinationLocation", destinationQRCode);
            startActivity(intentNavigationActivity);
        }
        if(!skipScanner){ //determine startLocation by scanning QR code
            Intent intentScannerActivity = new Intent(getActivity(), ScannerActivity.class);
            intentScannerActivity.putExtra("startLocation", userInputStartLocation);
            intentScannerActivity.putExtra("destinationLocation", destinationQRCode);
            intentScannerActivity.putExtra("availableRooms", roomNames);
            startActivity(intentScannerActivity);
        }

    }

}
