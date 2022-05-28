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

import androidx.fragment.app.FragmentManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.vos.navigation.BuildingVo;
import de.fhe.fhemobile.vos.navigation.RoomVo;
import de.fhe.fhemobile.widgets.picker.BuildingPicker;
import de.fhe.fhemobile.widgets.picker.FloorPicker;
import de.fhe.fhemobile.widgets.picker.RoomPicker;
import de.fhe.fhemobile.widgets.picker.base.OnItemChosenListener;

/**
 * Created by Nadja - 08.12.2021
 */
public class RoomSearchView extends SearchView {

    public RoomSearchView(final Context context, final AttributeSet attrs){ super(context, attrs); }

    public RoomSearchView(final Context context){ super(context); }

    @Override
    public void initializeView(final FragmentManager _Manager){
        mBuildingPicker.setFragmentManager(_Manager);
        mBuildingPicker.toggleEnabled(false);
        mBuildingPicker.setOnItemChosenListener(mBuildingListener);

        mFloorPicker.setFragmentManager(_Manager);
        mFloorPicker.toggleEnabled(false);
        mFloorPicker.setOnItemChosenListener(mFloorListener);

        mRoomPicker.setFragmentManager(_Manager);
        mRoomPicker.toggleEnabled(false);
        mRoomPicker.setOnItemChosenListener(mRoomListener);

        super.initializeView();

    }

    @Override
    public void setViewListener(final SearchView.IViewListener _Listener) {
        mViewListener = (RoomSearchView.IViewListener) _Listener;
    }

    @Override
    public SearchView.IViewListener getViewListener() {
        return mViewListener;
    }

    public void setBuildingItems(final List<BuildingVo> _Items){
        Collections.sort(_Items, new Comparator<BuildingVo>() {
            @Override
            public int compare(final BuildingVo o1, final BuildingVo o2) {
                return o1.getBuilding().compareTo(o2.getBuilding());
            }
        });
        mBuildingPicker.setItems(_Items);
        mBuildingPicker.toggleEnabled(true);
    }

    public void setFloorItems(final List<String> _Items){
        Collections.sort(_Items);
        mFloorPicker.setItems(_Items);
        mFloorPicker.toggleEnabled(true);
    }

    public void setRoomItems(final List<RoomVo> _Items){
        Collections.sort(_Items, new Comparator<RoomVo>() {
            @Override
            public int compare(final RoomVo o1, final RoomVo o2) {
                return o1.getRoomName().compareTo(o2.getRoomName());
            }
        });
        mRoomPicker.setItems(_Items);
        mRoomPicker.toggleEnabled(true);
    }


    public void toggleFloorPickerVisibility(final boolean _Visible){
        mFloorPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void toggleRoomPickerVisibility(final boolean _Visible){
        mRoomPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }


    /**
     * Reset BuildingVo Picker to "please select"
     */
    public void resetBuildingPicker(){ mBuildingPicker.reset(true);}

    /**
     * Reset Floor Picker to "please select"
     */
    public void resetFloorPicker(){ mFloorPicker.reset(true);}

    /**
     * Reset RoomVo Picker to "please select"
     */
    public void resetRoomPicker(){ mRoomPicker.reset(true);}


    /**
     * Set displayed value of the BuildingVo Picker
     * @param text to display
     */
    public void setBuildingDisplayValue(final String text){ mBuildingPicker.setDisplayValue(text); }

    /**
     * Set displayed value of the Floor Picker
     * @param text to display
     */
    public void setFloorDisplayValue(final String text){ mFloorPicker.setDisplayValue(text);}

    /**
     * Set displayed value of the RoomVo Picker
     * @param text to display
     */
    public void setRoomDisplayValue(final String text){ mRoomPicker.setDisplayValue(text);}


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mBuildingPicker = (BuildingPicker)  findViewById(R.id.navigationPickerBuilding);
        mFloorPicker    = (FloorPicker)     findViewById(R.id.navigationPickerFloor);
        mRoomPicker     = (RoomPicker)      findViewById(R.id.navigationPickerRoom);

    }

    //Returns the selected building
    private final OnItemChosenListener mBuildingListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null){
                mViewListener.onBuildingChosen(_ItemId);
            }
        }
    };

    //Returns the selected floor
    private final OnItemChosenListener mFloorListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null){
                mViewListener.onFloorChosen(_ItemId);
            }
        }
    };

    //Returns the selected room
    private final OnItemChosenListener mRoomListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(final String _ItemId, final int _ItemPos) {
            if (mViewListener != null){
                mViewListener.onRoomChosen(_ItemId);
            }
        }
    };


    public interface IViewListener extends SearchView.IViewListener{
        void onBuildingChosen(String _building);
        void onFloorChosen(String _floor);
        void onRoomChosen(String _room);
    }

    IViewListener   mViewListener;
    private BuildingPicker  mBuildingPicker;
    private FloorPicker     mFloorPicker;
    private RoomPicker      mRoomPicker;

}
