package de.fhe.fhemobile.vos.semesterdata;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 24.01.14.
 */
public class DateVo implements Parcelable {

    public DateVo() {
    }

    public DateVo(String mDate, String mName) {
        this.mDate = mDate;
        this.mName = mName;
    }

    public DateVo(Parcel _In) {
        mDate = _In.readString();
        mName = _In.readString();
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
        dest.writeString(mDate);
        dest.writeString(mName);
    }

    public static final Parcelable.Creator<DateVo> CREATOR
            = new Parcelable.Creator<DateVo>() {
        public DateVo createFromParcel(Parcel in) {
            return new DateVo(in);
        }

        public DateVo[] newArray(int size) {
            return new DateVo[size];
        }
    };

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    @SerializedName("date")
    private String mDate;

    @SerializedName("name")
    private String mName;


}
