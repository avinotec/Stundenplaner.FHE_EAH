/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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
package de.fhe.fhemobile.vos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 04.03.15.
 */
public class WeatherResponse {
    
    public String getTemperature() {
        return mTemperature;
    }

    public void setTemperature(final String _temperature) {
        mTemperature = _temperature;
    }

    public String getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(final String _windSpeed) {
        mWindSpeed = _windSpeed;
    }

    public String getWindDirection() {
        return mWindDirection;
    }

    public void setWindDirection(final String _windDirection) {
        mWindDirection = _windDirection;
    }

    public String getChill() {
        return mChill;
    }

    public void setChill(final String _chill) {
        mChill = _chill;
    }

    public String getProvider() {
        return mProvider;
    }

    public void setProvider(final String _provider) {
        mProvider = _provider;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(final String _code) {
        mCode = _code;
    }

    public int getBackgroundId() {
        return mBackgroundId;
    }

    public void setBackgroundId(final int _backgroundId) {
        mBackgroundId = _backgroundId;
    }

    public int getIconId() {
        return mIconId;
    }

    public void setIconId(final int _iconId) {
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
