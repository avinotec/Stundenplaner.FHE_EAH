/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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
import java.util.List;
import java.util.Locale;

import de.fhe.fhemobile.Main;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.HeaderListAdapter;
import de.fhe.fhemobile.vos.timetable.TimetableDayVo;
import de.fhe.fhemobile.vos.timetable.TimetableEventVo;
import de.fhe.fhemobile.vos.timetable.TimetableWeekVo;
import de.fhe.fhemobile.widgets.headerList.HeaderItem;
import de.fhe.fhemobile.widgets.headerList.IBaseItem;
import de.fhe.fhemobile.widgets.headerList.TimetableEventItem;

/**
 * Created by paul on 23.01.14.
 */
public class TimetableWeekView extends LinearLayout {

    private static final String TAG = TimetableWeekView.class.getSimpleName();

    public TimetableWeekView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void initializeView(final TimetableWeekVo weekVo) {
        mWeekHeader.setText(Main.getSafeString(R.string.timetable_week) + " " + weekVo.getGuiSemesterWeek()); // $NON-NLS
        buildListEntries(weekVo);

        final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.", Locale.ROOT); // PST`
        final String startDateStr = formatter.format(weekVo.getWeekStart());
        final String endDateStr = formatter.format(weekVo.getWeekEnd());
        mWeekRange.setText(startDateStr + " – " + endDateStr); // $NON-NLS

        final HeaderListAdapter adapter = new HeaderListAdapter(mContext, mData);
        mEventList.setAdapter(adapter);
    }

    private void buildListEntries(final TimetableWeekVo _Data) {
        mData = new ArrayList<>();

        for (final TimetableDayVo dayVo : _Data.getDays()) {

            mData.add(new HeaderItem(dayVo.getDayName()));
            for (final TimetableEventVo eventVo : dayVo.getEvents()) {

                if ( BuildConfig.DEBUG ) Assert.assertNotNull(eventVo);
                if ( eventVo != null ) {
                    mData.add(new TimetableEventItem(
                                    eventVo.getStartTimeString() + " – " + eventVo.getEndTimeString(),
                                    eventVo.getGuiTitle(),
                                    eventVo.getLocationListAsString(),
                                    eventVo.getLecturerListAsString()
                            )
                    );
                }
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mWeekHeader = (TextView) findViewById(R.id.tv_timetable_week_header);
        mWeekRange = (TextView) findViewById(R.id.tv_timetable_week_range);
        mEventList = (ListView) findViewById(R.id.lv_timetable_week_list);
    }

    private final Context mContext;

    private List<IBaseItem> mData;

    private TextView mWeekHeader;
    private TextView mWeekRange;
    private ListView mEventList;

}
