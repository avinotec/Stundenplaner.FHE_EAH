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
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputLayout;

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

        toggleStartInputCardVisibility(false);

        mQrButton.setOnClickListener(mQrClickListener);
        mGoButton.setOnClickListener(mGoClickListener);
        toggleGoButtonEnabled(false);

    }

    public String getStartInputText(){
        Editable text = mStartInputText.getText();

        //getText() returns null if no text was entered -> error message is displayed
        if(text == null){
            return null;

        }else{
            return text.toString();
        }
    }

    /**
     * Set error message that is displayed at the start input layout
     * Message: Room not found!
     */
    public void setInputErrorRoomNotFound(){
        mStartInputLayout.setError(
                getResources().getString(R.string.error_message_room_input_invalid));
    }

    /**
     * Set error message that is displayed at the start input layout
     * Message: No Room entered!
     */
    public void setInputErrorNoInput(){
        mStartInputLayout.setError(
                getResources().getString(R.string.error_message_no_room_input));
    }


    public void setViewListener(final IViewListener _Listener){
        mViewListener = _Listener;
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
        mRoomPicker.setItems(_Items);
        mRoomPicker.toggleEnabled(true);
    }


    public void toggleFloorPickerVisibility(final boolean _Visible){
        mFloorPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void toggleRoomPickerVisibility(final boolean _Visible){
        mRoomPicker.setVisibility(_Visible ? VISIBLE : GONE);
    }

    public void toggleStartInputCardVisibility(final boolean _Visible){
        mStartInputCard.setVisibility(_Visible ? VISIBLE : GONE);
        mStartInputCard.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
    }

    public void toggleGoButtonEnabled(final boolean _Enabled){
        mGoButton.setEnabled(_Enabled);
        if(_Enabled){
            mGoButton.setBackgroundResource(R.drawable.buttonshape_filled);
        }else{
            mGoButton.setBackgroundResource(R.drawable.buttonshape_filled_disabled);
        }
    }


    /**
     * Reset Building Picker to "please select"
     */
    public void resetBuildingPicker(){ mBuildingPicker.reset(true);}

    /**
     * Reset Floor Picker to "please select"
     */
    public void resetFloorPicker(){ mFloorPicker.reset(true);}

    /**
     * Reset Room Picker to "please select"
     */
    public void resetRoomPicker(){ mRoomPicker.reset(true);}


    /**
     * Set displayed value of the Building Picker
     * @param text to display
     */
    public void setBuildingDisplayValue(String text){ mBuildingPicker.setDisplayValue(text); }

    /**
     * Set displayed value of the Floor Picker
     * @param text to display
     */
    public void setFloorDisplayValue(String text){ mFloorPicker.setDisplayValue(text);}

    /**
     * Set displayed value of the Room Picker
     * @param text to display
     */
    public void setRoomDisplayValue(String text){ mRoomPicker.setDisplayValue(text);}


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mBuildingPicker = (BuildingPicker)  findViewById(R.id.navigationPickerBuilding);
        mFloorPicker    = (FloorPicker)     findViewById(R.id.navigationPickerFloor);
        mRoomPicker     = (RoomPicker)      findViewById(R.id.navigationPickerRoom);

        mStartInputCard = (CardView) findViewById(R.id.cardviewStart);

        mStartInputLayout = (TextInputLayout) findViewById(R.id.navigation_input_layout_start);
        mStartInputText   = (EditText) findViewById(R.id.navigation_input_edittext_start);

        mQrButton   = (ImageButton) findViewById(R.id.navigationButtonQR);
        mGoButton   = (Button)      findViewById(R.id.navigationDialogButton);


    }

    //Returns the selected building
    private final OnItemChosenListener mBuildingListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(String _ItemId, int _ItemPos) {
            if (mViewListener != null){
                mViewListener.onBuildingChosen(_ItemId);
            }
        }
    };

    //Returns the selected floor
    private final OnItemChosenListener mFloorListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(String _ItemId, int _ItemPos) {
            if (mViewListener != null){
                mViewListener.onFloorChosen(_ItemId);
            }
        }
    };

    //Returns the selected room
    private final OnItemChosenListener mRoomListener = new OnItemChosenListener() {
        @Override
        public void onItemChosen(String _ItemId, int _ItemPos) {
            if (mViewListener != null){
                mViewListener.onRoomChosen(_ItemId);
            }
        }
    };

    //Listener for QR-Code button
    private final OnClickListener mQrClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mViewListener != null){
                mViewListener.onQrClicked();
            }
        }
    };

    //Listener for Go! button
    private final OnClickListener mGoClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mViewListener != null){
                mViewListener.onGoClicked();
            }
        }
    };


    public interface IViewListener{
        void onBuildingChosen(String _building);
        void onFloorChosen(String _floor);
        void onRoomChosen(String _room);
        void onQrClicked();
        void onGoClicked();
    }

    private IViewListener   mViewListener;
    private BuildingPicker  mBuildingPicker;
    private FloorPicker     mFloorPicker;
    private RoomPicker      mRoomPicker;

    private CardView mStartInputCard;

    private TextInputLayout mStartInputLayout;
    private EditText        mStartInputText;

    private ImageButton mQrButton;
    private Button      mGoButton;

}
