/*
 *  Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
            //Der alte Quellcode hatte explizit versucht GMail zu öffnen. War Gmail nicht installiert, gab es einen Absturz.
            //Jetzt wird nach einer App gesucht, die mit "mailto:"-Url umgehen kann. Ist eine gefunden, wird diese benutzt,
            //sind mehrere vorhanden, kann man auswählen. Ist keine installiert, zeigt das System dies selbstständig an.
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
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
