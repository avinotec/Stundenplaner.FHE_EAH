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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.StickyHeaderAdapter;
import de.fhe.fhemobile.adapters.mensa.MensaDayAdapter;
import de.fhe.fhemobile.models.mensa.MensaFoodModel;
import de.fhe.fhemobile.utils.headerlistview.HeaderListView;
import de.fhe.fhemobile.vos.mensa.MensaDayVo;
import de.fhe.fhemobile.vos.mensa.MensaFoodItemCollectionVo;
import de.fhe.fhemobile.vos.mensa.MensaFoodItemVo;
import de.fhe.fhemobile.widgets.stickyHeaderList.DefaultHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.MensaImageRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.MensaRowItem;

/**
 * Created by Nadja - 03/2022
 */
public class MensaDayViewNEW extends LinearLayout {

    private static final String TAG = "MensaDayView"; //$NON-NLS


    public MensaDayViewNEW(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void initializeView(final MensaDayVo _Data){
        if(_Data != null) {
            mHeader.setText(_Data.getDate());
            MensaDayAdapter adapter = new MensaDayAdapter(mContext);
            adapter.setItems(_Data.getItems());

            mMensaItemList.setAdapter(adapter);
        }
        else {
            mMensaProgressBar.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mHeader         = (TextView) findViewById(R.id.tv_mensa_day_header);
        mMensaItemList  = (ListView) findViewById(R.id.lv_mensa_day_menu);
    }


    private final Context   mContext;

    private ProgressBar mMensaProgressBar   = null;

    private TextView            mHeader;
    private ListView            mMensaItemList;

}
