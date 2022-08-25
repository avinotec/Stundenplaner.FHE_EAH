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
package de.fhe.fhemobile.vos.canteen;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Value object for a menu (a collection of {@link CanteenDishVo}s) of one day
 * Created by paul on 10.02.14
 */
public class CanteenMenuDayVo implements Parcelable {

    public CanteenMenuDayVo(final List<CanteenDishVo> mDishes, final String mDate) {
        this.mDishes = mDishes;
        this.mDate = mDate;
    }

    public CanteenMenuDayVo(final Parcel _In) {
        _In.readTypedList(mDishes, CanteenDishVo.CREATOR);
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
        dest.writeTypedList(mDishes);
        dest.writeString(mDate);
    }

    public static final Parcelable.Creator<CanteenMenuDayVo> CREATOR
            = new Parcelable.Creator<CanteenMenuDayVo>() {
        public CanteenMenuDayVo createFromParcel(final Parcel in) {
            return new CanteenMenuDayVo(in);
        }

        public CanteenMenuDayVo[] newArray(final int size) {
            return new CanteenMenuDayVo[size];
        }
    };

    public List<CanteenDishVo> getItems() {
        return mDishes;
    }

    public String getHeadline() {
        return mDate;
    }



    private List<CanteenDishVo> mDishes = new ArrayList<CanteenDishVo>();
    private final String mDate;
}
