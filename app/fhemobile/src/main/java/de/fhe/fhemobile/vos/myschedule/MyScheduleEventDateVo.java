package de.fhe.fhemobile.vos.myschedule;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import de.fhe.fhemobile.utils.myschedule.MyScheduleUtils;

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
     * Get start date time
     * @return Start date tim as long
     */
    public long getStartTime() {
        return MyScheduleUtils.convertEahApiTimeToUtc(mStartDateTime);
    }

    /**
     * Get end date time
     * @return End date time as long
     */
    public long getEndTime() {
        return MyScheduleUtils.convertEahApiTimeToUtc(mEndDateTime);
    }

    public Date getStartDateTime() {
        return new Date(mStartDateTime * 1000);
    }

    public Date getEndDateTime() {
        return new Date(mEndDateTime * 1000);
    }

    /* not used
    public String getStartDateTimeAsString() {
        return new Date(mStartDateTime * 1000).toString();
    } */

    @SerializedName("StartDateTime")
    private long mStartDateTime;

    @SerializedName("EndDateTime")
    private long mEndDateTime;
}
