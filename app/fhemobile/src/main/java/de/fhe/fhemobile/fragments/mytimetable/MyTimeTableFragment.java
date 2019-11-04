package de.fhe.fhemobile.fragments.mytimetable;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.timeTableChanges.RequestModel;
import de.fhe.fhemobile.models.timeTableChanges.ResponseModel;
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

/*
	@Override
	public void onResume() {
		super.onResume();
	}
*/


	@Override
	public void onDetach() {
		super.onDetach();
		final RequestModel request = new RequestModel(1, MainActivity.getFirebaseToken(),new Date().getTime()-86400000);
		String title="";
		String setID="";
		for (FlatDataStructure event:MyTimeTableView.getLessons()){
			String eventTitle=FlatDataStructure.cutEventTitle(event.getEvent().getTitle());

			if((title.equals(eventTitle)&&setID.equals(event.getStudyGroup().getTimeTableId()))==false){
				request.addLesson(event.getStudyGroup().getTimeTableId(),event.getEvent().getTitle());
				title=eventTitle;
				setID=event.getStudyGroup().getTimeTableId();
			}
		}

		final Gson gson= new Gson();
		final String json = gson.toJson(request);
		Log.d(TAG, "onDetach: "+json);

		NetworkHandler.getInstance().registerTimeTableChanges(json, new Callback<ResponseModel>() {
			final int CHANGEREASON_EDIT = 1;
			final int CHANGEREASON_NEW = 3;
			final int CHANGEREASON_DELETE = 2;
			@Override
			public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
				Log.d(TAG, "onResponse: "+response.raw().request().url());
				Log.d(TAG, "onResponse code: "+response.code()+" geparsed: "+gson.toJson(response.body()));
				List<ResponseModel.Change> changes = response.body().getChanges();

				final List<String[]> negativeList = MyTimeTableView.generateNegativeLessons();
				Iterator<ResponseModel.Change> iterator= changes.iterator();

				while(iterator.hasNext()){
					ResponseModel.Change change=iterator.next();
					//TODO: Negativ Liste von der gespeicherten Liste erstellen
					boolean isInNegativeList=false;
					for(String[] negativeEvent:negativeList){
						if(change.getNewEventJson().getTitle().contains(negativeEvent[0])
								&& change.getSetSplusKey().equals(negativeEvent[1])){
							isInNegativeList=true;
						}

					}
					if(isInNegativeList){
						iterator.remove();

					}
				}

//				String changesAsString="";
//				for(ResponseModel.Change change: changes){
//					changesAsString+=(change.getChangesReasonText()+"/n/n");
//				}

//                new AlertDialog.Builder(MyTimeTableFragment.this.getActivity())
//                        .setTitle("Änderungen")
//                       // .setMessage(changesAsString)
//                        .setMessage("test")
//
//                        // Specifying a listener allows you to take an action before dismissing the dialog.
//                        // The dialog is automatically dismissed when a dialog button is clicked.
//                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Continue with delete operation
//                            }
//                        })
//                        .create()
//
//                        // A null listener allows the button to dismiss the dialog and take no further action.
//                        .show();


				for(ResponseModel.Change change : changes){
					//Aenderung eines Events: suche den Event und ueberschreibe seine Daten
					if(change.getChangesReason()==CHANGEREASON_EDIT) {
						FlatDataStructure event = FlatDataStructure.getEventByID(MyTimeTableView.getLessons(),change.getNewEventJson().getUid());
						if(event!=null){
							event.setEvent(change.getNewEventJson());
						}
					}
					//Hinzufuegen eines neuen Events: Erstelle ein neues Element vom Typ FlatDataStructure, schreibe alle Set-, Semester- und Studiengangdaten in diesen
					//und fuege dann die Eventdaten des neuen Events hinzu. Anschliessend in die Liste hinzufuegen.
					if(change.getChangesReason()==CHANGEREASON_NEW) {
						FlatDataStructure event = FlatDataStructure.queryGetEventsByStudyGroupTitle(MyTimeTableView.getLessons(),change.getSetSplusKey()).get(0).copy();
						event.setEvent(change.getNewEventJson());
						MyTimeTableView.getLessons().add(event);

					}
					//Loeschen eines Events: Suche den Event mit der SplusID und lösche ihn aus der Liste.
					if(change.getChangesReason()==CHANGEREASON_DELETE){
						FlatDataStructure event = FlatDataStructure.getEventByID(MyTimeTableView.getLessons(),change.getNewEventJson().getUid());
						MyTimeTableView.getLessons().remove(event);
					}
				}

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
