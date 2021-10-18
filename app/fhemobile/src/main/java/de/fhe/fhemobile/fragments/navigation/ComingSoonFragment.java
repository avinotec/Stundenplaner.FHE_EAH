package de.fhe.fhemobile.fragments.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;

/**
 *
 */
public class ComingSoonFragment extends FeatureFragment{

    private static final String TAG = "COMING_SOON";

    private View      mView;

    public ComingSoonFragment(){
        // Required empty public constructor
    }

    public static ComingSoonFragment newInstance() {
        ComingSoonFragment fragment = new ComingSoonFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_coming_soon, container, false);

        return mView;
    }

}
