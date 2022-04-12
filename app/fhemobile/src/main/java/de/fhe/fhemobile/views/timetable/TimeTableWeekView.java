/*
 *  Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package de.fhe.fhemobile.views.timetable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.multidex.BuildConfig;

import org.junit.Assert;

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

    private static final String TAG = "TimeTableWeekView"; //$NON-NLS

    public TimeTableWeekView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void initializeView(final TimeTableWeekVo _Data) {
        mHeading.setText(Main.getSafeString(R.string.timetable_week) + " " + _Data.getSemesterWeek()); // $NON-NLS
        buildListEntries(_Data);

        final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.", Locale.getDefault()); // PST`
        final String startDateStr = formatter.format(_Data.getWeekStart());
        final String endDateStr = formatter.format(_Data.getWeekEnd());
        mWeekRange.setText(startDateStr + " – " + endDateStr); // $NON-NLS

        final HeaderListAdapter adapter = new HeaderListAdapter(mContext, mData);
        mDateList.setAdapter(adapter);
    }

    private void buildListEntries(final TimeTableWeekVo _Data) {
        mData = new ArrayList<>();

        for (final TimeTableDayVo dayVo : _Data.getDays()) {

            mData.add(new HeaderItem(dayVo.getDayName()));
            for (final TimeTableEventVo eventVo : dayVo.getEvents()) {

                if ( BuildConfig.DEBUG ) Assert.assertTrue( eventVo != null );
                if ( eventVo != null ) {
                    mData.add(new TimeTableEventItem(
                                    eventVo.getStartTime() + " – " + eventVo.getEndTime(),
                                    eventVo.getTitle(),
                                    eventVo.getRoom(),
                                    eventVo.getLecturer()
                            )
                    );
                }
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mHeading   = (TextView) findViewById(R.id.tv_timetable_week_heading);
        mWeekRange = (TextView) findViewById(R.id.tv_timetable_week_range);
        mDateList  = (ListView) findViewById(R.id.lv_timetable_week_list);
    }

    private final Context mContext;

    private List<IBaseItem> mData;

    private TextView mHeading;
    private TextView mWeekRange;
    private ListView mDateList;

}
