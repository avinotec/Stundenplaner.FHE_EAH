package de.fhe.fhemobile.adapters.mytimetable;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableCourseComponent;

public class MyTimeTableDialogAdapter extends MyTimeTableAdapter{

    private static final String TAG = "MyTimeTableDialogAdapter";

    public MyTimeTableDialogAdapter(Context context) {
        super(context);
        setRoomVisible(false);
    }


    @Override
    protected View.OnClickListener getAddCourseBtnOnClickListener(
            final MyTimeTableCourseComponent currentItem, final Button btnAddCourse) {

        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                btnAddCourse.setActivated(!btnAddCourse.isActivated());

                if(btnAddCourse.isActivated()){
                    MainActivity.addToSubscribedCourseComponentsAndUpdateAdapters(currentItem);
                }else{
                    MainActivity.removeFromSubscribedCourseComponentsAndUpdateAdapters(currentItem);
                }
            }
        };
    }

}
