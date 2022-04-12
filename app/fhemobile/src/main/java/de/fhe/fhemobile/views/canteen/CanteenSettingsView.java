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
import de.fhe.fhemobile.models.canteen.CanteenFoodModel;

/**
 * Created by paul on 12.02.14.
 */
public class CanteenSettingsView extends FrameLayout {

    public interface ViewListener {
        void onCanteenChosen(Integer _Id, Integer _Position);
    }

    public CanteenSettingsView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mModel = CanteenFoodModel.getInstance();

        mModel.addListener(CanteenFoodModel.ChangeEvent.RECEIVED_CHOICE_ITEMS, mChoiceItemsListener);
    }

    public void initializeView(final ViewListener _Listener) {
        mViewListener = _Listener;
    }

    public void destroy() {
        mModel.removeListener(CanteenFoodModel.ChangeEvent.RECEIVED_CHOICE_ITEMS, mChoiceItemsListener);

        mViewListener = null;
        mChoiceItemsListener = null;
    }

    public void initContent() {
        mAdapter = new CanteenSettingsAdapter(mContext, mModel.getChoiceItems());
        mChoiceListView.setAdapter(mAdapter);
        mChoiceListView.setOnItemClickListener(mCanteenSelectListener);
        mChoiceListView.setItemChecked(mModel.getSelectedItemPosition(), true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mChoiceListView = (ListView) findViewById(R.id.lv_canteen_settings);

    }

    private final AdapterView.OnItemClickListener mCanteenSelectListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            mViewListener.onCanteenChosen((int) mAdapter.getItemId(position), position);
        }
    };

    private EventListener mChoiceItemsListener = new EventListener() {
        @Override
        public void onEvent(final Event event) {
            initContent();
        }
    };

    private final Context mContext;

    private final CanteenFoodModel mModel;

    private ViewListener mViewListener;
    private CanteenSettingsAdapter mAdapter;

    private ListView mChoiceListView;

}
