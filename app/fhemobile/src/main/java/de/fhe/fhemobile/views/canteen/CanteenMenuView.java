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
package de.fhe.fhemobile.views.canteen;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.StickyHeaderAdapter;
import de.fhe.fhemobile.events.CanteenChangeEvent;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.canteen.CanteenModel;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.utils.headerlistview.HeaderListView;
import de.fhe.fhemobile.vos.canteen.CanteenDishVo;
import de.fhe.fhemobile.vos.canteen.CanteenMenuDayVo;
import de.fhe.fhemobile.widgets.stickyHeaderList.CanteenRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.DefaultHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.CanteenImageRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IRowItem;

/**
 * Created by paul on 23.01.14
 * Edited by Nadja on 05.05.2022
 */
public class CanteenMenuView extends LinearLayout {

    private static final String TAG = CanteenMenuView.class.getSimpleName();

    public interface ViewListener {
    }

    public CanteenMenuView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mCanteenProgressBar = (ProgressBar)     findViewById(R.id.progress_indicator_canteen);
        mErrorText          = (TextView)        findViewById(R.id.tv_canteen_error);
        mMenuDaysListView   = (HeaderListView)  findViewById(R.id.lv_canteen_menu);
        mCanteenNameText    = (TextView)        findViewById(R.id.tv_canteen_title);
    }

    public void initializeView(final String _CanteenId){
        mCanteenId = _CanteenId;
        mCanteenNameText.setText(UserSettings.getInstance().getSelectedCanteen(mCanteenId).getCanteenName());

        if(CanteenModel.getInstance().getMenu(mCanteenId) != null) {
            populateMenuDaysList();
        } else {
            mCanteenProgressBar.setVisibility(VISIBLE);
        }

        Log.d(TAG, mCanteenId + " View initialized");
    }

    public void registerModelListener() {
        CanteenModel.getInstance().addListener(
                CanteenChangeEvent.getReceivedCanteenMenuEventWithCanteenId(mCanteenId),
                mReceivedCanteenMenuEventListener);

        CanteenModel.getInstance().addListener(
                CanteenChangeEvent.getReceivedEmptyMenuEventWithCanteenId(mCanteenId),
                mReceivedEmptyCanteenMenuEventListener);

    }

    public void deregisterModelListener() {
        CanteenModel.getInstance().removeListener(
                CanteenChangeEvent.getReceivedCanteenMenuEventWithCanteenId(mCanteenId),
                mReceivedCanteenMenuEventListener);

        CanteenModel.getInstance().removeListener(
                CanteenChangeEvent.getReceivedEmptyMenuEventWithCanteenId(mCanteenId),
                mReceivedEmptyCanteenMenuEventListener);
    }

    /**
     * Load menu from {@link CanteenModel}
     * and use list of {@link CanteenMenuDayVo}s to populate listview
     */
    void populateMenuDaysList() {
        final ArrayList<IHeaderItem> sectionList = new ArrayList<>();

        final DefaultHeaderItem headerSection = new DefaultHeaderItem("", false);
        headerSection.addItem(new CanteenImageRowItem(R.drawable.th_canteen));

        sectionList.add(headerSection);

        final List<CanteenMenuDayVo> menuDaysList = CanteenModel.getInstance().getMenu(mCanteenId);

        for (final CanteenMenuDayVo collection : menuDaysList) {
            final ArrayList<IRowItem> rowItems = new ArrayList<>();

            for (final CanteenDishVo canteenItem : collection.getItems()) {
                rowItems.add(new CanteenRowItem(canteenItem));
            }

            sectionList.add(new DefaultHeaderItem(collection.getHeadline(), true, rowItems));
        }

        final StickyHeaderAdapter mAdapter = new StickyHeaderAdapter(getContext(), sectionList);
        mMenuDaysListView.setAdapter(mAdapter);

        Log.d(TAG, mCanteenId + " List populated");
    }




    private final EventListener mReceivedCanteenMenuEventListener = new EventListener() {
        @Override
        public void onEvent(final Event event) {
            mCanteenProgressBar.setVisibility(GONE);
            populateMenuDaysList();
        }
    };

    private final EventListener mReceivedEmptyCanteenMenuEventListener = new EventListener() {
        @Override
        public void onEvent(final Event event) {
            mCanteenProgressBar.setVisibility(GONE);
            mErrorText.setVisibility(VISIBLE);
        }
    };

    private String mCanteenId;

    private HeaderListView mMenuDaysListView;
    private TextView mCanteenNameText;

    ProgressBar mCanteenProgressBar;
    TextView mErrorText;




}
