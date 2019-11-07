package de.fhe.fhemobile.fragments.phonebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.phonebook.EmployeeInformationActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.views.phonebook.PhonebookSearchView;


public class PhonebookSearchFragment extends FeatureFragment {

    public PhonebookSearchFragment() {
        // Required empty public constructor
    }

    public static PhonebookSearchFragment newInstance() {
        PhonebookSearchFragment fragment = new PhonebookSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null) {
            mView.destroy();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (PhonebookSearchView) inflater.inflate(R.layout.fragment_phonebook_search, container, false);
        mView.initializeView(mViewListener);
        return mView;
    }

    private final PhonebookSearchView.ViewListener mViewListener = new PhonebookSearchView.ViewListener() {
        @Override
        public void onSearchClicked(String _FirstName, String _LastName) {
            NetworkHandler.getInstance().fetchEmployees(_FirstName, _LastName);
        }

        @Override
        public void onEmployeesFound() {
            Intent intent = new Intent(getActivity(), EmployeeInformationActivity.class);
            startActivity(intent);
        }
    };

    private static final String LOG_TAG = PhonebookSearchFragment.class.getSimpleName();

    private PhonebookSearchView mView;
}
