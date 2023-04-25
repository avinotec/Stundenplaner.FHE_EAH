package de.fhe.fhemobile.adapters.myschedule;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import de.fhe.fhemobile.models.myschedule.MyScheduleModel;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;

public class MyScheduleDialogAdapter extends AbstractMyScheduleAdapter {

    private static final String TAG = MyScheduleDialogAdapter.class.getSimpleName();

    public MyScheduleDialogAdapter() {
        super();
        setRoomVisible(false);
    }


    @Override
    protected View.OnClickListener getAddEventSeriesBtnOnClickListener(
            final MyScheduleEventSeriesVo currentItem, final Button btnAddCourse) {

        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                btnAddCourse.setActivated(!btnAddCourse.isActivated());

                if(btnAddCourse.isActivated()){
                    MyScheduleModel.getInstance().addToSubscribedEventSeriesAndUpdateAdapters(currentItem);
                } else {
                    MyScheduleModel.getInstance().removeFromSubscribedEventSeriesAndUpdateAdapters(currentItem);
                }
            }
        };
    }

}
