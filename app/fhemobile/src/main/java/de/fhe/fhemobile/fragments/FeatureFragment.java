package de.fhe.fhemobile.fragments;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by paul on 18.03.15.
 */
public abstract class FeatureFragment extends Fragment {


    public void onRestoreActionBar(ActionBar _ActionBar) {

    }

    public void setActionBarTitle(String _Title) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(_Title);
        }
    }

    public void setActionBarTitle(@StringRes int _Title) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(_Title);
    }

}
