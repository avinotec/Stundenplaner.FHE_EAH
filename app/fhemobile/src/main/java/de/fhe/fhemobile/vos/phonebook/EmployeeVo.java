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
package de.fhe.fhemobile.vos.phonebook;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


/**
 * Created by paul on 22.01.14.
 */
public class EmployeeVo implements Parcelable {

    public EmployeeVo() {

    }

    public EmployeeVo(final Integer mId, final String mFirstName, final String mLastName, final String mTitle, final String mPhone, final String mFax, final String mRole, final String mMail, final String mDivision, final String mRoom) {
        this.mId = mId;
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mTitle = mTitle;
        this.mPhone = mPhone;
        this.mFax = mFax;
        this.mRole = mRole;
        this.mMail = mMail;
        this.mDivision = mDivision;
        this.mRoom = mRoom;
    }

    public String getFullName() {
        return mTitle + " " + mFirstName + " " + mLastName;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(final Integer id) {
        mId = id;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(final String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(final String lastName) {
        mLastName = lastName;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(final String title) {
        mTitle = checkForEmpty(title);
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(final String phone) {
        mPhone = checkForEmpty(phone);
    }

    public String getFax() {
        return mFax;
    }

    public void setFax(final String fax) {
        mFax = checkForEmpty(fax);
    }

    public String getRole() {
        return mRole;
    }

    public void setRole(final String role) {
        mRole = checkForEmpty(role);
    }

    public String getMail() {
        return mMail;
    }

    public void setMail(final String mail) {
        mMail = checkForEmpty(mail);
    }

    public String getDivision() {
        return mDivision;
    }

    public void setDivision(final String division) {
        mDivision = checkForEmpty(division);
    }

    public String getRoom() {
        return mRoom;
    }

    public void setRoom(final String room) {
        mRoom = checkForEmpty(room);
    }

    private String checkForEmpty(final String _Source) {
        final String temp;
        if(_Source.contentEquals("-;")) {
            temp = "";
        }
        else {
            temp = _Source;
        }
        return temp;
    }

    //---------------------------------------------------------
    // Parcel Stuff
    //---------------------------------------------------------

    public EmployeeVo(final Parcel _In) {
        mId = _In.readInt();
        mFirstName = _In.readString();
        mLastName = _In.readString();
        mTitle = _In.readString();
        mPhone = _In.readString();
        mFax = _In.readString();
        mRole = _In.readString();
        mMail = _In.readString();
        mDivision = _In.readString();
        mRoom = _In.readString();
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
        dest.writeInt(mId);
        dest.writeString(mFirstName);
        dest.writeString(mLastName);
        dest.writeString(mTitle);
        dest.writeString(mPhone);
        dest.writeString(mFax);
        dest.writeString(mRole);
        dest.writeString(mMail);
        dest.writeString(mDivision);
        dest.writeString(mRoom);
    }

    public static final Parcelable.Creator<EmployeeVo> CREATOR
            = new Parcelable.Creator<EmployeeVo>() {
        public EmployeeVo createFromParcel(final Parcel in) {
            return new EmployeeVo(in);
        }

        public EmployeeVo[] newArray(final int size) {
            return new EmployeeVo[size];
        }
    };
    
    //---------------------------------------------------------
    // Member
    //---------------------------------------------------------

    @SerializedName("id")
    private Integer mId;

    @SerializedName("firstName")
    private String mFirstName;

    @SerializedName("lastName")
    private String mLastName;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("phone")
    private String mPhone;

    @SerializedName("fax")
    private String mFax;

    @SerializedName("role")
    private String mRole;

    @SerializedName("mail")
    private String mMail;

    @SerializedName("division")
    private String mDivision;

    @SerializedName("room")
    private String mRoom;

}
