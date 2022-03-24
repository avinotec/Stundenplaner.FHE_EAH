package de.fhe.fhemobile.fragments.navigation;

import static de.fhe.fhemobile.utils.Define.Navigation.KEY_SCANNED_ROOM;
import static de.fhe.fhemobile.utils.Define.Navigation.REQUEST_SCANNED_START_ROOM;
import static de.fhe.fhemobile.utils.Define.Navigation.SP_NAVIGATION;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentResultListener;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.views.navigation.SearchView;
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

        //listen for NavigationScannerFragment to send scanning result
        //note: the listener does not fire until the fragment is STARTED
        getParentFragmentManager().setFragmentResultListener(REQUEST_SCANNED_START_ROOM, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                String result = bundle.getString(KEY_SCANNED_ROOM);
                Log.d(TAG, "scanned QR code received in SearchFragment: "+result);

                mStartRoom = validateAndGetRoom(result);
                getSearchView().setStartInputText(mStartRoom.getRoomName());
                getSearchView().invalidate();

            }
        });
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
                new NavigationScannerFragment().newInstance(), true, NavigationScannerFragment.TAG);

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
     * if true, then the startRoom variable is set and text of the EditText is set to room name
     * if false, an error message is shown
     * @param inputRoom room to validate
     * @return true if room is valid, false if inputRoom is invalid, missing or empty
     */
    protected boolean validateInputAndSetStartRoom(String inputRoom){

        if(inputRoom != null && !"".equals(inputRoom)){

            mStartRoom = validateAndGetRoom(inputRoom);
            if(mStartRoom == null) {
                getSearchView().setInputErrorRoomNotFound();
                return false;
            }else{
                getSearchView().setStartInputText(mStartRoom.getRoomName());
                return true;
            }
        }
        //no start room inputRoom
        else {
            getSearchView().setInputErrorNoInput();
            return  false;
        }
    }

    private RoomVo validateAndGetRoom(String inputRoom) {
        RoomVo roomFound = null;

        //a room number has been entered
        if (inputRoom != null && !"".equals(inputRoom)) {

            //reminder: there is an exception from room format xx.xx.xx -> 05.3Z.xxx

            //if room number has been entered without separating dots
            if (inputRoom.matches("\\d{6,7}|(\\d{2}-\\d{3})")) {
                inputRoom = inputRoom.substring(0, 2) + "." + inputRoom.substring(2, 4) + "."
                        + inputRoom.substring(4);
            }
            //if room number has been entered without leading zeros
            else if (!inputRoom.matches("\\d{2}\\.(\\d{2}|(3Z)|-\\d)\\.\\d{2,3}")
                    && inputRoom.matches("\\d{1,2}\\.(\\d{1,2}|(3Z)|-\\d)\\.\\d{1,3}")) {

                String[] inputArray = inputRoom.split("\\.");
                //format input
                inputRoom = (String.format("%2s", inputArray[0]) + "."
                        + String.format("%2s", inputArray[1]) + "."
                        + String.format("%2s", inputArray[2])).replace(" ", "0");
            }

            //inputArray[0] = building, inputArray[1] = floor, inputArray[2] = room number
            String[] inputArray = inputRoom.split("\\.");

            //correct floor from 03 to 3Z if necessary
            if(inputRoom.matches("05\\.03\\.\\d{3}")){
                inputRoom = inputArray[0] + ".3Z." + inputArray[2];
            }

            //check room list for matching names
            for (final RoomVo room : NavigationFragment.rooms) {
                if (room.getRoomName().equals(inputRoom)) {
                    roomFound = room;
                }
                //if no room has been found then search for a close one
                //check if room is in same building on same floor
                else if (room.getBuilding().equals(inputArray[0])
                        && room.getFloorString().equals(inputArray[1])){

                    int inputRoomNumber = Integer.parseInt(inputArray[2]);
                    int roomDiff = Math.abs(Integer.parseInt(room.getRoomNumber()) - inputRoomNumber);

                    //if room is closer than room found before
                    if(roomFound == null){
                        roomFound = room;
                    }
                    else if(roomDiff < Math.abs(Integer.parseInt(roomFound.getRoomNumber()) - inputRoomNumber)){
                        roomFound = room;
                    }
                }
            }
        }

        return roomFound;
    }

}
