/*
 * Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fhe.fhemobile.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.TimeZone;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.vos.CafeAquaResponse;
import hirondelle.date4j.DateTime;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Paul Cech on 01.04.15.
 */
public class AquaWidget extends RelativeLayout {

    public AquaWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        preInitialize();
    }

    public void update(boolean _forceUpdate) {

        if (_forceUpdate ||
            DateTime.now(TimeZone.getDefault()).getMilliseconds(TimeZone.getDefault()) - mLastTimeUpdated > UPDATE_INTERVALL) {

            if (!mStartedFetching) {
                mStartedFetching = true;
                NetworkHandler.getInstance().fetchCafeAquaStatus(mResponseCallback);
            }
        }
    }

    private void preInitialize() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_aqua, this, true);
        setOnClickListener(mClickListener);
    }

    private void postInitialize() {
        update(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mStatusLabel = (ImageView) findViewById(R.id.aquaStatus);

        postInitialize();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        update(false);
    }



    private final Callback<CafeAquaResponse> mResponseCallback = new Callback<CafeAquaResponse>() {
        @Override
        public void onResponse(Call<CafeAquaResponse> call, Response<CafeAquaResponse> response) {
            mStartedFetching = false;
            if (mStatusLabel != null && response.body() != null ) {
                mStatusLabel.setImageResource(response.body().isOpen() ? R.drawable.cafe_aqua_open : R.drawable.cafe_aqua_closed);
                mLastTimeUpdated = DateTime.now(TimeZone.getDefault()).getMilliseconds(TimeZone.getDefault());
            }
        }

        @Override
        public void onFailure(Call<CafeAquaResponse> call, Throwable t) {
            mStartedFetching = false;
        }
    };

    private final OnClickListener mClickListener = v -> update(true);

    private static final String LOG_TAG             = AquaWidget.class.getSimpleName();

    private static final long   UPDATE_INTERVALL    =  900000;

    private final Context     mContext;

    private ImageView   mStatusLabel;

    private long        mLastTimeUpdated = 0;
    private boolean     mStartedFetching = false;

}
