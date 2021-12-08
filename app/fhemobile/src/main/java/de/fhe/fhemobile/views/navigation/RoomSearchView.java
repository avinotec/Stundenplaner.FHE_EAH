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

package de.fhe.fhemobile.views.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.models.navigation.Building;
import de.fhe.fhemobile.models.navigation.Room;
import de.fhe.fhemobile.widgets.picker.BuildingPicker;
import de.fhe.fhemobile.widgets.picker.FloorPicker;
import de.fhe.fhemobile.widgets.picker.RoomPicker;
import de.fhe.fhemobile.widgets.picker.base.OnItemChosenListener;

/**
 * Created by Nadja - 08.12.2021
 */
public class RoomSearchView extends LinearLayout {

    public RoomSearchView(final Context context, final AttributeSet attrs){ super(context, attrs); }


    public RoomSearchView(final Context context){ super(context); }

    public void setViewListener(final IViewListener _Listener){
        mViewListener = _Listener;
    }

    public void intializeView(final FragmentManager _Manager){
        mBuildingPicker.setFragmentManager(_Manager);
        mBuildingPicker.toggleEnabled(false);
        mBuildingPicker.setOnItemChosenListener(mBuildingListener);

        mFloorPicker.setFragmentManager(_Manager);
        mFloorPicker.toggleEnabled(false);
        mFloorPicker.setOnItemChosenListener(mFloorListener);

        mRoomPicker.setFragmentManager(_Manager);
        mRoomPicker.toggleEnabled(false);
        mRoomPicker.setOnItemChosenListener(mRoomListener);

    }

    public void setBuildingItems(final List<Building> _Items){
        Collections.sort(_Items, new Comparator<Building>() {
            @Override
            public int compare(Building o1, Building o2) {
                return o1.getBuilding().compareTo(o2.getBuilding());
            }
        });
        mBuildingPicker.setItems(_Items);
        mBuildingPicker.toggleEnabled(true);
    }

    public void setFloorItems(final List<Integer> _Items){
        Collections.sort(_Items);
        mFloorPicker.setItems(_Items);
        mFloorPicker.toggleEnabled(true);
    }

    public void setRoomItems(final List<Room> _Items){
        Collections.sort(_Items, new Comparator<Room>() {
            @Override
            public int compare(Room o1, Room o2) {
                return o1.getRoomName().compareTo(o2.getRoomName());
            }
        });
    }

    public void toggleFloorPickerVisibility(final boolean _Visible){
        mFloorPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void toggleRoomPickerVisibility(final boolean _Visible){
        mRoomPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void resetFloorPicker(){ mFloorPicker.reset(true);}

    public void resetRoomPicker(){ mRoomPicker.reset(true);}

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mBuildingPicker = (BuildingPicker) findViewById(R.id.navigationPickerBuilding);
        mFloorPicker = (FloorPicker) findViewById(R.id.navigationPickerFloor);
        mRoomPicker = (RoomPicker) findViewById(R.id.navigationPickerRoom);
    }

    //Returns the selected building
    private final OnItemChosenListener mBuildingListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(String _ItemId, int _ItemPos) {
            if (mViewListener != null){
                mViewListener.onBuildingSelected(_ItemId);
            }
        }
    };

    //Returns the selected floor
    private final OnItemChosenListener mFloorListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(String _ItemId, int _ItemPos) {
            if (mViewListener != null){
                mViewListener.onFloorSelected(_ItemId);
            }
        }
    };

    //Returns the selected room
    private final OnItemChosenListener mRoomListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(String _ItemId, int _ItemPos) {
            if (mViewListener != null){
                mViewListener.onRoomSelected(_ItemId);
            }
        }
    };


    public interface IViewListener{
        void onBuildingSelected(String _building);
        void onFloorSelected(String _floor);
        void onRoomSelected(String _room);
    }

    private IViewListener mViewListener;
    private BuildingPicker mBuildingPicker;
    private FloorPicker mFloorPicker;
    private RoomPicker mRoomPicker;

}
