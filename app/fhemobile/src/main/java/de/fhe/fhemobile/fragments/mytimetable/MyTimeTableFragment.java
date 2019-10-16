package de.fhe.fhemobile.fragments.mytimetable;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.TimeTableChanges.RequestModel;
import de.fhe.fhemobile.models.TimeTableChanges.ResponseModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.views.timetable.MyTimeTableView;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;
import de.fhe.fhemobile.vos.timetable.StudyCourseVo;
import de.fhe.fhemobile.vos.timetable.TermsVo;
import de.fhe.fhemobile.vos.timetable.TimeTableResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyTimeTableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTimeTableFragment extends FeatureFragment {
    public static final String TAG = "MyTimeTableFragment";

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


    @Override
    public void onDetach() {
        super.onDetach();
        final RequestModel request = new RequestModel(1, MainActivity.getFirebaseToken(),new Date().getTime()-86400000);
        String title="";
        String setID="";
        for (FlatDataStructure event:MyTimeTableView.getLessons()){
            String eventTitle="";
            try {
                Pattern p = Pattern.compile("^(.*[a-z|A-Z|ä|Ä|ü|Ü|ö|Ö|ß])");
                Matcher m = p.matcher(event.getEvent().getTitle());
                if(m.find()){
                    eventTitle =m.group(1);
                    Log.d(TAG, "eventTitle: "+eventTitle);
                }
                else {
                    eventTitle = event.getEvent().getTitle();
                }

            }
            catch (Exception e){
                Log.d(TAG, "onDetach: Error",e);
            }
            if((title.equals(eventTitle)&&setID.equals(event.getStudyGroup().getTimeTableId()))==false){
                request.addLesson(event.getStudyGroup().getTimeTableId(),event.getEvent().getTitle());
                title=eventTitle;
                setID=event.getStudyGroup().getTimeTableId();
            }
        }
        final Gson gson= new Gson();
        String json = gson.toJson(request);
        Log.d(TAG, "onDetach: "+json);
        NetworkHandler.getInstance().registerTimeTableChanges(json, new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                Log.d(TAG, "onResponse: "+response.raw().request().url());

                Log.d(TAG, "onResponse code: "+response.code()+" geparsed: "+gson.toJson(response.body()));
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.toString());
            }
        });
    }

    private MyTimeTableView     mView;

    private TimeTableResponse mResponse;
    private StudyCourseVo     mChosenCourse;
    private TermsVo           mChosenTerm;
    private String            mChosenTimetableId;
}
