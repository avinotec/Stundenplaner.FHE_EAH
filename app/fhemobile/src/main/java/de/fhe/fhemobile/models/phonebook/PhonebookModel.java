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

        public ChangeEvent(String type) {
            super(type);
        }
    }

    public ArrayList<EmployeeVo> getFoundEmployees() {
        return mFoundEmployees;
    }

    public void setFoundEmployees(ArrayList<EmployeeVo> _FoundEmployees) {
        mFoundEmployees = _FoundEmployees;
        if(mFoundEmployees != null) {
            
            // remove "-;" from title, when the person has no title available
            for (EmployeeVo employee : mFoundEmployees) {
                if (employee.getTitle().equals("-;")) {
                    employee.setTitle("");
                }
            }
            
            notifyChange(ChangeEvent.EMPLOYEES_SAVED);
        }
        else {
            notifyChange(ChangeEvent.EMPLOYEES_EMPTY);
        }
    }

    public void addEmployeeToList(EmployeeVo _Employee) {
        mFoundEmployees.add(_Employee);
    }

    public static PhonebookModel getInstance() {
        if(ourInstance == null) {
            ourInstance = new PhonebookModel();
        }
        return ourInstance;
    }

    private void notifyChange(String type) {
        dispatchEvent(new ChangeEvent(type));
    }

    private PhonebookModel() {
        super();
    }

    private static PhonebookModel ourInstance = null;

    private ArrayList<EmployeeVo> mFoundEmployees = new ArrayList<EmployeeVo>();

}
