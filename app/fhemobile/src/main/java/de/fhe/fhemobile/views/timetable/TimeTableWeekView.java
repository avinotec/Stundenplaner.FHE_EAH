package de.fhe.fhemobile.views.timetable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.HeaderListAdapter;
import de.fhe.fhemobile.vos.timetable.TimeTableDayVo;
import de.fhe.fhemobile.vos.timetable.TimeTableEventVo;
import de.fhe.fhemobile.vos.timetable.TimeTableWeekVo;
import de.fhe.fhemobile.widgets.headerList.HeaderItem;
import de.fhe.fhemobile.widgets.headerList.IBaseItem;
import de.fhe.fhemobile.widgets.headerList.TimeTableEventItem;

/**
 * Created by paul on 23.01.14.
 */
public class TimeTableWeekView extends LinearLayout {

    public TimeTableWeekView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void initializeView(final TimeTableWeekVo _Data) {
        mHeading.setText(Main.getSafeString(R.string.timetable_week) + " " + _Data.getWeekInYear());
        buildListEntries(_Data);
        calculateWeekRange(_Data);

        final HeaderListAdapter adapter = new HeaderListAdapter(mContext, mData);
        mDateList.setAdapter(adapter);
    }

    private void calculateWeekRange(final TimeTableWeekVo _Data) {

        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, _Data.getWeekInYear());
        calendar.set(Calendar.YEAR, _Data.getYear());

        final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.", Locale.getDefault()); // PST`
        final Date startDate = calendar.getTime();
        final String startDateInStr = formatter.format(startDate);

        calendar.add(Calendar.DATE, 4);
        final Date enddate = calendar.getTime();
        final String endDaString = formatter.format(enddate);

        mWeekRange.setText(startDateInStr + " - " + endDaString);
    }

    private void buildListEntries(final TimeTableWeekVo _Data) {
        mData = new ArrayList<>();

        for (TimeTableDayVo dayVo : _Data.getDays()) {

            mData.add(new HeaderItem(dayVo.getName()));
            for (TimeTableEventVo eventVo : dayVo.getEvents()) {
                mData.add(new TimeTableEventItem(
                        eventVo.getStartTime() + " - " + eventVo.getEndTime(),
                        eventVo.getShortTitle(),
                        eventVo.getRoom(),
                        eventVo.getLecturer()
                        )
                );
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mHeading   = (TextView) findViewById(R.id.timeTableWeekHeading);
        mWeekRange = (TextView) findViewById(R.id.timeTableWeekRange);
        mDateList  = (ListView) findViewById(R.id.timeTableWeekList);
    }

    private static final String LOG_TAG = TimeTableWeekView.class.getSimpleName();

    private Context mContext;

    private List<IBaseItem> mData;

    private TextView mHeading;
    private TextView mWeekRange;
    private ListView mDateList;

}
