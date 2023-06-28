/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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
package de.fhe.fhemobile.views.phonebook;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.models.phonebook.PhonebookModel;
import de.fhe.fhemobile.vos.phonebook.EmployeeVo;

/**
 * Created by paul on 11.02.14.
 */
public class EmployeeInformationView extends FrameLayout {

    public interface ViewListener {
        void onMailClicked();
        void onPhoneClicked();
    }

    public EmployeeInformationView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public void initializeView(final ViewListener _Listener) {
        mViewListener = _Listener;

    }

    public void populateView(final EmployeeVo _Employee) {
        final SpannableString mail = new SpannableString(_Employee.getMail());
        mail.setSpan(new UnderlineSpan(), 0, _Employee.getMail().length(), 0);

        final String phoneNumber = PhonebookModel.PHONE_NUMBER_PREFIX + _Employee.getPhone();
        final SpannableString phone = new SpannableString(phoneNumber);
        phone.setSpan(new UnderlineSpan(), 0, phoneNumber.length(), 0);

        mName.setText(_Employee.getFullName());
        mPhone.setText(phone);
        mFax.setText(PhonebookModel.PHONE_NUMBER_PREFIX + _Employee.getFax()); // $NON-NLS
        mRole.setText(_Employee.getRole());
        mMail.setText(mail);
        mDivision.setText(_Employee.getDivision());
        mRoom.setText(_Employee.getRoom());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mName = (TextView) findViewById(R.id.employeeName);
        mPhone = (TextView) findViewById(R.id.employeePhone);
        mFax = (TextView) findViewById(R.id.employeeFax);
        mRole = (TextView) findViewById(R.id.employeeRole);
        mMail = (TextView) findViewById(R.id.employeeMail);
        mDivision = (TextView) findViewById(R.id.employeeDivision);
        mRoom = (TextView) findViewById(R.id.employeeRoom);

        mMail.setOnClickListener(mMailClick);
        mPhone.setOnClickListener(mPhoneClick);

    }

    private final OnClickListener mMailClick = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            mViewListener.onMailClicked();
        }
    };

    private final OnClickListener mPhoneClick = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            mViewListener.onPhoneClicked();
        }
    };

    ViewListener mViewListener;

    private TextView mName;
    private TextView mPhone;
    private TextView mFax;
    private TextView mRole;
    private TextView mMail;
    private TextView mDivision;
    private TextView mRoom;
}
