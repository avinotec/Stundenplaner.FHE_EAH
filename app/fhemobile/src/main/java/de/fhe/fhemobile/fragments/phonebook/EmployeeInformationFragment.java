package de.fhe.fhemobile.fragments.phonebook;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.models.phonebook.PhonebookModel;
import de.fhe.fhemobile.views.phonebook.EmployeeInformationView;
import de.fhe.fhemobile.vos.phonebook.EmployeeVo;


public class EmployeeInformationFragment extends Fragment {

    public EmployeeInformationFragment() {
    }

    public static EmployeeInformationFragment newInstance() {
        EmployeeInformationFragment fragment = new EmployeeInformationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (EmployeeInformationView) inflater.inflate(R.layout.fragment_employee_information, container, false);
        mView.initializeView(mViewListener);

        mView.populateView(mEmployee);
        return mView;
    }

    public void setEmployee(EmployeeVo _Employee) {
        mEmployee = _Employee;
    }

    private final EmployeeInformationView.ViewListener mViewListener = new EmployeeInformationView.ViewListener() {
        @Override
        public void onMailClicked() {
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
            //sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
            sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { mEmployee.getMail()});
            sendIntent.setData(Uri.parse("mailto:"));
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_default_subject));
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_default_text));
            startActivity(Intent.createChooser(sendIntent, ""));
        }

        @Override
        public void onPhoneClicked() {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + PhonebookModel.PHONE_NUMBER_PREFIX + mEmployee.getPhone()));
            startActivity(intent);
        }
    };

    private EmployeeInformationView mView;
    private EmployeeVo mEmployee;

}
