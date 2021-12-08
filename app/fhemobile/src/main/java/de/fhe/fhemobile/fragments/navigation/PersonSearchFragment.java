package de.fhe.fhemobile.fragments.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonSearchFragment extends FeatureFragment {



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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_person_search, container, false);
    }
}