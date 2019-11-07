/*
 * Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhe.fhemobile.views.phonebook;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.phonebook.PhonebookAdapter;
import de.fhe.fhemobile.models.phonebook.PhonebookModel;

/**
 * Created by paul on 01.04.14.
 */
public class EmployeeListView extends FrameLayout {

    public interface ViewListener {
        void onListItemClicked(Integer _ListPosition);
    }

    public EmployeeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mModel   = PhonebookModel.getInstance();
    }

    public void initializeView(ViewListener _Listener) {
        mViewListener = _Listener;

        if(mModel.getFoundEmployees() != null) {
            initializeList();
        }
//        else {
//            // TODO: Handle no Employees
//        }

        mList.setOnItemClickListener(mListClickListener);
    }

    public void destroy() {
        mViewListener   = null;
        mModel          = null;
        mList           = null;
        mContext        = null;
    }

    /**
     * Finalize inflating a view from XML.  This is called as the last phase
     * of inflation, after all child views have been added.
     * <p/>
     * <p>Even if the subclass overrides onFinishInflate, they should always be
     * sure to call the super method, so that we get called.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mList = (ListView) findViewById(R.id.employeeResults);
    }

    private void initializeList() {
        mList.setAdapter(new PhonebookAdapter(mContext, mModel.getFoundEmployees()));
    }

    private final AdapterView.OnItemClickListener mListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mViewListener.onListItemClicked(position);
        }
    };

    private Context         mContext;
    private ViewListener    mViewListener;

    private PhonebookModel  mModel;

    private ListView        mList;

}
