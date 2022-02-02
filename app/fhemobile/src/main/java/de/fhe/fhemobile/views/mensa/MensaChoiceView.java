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
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.mensa.MensaChoiceAdapter;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.mensa.MensaFoodModel;

/**
 * Created by paul on 12.02.14.
 */
public class MensaChoiceView extends FrameLayout {

    public interface ViewListener {
        void onMensaChosen(Integer _Id, Integer _Position);
    }

    public MensaChoiceView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mModel = MensaFoodModel.getInstance();

        mModel.addListener(MensaFoodModel.ChangeEvent.RECEIVED_CHOICE_ITEMS, mChoiceItemsListener);
    }

    public void initView(final ViewListener _Listener) {
        mViewListener = _Listener;
    }

    public void destroy() {
        mModel.removeListener(MensaFoodModel.ChangeEvent.RECEIVED_CHOICE_ITEMS, mChoiceItemsListener);

        mViewListener = null;
        mChoiceItemsListener = null;
    }

    public void initContent() {
        mAdapter = new MensaChoiceAdapter(mContext, mModel.getChoiceItems());
        mChoiceListView.setAdapter(mAdapter);
        mChoiceListView.setOnItemClickListener(mMensaSelectListener);
        mChoiceListView.setItemChecked(mModel.getSelectedItemPosition(), true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mChoiceListView = (ListView) findViewById(R.id.mensaChoiceListView);

    }

    private final AdapterView.OnItemClickListener mMensaSelectListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            mViewListener.onMensaChosen((int) mAdapter.getItemId(position), position);
        }
    };

    private EventListener mChoiceItemsListener = new EventListener() {
        @Override
        public void onEvent(final Event event) {
            initContent();
        }
    };

    private final Context mContext;

    private final MensaFoodModel mModel;

    private ViewListener mViewListener;
    private MensaChoiceAdapter mAdapter;

    private ListView mChoiceListView;

}
