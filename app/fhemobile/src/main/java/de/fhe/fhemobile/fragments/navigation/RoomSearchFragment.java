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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.navigation.Building;
import de.fhe.fhemobile.models.navigation.Room;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.utils.navigation.JSONHandler;
import de.fhe.fhemobile.views.navigation.RoomSearchView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * created by Nadja
 */
public class RoomSearchFragment extends FeatureFragment {

    @NonNls
    public static final String PREFS_NAVIGATION_ROOM_CHOICE = "room";

    private RoomSearchView mView;

    private String mChosenBuilding;
    private String mChosenFloor;
    // destination and start room
    private Room mChosenRoom;
    private Room mStartRoom;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RoomSearchFragment.
     */
    public static RoomSearchFragment newInstance() {
        RoomSearchFragment fragment = new RoomSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public RoomSearchFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChosenBuilding = null;
        mChosenFloor = null;
        mChosenRoom = null;
        mStartRoom = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = (RoomSearchView) inflater.inflate(R.layout.fragment_room_search, container, false);
        mView.setViewListener(mViewListener);
        mView.initializeView(getChildFragmentManager());

        //set items of building picker
        List<Building> buildings = new ArrayList<>();
        buildings.add(new Building("01"));
        buildings.add(new Building("03"));
        buildings.add(new Building("02"));
        buildings.add(new Building("04"));
        buildings.add(new Building("05"));
        mView.setBuildingItems(buildings);

        return mView;
    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences mSP = Main.getAppContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String previousRoomChoice = mSP.getString(PREFS_NAVIGATION_ROOM_CHOICE, "");
        if(!previousRoomChoice.equals("")){
            for (Room room : MainActivity.rooms){
                if(previousRoomChoice.equals(room.getRoomName())){
                    mChosenBuilding = room.getBuilding();
                    mChosenFloor = room.getFloorString();
                    mChosenRoom = room;

                    mView.toggleFloorPickerVisibility(true);
                    mView.toggleRoomPickerVisibility(true);

                    mView.setBuildingDisplayValue(mChosenBuilding);
                    mView.setFloorDisplayValue(mChosenFloor);
                    mView.setRoomDisplayValue(mChosenRoom.getRoomName());
                }
            }
        } else {
            mChosenBuilding = null;
            mChosenFloor = null;
            mChosenRoom = null;
        }

    }

    private void proceedToNavigation(){
        ((MainActivity) getActivity()).changeFragment(
                new NavigationFragment().newInstance(mStartRoom, mChosenRoom), true);
    }


    private final RoomSearchView.IViewListener mViewListener = new RoomSearchView.IViewListener() {
        @Override
        public void onBuildingChosen(String _building) {
            mView.toggleRoomPickerVisibility(false);
            mView.toggleGoButtonEnabled(false);
            mView.resetFloorPicker();
            mView.resetRoomPicker();

            mChosenFloor = null;
            mChosenRoom = null;

            //check if building has any rooms
            boolean buildingValid = false;
            List<Integer> floors = new ArrayList<>();
            if(MainActivity.rooms.isEmpty()) JSONHandler.loadRooms(getContext());
            for(Room room : MainActivity.rooms){
                if(room.getBuilding() != null && room.getBuilding().equals(_building)){

                    //add floor to floor picker items if not yet contained
                    if(!floors.contains(room.getFloorInt())){
                        floors.add(room.getFloorInt());
                    }

                    buildingValid = true;
                }
            }

            //if building has rooms then enable the floor picker and set its items
            if (buildingValid) {
                mChosenBuilding = _building;
                mView.setFloorItems(floors);
                mView.toggleFloorPickerVisibility(true);
            } else {
                Utils.showToast(R.string.navigation_dialog_empty_building);
                mView.toggleFloorPickerVisibility(false);
            }
        }

        @Override
        public void onFloorChosen(String _floor) {
            mView.toggleFloorPickerVisibility(true);
            mView.toggleGoButtonEnabled(false);
            mView.resetRoomPicker();

            mChosenRoom = null;

            //check if floor has any rooms
            boolean floorValid = false;
            List<Room> rooms = new ArrayList<>();
            if(MainActivity.rooms.isEmpty()) JSONHandler.loadRooms(getContext());

            for(Room room : MainActivity.rooms){

                //String _floor is a single digit "-1" or "3" --> treat as integer
                if(room.getBuilding().equals(mChosenBuilding)
                        && room.getFloorInt() == Integer.parseInt(_floor)){
                    floorValid = true;
                    rooms.add(room);
                }
            }

            //if floor has rooms then enable the room picker
            if (floorValid) {
                mChosenFloor = _floor;
                mView.setRoomItems(rooms);
                mView.toggleRoomPickerVisibility(true);
            } else {
                Utils.showToast(R.string.navigation_dialog_empty_building);
                mView.toggleRoomPickerVisibility(false);
            }
        }

        @Override
        public void onRoomChosen(String _room) {
            mView.toggleRoomPickerVisibility(true);
            mView.toggleGoButtonEnabled(false);

            //search for room object
            if(MainActivity.rooms.isEmpty()) JSONHandler.loadRooms(getContext());
            for(Room room : MainActivity.rooms){
                if(room.getRoomName() != null && room.getRoomName().equals(_room)){

                    mChosenRoom = room;
                    mView.toggleGoButtonEnabled(true);
                    mView.toggleStartInputCardVisibility(true);

                    final SharedPreferences sharedPreferences = Main.getAppContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PREFS_NAVIGATION_ROOM_CHOICE, mChosenRoom.getRoomName());
                    editor.apply();

                    break;
                }
            }
        }


        @Override
        public void onQrClicked() {
            //todo: open QrScanner
        }

        @Override
        public void onGoClicked() {
            if(mChosenRoom != null && validateStartInput()){
                proceedToNavigation();

                mChosenRoom = null;
                mChosenBuilding = null;
                mChosenFloor = null;
                mStartRoom = null;

            } else {
                Utils.showToast(R.string.timetable_error_incomplete);
            }
        }
    };

    /**
     * Checks if the room entered by the user as start is valid
     * @return true if valid, false if invalid or input missing
     */
    private boolean validateStartInput(){

        String input = mView.getStartInputText();

        boolean valid = false;
        //a room number has been entered
        if(input != null){

            //check room list for matching names
            for (Room room : MainActivity.rooms){
                if (room.getRoomName().equals(input)){
                    valid = true;
                }
            }

            if(!valid) mView.setInputErrorRoomNotFound();
        }
        //no start room input
        else {
            mView.setInputErrorNoInput();
        }

        return valid;
    }
}