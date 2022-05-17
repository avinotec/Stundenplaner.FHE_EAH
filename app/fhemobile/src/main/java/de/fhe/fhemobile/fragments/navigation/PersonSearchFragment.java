package de.fhe.fhemobile.fragments.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.views.navigation.SearchView;
import de.fhe.fhemobile.vos.navigation.PersonVo;
import de.fhe.fhemobile.vos.navigation.RoomVo;
import de.fhe.fhemobile.utils.navigation.JSONHandler;
import de.fhe.fhemobile.views.navigation.PersonSearchView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * created by Nadja
 */
public class PersonSearchFragment extends SearchFragment {

    public static final String TAG = PersonSearchFragment.class.getSimpleName();

    @NonNls
    public static final String PREFS_NAVIGATION_PERSON_CHOICE = "navigation person";

    private PersonSearchView mView;

    private HashMap<String, PersonVo> persons;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PersonSearchFragment.
     */
    public static PersonSearchFragment newInstance() {
        final PersonSearchFragment fragment = new PersonSearchFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PersonSearchFragment() {
        super(PREFS_NAVIGATION_PERSON_CHOICE);
    }

//    @Override
//    public void onCreate(final Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    /**
     * Method is called within super.onCreateView()
     * @param inflater the inflater to use
     * @param container
     */
    @Override
    protected void inflateAndInitializeView(LayoutInflater inflater, ViewGroup container) {

        // Inflate the layout for this fragment
                mView = (PersonSearchView) inflater.inflate(R.layout.fragment_person_search, container, false);
        mView.setViewListener(mViewListener);
        mView.initializeView(getChildFragmentManager());

        persons = JSONHandler.loadPersons();
        final List<PersonVo> personList = new ArrayList<>(persons.values());
        mView.setPersonItems(personList);

    }

    /**
     * Is called within super.onResume() when previous person choice is loaded from shared preferences
     * @param previousChoice String loaded from shared preferences
     */
    @Override
    protected void initializePickersFromSharedPreferences(String previousChoice) {
        if(previousChoice == null || !previousChoice.isEmpty()){
            final PersonVo chosenPerson = persons.get(previousChoice);
            if(chosenPerson != null){
                final String chosenPersonsRoom = chosenPerson.getRoom();
                for (final RoomVo room : NavigationFragment.rooms){

                    if(room.getRoomName().equals(chosenPersonsRoom)){
                        mDestRoom = room;
                        mView.setPersonDisplayValue(previousChoice);

                        mView.toggleStartInputCardVisibility(true);
                        mView.toggleGoButtonEnabled(true);

                        break;
                    }
                }
            }
        }else{
            mDestRoom = null;
        }
    }

    @Override
    protected SearchView getSearchView() {
        return mView;
    }

    private final PersonSearchView.IViewListener mViewListener = new PersonSearchView.IViewListener() {

        @Override
        public void onPersonChosen(final String _person) {
            mView.toggleGoButtonEnabled(false);

            //search for room object
            if(NavigationFragment.rooms.isEmpty()) JSONHandler.loadRooms();
            for(RoomVo room : NavigationFragment.rooms){
                final String roomOfPerson = persons.get(_person).getRoom();
                if(room.getRoomName() != null && room.getRoomName().equals(roomOfPerson)){

                    mDestRoom = room;
                    mView.toggleGoButtonEnabled(true);
                    mView.toggleStartInputCardVisibility(true);

                    saveToSharedPreferences(_person);

                    break;
                }
            }
        }


        @Override
        public void onQrClicked() {
            onQrButtonClicked();
        }


        @Override
        public void onGoClicked() {
            onGoButtonCLicked();
        }
    };
}
