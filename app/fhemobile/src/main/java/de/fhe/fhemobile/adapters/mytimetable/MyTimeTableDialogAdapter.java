package de.fhe.fhemobile.adapters.mytimetable;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventSeriesVo;

public class MyTimeTableDialogAdapter extends AbstractMyTimeTableAdapter {

    private static final String TAG = MyTimeTableDialogAdapter.class.getSimpleName();

    public MyTimeTableDialogAdapter(Context context) {
        super(context);
        setRoomVisible(false);
    }


    @Override
    protected View.OnClickListener getAddEventSeriesBtnOnClickListener(
            final MyTimeTableEventSeriesVo currentItem, final Button btnAddCourse) {

        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                btnAddCourse.setActivated(!btnAddCourse.isActivated());

                if(btnAddCourse.isActivated()){
                    MainActivity.addToSubscribedEventSeriesAndUpdateAdapters(currentItem);
                }else{
                    MainActivity.removeFromSubscribedEventSeriesAndUpdateAdapters(currentItem);
                }
            }
        };
    }

}
