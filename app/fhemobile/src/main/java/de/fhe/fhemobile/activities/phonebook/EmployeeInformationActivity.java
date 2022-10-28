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
package de.fhe.fhemobile.activities.phonebook;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.SecondaryActivity;
import de.fhe.fhemobile.fragments.phonebook.EmployeeInformationFragment;
import de.fhe.fhemobile.fragments.phonebook.EmployeeListFragment;
import de.fhe.fhemobile.models.phonebook.PhonebookModel;
import de.fhe.fhemobile.vos.phonebook.EmployeeVo;

public class EmployeeInformationActivity extends SecondaryActivity
    implements EmployeeListFragment.EmployeeListCallbacks {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.activity_employee_information);

        mModel = PhonebookModel.getInstance();

        if(savedInstanceState != null) {
            mEmployee = savedInstanceState.getParcelable(STATE_EMPLOYEE);
            final ArrayList<Parcelable> temp = savedInstanceState.getParcelableArrayList(STATE_EMPLOYEE_LIST);
            for(final Parcelable parcel : temp) {
                mModel.addEmployeeToList((EmployeeVo) parcel);
            }
        }

        final EmployeeListFragment mListFragment = EmployeeListFragment.newInstance();
        mEmployeeFragment = EmployeeInformationFragment.newInstance();

        setFragment(mListFragment);
    }

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        super.onStart();
        mModel = PhonebookModel.getInstance();
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_EMPLOYEE, mEmployee);
        outState.putParcelableArrayList(STATE_EMPLOYEE_LIST, mModel.getFoundEmployees());
    }

    /**
     * Inherited Method from EmployeeListFragment. Notifies the Single View and loads the corresponding employee.
     * @param _ListPosition
     */
    @Override
    public void onEmployeeChosen(final Integer _ListPosition) {
        mEmployee = PhonebookModel.getInstance().getFoundEmployees().get(_ListPosition);
        loadEmployeeDetailView();
    }

    private void loadEmployeeDetailView() {
        mEmployeeFragment.setEmployee(mEmployee);

        setFragment(mEmployeeFragment);
    }

    private static final String STATE_EMPLOYEE_LIST = "stateEmployeeList";
    private static final String STATE_EMPLOYEE      = "stateEmployee";

    private PhonebookModel mModel;

    private EmployeeInformationFragment mEmployeeFragment;
    private EmployeeVo                  mEmployee;

}
