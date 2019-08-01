package de.fhe.fhemobile.fragments.impressum;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.views.impressum.ImpressumView;


public class ImpressumFragment extends FeatureFragment {

    private ImpressumView mView;

    public ImpressumFragment() {
        // Required empty public constructor
    }

    public static ImpressumFragment newInstance() {
        ImpressumFragment fragment = new ImpressumFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (ImpressumView) inflater.inflate(R.layout.fragment_impressum, container, false);
        return mView;
    }


}
