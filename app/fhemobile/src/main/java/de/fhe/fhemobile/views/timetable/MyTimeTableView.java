package de.fhe.fhemobile.views.timetable;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.adapters.timetable.SelectedLessonAdapter;
import de.fhe.fhemobile.comparator.Date_Comparator;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableDialogFragment;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

/**
 * Created by paul on 12.03.15.
 */
public class MyTimeTableView extends LinearLayout {

    public MyTimeTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public MyTimeTableView(Context context) {
        super(context);
        mContext = context;
    }

    public void initializeView(FragmentManager _Manager) {
        mFragmentManager = _Manager;


    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAddButton      = (Button)            findViewById(R.id.timetableAddLesson);
        mLessonList     = (ListView)          findViewById(R.id.lvLessons);

        mAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createAddDialog();
            }
        });
        selectedLessonAdapter = new SelectedLessonAdapter(mContext);
        mLessonList.setAdapter(selectedLessonAdapter);
    }
    private void createAddDialog(){

        FragmentManager fm = mFragmentManager;
        MyTimeTableDialogFragment myTimeTableDialogFragment = MyTimeTableDialogFragment.newInstance();
        myTimeTableDialogFragment.show(fm, "fragment_edit_name");

    }

    private static Context           mContext;
    private FragmentManager   mFragmentManager;


    private Button            mAddButton;
    private ListView          mLessonList;

    private static SelectedLessonAdapter selectedLessonAdapter;



    private static List<FlatDataStructure> getSortedList(Comparator<FlatDataStructure> comparator){
        List<FlatDataStructure> sortedList = new ArrayList<FlatDataStructure>(MainActivity.selectedLessons);
        Collections.sort(sortedList,comparator);
        return sortedList;
    }
    public static void setCompleteLessons(List<FlatDataStructure> lessons){
        if(lessons==null){

            MainActivity.completeLessons=new ArrayList<>();
        }
        else{
            MainActivity.completeLessons=lessons;

        }

    }
    public static List<FlatDataStructure> getCompleteLessons(){

        return MainActivity.completeLessons;

    }
    public static void setLessons(List<FlatDataStructure> lessons){
        if(lessons==null){
            MainActivity.selectedLessons=new ArrayList<>();
        }else{

            MainActivity.selectedLessons=lessons;
            MainActivity.sortedLessons=getSortedList(new Date_Comparator());

        }

    }
    public static List<FlatDataStructure> getLessons(){
        return MainActivity.selectedLessons;
    }
    public static List<String[]>generateNegativeLessons(){
    	List<String[]> negativeList= new ArrayList<>();
    	for(FlatDataStructure eventInCompleteList: MainActivity.completeLessons){
    	    boolean exists=false;
    		for(String[] eventdata:negativeList){
    		    if(eventdata[0].equals(FlatDataStructure.cutEventTitle(eventInCompleteList.getEvent().getTitle()))
                && eventdata[1].equals(eventInCompleteList.getStudyGroup().getTimeTableId())){
                    exists=true;
                    break;
                }
            }
    		if(exists==false) {
    		    boolean isSelected=false;
                for (FlatDataStructure eventInSelectedList : MainActivity.selectedLessons) {
                    if (FlatDataStructure.cutEventTitle(eventInCompleteList.getEvent().getTitle()).equals(FlatDataStructure.cutEventTitle(eventInSelectedList.getEvent().getTitle()))
                    && eventInCompleteList.getStudyGroup().getTimeTableId().equals(eventInSelectedList.getStudyGroup().getTimeTableId())){
                        isSelected=true;
                    }
                }
                if(isSelected==false){
                    String[] eventdata = new String[2];
                    eventdata[0]=FlatDataStructure.cutEventTitle(eventInCompleteList.getEvent().getTitle());
                    eventdata[1]=eventInCompleteList.getStudyGroup().getTimeTableId();
                    negativeList.add(eventdata);
                }
            }
	    }
       return negativeList;
    }
    public static List<FlatDataStructure> getSortedLessons(){return MainActivity.sortedLessons;}

    public static boolean removeLesson(FlatDataStructure lesson){
        MainActivity.selectedLessons.remove(lesson);
        MainActivity.sortedLessons=getSortedList(new Date_Comparator());
        Gson gson = new Gson();
        String json = gson.toJson(MyTimeTableView.getLessons());
        SharedPreferences sharedPreferences =mContext.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("list",json);
        editor.commit();
        selectedLessonAdapter.notifyDataSetChanged();
        return true;
    }
    public static boolean addLesson(FlatDataStructure lesson){
        MainActivity.selectedLessons.add(lesson);
        MainActivity.sortedLessons=getSortedList(new Date_Comparator());
        Gson gson = new Gson();
        String json = gson.toJson(MyTimeTableView.getLessons());
        SharedPreferences sharedPreferences =mContext.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("list",json);
        editor.commit();
        selectedLessonAdapter.notifyDataSetChanged();
        return true;

    }
    public static List<FlatDataStructure> loadSelectedList(List<FlatDataStructure> selectedLessons){
        List<FlatDataStructure> completeSelectedList = new ArrayList();
        List<String> studygroups = new ArrayList();
        for(FlatDataStructure event:selectedLessons){
            if(!studygroups.contains(event.getStudyGroup().getTimeTableId())){
                studygroups.add(event.getStudyGroup().getTimeTableId());
                completeSelectedList.addAll(NetworkHandler.getInstance().reloadEvents(event));
            }
        }

    return completeSelectedList;
    }

}
