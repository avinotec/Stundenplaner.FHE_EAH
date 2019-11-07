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
package de.fhe.fhemobile.views.phonebook;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.phonebook.PhonebookModel;

public class PhonebookSearchView extends FrameLayout {

    public interface ViewListener {
        void onSearchClicked(String _FirstName, String _LastName);
        void onEmployeesFound();
    }

    public PhonebookSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        mModel = PhonebookModel.getInstance();
    }

    public void initializeView(ViewListener _Listener) {
        mViewListener = _Listener;

        mSearchButton.setOnClickListener(mSearchClick);

        mModel.addListener(PhonebookModel.ChangeEvent.EMPLOYEES_SAVED, mFoundEmployeesListener);
        mModel.addListener(PhonebookModel.ChangeEvent.EMPLOYEES_EMPTY, mFoundNoEmployeesListener);
    }

    /**
     * Remove the listener from the model
     */
    public void destroy() {
        mViewListener = null;

        mModel.removeListener(PhonebookModel.ChangeEvent.EMPLOYEES_EMPTY, mFoundNoEmployeesListener);
        mModel.removeListener(PhonebookModel.ChangeEvent.EMPLOYEES_SAVED, mFoundEmployeesListener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mFirstName          = (EditText)    findViewById(R.id.phonebookFirstName);
        mLastName           = (EditText)    findViewById(R.id.phoneBookLastName);
        mSearchButton       = (Button)      findViewById(R.id.phonebookSearchButton);
        mProgressIndicator  = (ProgressBar) findViewById(R.id.progressIndicator);
        mErrorText          = (TextView)    findViewById(R.id.phonebookError);
    }

    private final EventListener mFoundEmployeesListener = new EventListener() {
        @Override
        public void onEvent(Event event) {
            mProgressIndicator.setVisibility(GONE);
            mErrorText.setVisibility(GONE);
            mViewListener.onEmployeesFound();
        }
    };

    private final EventListener mFoundNoEmployeesListener = new EventListener() {
        @Override
        public void onEvent(Event event) {
            mProgressIndicator.setVisibility(GONE);
            mErrorText.setVisibility(VISIBLE);
        }
    };

    private final OnClickListener mSearchClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mErrorText.setVisibility(GONE);
            mProgressIndicator.setVisibility(VISIBLE);
            mViewListener.onSearchClicked(mFirstName.getText().toString(), mLastName.getText().toString());
        }
    };

    private static final String LOG_TAG = PhonebookSearchView.class.getSimpleName();

    private final Context mContext;

    private EditText mFirstName;
    private EditText mLastName;
    private Button   mSearchButton;
    private ProgressBar mProgressIndicator;

    private TextView mErrorText;

    private final PhonebookModel mModel;

    private ViewListener mViewListener;
}
