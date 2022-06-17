package de.fhe.fhemobile.adapters.myschedule;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import java.util.List;

import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.vos.myschedule.MyScheduleEventSeriesVo;

public class MyScheduleSettingsAdapter extends AbstractMyScheduleAdapter {

    private static final String TAG = MyScheduleSettingsAdapter.class.getSimpleName();

    public MyScheduleSettingsAdapter(Context context, List<MyScheduleEventSeriesVo> _items) {
        super(context);
        setItems(_items);
        setRoomVisible(true);
    }


    @Override
    protected View.OnClickListener getAddEventSeriesBtnOnClickListener(
            final MyScheduleEventSeriesVo currentItem, final Button btnAddCourse) {

        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                MainActivity.removeFromSubscribedEventSeriesAndUpdateAdapters(currentItem);
            }
        };
    }

}
