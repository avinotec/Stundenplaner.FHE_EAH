package de.fhe.fhemobile.fragments.navigation;

import static de.fhe.fhemobile.utils.Define.SP_NAVIGATION;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.utils.navigation.JSONHandler;
import de.fhe.fhemobile.views.navigation.PersonSearchView;
import de.fhe.fhemobile.views.navigation.SearchView;
import de.fhe.fhemobile.vos.navigation.PersonVo;
import de.fhe.fhemobile.vos.navigation.RoomVo;

/**
 * Abstract class for {@link RoomSearchFragment} and {@link PersonSearchFragment}
 *
 * created by Nadja - 03/2022
 */
public abstract class SearchFragment extends FeatureFragment {

    public static String TAG = "SearchFragment"; //$NON-NLS

    private final String PREFS_NAVIGATION;

    protected RoomVo mDestRoom;
    protected RoomVo mStartRoom;

    public SearchFragment(String prefTag) {
        // Required empty public constructor
        PREFS_NAVIGATION = prefTag;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDestRoom = null;
        mStartRoom = null;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        inflateAndInitializeView(inflater, container);
        return getSearchView();
    }

    /**
     * Inflate the layout for this fragment
     * @param inflater the inflater to use
     */
    protected abstract void inflateAndInitializeView(final LayoutInflater inflater, final ViewGroup container);


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences mSP = Main.getAppContext().getSharedPreferences(SP_NAVIGATION, Context.MODE_PRIVATE);
        String previousChoice = mSP.getString(PREFS_NAVIGATION, "");

        initializePickersFromSharedPreferences(previousChoice);
    }

    protected abstract void initializePickersFromSharedPreferences(String previousChoice);


    public void setStartRoom(String startRoom) {
        validateInputAndSetStartRoom(startRoom);
    }

    /**
     * Return mView of the inheriting class
     * @return
     */
    protected abstract SearchView getSearchView();

    /**
     * Specify behaviour for the QR code button
     */
    protected void onQrButtonClicked(){
        mStartRoom = null;

        //note: Scanner is not added to back stack
        ((MainActivity) getActivity()).changeFragment(
                new NavigationScannerFragment().newInstance(TAG), true, NavigationScannerFragment.TAG);
    }

    /**
     * Method to call when Go Button is clicked (within IViewListener.OnGoClicked())
     * The user input is validated, startRoom set and after the current fragment
     * changed to {@link NavigationFragment} startRoom and destRoom are cleared
     * @return true if no problems occurred and the navigation could be started
     */
    protected boolean onGoButtonCLicked(){
        if(mDestRoom != null){
            if(validateInputAndSetStartRoom(getSearchView().getStartInputText())){
                ((MainActivity) getActivity()).changeFragment(
                        new NavigationFragment().newInstance(mStartRoom, mDestRoom),
                        true, NavigationFragment.TAG);

                mDestRoom = null;
                mStartRoom = null;
                return true;
            }

        } else {
            Utils.showToast(R.string.timetable_error_incomplete);
        }

        return false;
    }

    /**
     * Save the user's choice to shared preferences
     * @param choice the chosen room or person
     */
    protected void saveToSharedPreferences(String choice){
        final SharedPreferences sharedPreferences = Main.getAppContext().getSharedPreferences(SP_NAVIGATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREFS_NAVIGATION, choice);
        editor.apply();
    }

    /**
     * Checks if the room entered by the user as start is valid
     * if true, then the startRoom variable is set
     * if false, an error message is shown
     * @param inputRoom room to validate
     * @return true if room is valid, false if inputRoom is invalid, missing or empty
     */
    protected boolean validateInputAndSetStartRoom(String inputRoom){

        boolean valid = false;
        //a room number has been entered
        if(inputRoom != null && !"".equals(inputRoom)){

            //reminder: there is an exception from room format xx.xx.xx -> 05.3Z.xxx

            //if room number has been entered without separating dots
            if(inputRoom.matches("\\d{6,7}")){
                inputRoom = inputRoom.substring(0,2) +"."+ inputRoom.substring(2,4) +"."
                        + inputRoom.substring(4);
            }
            //if room number has been entered without leading zeros
            else if(!inputRoom.matches("\\d{2}\\.(\\d{2}|(3Z))\\.\\d{2,3}")
                    && inputRoom.matches("\\d{1,2}\\.(\\d{1,2}|(3Z))\\.\\d{1,3}")){

                String[] inputArray = inputRoom.split("\\.");
                inputRoom = (String.format("%2s", inputArray[0]) + "."
                        + String.format("%2s", inputArray[1]) + "."
                        + String.format("%2s", inputArray[2])).replace(" ", "0");
            }

            //check room list for matching names
            for (final RoomVo room : MainActivity.rooms){
                if (room.getRoomName().equals(inputRoom)){
                    valid = true;
                    mStartRoom = room;
                    getSearchView().setStartInputText(mStartRoom.getRoomName());
                }
            }

            if(!valid) getSearchView().setInputErrorRoomNotFound();
        }
        //no start room inputRoom
        else {
            getSearchView().setInputErrorNoInput();
        }

        return valid;
    }
}
