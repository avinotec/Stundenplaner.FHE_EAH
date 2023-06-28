package de.fhe.fhemobile.vos.myschedule;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

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
        dest.writeLong(mGermanStartDateTime);
        dest.writeLong(mGermanEndDateTime);
    }

    MyScheduleEventDateVo(final Parcel in) {
        this.mGermanStartDateTime = in.readLong();
        this.mGermanEndDateTime = in.readLong();
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
        if(mStartTime == 0){
            mStartTime = MyScheduleUtils.convertEahApiTimeToUtc(mGermanStartDateTime);
        }
        return mStartTime;
    }

    /**
     * Get end date time
     * @return End date time as long
     */
    public long getEndTime() {
        if(mEndTime == 0){
            mEndTime = MyScheduleUtils.convertEahApiTimeToUtc(mGermanEndDateTime);
        }
        return mEndTime;
    }

    /**
     * Get german start time in long in seconds
     * (note: do not use this except for constructors)
     * @return
     */
    public long getGermanStartTime() {
        return mGermanStartDateTime;
    }

    /**
     * Get german end time in long in seconds
     * (note: do not use this except for constructors)
     * @return
     */
    public long getGermanEndTime() {
        return mGermanEndDateTime;
    }

// --Commented out by Inspection START (28.06.2023 17:19):
//    public Date getGermanStartDate() {
//        return new Date(mGermanStartDateTime * 1000);
//    }
// --Commented out by Inspection STOP (28.06.2023 17:19)

// --Commented out by Inspection START (28.06.2023 17:19):
//    public Date getGermanEndDate() {
//        return new Date(mGermanEndDateTime * 1000);
//    }
// --Commented out by Inspection STOP (28.06.2023 17:19)

    @SerializedName("StartDateTime")
    private long mGermanStartDateTime;

    @SerializedName("EndDateTime")
    private long mGermanEndDateTime;

    private long mStartTime;
    private long mEndTime;

}
