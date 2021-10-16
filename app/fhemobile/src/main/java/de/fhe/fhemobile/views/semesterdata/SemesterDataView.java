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
package de.fhe.fhemobile.views.semesterdata;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.StickyHeaderAdapter;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.semesterdata.SemesterDataModel;
import de.fhe.fhemobile.utils.SemesterDataUtils;
import de.fhe.fhemobile.utils.headerlistview.HeaderListView;
import de.fhe.fhemobile.vos.semesterdata.SemesterTimesVo;
import de.fhe.fhemobile.vos.semesterdata.SemesterVo;
import de.fhe.fhemobile.widgets.stickyHeaderList.DefaultHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.DoubleRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.ImageRowItem;

/**
 * Created by paul on 23.02.14.
 */
public class SemesterDataView extends LinearLayout {

    public SemesterDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mModel   = SemesterDataModel.getInstance();
    }

    public void initializeView() {
        if(mModel.getSemesterData() != null) {
            initializeList();
        }
        else {
            mProgress.setVisibility(VISIBLE);
        }
    }

    public void registerModelListener() {
        mModel.addListener(SemesterDataModel.ChangeEvent.RECEIVED_SEMESTER_DATA,     mModelListener);
        mModel.addListener(SemesterDataModel.ChangeEvent.SEMESTER_SELECTION_CHANGED, mModelListener);
    }

    public void deregisterModelListener() {
        mModel.removeListener(SemesterDataModel.ChangeEvent.RECEIVED_SEMESTER_DATA,     mModelListener);
        mModel.removeListener(SemesterDataModel.ChangeEvent.SEMESTER_SELECTION_CHANGED, mModelListener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mListView  = (HeaderListView) findViewById(R.id.semesterListView);
        mProgress  = (ProgressBar)    findViewById(R.id.semesterProgressBar);

        // To prevent crashing.
        // See: https://github.com/applidium/HeaderListView/issues/28
        mListView.getListView().setId(R.id.listMode);
    }

    private void initializeList() {
        SemesterVo chosenSemester = mModel.getSemesterData()[mModel.getChosenSemester()];

        ArrayList<IHeaderItem> sectionList = new ArrayList<>();

        DefaultHeaderItem headerSection = new DefaultHeaderItem("", false);
        headerSection.addItem(new ImageRowItem(R.drawable.th_termdates));

        sectionList.add(headerSection);

        ArrayList<IRowItem> courseRowItems = new ArrayList<>();
        for (SemesterTimesVo times : chosenSemester.getCourseTimes()) {
            courseRowItems.add(new DoubleRowItem(SemesterDataUtils.getHeadline(times), SemesterDataUtils.getSubHeadline(times)));
        }
        sectionList.add(new DefaultHeaderItem(mContext.getString(R.string.semester_course_times), true, courseRowItems));

        ArrayList<IRowItem> holidayRowItems = new ArrayList<>();
        for (SemesterTimesVo times : chosenSemester.getHolidays()) {
            holidayRowItems.add(new DoubleRowItem(SemesterDataUtils.getHeadline(times), SemesterDataUtils.getSubHeadline(times)));
        }
        sectionList.add(new DefaultHeaderItem(mContext.getString(R.string.semester_holidays), true, holidayRowItems));

        ArrayList<IRowItem> datesRowItems = new ArrayList<>();
        for (SemesterTimesVo times : chosenSemester.getImportantDates()) {
            datesRowItems.add(new DoubleRowItem(SemesterDataUtils.getHeadline(times), SemesterDataUtils.getSubHeadline(times)));
        }
        sectionList.add(new DefaultHeaderItem(mContext.getString(R.string.semester_important_dates), true, datesRowItems));

        mAdapter = new StickyHeaderAdapter(mContext, sectionList);

        mProgress.setVisibility(GONE);
        mListView.setAdapter(mAdapter);
    }

    private final EventListener mModelListener = new EventListener() {
        @Override
        public void onEvent(Event event) {
            initializeList();
        }
    };

    private static final String LOG_TAG = SemesterDataView.class.getSimpleName();

    private final Context                     mContext;

    private final SemesterDataModel           mModel;
    private StickyHeaderAdapter         mAdapter;

    private ProgressBar                 mProgress;
    private HeaderListView              mListView;
}
