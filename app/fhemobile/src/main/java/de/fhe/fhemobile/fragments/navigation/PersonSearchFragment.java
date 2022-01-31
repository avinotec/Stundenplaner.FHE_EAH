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
import de.fhe.fhemobile.models.navigation.Person;
import de.fhe.fhemobile.models.navigation.Room;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.utils.navigation.JSONHandler;
import de.fhe.fhemobile.views.navigation.PersonSearchView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonSearchFragment extends FeatureFragment {

    public static final String TAG = "PersonSearchFragment"; //$NON-NLS

    @NonNls
    public static final String PREFS_NAVIGATION_PERSON_CHOICE = "navigation person";

    private PersonSearchView mView;

    private Room mDestRoom;
    private Room mStartRoom;

    private HashMap<String, Person> persons;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PersonSearchFragment.
     */
    public static PersonSearchFragment newInstance() {
        PersonSearchFragment fragment = new PersonSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PersonSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDestRoom = null;
        mStartRoom = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = (PersonSearchView) inflater.inflate(R.layout.fragment_person_search, container, false);
        mView.setViewListener(mViewListener);
        mView.initializeView(getChildFragmentManager());

        persons = JSONHandler.loadPersons(getContext());
        List<Person> personList = new ArrayList();
        personList.addAll(persons.values());
        mView.setPersonItems(personList);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences mSP = Main.getAppContext().getSharedPreferences(SP_NAVIGATION, Context.MODE_PRIVATE);
        String previousPersonChoice = mSP.getString(PREFS_NAVIGATION_PERSON_CHOICE, "");

        if(!"".equals(previousPersonChoice)){
            Person chosenPerson = persons.get(previousPersonChoice);
            if(chosenPerson != null){
                String chosenPersonsRoom = chosenPerson.getRoom();
                for (Room room : MainActivity.rooms){

                    if(room.getRoomName().equals(chosenPersonsRoom)){
                        mDestRoom = room;
                        mView.setPersonDisplayValue(previousPersonChoice);

                        mView.toggleStartInputCardVisibility(true);
                        mView.toggleGoButtonEnabled(true);

                        break;
                    }
                }
            }
        }
    }

    private final PersonSearchView.IViewListener mViewListener = new PersonSearchView.IViewListener() {

        @Override
        public void onPersonChosen(String _person) {
            mView.toggleGoButtonEnabled(false);

            //search for room object
            if(MainActivity.rooms.isEmpty()) JSONHandler.loadRooms(getContext());
            for(Room room : MainActivity.rooms){
                String roomOfPerson = persons.get(_person).getRoom();
                if(room.getRoomName() != null && room.getRoomName().equals(roomOfPerson)){

                    mDestRoom = room;
                    mView.toggleGoButtonEnabled(true);
                    mView.toggleStartInputCardVisibility(true);

                    final SharedPreferences sharedPreferences = Main.getAppContext().getSharedPreferences(SP_NAVIGATION, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PREFS_NAVIGATION_PERSON_CHOICE, _person);
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
            if(mDestRoom != null){
                if(validateInputAndSetStartRoom()){
                    ((MainActivity) getActivity()).changeFragment(
                            new NavigationFragment().newInstance(mStartRoom, mDestRoom), true, TAG);

                    mDestRoom = null;
                    mStartRoom = null;
                }

            } else {
                Utils.showToast(R.string.timetable_error_incomplete);
            }
        }
    };

    /**
     * Checks if the room entered by the user as start is valid
     * if true, then the startRoom variable is set
     * if false, an error message is shown
     * @return true if valid, false if invalid or input missing
     */
    private boolean validateInputAndSetStartRoom(){

        String input = mView.getStartInputText();

        boolean valid = false;
        //a room number has been entered
        if(input != null){

            //check room list for matching names
            for (Room room : MainActivity.rooms){
                if (room.getRoomName().equals(input)){
                    valid = true;
                    mStartRoom = room;
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
