package de.fhe.fhemobile.adapters.mytimetable;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableCourseComponent;

public class MyTimeTableOverviewAdapterNEW extends MyTimeTableAdapter{

    private static final String TAG = "MyTimeTableOverviewAdapter";

    public MyTimeTableOverviewAdapterNEW(Context context, List<MyTimeTableCourseComponent> _items) {
        super(context);
        setItems(_items);
        setRoomVisible(true);
    }


    @Override
    protected View.OnClickListener getAddCourseBtnOnClickListener(
            final MyTimeTableCourseComponent currentItem, final ViewGroup parent, final Button btnAddCourse) {

        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                MainActivity.removeFromSubscribedCourseComponentsAndUpdateAdapters(currentItem);
            }
        };
    }

}
