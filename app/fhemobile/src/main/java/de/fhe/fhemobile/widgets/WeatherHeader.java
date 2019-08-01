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
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
    
    private Callback<WeatherResponse> mWeatherResponseCallback = new Callback<WeatherResponse>() {
        @Override
        public void success(WeatherResponse t, Response response) {
            // MS: Bei den News sind die news/0 kaputt
            if ( t != null ) {
                update(t.getTemperature(), t.getBackgroundId(), t.getIconId());
            }
        }

        @Override
        public void failure(RetrofitError error) {
            // Show some kind of error
        }
    };
    
    private Context   mContext;
    
    private ImageView mBackground;
    private ImageView mIcon;
    private TextView  mTemperature;
    private TextView  mDayOfWeek;
    private TextView  mDate;
}
