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
package de.fhe.fhemobile.views.maps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.maps.MapsAdapter;
import de.fhe.fhemobile.models.maps.MapsModel;

/**
 * View where the user selects a building that's maps/floorplans should be shown (in a new acitivity/MapsActivty)
 *
 * Created by paul on 23.02.14.
 * Edit Nadja 02.12.2021: rename form MapsView to MapsDialogView
 */
public class MapsDialogView extends FrameLayout {

    @FunctionalInterface
    public interface ViewListener {
        void onMapItemClicked(Integer _Position);
    }

    public MapsDialogView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mModel = MapsModel.getInstance();
    }

    public void initializeView(final ViewListener _Listener) {
        mViewListener = _Listener;

        final MapsAdapter mAdapter = new MapsAdapter(mContext, mModel.getMaps());

        mMapsChoiceList.setAdapter(mAdapter);
        mMapsChoiceList.setOnItemClickListener(mMapsClickListener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mMapsChoiceList = (ListView) findViewById(R.id.mapsChoiceList);

    }

    private final AdapterView.OnItemClickListener mMapsClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            mViewListener.onMapItemClicked(position);
        }
    };

    private static final String LOG_TAG = MapsDialogView.class.getSimpleName();

    private final Context mContext;
    ViewListener mViewListener;

    private final MapsModel mModel;

    private ListView mMapsChoiceList;
}
