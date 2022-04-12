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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.canteen.CanteenDayAdapter;
import de.fhe.fhemobile.vos.canteen.CanteenDayVo;

/**
 * Created by Nadja - 03/2022
 */
public class CanteenDayViewNEW extends LinearLayout {

    private static final String TAG = CanteenDayViewNEW.class.getSimpleName(); //$NON-NLS


    public CanteenDayViewNEW(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void initializeView(final CanteenDayVo _Data){
        if(_Data != null) {
            mHeader.setText(_Data.getDate());
            CanteenDayAdapter adapter = new CanteenDayAdapter(mContext);
            adapter.setItems(_Data.getItems());

            mCanteenItemList.setAdapter(adapter);
        }
        else {
            mCanteenProgressBar.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mHeader         = (TextView) findViewById(R.id.tv_canteen_day_header);
        mCanteenItemList = (ListView) findViewById(R.id.lv_canteen_day_menu);
    }


    private final Context   mContext;

    private ProgressBar mCanteenProgressBar = null;

    private TextView mHeader;
    private ListView mCanteenItemList;

}
