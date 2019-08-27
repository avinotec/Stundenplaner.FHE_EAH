package de.fhe.fhemobile.views.timetable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.FragmentManager;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableDialogFragment;

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
    }
    private void createAddDialog(){

        FragmentManager fm = mFragmentManager;
        MyTimeTableDialogFragment myTimeTableDialogFragment = MyTimeTableDialogFragment.newInstance();
        myTimeTableDialogFragment.show(fm, "fragment_edit_name");


//        Dialog addDialog = new Dialog(mContext);
//        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        addDialog.setCancelable(false);
//        addDialog.setContentView(R.layout.add_time_table);
//        addDialog.show();
    }

    private Context           mContext;
    private FragmentManager   mFragmentManager;


    private Button            mAddButton;
    private ListView          mLessonList;

}
