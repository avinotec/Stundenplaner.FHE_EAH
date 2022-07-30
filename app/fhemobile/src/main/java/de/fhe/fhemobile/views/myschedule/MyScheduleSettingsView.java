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
package de.fhe.fhemobile.views.myschedule;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.myschedule.MyScheduleDialogFragment;


public class MyScheduleSettingsView extends LinearLayout {

    private FragmentManager mFragmentManager;
    private ListView mCourseListView;


    public MyScheduleSettingsView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScheduleSettingsView(final Context context) {
        super(context);
    }

    public void initializeView(final FragmentManager _Manager) {
        mFragmentManager = _Manager;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        final FloatingActionButton mAddButton = (FloatingActionButton) findViewById(R.id.btn_myschedule_settings_add_eventseries);
        mAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                createAddDialog();
            }
        });


        mCourseListView = (ListView) findViewById(R.id.lv_myschedule_settings);
        mCourseListView.setAdapter(MainActivity.myScheduleSettingsAdapter);
    }

    /**
     * Sets view to show if the course list is empty
     */
    public void setCourseListEmptyView(){
        final TextView emptyView = new TextView( getContext() );
        emptyView.setText(getResources().getString(R.string.myschedule_empty));
        mCourseListView.setEmptyView(emptyView);
    }


    void createAddDialog(){
        final FragmentManager fm = mFragmentManager;
        final MyScheduleDialogFragment myScheduleDialogFragment = MyScheduleDialogFragment.newInstance();
        myScheduleDialogFragment.show(fm, "fragment_edit_name");
    }
}
