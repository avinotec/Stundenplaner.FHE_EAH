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
package de.fhe.fhemobile.vos.canteen;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 04.02.14.
 */
public class CanteenDishVo implements Parcelable {

// --Commented out by Inspection START (02.11.2021 17:19):
//    public CanteenDishVo() {
//    }
// --Commented out by Inspection STOP (02.11.2021 17:19)

    public CanteenDishVo(final String mTitle, final String mDescription, final String mIngredients, final String mPrice, final long mDate,
                         final String mDateString, final String mCanteenName, final Integer mCanteenId) {
        this.mTitle         = mTitle;
        this.mDescription   = mDescription;
        this.mIngredients   = mIngredients;
        this.mPrice         = mPrice;
        this.mDate          = mDate;
        this.mDateString    = mDateString;
        this.mCanteenName   = mCanteenName;
        this.mCanteenId = mCanteenId;
    }

    public CanteenDishVo(final Parcel _In) {
        mTitle          = _In.readString();
        mDescription    = _In.readString();
        mIngredients    = _In.readString();
        mPrice          = _In.readString();
        mDate           = _In.readLong();
        mDateString     = _In.readString();
        mCanteenName    = _In.readString();
        mCanteenId      = _In.readInt();
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
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mIngredients);
        dest.writeString(mPrice);
        dest.writeLong(mDate);
        dest.writeString(mDateString);
        dest.writeString(mCanteenName);
        dest.writeInt(mCanteenId);
    }

    public static final Parcelable.Creator<CanteenDishVo> CREATOR
            = new Parcelable.Creator<CanteenDishVo>() {
        public CanteenDishVo createFromParcel(final Parcel in) {
            return new CanteenDishVo(in);
        }

        public CanteenDishVo[] newArray(final int size) {
            return new CanteenDishVo[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

// --Commented out by Inspection START (02.11.2021 17:19):
//    public void setTitle(String mTitle) {
//        this.mTitle = mTitle;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:19)

    public String getDescription() {
        return mDescription;
    }

// --Commented out by Inspection START (02.11.2021 17:18):
//    public void setDescription(String mDescription) {
//        this.mDescription = mDescription;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:18)

// --Commented out by Inspection START (02.11.2021 17:19):
//    public String getIngredients() {
//        return mIngredients;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:19)

// --Commented out by Inspection START (02.11.2021 17:18):
//    public void setIngredients(String mIngredients) {
//        this.mIngredients = mIngredients;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:18)

    public String getPrice() {
        return mPrice;
    }

// --Commented out by Inspection START (02.11.2021 17:18):
//    public void setPrice(String mPrice) {
//        this.mPrice = mPrice;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:18)

    public long getDate() {
        return mDate;
    }

// --Commented out by Inspection START (02.11.2021 17:18):
//    public void setDate(long mDate) {
//        this.mDate = mDate;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:18)

    public String getDateString() {
        return mDateString;
    }

// --Commented out by Inspection START (02.11.2021 17:18):
//    public void setDateString(String mDateString) {
//        this.mDateString = mDateString;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:18)

// --Commented out by Inspection START (02.11.2021 17:19):
//    public String getMensaName() {
//        return mCanteenName;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:19)

// --Commented out by Inspection START (02.11.2021 17:18):
//    public void setMensaName(String mCanteenName) {
//        this.mCanteenName = mCanteenName;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:18)

// --Commented out by Inspection START (02.11.2021 17:19):
//    public Integer getMensaId() {
//        return mCanteenId;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:19)

// --Commented out by Inspection START (02.11.2021 17:18):
//    public void setMensaId(Integer mCanteenId) {
//        this.mCanteenId = mCanteenId;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:18)


    //********************************************************************************************
    @SerializedName("title")
    private final String mTitle;

    @SerializedName("description")
    private final String mDescription;

    @SerializedName("ingredients")
    private final String mIngredients;

    @SerializedName("price")
    private final String mPrice;

    @SerializedName("date")
    private final long   mDate;

    @SerializedName("dateAsString")
    private final String mDateString;

    @SerializedName("mensaName")
    private final String mCanteenName;

    @SerializedName("mensaId")
    private final Integer mCanteenId;


}