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

package de.fhe.fhemobile.fragments.timetable;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.views.timetable.TimeTableWeekView;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;


public class TimeTableWeekFragment extends Fragment {

    public TimeTableWeekFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TimeTableFragment.
     */
    public static TimeTableWeekFragment newInstance(final TimeTableWeekVo _Week) {
        final TimeTableWeekFragment fragment = new TimeTableWeekFragment();
        final Bundle args = new Bundle();
        args.putParcelable(PARAM_TIMETABLE_WEEK, _Week);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mWeek = getArguments().getParcelable(PARAM_TIMETABLE_WEEK);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (TimeTableWeekView) inflater.inflate(R.layout.fragment_time_table_week, container, false);
        mView.initializeView(mWeek);
        return mView;
    }


    private static final String LOG_TAG              = TimeTableWeekFragment.class.getSimpleName();

    private static final String PARAM_TIMETABLE_WEEK = "paramTimeTableWeek";

    private TimeTableWeekView mView;
    private TimeTableWeekVo   mWeek;

}
