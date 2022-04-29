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
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.canteen.CanteenSettingsAdapter;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.canteen.CanteenModel;
import de.fhe.fhemobile.utils.UserSettings;

/**
 * Created by paul on 12.02.14
 * Edited by Nadja - 04/2022
 */
public class CanteenSettingsView extends FrameLayout {

    public interface ViewListener {
        void onCanteenSelected(Integer _Id, Integer _Position);
    }

    public CanteenSettingsView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mCanteenModel = CanteenModel.getInstance();

        mCanteenModel.addListener(CanteenModel.CanteenChangeEvent.RECEIVED_CANTEENS, mCanteenSelectedListener);
    }

    public void initializeView(final ViewListener _Listener) {
        mCanteenViewListener = _Listener;
    }

    public void destroy() {
        mCanteenModel.removeListener(CanteenModel.CanteenChangeEvent.RECEIVED_CANTEENS, mCanteenSelectedListener);

        mCanteenViewListener = null;
        mCanteenSelectedListener = null;
    }

    /**
     * Set adapter and OnItemClickListener of canteen list view,
     * and set selected canteens as checked
     */
    public void initCanteenSelectionListView() {
        mCanteenListAdapter = new CanteenSettingsAdapter(mContext, mCanteenModel.getCanteens());
        mCanteenListView.setAdapter(mCanteenListAdapter);
        mCanteenListView.setOnItemClickListener(mCanteenSelectListener);

        for(int i : UserSettings.getInstance().getListOfSelectedCanteenIdsAsInt()){
            mCanteenListView.setItemChecked(i, true);
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
            mCanteenViewListener.onCanteenSelected((int) mCanteenListAdapter.getItemId(position), position);
        }
    };

    private EventListener mCanteenSelectedListener = new EventListener() {
        @Override
        public void onEvent(final Event event) {
            initCanteenSelectionListView();
        }
    };

    private final Context           mContext;
    private final CanteenModel      mCanteenModel;

    private ViewListener            mCanteenViewListener;

    private ListView                mCanteenListView;
    private CanteenSettingsAdapter  mCanteenListAdapter;


}
