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
package de.fhe.fhemobile.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.TimeZone;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.Utils;
import de.fhe.fhemobile.vos.WeatherResponse;
import hirondelle.date4j.DateTime;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by paul on 04.03.15.
 */
public class WeatherHeader extends RelativeLayout {
    
    public WeatherHeader(Context context) {
        super(context);
        mContext = context;

        preInitialize();
    }

    public WeatherHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        preInitialize();
    }
    
    private void preInitialize() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_weather_header, this, true);
    }
    
    private void postInitialize() {
        DateTime dt = DateTime.today(TimeZone.getDefault());
        
        mDayOfWeek.setText(dt.format("WWWW", Locale.getDefault()));
        mDate.setText(dt.format("DD. MMMM", Locale.getDefault()));

        NetworkHandler.getInstance().fetchWeather(mWeatherResponseCallback);
    }
    
    private void update(String _Temperature, int _BackgroundId, int _IconId) {
        mTemperature.setText(_Temperature);
        mBackground.setImageResource(Utils.getResourceId("wetter_bg" + _BackgroundId, "drawable"));
        mIcon.setImageResource(Utils.getResourceId("wetter_icon" + _BackgroundId, "drawable"));
        
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        
        mBackground  = (ImageView) findViewById(R.id.weatherBackground);
        mIcon        = (ImageView) findViewById(R.id.weatherIcon);
        mTemperature = (TextView)  findViewById(R.id.weatherTemperature);
        mDayOfWeek   = (TextView)  findViewById(R.id.weatherDayOfWeek);
        mDate        = (TextView)  findViewById(R.id.weatherDate);
        
        postInitialize();
    }
    
    private final Callback<WeatherResponse> mWeatherResponseCallback = new Callback<WeatherResponse>() {
        @Override
        public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
            // MS: Bei den News sind die news/0 kaputt
            if ( response.body() != null ) {
                update(response.body().getTemperature(), response.body().getBackgroundId(), response.body().getIconId());
            }
        }

        @Override
        public void onFailure(Call<WeatherResponse> call, Throwable t) {

        }

    };
    
    private final Context   mContext;
    
    private ImageView mBackground;
    private ImageView mIcon;
    private TextView  mTemperature;
    private TextView  mDayOfWeek;
    private TextView  mDate;
}
