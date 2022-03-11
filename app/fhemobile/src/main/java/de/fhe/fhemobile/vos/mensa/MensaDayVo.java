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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nadja - 03/2022
 */
public class MensaDayVo implements Parcelable {

    public MensaDayVo(final List<MensaFoodItemVo> items, final String date) {
        this.mItems = items;
        this.mDate = date;
    }

    public MensaDayVo(final Parcel _In) {
        _In.readTypedList(mItems, MensaFoodItemVo.CREATOR);
        mDate = _In.readString();
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
        dest.writeTypedList(mItems);
        dest.writeString(mDate);
    }

    public static final Creator<MensaDayVo> CREATOR
            = new Creator<MensaDayVo>() {
        public MensaDayVo createFromParcel(final Parcel in) {
            return new MensaDayVo(in);
        }

        public MensaDayVo[] newArray(final int size) {
            return new MensaDayVo[size];
        }
    };

    public List<MensaFoodItemVo> getItems() {
        return mItems;
    }

    public String getDate() {
        return mDate;
    }



    private List<MensaFoodItemVo> mItems = new ArrayList<MensaFoodItemVo>();
    private final String mDate;
}
