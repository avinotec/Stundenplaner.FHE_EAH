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
     * @return A new instance of fragment TimeTableEventsFragment.
     */
    public static TimeTableWeekFragment newInstance(TimeTableWeekVo _Week) {
        TimeTableWeekFragment fragment = new TimeTableWeekFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_TIMETABLE_WEEK, _Week);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mWeek = getArguments().getParcelable(PARAM_TIMETABLE_WEEK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
