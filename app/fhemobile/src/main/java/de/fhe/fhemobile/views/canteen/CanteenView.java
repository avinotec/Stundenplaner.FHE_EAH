/*
 *  Copyright (c) 2019-2022 Ernst-Abbe-Hochschule Jena
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
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import java.text.SimpleDateFormat;
import java.util.Locale;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.canteen.CanteenPagerAdapter;
import de.fhe.fhemobile.canteencardbalance.canteencardreader.CardBalance;
import de.fhe.fhemobile.events.CanteenChangeEvent;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.canteen.CanteenModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.UserSettings;


/**
 * Created by Nadja on 05.05.2022
 */
public class CanteenView extends LinearLayout {

    public CanteenView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mNoCanteensSelectedText = (TextView) findViewById(R.id.tv_canteen_no_canteens_selected);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_canteen);
        mCanteenCardBalanceText = (TextView) findViewById(R.id.tv_canteen_card_balance);
    }

    public void initializeView(final FragmentManager _Manager, final Lifecycle _Lifecycle){

        if(!UserSettings.getInstance().getSelectedCanteenIds().isEmpty()){
            final ViewPager2 viewPager = findViewById(R.id.viewpager_canteen);
            viewPager.setAdapter(new CanteenPagerAdapter(_Manager, _Lifecycle));
        } else{
            mNoCanteensSelectedText.setVisibility(VISIBLE);
        }

        //refresh gesture
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            NetworkHandler.getInstance().fetchCanteenMenus();
        });

        setCanteenCardBalanceView();
    }

    public void registerModelListener() {
        CanteenModel.getInstance().addListener(
                CanteenChangeEvent.RECEIVED_All_CANTEEN_MENUS,
                mReceivedAllCanteenMenusEventListener);
        CanteenModel.getInstance().addListener(CanteenChangeEvent.RECEIVED_CANTEENS, mReceivedCanteenCardBalanceListener);
    }

    public void deregisterModelListener() {
        CanteenModel.getInstance().removeListener(
                CanteenChangeEvent.RECEIVED_All_CANTEEN_MENUS,
                mReceivedAllCanteenMenusEventListener);
        CanteenModel.getInstance().removeListener(CanteenChangeEvent.RECEIVED_CANTEENS, mReceivedCanteenCardBalanceListener);
    }

    /**
     * Stop refreshing animation started by swipe down gesture
     */
    public void stopRefreshingAnimation() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void setCanteenCardBalanceView(){
        String text;

        CardBalance balance = CanteenModel.getInstance().getCanteenCardBalance();
        if(balance != null){
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.ROOT);
            String dateOfStatus = balance.getDateOfStatus() != null ?
                    sdf.format(balance.getDateOfStatus()) : NO_CANTEEN_BALANCE_DATE_TEXT;
            text = getResources().getString(R.string.canteen_card_balance_status, balance.getBalance(), dateOfStatus);

         } else {
            text = getResources().getString(R.string.canteen_card_balance_status, NO_BALANCE_TEXT, NO_CANTEEN_BALANCE_DATE_TEXT);
        }

        mCanteenCardBalanceText.setText(text);
    }

    private final EventListener mReceivedAllCanteenMenusEventListener = new EventListener() {
        @Override
        public void onEvent(Event event) {
            stopRefreshingAnimation();
        }
    };


    private final EventListener mReceivedCanteenCardBalanceListener = new EventListener() {
        @Override
        public void onEvent(Event event) {
            setCanteenCardBalanceView();
        }
    };

    private TextView            mNoCanteensSelectedText;
    private SwipeRefreshLayout  mSwipeRefreshLayout;
    private TextView            mCanteenCardBalanceText;

    private static final String NO_BALANCE_TEXT = "--,--";
    private static final String NO_CANTEEN_BALANCE_DATE_TEXT = "__.__.__";

}
