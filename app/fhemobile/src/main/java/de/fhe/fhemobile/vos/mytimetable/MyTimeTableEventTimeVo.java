package de.fhe.fhemobile.vos.mytimetable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MyTimeTableEventTimeVo implements Parcelable{

    public MyTimeTableEventTimeVo() {
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

    private MyTimeTableEventTimeVo(final Parcel in) {
        this.mStartDateTime = in.readLong();
        this.mEndDateTime = in.readLong();
    }


    public static final Parcelable.Creator<MyTimeTableEventTimeVo> CREATOR = new Parcelable.Creator<MyTimeTableEventTimeVo>() {
        public MyTimeTableEventTimeVo createFromParcel(final Parcel source) {
            return new MyTimeTableEventTimeVo(source);
        }

        public MyTimeTableEventTimeVo[] newArray(final int size) {
            return new MyTimeTableEventTimeVo[size];
        }
    };

    /**
     * Get start date time in seconds
     * @return
     */
    public long getStartDateTimeInSec() {
        return mStartDateTime;
    }

    /**
     * Get end date time in seconds
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
