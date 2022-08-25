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
package de.fhe.fhemobile.vos.news;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 01.02.14.
 */
public class NewsItemVo implements Parcelable {

    public NewsItemVo() {
    }

    public NewsItemVo(final String _title) {
        mTitle       = _title;
        mLink        = "";
        mDescription = "Description";
        mEncoded     = "Encoded";
        mAuthor      = "Paul";
        mPubDate     = "Wed, 08 Apr 2015 08:15:00 +0200";
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(final String mTitle) {
        this.mTitle = mTitle;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(final String mLink) {
        this.mLink = mLink;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(final String mDescription) {
        this.mDescription = mDescription;
    }

    public String getEncoded() {
        return mEncoded;
    }

    public void setEncoded(final String mEncoded) {
        this.mEncoded = mEncoded;
    }

    public String[] getCategories() {
        return mCategories;
    }

    public void setCategories(final String[] mCategories) {
        this.mCategories = mCategories;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(final String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getPubDate() {
        if(null != mPubDate){
            return mPubDate.substring(4, 22);
        }
        return "";
    }

    public void setPubDate(final String mPubDate) {
        this.mPubDate = mPubDate;
    }


    @SerializedName("title")
    private String mTitle;

    @SerializedName("link")
    private String mLink;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("encoded")
    private String mEncoded;

    @SerializedName("category")
    private String[] mCategories;

    @SerializedName("author")
    private String mAuthor;

    @SerializedName("pubDate")
    private String mPubDate;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(this.mTitle);
        dest.writeString(this.mLink);
        dest.writeString(this.mDescription);
        dest.writeString(this.mEncoded);
        dest.writeStringArray(this.mCategories);
        dest.writeString(this.mAuthor);
        dest.writeString(this.mPubDate);
    }

    NewsItemVo(final Parcel in) {
        this.mTitle = in.readString();
        this.mLink = in.readString();
        this.mDescription = in.readString();
        this.mEncoded = in.readString();
        this.mCategories = in.createStringArray();
        this.mAuthor = in.readString();
        this.mPubDate = in.readString();
    }

    public static final Parcelable.Creator<NewsItemVo> CREATOR = new Parcelable.Creator<NewsItemVo>() {
        public NewsItemVo createFromParcel(final Parcel source) {
            return new NewsItemVo(source);
        }

        public NewsItemVo[] newArray(final int size) {
            return new NewsItemVo[size];
        }
    };
}
