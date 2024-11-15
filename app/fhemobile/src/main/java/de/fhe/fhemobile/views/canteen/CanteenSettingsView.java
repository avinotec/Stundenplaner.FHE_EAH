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
package de.fhe.fhemobile.views.canteen;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;


import java.util.ArrayList;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.canteen.CanteenSettingsAdapter;
import de.fhe.fhemobile.events.CanteenChangeEvent;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.canteen.CanteenModel;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.vos.canteen.CanteenVo;

/**
 * Created by paul on 12.02.14
 * Edited by Nadja - 04/2022
 */
public class CanteenSettingsView extends LinearLayout {

    @FunctionalInterface
    public interface ViewListener {
        void onCanteenClicked(final String _CanteenId);
    }

    public CanteenSettingsView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mCanteenModel = CanteenModel.getInstance();

        mCanteenModel.addListener(CanteenChangeEvent.RECEIVED_CANTEENS, mCanteenSelectedListener);
    }

    public void initializeView(final ViewListener _Listener) {
        mCanteenViewListener = _Listener;
    }

//    TODO destroy() never gets called. So the intended actions never happen.
// --Commented out by Inspection START (23.04.2023 15:05):
//        public void destroy() {
//        mCanteenModel.removeListener(CanteenChangeEvent.RECEIVED_CANTEENS, mCanteenSelectedListener);
//
//        mCanteenViewListener = null;
//        mCanteenSelectedListener = null;
//    }
// --Commented out by Inspection STOP (23.04.2023 15:05)

    /**
     * Set adapter and OnItemClickListener of canteen list view,
     * and set selected canteens as checked
     */
    public void initCanteenSelectionListView() {
        final CanteenSettingsAdapter mCanteenListAdapter = new CanteenSettingsAdapter(mContext, mCanteenModel.getCanteens());
        mCanteenListView.setAdapter(mCanteenListAdapter);
        mCanteenListView.setOnItemClickListener(mCanteenSelectListener);

        final ArrayList<String> selectedCanteens = UserSettings.getInstance().getSelectedCanteenIds();
        for(int i = 0; i < mCanteenListView.getCount(); i++){
            final String canteenId = ((CanteenVo) mCanteenListView.getItemAtPosition(i)).getCanteenId();

            if(selectedCanteens.contains(canteenId)){
                mCanteenListView.setItemChecked(i, true);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mCanteenListView = (ListView) findViewById(R.id.lv_canteen_choice);
        mCanteenListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    private final AdapterView.OnItemClickListener mCanteenSelectListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            mCanteenViewListener.onCanteenClicked(
                    ((CanteenVo) mCanteenListView.getItemAtPosition(position)).getCanteenId());
        }
    };

    /**
     * Listener for {@link CanteenChangeEvent#RECEIVED_CANTEENS}
     */
    private final EventListener mCanteenSelectedListener = new EventListener() {
        @Override
        public void onEvent(final Event event) {
            initCanteenSelectionListView();
        }
    };

    private final Context           mContext;
    private final CanteenModel      mCanteenModel;

    ViewListener                    mCanteenViewListener;

    private ListView                mCanteenListView;


}
