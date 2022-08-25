/*
 *  Copyright (c) 2019-2022 Ernst-Abbe-Hochschule Jena
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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.views.navigation.SearchView;
import de.fhe.fhemobile.vos.navigation.BuildingVo;
import de.fhe.fhemobile.vos.navigation.RoomVo;
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
public class RoomSearchFragment extends SearchFragment {

    public static final String TAG = RoomSearchFragment.class.getSimpleName();

    @NonNls
    public static final String PREFS_NAVIGATION_ROOM_CHOICE = "navigation room";

    RoomSearchView mView;

    //building and floor chosen as destination
    String mDestBuilding;
    String mDestFloor;

    //set items of building picker
    static final List<BuildingVo> buildings = new ArrayList<>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RoomSearchFragment.
     */
    public static RoomSearchFragment newInstance() {
        final RoomSearchFragment fragment = new RoomSearchFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public RoomSearchFragment() {
        super(PREFS_NAVIGATION_ROOM_CHOICE);
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDestBuilding = null;
        mDestFloor = null;
    }

    /**
     * Method is called within super.onCreateView()
     * @param inflater the inflater to use
     * @param container
     */
    @Override
    protected void inflateAndInitializeView(final LayoutInflater inflater, final ViewGroup container) {
        // Inflate the layout for this fragment
        mView = (RoomSearchView) inflater.inflate(R.layout.fragment_room_search, container, false);
        mView.setViewListener(mViewListener);
        mView.initializeView(getChildFragmentManager());

        //set items of building picker
        // cache the values
        if (buildings.isEmpty()) {
            buildings.add(new BuildingVo("01"));
            buildings.add(new BuildingVo("03"));
            buildings.add(new BuildingVo("02"));
            buildings.add(new BuildingVo("04"));
            buildings.add(new BuildingVo("05"));
        }
        ((RoomSearchView) mView).setBuildingItems(buildings);
    }

    /**
     * Is called within super.onResume() when previous room choice is loaded from shared preferences
     * @param previousChoice String loaded from shared preferences
     */
    @Override
    protected void initializePickersFromSharedPreferences(String previousChoice) {
        //previousChoice is loaded from shared preferences before with default "" and thus never null
        if(!previousChoice.isEmpty()){
            for (final RoomVo room : NavigationFragment.rooms){
                if(previousChoice.equals(room.getRoomName())){
                    mDestBuilding = room.getBuilding();
                    mDestFloor = room.getFloorString();
                    mDestRoom = room;

                    mView.toggleFloorPickerVisibility(true);
                    mView.toggleRoomPickerVisibility(true);

                    mView.setBuildingDisplayValue(mDestBuilding);
                    mView.setFloorDisplayValue(mDestFloor);
                    mView.setRoomDisplayValue(mDestRoom.getRoomName());

                    mView.toggleStartInputCardVisibility(true);
                    mView.toggleGoButtonEnabled(true);

                    break;
                }
            }
        } else {
            mDestBuilding = null;
            mDestFloor = null;
            mDestRoom = null;
        }
    }

    @Override
    protected SearchView getSearchView() {
        return mView;
    }

    private final RoomSearchView.IViewListener mViewListener = new RoomSearchView.IViewListener() {
        @Override
        public void onBuildingChosen(final String _building) {
            mView.toggleRoomPickerVisibility(false);
            mView.toggleGoButtonEnabled(false);
            mView.resetFloorPicker();
            mView.resetRoomPicker();

            mDestFloor = null;
            mDestRoom = null;

            //check if building has any rooms
            boolean buildingValid = false;
            final List<String> floors = new ArrayList<>();
            if(NavigationFragment.rooms.isEmpty()) JSONHandler.loadRooms();
            for(final RoomVo room : NavigationFragment.rooms){
                if(room.getBuilding() != null && room.getBuilding().equals(_building)){

                    //add floor to floor picker items if not yet contained
                    if(!floors.contains(room.getFloorString())){
                        floors.add(room.getFloorString());
                    }

                    buildingValid = true;
                }
            }

            //if building has rooms then enable the floor picker and set its items
            if (buildingValid) {
                mDestBuilding = _building;
                mView.setFloorItems(floors);
                mView.toggleFloorPickerVisibility(true);
            } else {
                Utils.showToast(R.string.navigation_dialog_empty_building);
                mView.toggleFloorPickerVisibility(false);
            }
        }

        @Override
        public void onFloorChosen(final String _floor) {
            mView.toggleFloorPickerVisibility(true);
            mView.toggleGoButtonEnabled(false);
            mView.resetRoomPicker();

            mDestRoom = null;

            //check if floor has any rooms
            boolean floorValid = false;
            final List<RoomVo> rooms = new ArrayList<>();
            if(NavigationFragment.rooms.isEmpty()) JSONHandler.loadRooms();

            for(final RoomVo room : NavigationFragment.rooms){

                //_floor is string "-1" or "03" or "3Z"
                if(room.getBuilding().equals(mDestBuilding)
                        && room.getFloorString().equals(_floor)){
                    floorValid = true;
                    rooms.add(room);
                }
            }

            //if floor has rooms then enable the room picker
            if (floorValid) {
                mDestFloor = _floor;
                mView.setRoomItems(rooms);
                mView.toggleRoomPickerVisibility(true);
            } else {
                Utils.showToast(R.string.navigation_dialog_empty_building);
                mView.toggleRoomPickerVisibility(false);
            }
        }

        @Override
        public void onRoomChosen(final String _room) {
            mView.toggleRoomPickerVisibility(true);
            mView.toggleGoButtonEnabled(false);

            //search for room object
            if(NavigationFragment.rooms.isEmpty()) JSONHandler.loadRooms();
            for(final RoomVo room : NavigationFragment.rooms){
                if(room.getRoomName() != null && room.getRoomName().equals(_room)){

                    mDestRoom = room;
                    mView.toggleGoButtonEnabled(true);
                    mView.toggleStartInputCardVisibility(true);

                    saveToSharedPreferences(mDestRoom.getRoomName());

                    break;
                }
            }

            if(mDestRoom == null) showRoomNotFoundErrorToast();
        }


        @Override
        public void onQrClicked() {
            onQrButtonClicked();
        }

        @Override
        public void onGoClicked() {
            //call onGoButtonClicked() and clear choice on success
            if(onGoButtonCLicked()){
                mDestBuilding = null;
                mDestFloor = null;
            }
        }
    };
}
