package de.fhe.fhemobile.views.timetable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.timetable.SelectedLessonAdapter;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableDialogFragment;
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
    private final static ArrayList<FlatDataStructure> selectedLessons = new ArrayList();
    public static ArrayList<FlatDataStructure> getLessons(){
        return selectedLessons;
    }
    public static boolean removeLesson(FlatDataStructure lesson){
        selectedLessons.remove(lesson);
        selectedLessonAdapter.notifyDataSetChanged();
        return true;
    }
    public static boolean addLesson(FlatDataStructure lesson){
        selectedLessons.add(lesson);
        selectedLessonAdapter.notifyDataSetChanged();
        return true;

    }

}
