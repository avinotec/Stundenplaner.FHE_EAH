package de.fhe.fhemobile.vos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 04.03.15.
 */
public class WeatherResponse {
    
    public String getTemperature() {
        return mTemperature;
    }

    public void setTemperature(String _temperature) {
        mTemperature = _temperature;
    }

    public String getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(String _windSpeed) {
        mWindSpeed = _windSpeed;
    }

    public String getWindDirection() {
        return mWindDirection;
    }

    public void setWindDirection(String _windDirection) {
        mWindDirection = _windDirection;
    }

    public String getChill() {
        return mChill;
    }

    public void setChill(String _chill) {
        mChill = _chill;
    }

    public String getProvider() {
        return mProvider;
    }

    public void setProvider(String _provider) {
        mProvider = _provider;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String _code) {
        mCode = _code;
    }

    public int getBackgroundId() {
        return mBackgroundId;
    }

    public void setBackgroundId(int _backgroundId) {
        mBackgroundId = _backgroundId;
    }

    public int getIconId() {
        return mIconId;
    }

    public void setIconId(int _iconId) {
        mIconId = _iconId;
    }

    // #############################################################################################
    
    @SerializedName("temperature")
    private String mTemperature;
    
    @SerializedName("windSpeed")
    private String mWindSpeed;
    
    @SerializedName("windDirection")
    private String mWindDirection;
    
    @SerializedName("chill")
    private String mChill;
    
    @SerializedName("provider")
    private String mProvider;
    
    @SerializedName("code")
    private String mCode;
    
    @SerializedName("backgroundId")
    private int    mBackgroundId;
    
    @SerializedName("iconId")
    private int    mIconId;
}
