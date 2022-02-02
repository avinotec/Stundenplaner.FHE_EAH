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
package de.fhe.fhemobile.models.phonebook;

import java.util.ArrayList;

import de.fhe.fhemobile.events.EventDispatcher;
import de.fhe.fhemobile.events.SimpleEvent;
import de.fhe.fhemobile.vos.phonebook.EmployeeVo;

/**
 * Created by paul on 22.01.14.
 */
public class PhonebookModel extends EventDispatcher {

    public static final String PHONE_NUMBER_PREFIX = "";

    public static class ChangeEvent extends SimpleEvent {
        public static final String EMPLOYEES_SAVED = "employeesSaved";
        public static final String EMPLOYEES_EMPTY = "employeesEmpty";

        public ChangeEvent(final String type) {
            super(type);
        }
    }

    public ArrayList<EmployeeVo> getFoundEmployees() {
        return mFoundEmployees;
    }

    public void setFoundEmployees(final ArrayList<EmployeeVo> _FoundEmployees) {
        this.mFoundEmployees = _FoundEmployees;
        if(mFoundEmployees != null) {
            
            // remove "-;" from title, when the person has no title available
            for (final EmployeeVo employee : mFoundEmployees) {
                if ("-;".equals(employee.getTitle())) {
                    employee.setTitle("");
                }
            }
            
            notifyChange(ChangeEvent.EMPLOYEES_SAVED);
        }
        else {
            notifyChange(ChangeEvent.EMPLOYEES_EMPTY);
        }
    }

    public void addEmployeeToList(final EmployeeVo _Employee) {
        mFoundEmployees.add(_Employee);
    }

    public static PhonebookModel getInstance() {
        if(ourInstance == null) {
            ourInstance = new PhonebookModel();
        }
        return ourInstance;
    }

    private void notifyChange(final String type) {
        dispatchEvent(new ChangeEvent(type));
    }

    private PhonebookModel() {
        super();
    }

    private static PhonebookModel ourInstance;

    private ArrayList<EmployeeVo> mFoundEmployees = new ArrayList<EmployeeVo>();

}
