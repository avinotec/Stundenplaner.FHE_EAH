package de.fhe.fhemobile.adapters.mytimetable;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import java.util.List;

import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.vos.mytimetable.MyTimeTableEventSeriesVo;

public class MyTimeTableOverviewAdapter extends AbstractMyTimeTableAdapter {

    private static final String TAG = "MyTimeTableOverviewAdapter";

    public MyTimeTableOverviewAdapter(Context context, List<MyTimeTableEventSeriesVo> _items) {
        super(context);
        setItems(_items);
        setRoomVisible(true);
    }


    @Override
    protected View.OnClickListener getAddCourseBtnOnClickListener(
            final MyTimeTableEventSeriesVo currentItem, final Button btnAddCourse) {

        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                MainActivity.removeFromSubscribedEventSeriesAndUpdateAdapters(currentItem);
            }
        };
    }

}
