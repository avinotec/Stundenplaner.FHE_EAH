/*
 *  Copyright (c) 2019-2021 Ernst-Abbe-Hochschule Jena
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

import android.content.Intent;
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
 * source: Bachelor Thesis from Tim Münziger from SS2020
 * edit and integration: Nadja 09.2021
 */
public class NavigationDialogFragmentOLD extends FeatureFragment implements View.OnClickListener{

    //Constants
    private static final String TAG = "NavigationDialogFrag"; //$NON-NLS //nur "Frag" weil Tag sonst zu lang

    private static final String JUST_LOCATION = "location";
    // --Commented out by Inspection (02.11.2021 17:27):private static final String JSON_FILE_ROOMS = "rooms.json";


    //Variables
    private String destinationQRCode;
    private ArrayList<Room> rooms = new ArrayList<>();
    private TextInputLayout startLocationDisplayedErrorText;
    private TextInputLayout destinationLocationDisplayedErrorText;
    private TextInputEditText startLocationInputText;
    private TextInputEditText destinationLocationInputText;
    //roomsIndex und personsIndex dienen zur Ueberpruefung zulaessiger Eingabekombination (start + destination)
    private int roomsIndex = 0;
    private int personsIndex = 0;
    //json string containing rooms, to send rooms via intent
    private String roomsJson;
    final private ArrayList<String> roomNames = new ArrayList<>();
    final private ArrayList<String> persons = new ArrayList<>();

    private View      mView;

    public NavigationDialogFragmentOLD(){
        // Required empty public constructor
    }

    public static NavigationDialogFragmentOLD newInstance() {
        final NavigationDialogFragmentOLD fragment = new NavigationDialogFragmentOLD();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_navigation_dialog, container, false);

        loadRoomNamesAndPersons();
        //Spinners
        fillSpinners();
        final Spinner searchByRoomSpinner = mView.findViewById(R.id.spinner_by_room);
        final Spinner searchByPersonSpinner = mView.findViewById(R.id.spinner_by_person);


