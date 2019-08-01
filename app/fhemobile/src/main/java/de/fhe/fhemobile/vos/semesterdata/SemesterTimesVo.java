package de.fhe.fhemobile.vos.semesterdata;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 27.01.14.
 */
public class SemesterTimesVo implements Parcelable {

    public SemesterTimesVo() {
    }

    public SemesterTimesVo(DateVo mDate, PeriodVo mPeriod) {
        this.mDate = mDate;
        this.mPeriod = mPeriod;
    }

    public SemesterTimesVo(Parcel _In) {
        mDate = _In.readParcelable(DateVo.class.getClassLoader());
        mPeriod = _In.readParcelable(PeriodVo.class.getClassLoader());
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
        dest.writeParcelable(mDate, 0);
        dest.writeParcelable(mPeriod, 0);
    }

    public static final Parcelable.Creator<SemesterTimesVo> CREATOR
            = new Parcelable.Creator<SemesterTimesVo>() {
        public SemesterTimesVo createFromParcel(Parcel in) {
            return new SemesterTimesVo(in);
        }

        public SemesterTimesVo[] newArray(int size) {
            return new SemesterTimesVo[size];
        }
    };


    public DateVo getDate() {
        return mDate;
    }

    public void setDate(DateVo mDate) {
        this.mDate = mDate;
    }

    public PeriodVo getPeriod() {
        return mPeriod;
    }

    public void setPeriod(PeriodVo mPeriod) {
        this.mPeriod = mPeriod;
    }


    @SerializedName("date")
    private DateVo mDate;

    @SerializedName("period")
    private PeriodVo mPeriod;


}
