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
package de.fhe.fhemobile.views.semesterdates;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.StickyHeaderAdapter;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.events.SemesterDatesChangeEvent;
import de.fhe.fhemobile.models.semesterdates.SemesterDatesModel;
import de.fhe.fhemobile.utils.semesterdates.SemesterDatesUtils;
import de.fhe.fhemobile.utils.headerlistview.HeaderListView;
import de.fhe.fhemobile.vos.semesterdates.SemesterPeriodOrDateVo;
import de.fhe.fhemobile.vos.semesterdates.SemesterVo;
import de.fhe.fhemobile.widgets.stickyHeaderList.DefaultHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.DoubleRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.ImageRowItem;

/**
 * Created by paul on 23.02.14.
 */
public class SemesterDatesView extends LinearLayout {

    public SemesterDatesView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mModel   = SemesterDatesModel.getInstance();
    }

    public void initializeView() {
        if(mModel.getSemesterVos() != null) {
            initializeList();
            final TextView tvTitle = findViewById(R.id.tv_semester_title);
            //decided for name instead of long name when introducing previous and next semester buttons
            tvTitle.setText(mModel.getChosenSemesterVo().getLongName());
        }
        else {
            mProgress.setVisibility(VISIBLE);
        }
    }

    public void registerModelListener() {
        mModel.addListener(SemesterDatesChangeEvent.RECEIVED_SEMESTER_DATES,     mModelListener);
        mModel.addListener(SemesterDatesChangeEvent.SEMESTER_SELECTION_CHANGED, mModelListener);
    }

    public void deregisterModelListener() {
        mModel.removeListener(SemesterDatesChangeEvent.RECEIVED_SEMESTER_DATES,     mModelListener);
        mModel.removeListener(SemesterDatesChangeEvent.SEMESTER_SELECTION_CHANGED, mModelListener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mListView  = (HeaderListView) findViewById(R.id.semesterListView);
        mProgress  = (ProgressBar)    findViewById(R.id.semesterProgressBar);

        // To prevent crashing.
        // See: https://github.com/applidium/HeaderListView/issues/28
        //mListView.getListView().setId(R.id.listMode);
        //MS neue Lösung, da ab 2021 die Ressourcen nun in separaten Namensräumen verwendet werden
        final HeaderListView yourListView = (HeaderListView) findViewWithTag("HeaderListViewTag");
        mListView.getListView().setId( yourListView.getId() );
    }

    void initializeList() {
        final SemesterVo chosenSemester = mModel.getSemesterVos()[mModel.getChosenSemester()];

        final ArrayList<IHeaderItem> sectionList = new ArrayList<>();

        final DefaultHeaderItem headerSection = new DefaultHeaderItem("", false);
        headerSection.addItem(new ImageRowItem(R.drawable.th_semesterdates));

        sectionList.add(headerSection);

        final ArrayList<IRowItem> courseRowItems = new ArrayList<>();
        for (final SemesterPeriodOrDateVo times : chosenSemester.getCourseTimes()) {
            courseRowItems.add(new DoubleRowItem(SemesterDatesUtils.getHeadline(times), SemesterDatesUtils.getSubHeadline(times)));
        }
        sectionList.add(new DefaultHeaderItem(mContext.getString(R.string.semester_periods), true, courseRowItems));

        final ArrayList<IRowItem> holidayRowItems = new ArrayList<>();
        for (final SemesterPeriodOrDateVo times : chosenSemester.getHolidays()) {
            holidayRowItems.add(new DoubleRowItem(SemesterDatesUtils.getHeadline(times), SemesterDatesUtils.getSubHeadline(times)));
        }
        sectionList.add(new DefaultHeaderItem(mContext.getString(R.string.semester_holidays), true, holidayRowItems));

        final ArrayList<IRowItem> datesRowItems = new ArrayList<>();
        for (final SemesterPeriodOrDateVo times : chosenSemester.getImportantDates()) {
            datesRowItems.add(new DoubleRowItem(SemesterDatesUtils.getHeadline(times), SemesterDatesUtils.getSubHeadline(times)));
        }
        sectionList.add(new DefaultHeaderItem(mContext.getString(R.string.semester_important_dates), true, datesRowItems));

        final StickyHeaderAdapter mAdapter = new StickyHeaderAdapter(mContext, sectionList);

        mProgress.setVisibility(GONE);
        mListView.setAdapter(mAdapter);
    }

    private final EventListener mModelListener = new EventListener() {
        @Override
        public void onEvent(final Event event) {
            initializeList();
        }
    };

    private static final String LOG_TAG = SemesterDatesView.class.getSimpleName();

    private final Context                     mContext;

    private final SemesterDatesModel    mModel;

    private ProgressBar                 mProgress;
    private HeaderListView              mListView;
}
