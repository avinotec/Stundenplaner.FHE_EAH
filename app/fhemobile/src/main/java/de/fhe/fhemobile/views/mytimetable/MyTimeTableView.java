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
package de.fhe.fhemobile.views.mytimetable;

import static de.fhe.fhemobile.utils.Utils.correctUmlauts;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.adapters.mytimetable.MyTimeTableSelectedLessonAdapter;
import de.fhe.fhemobile.comparator.Date_Comparator;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableDialogFragment;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.vos.timetable.FlatDataStructure;

/**
 * Created by paul on 12.03.15.
 */
public class MyTimeTableView extends LinearLayout {

    /** sortedLessons: Liste der selectedLessons sortiert für die Ausgabe im view, ausschließlich dafür benötigt. */
    public static List<FlatDataStructure> sortedLessons = new ArrayList<>();
    public static List<FlatDataStructure> selectedLessons = new ArrayList();

    private FragmentManager   mFragmentManager;

    private Button            mAddButton;
    private ListView          mLessonList;

    private static MyTimeTableSelectedLessonAdapter myTimeTableSelectedLessonAdapter;

    public MyTimeTableView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTimeTableView(final Context context) {
        super(context);
    }

    public void initializeView(final FragmentManager _Manager) {
        mFragmentManager = _Manager;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAddButton      = (Button)            findViewById(R.id.timetableAddLesson);
        mLessonList     = (ListView)          findViewById(R.id.lvLessons);

        mAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                createAddDialog();
            }
        });
        myTimeTableSelectedLessonAdapter = new MyTimeTableSelectedLessonAdapter(Main.getAppContext());
        mLessonList.setAdapter(myTimeTableSelectedLessonAdapter);
    }


    private void createAddDialog(){
        final FragmentManager fm = mFragmentManager;
        MyTimeTableDialogFragment myTimeTableDialogFragment = MyTimeTableDialogFragment.newInstance();
        myTimeTableDialogFragment.show(fm, "fragment_edit_name");
    }

    /**
     * returns the "selectedLessons" in sorted order
     * @param comparator
     * @return
     */
    private static List<FlatDataStructure> getSortedList(Comparator<FlatDataStructure> comparator){
        List<FlatDataStructure> sortedList = new ArrayList<FlatDataStructure>(selectedLessons);
        if(sortedList.isEmpty() == false){
            Collections.sort(sortedList,comparator);
        }
        return sortedList;
    }

    /**
     * Stores the list of completeLessons
     * @param completeLessons
     */
    public static void setCompleteLessons(List<FlatDataStructure> completeLessons){
        if(completeLessons == null){
            MainActivity.completeLessons = new ArrayList<>();
        } else {
            MainActivity.completeLessons = completeLessons;
        }
    }

    /**
     * returns the complete lessons list
     * @return
     */
    public static List<FlatDataStructure> getCompleteLessons(){
        return MainActivity.completeLessons;
    }

    public static void setLessons(final List<FlatDataStructure> lessons){
        if(lessons == null){
            selectedLessons = new ArrayList<>();
        } else {

            selectedLessons = lessons;
            sortedLessons = getSortedList(new Date_Comparator());
        }

    }

    public static List<FlatDataStructure> getLessons(){
        return selectedLessons;
    }

    /**
     *
     * @return
     */
    public static List<String[]> generateNegativeLessons(){

    	final List<String[]> negativeList = new ArrayList<>();

    	//Durchsuche alle Vorlesungen
    	for(FlatDataStructure eventInCompleteList : MainActivity.completeLessons){
            boolean exists = false;

            //überspringe einzelne Vorlesungsevents wenn der Vorlesungstitel schon zur negativeList hinzugefügt wurde
    		for(String[] eventdata : negativeList) {
    		    if(eventdata[0].equals( FlatDataStructure.cutEventTitle(eventInCompleteList.getEvent().getTitle()))
                && eventdata[1].equals( eventInCompleteList.getStudyGroup().getTimeTableId())){
                    exists = true;
                    break;
                }
            }

    		//dieses eventInCompleteList gibt es noch nicht in der negativeList
    		if(!exists) {
    		    boolean isSelected = false;
    		    //durchsuche selectedLessons nach dem eventInCompletedList
                for (FlatDataStructure eventInSelectedList : selectedLessons) {

                    if (FlatDataStructure.cutEventTitle(eventInCompleteList.getEvent().getTitle())
                            .equals(FlatDataStructure.cutEventTitle(eventInSelectedList.getEvent().getTitle()))
                    && eventInCompleteList.getStudyGroup().getTimeTableId()
                            .equals(eventInSelectedList.getStudyGroup().getTimeTableId())){
                        isSelected = true;
                    }

                }
                //add event to negative List if eventInCompletedList is in selectedLessons
                if(!isSelected){
                    final String[] eventdata = new String[2];
                    //add an event (title + id) to negativeList
                    eventdata[0] = FlatDataStructure.cutEventTitle(eventInCompleteList.getEvent().getTitle());
                    eventdata[1] = eventInCompleteList.getStudyGroup().getTimeTableId();
                    negativeList.add(eventdata);
                }
            }
	    }

       return negativeList;
    }

    /**
     *
     * @return sorted Lessons list
     */
    public static List<FlatDataStructure> getSortedLessons(){  return sortedLessons;  }

    /**
     *
     * @param lesson
     */
    public static void removeLesson(final FlatDataStructure lesson){
        selectedLessons.remove(lesson);
        sortedLessons = getSortedList(new Date_Comparator());

        final Gson gson = new Gson();
        final String json = correctUmlauts(gson.toJson(MyTimeTableView.getLessons()));
        final SharedPreferences sharedPreferences = Main.getAppContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("list", json);
        editor.apply();

        myTimeTableSelectedLessonAdapter.notifyDataSetChanged();
    }

    /**
     *
     * @param lesson
     */
    public static void addLesson(final FlatDataStructure lesson){
        selectedLessons.add(lesson);
        sortedLessons = getSortedList(new Date_Comparator());

        final Gson gson = new Gson();
        final String json = correctUmlauts(gson.toJson(MyTimeTableView.getLessons()));
        final SharedPreferences sharedPreferences = Main.getAppContext().getSharedPreferences("prefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("list", json);
        editor.apply();
        myTimeTableSelectedLessonAdapter.notifyDataSetChanged();
    }

    /**
     *
     * @param selectedLessons
     * @return
     */
    public static List<FlatDataStructure> loadSelectedList(final List<FlatDataStructure> selectedLessons){

        final List<FlatDataStructure> completeSelectedList = new ArrayList();
        final List<String> studygroups = new ArrayList();

        for(final FlatDataStructure event:selectedLessons){
            if(!studygroups.contains(event.getStudyGroup().getTimeTableId())){
                studygroups.add(event.getStudyGroup().getTimeTableId());
                completeSelectedList.addAll(NetworkHandler.getInstance().reloadEvents(event));
            }
        }

        return completeSelectedList;
    }

    /**
     *
     * @param text
     */
    public void setEmptyText(final String text){
        final TextView emptyView = findViewById(R.id.emptyView);
        emptyView.setText(text);
        mLessonList.setEmptyView(emptyView);
    }

}
