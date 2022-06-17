package de.fhe.fhemobile.vos.myschedule;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MyScheduleEventDateVo implements Parcelable{

    public MyScheduleEventDateVo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeLong(mStartDateTime);
        dest.writeLong(mEndDateTime);
    }

    MyScheduleEventDateVo(final Parcel in) {
        this.mStartDateTime = in.readLong();
        this.mEndDateTime = in.readLong();
    }


    public static final Parcelable.Creator<MyScheduleEventDateVo> CREATOR = new Parcelable.Creator<MyScheduleEventDateVo>() {
        public MyScheduleEventDateVo createFromParcel(final Parcel source) {
            return new MyScheduleEventDateVo(source);
        }

        public MyScheduleEventDateVo[] newArray(final int size) {
            return new MyScheduleEventDateVo[size];
        }
    };

    /**
     * Get start date time in seconds
     * (attention: needs to be multiplied by 1000 when used to create a {@link Date})
     * @return
     */
    public long getStartDateTimeInSec() {
        return mStartDateTime;
    }

    /**
     * Get end date time in seconds
     * (attention: needs to be multiplied by 1000 when used to create a {@link Date})
     * @return
     */
    public long getEndDateTimeInSec() {
        return mEndDateTime;
    }

    public Date getStartDateTime() {
        return new Date(mStartDateTime * 1000);
    }

    public Date getEndDateTime() {
        return new Date(mEndDateTime * 1000);
    }

    public String getStartDateTimeAsString() {
        return new Date(mStartDateTime * 1000).toString();
    }

    @SerializedName("StartDateTime")
    private long mStartDateTime;

    @SerializedName("EndDateTime")
    private long mEndDateTime;
}
