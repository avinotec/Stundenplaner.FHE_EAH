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
package de.fhe.fhemobile.views.mensa;

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
import de.fhe.fhemobile.models.mensa.MensaFoodModel;
import de.fhe.fhemobile.utils.headerlistview.HeaderListView;
import de.fhe.fhemobile.vos.mensa.MensaFoodItemCollectionVo;
import de.fhe.fhemobile.vos.mensa.MensaFoodItemVo;
import de.fhe.fhemobile.widgets.stickyHeaderList.DefaultHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.MensaImageRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.MensaRowItem;

/**
 * Created by paul on 23.01.14.
 */
public class MensaFoodView extends LinearLayout {

    public interface ViewListener {
    }

    public MensaFoodView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mModel  = MensaFoodModel.getInstance();

    }

    public void initView(ViewListener _Listener) {
        if(mModel.getFoodItems() != null) {
            populateList();
        }
        else {
            mMensaProgressBar.setVisibility(VISIBLE);
        }
    }

    public void registerModelListener() {
        mModel.addListener(MensaFoodModel.ChangeEvent.RECEIVED_FOOD_DATA, mReceivedMensaFoodListener);
        mModel.addListener(MensaFoodModel.ChangeEvent.RECEIVED_EMPTY_FOOD_DATA, mReceivedEmptyMensaFoodListener);

    }

    public void deregisterModelListener() {
        mModel.removeListener(MensaFoodModel.ChangeEvent.RECEIVED_FOOD_DATA, mReceivedMensaFoodListener);
        mModel.removeListener(MensaFoodModel.ChangeEvent.RECEIVED_EMPTY_FOOD_DATA, mReceivedEmptyMensaFoodListener);

    }

    public void populateList() {
        ArrayList<IHeaderItem> sectionList = new ArrayList<>();

        DefaultHeaderItem headerSection = new DefaultHeaderItem("", false);
        headerSection.addItem(new MensaImageRowItem(R.drawable.th_mensa));

        sectionList.add(headerSection);

        for (MensaFoodItemCollectionVo collection : mModel.getFoodItems()) {
            ArrayList<IRowItem> rowItems = new ArrayList<>();

            for (MensaFoodItemVo mensaItem : collection.getItems()) {
                rowItems.add(new MensaRowItem(mensaItem));
            }

            sectionList.add(new DefaultHeaderItem(collection.getHeadline(), true, rowItems));
        }

        mAdapter = new StickyHeaderAdapter(mContext, sectionList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mMensaProgressBar   = (ProgressBar)     findViewById(R.id.mensaProgressIndicator);
        mListView           = (HeaderListView)  findViewById(R.id.mensaListView);
        mErrorText          = (TextView)        findViewById(R.id.mensaFoodError);

        // To prevent crashing.
        // See: https://github.com/applidium/HeaderListView/issues/28
        mListView.getListView().setId(R.id.listMode);

    }

    private final EventListener mReceivedMensaFoodListener = new EventListener() {
        @Override
        public void onEvent(Event event) {
            mMensaProgressBar.setVisibility(GONE);
            populateList();
        }
    };

    private final EventListener mReceivedEmptyMensaFoodListener = new EventListener() {
        @Override
        public void onEvent(Event event) {
            mMensaProgressBar.setVisibility(GONE);
            mErrorText.setVisibility(VISIBLE);
        }
    };

    private final Context mContext;

    private MensaFoodModel      mModel              = null;

    private StickyHeaderAdapter mAdapter;

    private HeaderListView      mListView;
    private ProgressBar         mMensaProgressBar   = null;
    private TextView            mErrorText;


}
