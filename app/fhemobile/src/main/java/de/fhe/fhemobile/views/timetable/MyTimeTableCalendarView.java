package de.fhe.fhemobile.views.timetable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.timetable.CalendarAdapter;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableDialogFragment;
import de.fhe.fhemobile.fragments.mytimetable.MyTimeTableFragment;

/**
 * Created by paul on 12.03.15.
 */
public class MyTimeTableCalendarView extends LinearLayout {

    public MyTimeTableCalendarView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public MyTimeTableCalendarView(final Context context) {
        super(context);
        mContext = context;
    }

    public void initializeView(final FragmentManager _Manager) {
        mFragmentManager = _Manager;

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mEditButton = (Button)            findViewById(R.id.timetableEdit);
        mCalendarList = (ListView)          findViewById(R.id.lvCalendar);

        mEditButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                FragmentTransaction transaction= mFragmentManager.beginTransaction();
                transaction.replace(R.id.container,new MyTimeTableFragment(),MyTimeTableFragment.TAG)
                        .addToBackStack(MyTimeTableFragment.TAG)
                        .commit();
                //Todo: Wechsele zum EditorFragment (MyTimeTableFragment)
                //backstack berücksichtigen!
            }
        });
        calendarAdapter = new CalendarAdapter(mContext);
        mCalendarList.setAdapter(calendarAdapter);
    }
    private void createAddDialog(){

        final FragmentManager fm = mFragmentManager;
        MyTimeTableDialogFragment myTimeTableDialogFragment = MyTimeTableDialogFragment.newInstance();
        myTimeTableDialogFragment.show(fm, "fragment_edit_name");

    }

    private Context           mContext;
    private FragmentManager   mFragmentManager;

    private Button mEditButton;
    private ListView mCalendarList;

    private CalendarAdapter calendarAdapter;
}