        //Start location input field
        startLocationDisplayedErrorText = mView.findViewById(R.id.input_field_search_start_room_layout);
        startLocationInputText = mView.findViewById(R.id.input_field_search_start_room_edit);
        startLocationInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView textView, final int actionId, final KeyEvent keyEvent) {

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
        final Button findCurrentLocationByQRButton = mView.findViewById(R.id.button_location_qr);
        findCurrentLocationByQRButton.setOnClickListener(this);

        //Find current location button text
        final Button findCurrentLocationByTextInputButton = mView.findViewById(R.id.button_location_text);
        findCurrentLocationByTextInputButton.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //todo: Wenn bereits eine Navigation durchgeführt wurde, dann wird diese wieder aufgrufen
        // und nicht die Navigation für das eventuell neue Ziel (Nutzer kann also keine zweite Navigation mit neuen Eingaben durchführen)
    }

    @Override
    public void onClick(final View view) {
        try {
            //QR button or search button
            if (view.getId() == R.id.button_location_qr
                    || view.getId() == R.id.button_location_text) {
                processUserInput(view);
            }
        } catch (final Exception e) {
            Log.e(TAG, "onClick exception", e);
        }
    }

    /**
     * Reads room list from json files, generates person list and saves both to global variables roomNames and persons
     */
    private void loadRoomNamesAndPersons(){
        //Get lists of rooms and names for spinners
        final JSONHandler jsonHandler = new JSONHandler();
/*TODO loadRooms ist schon in NavigationActivity enthalten -> NavigationDialogFragmentOLD müsste die Raumliste dafür an Navigation Activity schicken
ArrayList<Room> kann aber nicht per Intent versendet werden, da Room eine eigene Klasse ist und es keine String-Repräsentation gibt
mögliche Lösung: Implementierung von Parcable nutzen (ähnlich bei Timetable -> s. TimeTableWeekVo etc.),
könnte sein dass dann sowohl Room als auch die Arrayliste (z.B. als neue Klasse RoomListVo) beide Parcable implementieren müssen
andere aktuelle Lösung: json-String senden und zweimal in Rooms parsen
 */
        if (rooms.isEmpty()) {
            try {
                roomsJson = JSONHandler.readFromAssets(getContext(), "rooms");
                rooms = jsonHandler.parseJsonRooms(roomsJson);
            } catch (Exception e) {
                Log.e(TAG, "error reading or parsing JSON file:", e);
            }
        }

        //Get lists of room names and persons for spinners

        try {
            for (int i = 0; i < rooms.size(); i++) {

                final String name = rooms.get(i).getRoomName();
                roomNames.add(name);

                if (!rooms.get(i).getPersons().isEmpty()) {
                    persons.addAll(rooms.get(i).getPersons());
                }
            }
        } catch (Exception e) {
            Log.e(TAG,"error creating lists for spinners:", e);
        }

        //Sort rooms and persons alphabetically
        if(!roomNames.isEmpty()){
            Collections.sort(roomNames, new Comparator<String>() {
                @Override
                public int compare(final String stringOne, final String stringTwo) {
                    return stringOne.compareTo(stringTwo);
                }
            });
        }
        if(!persons.isEmpty()){
            Collections.sort(persons, new Comparator<String>() {
                @Override
                public int compare(final String stringOne, final String stringTwo) {
                    return stringOne.compareTo(stringTwo);
                }
            });
        }

        //Default elements
        final String defaultSelection = getResources().getString(R.string.select_from_spinner);
        roomNames.add(0, defaultSelection);
        persons.add(0, defaultSelection);

    }


    //set roomNames and persons as options at searchByRoomSpinner and at searchByPersonSpinner
    //returns configured spinners as array: {searchByRoomSpinner,searchByPersonSpinner}
    private void fillSpinners(){
        final Spinner searchByRoomSpinner = mView.findViewById(R.id.spinner_by_room);
        final Spinner searchByPersonSpinner = mView.findViewById(R.id.spinner_by_person);

        //Spinner for room search
        final ArrayAdapter<String> searchByRoomAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_text, roomNames);
        searchByRoomSpinner.setAdapter(searchByRoomAdapter);
        searchByRoomSpinner.setSelection(0, false);
        searchByRoomSpinner.setEnabled(true);
        searchByRoomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            /**
             *
             * @param adapterView
             * @param view
             * @param index Position im Spinner, die der Anwender angeklickt hat.
             * @param id
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long id) {

                final Object item = adapterView.getItemAtPosition(index);

                if (item != null && index != 0) {

                    // bspw. "03.03.33"
                    String roomName = item.toString();
                    String checkQRCode = roomName.replace(".", "");

                    for (int i = 0; i < rooms.size(); i++) {


                        if (roomName.equals(rooms.get(i).getRoomName())) {

                            destinationQRCode = checkQRCode;
                            roomsIndex = i;
                            //searchByPersonSpinner.setSelection(0);
                            destinationLocationInputText.setText("");
                            break;
                        }
                    }
                } else {
                    roomsIndex = 0;
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
                //do nothing
            }
        });

        //Spinner for person search
        final ArrayAdapter<String> searchByPersonAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_text, persons);
        searchByPersonSpinner.setAdapter(searchByPersonAdapter);
        searchByPersonSpinner.setSelection(0, false);
        searchByPersonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int index, final long id) {

                final Object item = adapterView.getItemAtPosition(index);

                if (item != null && index != 0) {

                    for (int i = 0; i < rooms.size(); i++) {

                        for (int j = 0; j < rooms.get(i).getPersons().size(); j++) {

                            if (item.equals(rooms.get(i).getPersons().get(j))) {

                                destinationQRCode = rooms.get(i).getQRCode();
                                personsIndex = i;
                                //searchByRoomSpinner.setSelection(0);
                                destinationLocationInputText.setText("");
                                break;
                            }
                        }
                    }
                }

                if (item != null && index == 0) {
                    personsIndex = 0;
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
                //do nothing
            }
        });

    }

    //validate user input for start and destination location and set Error Messages if needed
    private void validateUserInput(){

        final ArrayList<String> availableRooms = new ArrayList<>();

        //Get available rooms
        for (int index = 0; index < rooms.size(); index++) {
            availableRooms.add(rooms.get(index).getRoomName());
        }

        //User start location input error handling
        if (!"".equals(startLocationInputText.getText().toString())
                && !availableRooms.contains(startLocationInputText.getText().toString())) {

            startLocationDisplayedErrorText.setError(getText(R.string.error_message_room_input));
        }

        if ("".equals(startLocationInputText.getText().toString())
                || availableRooms.contains(startLocationInputText.getText().toString())) {

            startLocationDisplayedErrorText.setError(null);
        }

        //User destination location input error handling
        if (!"".equals(destinationLocationInputText.getText().toString())
                && !availableRooms.contains(destinationLocationInputText.getText().toString())) {

            destinationLocationDisplayedErrorText.setError(getText(R.string.error_message_room_input));
        }

        if (destinationLocationInputText.getText().toString().equals("")
                || availableRooms.contains(destinationLocationInputText.getText().toString())) {

            destinationLocationDisplayedErrorText.setError(null);
        }
    }


    //User input processing, error handling and input combinations error handling
    private void processUserInput(View view) {

        String userInputStartLocation = Objects.requireNonNull(startLocationInputText.getText()).toString().replace(".", "");
        String userInputDestinationLocation = Objects.requireNonNull(destinationLocationInputText.getText()).toString().replace(".", "");


        validateUserInput();

        //user input was correct - finding position or navigation can be performed
        if (destinationLocationDisplayedErrorText.getError() == null
                && startLocationDisplayedErrorText.getError() == null) {

            boolean skipScanner = false;
            boolean inputValid = false;

            //Use start QR-Code to show current position
            if (roomsIndex == 0 && personsIndex == 0 && "".equals(userInputStartLocation)
                    && "".equals(userInputDestinationLocation) && view.getId() == R.id.button_location_qr) {

                destinationQRCode = JUST_LOCATION;
                skipScanner = false;
                inputValid = true;
            }

            //Use user start input to show current location
            if (roomsIndex == 0 && personsIndex == 0 && !"".equals(userInputStartLocation)
                    && "".equals(userInputDestinationLocation) && view.getId() == R.id.button_location_text) {

                destinationQRCode = JUST_LOCATION;
                skipScanner = true;
                inputValid = true;
            }

            //Use start QR-Code and destination selection to perform navigation
            if ((roomsIndex != 0 || personsIndex != 0) && "".equals(userInputStartLocation)
                    && "".equals(userInputDestinationLocation) && view.getId() == R.id.button_location_qr) {

                skipScanner = false;
                inputValid = true;
            }

            //Use start QR-Code and user destination input to perform navigation
            if (roomsIndex == 0 && personsIndex == 0 && "".equals(userInputStartLocation)
                    && !"".equals(userInputDestinationLocation) && view.getId() == R.id.button_location_qr) {

                destinationQRCode = userInputDestinationLocation;
                skipScanner = false;
                inputValid = true;
            }

            //Use user start input and destination selection to perform navigation
            if ((roomsIndex != 0 || personsIndex != 0) && !"".equals(userInputStartLocation)
                    && "".equals(userInputDestinationLocation) && view.getId() == R.id.button_location_text) {

                skipScanner = true;
                inputValid = true;
            }

            //Use user start and destination input to perform navigation
            if (roomsIndex == 0 && personsIndex == 0 && !"".equals(userInputStartLocation)
                    && !"".equals(userInputDestinationLocation) && view.getId() == R.id.button_location_text) {

                destinationQRCode = userInputDestinationLocation;
                skipScanner = true;
                inputValid = true;
            }

            if(inputValid) {
                startNavigationActivity(userInputStartLocation, roomNames, skipScanner);
            }

        }
    }


    //start Navigation activity and send intent
    private void startNavigationActivity(final String userInputStartLocation, final ArrayList<String> roomNames, final boolean skipScanner) {

        if(skipScanner){ //start location determined by user input
            final Intent intentNavigationActivity = new Intent(getActivity(), NavigationActivity.class);
            intentNavigationActivity.putExtra("startLocation", userInputStartLocation);
            intentNavigationActivity.putExtra("destinationLocation", destinationQRCode);
            intentNavigationActivity.putExtra("rooms", roomsJson);
            startActivity(intentNavigationActivity);
        }
        if(!skipScanner){ //determine startLocation by scanning QR code
            final Intent intentScannerActivity = new Intent(getActivity(), ScannerActivity.class);
            intentScannerActivity.putExtra("destinationLocation", destinationQRCode);
            intentScannerActivity.putExtra("availableRooms", roomNames);
            startActivity(intentScannerActivity);
        }

    }

}
