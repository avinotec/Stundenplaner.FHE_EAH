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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.StickyHeaderAdapter;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.canteen.CanteenModel;
import de.fhe.fhemobile.utils.headerlistview.HeaderListView;
import de.fhe.fhemobile.vos.canteen.CanteenMenuDayVo;
import de.fhe.fhemobile.vos.canteen.CanteenDishVo;
import de.fhe.fhemobile.widgets.stickyHeaderList.DefaultHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.CanteenImageRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.CanteenRowItem;

/**
 * Created by paul on 23.01.14.
 */
public class CanteenView extends LinearLayout {

    public interface ViewListener {
    }

    public CanteenView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mModel  = CanteenModel.getInstance();

    }

    public void initializeView() {
        if(mModel.getMenus() != null) {
            populateList();
        }
        else {
            mCanteenProgressBar.setVisibility(VISIBLE);
        }
    }

    public void registerModelListener() {
        mModel.addListener(CanteenModel.ChangeEvent.RECEIVED_FOOD_DATA, mReceivedCanteenFoodListener);
        mModel.addListener(CanteenModel.ChangeEvent.RECEIVED_EMPTY_FOOD_DATA, mReceivedEmptyCanteenFoodListener);

    }

    public void deregisterModelListener() {
        mModel.removeListener(CanteenModel.ChangeEvent.RECEIVED_FOOD_DATA, mReceivedCanteenFoodListener);
        mModel.removeListener(CanteenModel.ChangeEvent.RECEIVED_EMPTY_FOOD_DATA, mReceivedEmptyCanteenFoodListener);

    }

    public void populateList() {
        final ArrayList<IHeaderItem> sectionList = new ArrayList<>();

        final DefaultHeaderItem headerSection = new DefaultHeaderItem("", false);
        headerSection.addItem(new CanteenImageRowItem(R.drawable.th_canteen));

        sectionList.add(headerSection);

        for (final CanteenMenuDayVo collection : mModel.getMenus()) {
            final ArrayList<IRowItem> rowItems = new ArrayList<>();

            for (final CanteenDishVo canteenItem : collection.getItems()) {
                rowItems.add(new CanteenRowItem(canteenItem));
            }

            sectionList.add(new DefaultHeaderItem(collection.getHeadline(), true, rowItems));
        }

        mAdapter = new StickyHeaderAdapter(mContext, sectionList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mCanteenProgressBar = (ProgressBar)     findViewById(R.id.progress_indicator_canteen);
        mListView           = (HeaderListView)  findViewById(R.id.lv_canteen);
        mErrorText          = (TextView)        findViewById(R.id.tv_canteen_error);

        // To prevent crashing.
        // See: https://github.com/applidium/HeaderListView/issues/28
        //MS neue Lösung, da ab 2021 die Ressourcen nun in separaten Namensräumen verwendet werden
        //mListView.getListView().setId(R.id.listMode);
        final HeaderListView yourListView = (HeaderListView) findViewWithTag("HeaderListViewTag");
        mListView.getListView().setId( yourListView.getId() );

    }

    private final EventListener mReceivedCanteenFoodListener = new EventListener() {
        @Override
        public void onEvent(final Event event) {
            mCanteenProgressBar.setVisibility(GONE);
            populateList();
        }
    };

    private final EventListener mReceivedEmptyCanteenFoodListener = new EventListener() {
        @Override
        public void onEvent(final Event event) {
            mCanteenProgressBar.setVisibility(GONE);
            mErrorText.setVisibility(VISIBLE);
        }
    };

    private final Context mContext;

    private CanteenModel mModel              = null;

    private StickyHeaderAdapter mAdapter;

    private HeaderListView      mListView;
    private ProgressBar mCanteenProgressBar = null;
    private TextView            mErrorText;


}
