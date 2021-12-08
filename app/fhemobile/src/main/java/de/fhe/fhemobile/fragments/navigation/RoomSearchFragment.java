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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

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
 */
public class RoomSearchFragment extends FeatureFragment {

    private RoomSearchView mView;

    private Room mChosenRoom;
    private Building mChosenBuilding;
    private String mChosenFloor;


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

        mChosenRoom = null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (RoomSearchView) inflater.inflate(R.layout.fragment_room_search, container, false);
        mView.setViewListener(mViewListener);
        mView.initializeView(getChildFragmentManager());

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //todo: fetch last input from shared-preferences
    }


    private final RoomSearchView.IViewListener mViewListener = new RoomSearchView.IViewListener() {
        @Override
        public void onBuildingSelected(String _building) {
            mView.toggleFloorPickerVisibility(true);
            mView.resetBuildingPicker();
            mView.resetFloorPicker();
            mView.resetRoomPicker();

            mChosenBuilding = null;
            mChosenRoom = null;

            //check if building has any rooms
            boolean buildingValid = false;
            if(MainActivity.rooms.isEmpty()) JSONHandler.loadRooms(getContext());
            for(Room room : MainActivity.rooms){
                if(room.getBuilding() != null && room.getBuilding().equals(_building)){
                    //todo: set floor picker items
                    buildingValid = true;
                    break;
                }
            }

            //if building has rooms then enable the floor picker
            if (buildingValid) {
                mChosenBuilding = new Building(_building);

                mView.toggleFloorPickerVisibility(true);
            } else {
                Utils.showToast(R.string.navigation_dialog_empty_building);
                mView.toggleFloorPickerVisibility(false);
            }
        }

        @Override
        public void onFloorSelected(String _floor) {
            mView.toggleFloorPickerVisibility(true);
            mView.resetFloorPicker();
            mView.resetRoomPicker();

            mChosenRoom = null;

            //check if floor has any rooms
            boolean floorValid = false;
            if(MainActivity.rooms.isEmpty()) JSONHandler.loadRooms(getContext());
            for(Room room : MainActivity.rooms){
                if(room.getFloorString() != null && room.getFloorString().equals(_floor)){
                    floorValid = true;
                    //todo: set room picker items
                    break;
                }
            }

            //if floor has rooms then enable the room picker
            if (floorValid) {
                mChosenFloor = _floor;
                mView.toggleRoomPickerVisibility(true);
            } else {
                Utils.showToast(R.string.navigation_dialog_empty_building);
                mView.toggleRoomPickerVisibility(false);
            }
        }

        @Override
        public void onRoomSelected(String _room) {
            mView.toggleRoomPickerVisibility(true);
            mView.resetRoomPicker();

            //search for room object
            if(MainActivity.rooms.isEmpty()) JSONHandler.loadRooms(getContext());
            for(Room room : MainActivity.rooms){
                if(room.getRoomName() != null && room.getRoomName().equals(_room)){
                    mChosenRoom = room;
                    break;
                }
            }
        }
    };
}