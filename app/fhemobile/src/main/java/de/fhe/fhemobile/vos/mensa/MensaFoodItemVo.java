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
package de.fhe.fhemobile.vos.mensa;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 04.02.14.
 */
public class MensaFoodItemVo implements Parcelable {

    public MensaFoodItemVo() {
    }

    public MensaFoodItemVo(String mTitle, String mDescription, String mIngredients, String mPrice, long mDate, String mDateString, String mMensaName, Integer mMensaId) {
        this.mTitle         = mTitle;
        this.mDescription   = mDescription;
        this.mIngredients   = mIngredients;
        this.mPrice         = mPrice;
        this.mDate          = mDate;
        this.mDateString    = mDateString;
        this.mMensaName     = mMensaName;
        this.mMensaId       = mMensaId;
    }

    public MensaFoodItemVo(Parcel _In) {
        mTitle          = _In.readString();
        mDescription    = _In.readString();
        mIngredients    = _In.readString();
        mPrice          = _In.readString();
        mDate           = _In.readLong();
        mDateString     = _In.readString();
        mMensaName      = _In.readString();
        mMensaId        = _In.readInt();
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mIngredients);
        dest.writeString(mPrice);
        dest.writeLong(mDate);
        dest.writeString(mDateString);
        dest.writeString(mMensaName);
        dest.writeInt(mMensaId);
    }

    public static final Parcelable.Creator<MensaFoodItemVo> CREATOR
            = new Parcelable.Creator<MensaFoodItemVo>() {
        public MensaFoodItemVo createFromParcel(Parcel in) {
            return new MensaFoodItemVo(in);
        }

        public MensaFoodItemVo[] newArray(int size) {
            return new MensaFoodItemVo[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getIngredients() {
        return mIngredients;
    }

    public void setIngredients(String mIngredients) {
        this.mIngredients = mIngredients;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long mDate) {
        this.mDate = mDate;
    }

    public String getDateString() {
        return mDateString;
    }

    public void setDateString(String mDateString) {
        this.mDateString = mDateString;
    }

    public String getMensaName() {
        return mMensaName;
    }

    public void setMensaName(String mMensaName) {
        this.mMensaName = mMensaName;
    }

    public Integer getMensaId() {
        return mMensaId;
    }

    public void setMensaId(Integer mMensaId) {
        this.mMensaId = mMensaId;
    }


    //********************************************************************************************
    @SerializedName("title")
    private String mTitle;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("ingredients")
    private String mIngredients;

    @SerializedName("price")
    private String mPrice;

    @SerializedName("date")
    private long   mDate;

    @SerializedName("dateAsString")
    private String mDateString;

    @SerializedName("mensaName")
    private String mMensaName;

    @SerializedName("mensaId")
    private Integer mMensaId;


}
