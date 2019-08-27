package de.fhe.fhemobile.fragments.mytimetable;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.views.timetable.MyTimeTableView;
import de.fhe.fhemobile.vos.timetable.StudyCourseVo;
import de.fhe.fhemobile.vos.timetable.TermsVo;
import de.fhe.fhemobile.vos.timetable.TimeTableResponse;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyTimeTableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTimeTableFragment extends FeatureFragment {


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TimeTableFragment.
     */
    public static MyTimeTableFragment newInstance() {
        MyTimeTableFragment fragment = new MyTimeTableFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MyTimeTableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChosenCourse       = null;
        mChosenTerm         = null;
        mChosenTimetableId  = null;

        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (MyTimeTableView) inflater.inflate(R.layout.fragment_my_time_table, container, false);
        mView.initializeView(getChildFragmentManager());

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }




    private MyTimeTableView     mView;

    private TimeTableResponse mResponse;
    private StudyCourseVo     mChosenCourse;
    private TermsVo           mChosenTerm;
    private String            mChosenTimetableId;
}
